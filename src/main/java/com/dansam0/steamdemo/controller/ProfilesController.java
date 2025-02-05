package com.dansam0.steamdemo.controller;

import com.dansam0.steamdemo.dto.GameDto;
import com.dansam0.steamdemo.dto.PaginatedResponse;
import com.dansam0.steamdemo.entity.*;
import com.dansam0.steamdemo.service.GenreService;
import com.dansam0.steamdemo.service.ProfileGenreService;
import com.dansam0.steamdemo.service.ProfileService;
import com.dansam0.steamdemo.steam.SteamApi;
import com.dansam0.steamdemo.dto.ProfileDto;
import com.dansam0.steamdemo.dto.ProfileGenreDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

@Controller
public class ProfilesController {

    private ProfileService profileService;
    private GenreService genreService;
    private ProfileGenreService profileGenreService;
    private SteamApi steamApi;
    private int pageSize = 25;

    public ProfilesController(ProfileService profileService, GenreService genreService, ProfileGenreService profileGenreService, SteamApi steamApi){
        this.profileService = profileService;
        this.genreService = genreService;
        this.profileGenreService = profileGenreService;
        this.steamApi = steamApi;
    }

    @GetMapping("")
    public String showHomeWithProfiles(Model model){

        Page<Profile> profilePage = profileService.findAllWithPaginationAndSort(0, pageSize);

        List<Profile> profileList = profilePage.getContent();

        model.addAttribute("profiles", profileList);
        model.addAttribute("page", 0);
        model.addAttribute("maxPages", profilePage.getTotalPages());
        model.addAttribute("nameFilter", "");
        return "profiles/main";
    }

    @GetMapping("/searchByName")
    public String searchProfiles(@RequestParam("nameFilter") String nameFilter) throws UnsupportedEncodingException {

        if(nameFilter.isEmpty())
            return "redirect:/";

        String encodedNameFilter = URLEncoder.encode(nameFilter, StandardCharsets.UTF_8);
        return "redirect:/search?page=0&nameFilter=" + encodedNameFilter;
    }

    @GetMapping("/search")
    public String searchProfilesWithPagination(
            Model model, @RequestParam("page") int pNum,
            @RequestParam("nameFilter") String nameFilter, Pageable pageable){

        List<Profile> profiles = profileService.findAllByNameContainingOrderByNameAsc(nameFilter);

        int start = 0;

        for (int i = 0; i < pNum; i++){
            start += pageSize;
        }
        int end = Math.min((start + pageSize), profiles.size());

        Page<Profile> page = new PageImpl<>(profiles.subList(start, end), pageable, profiles.size());
        List<Profile> paginatedProfiles = page.getContent();

        model.addAttribute("profiles", paginatedProfiles);
        model.addAttribute("page", pNum);
        model.addAttribute("size", pageSize);
        model.addAttribute("maxPages", (float) profiles.size()/pageSize);
        model.addAttribute("nameFilter", nameFilter);

        return "profiles/main";
    }

    @PostMapping("/find")
    public String save(RedirectAttributes redirectAttrs, @RequestParam("profileUrl") String profileUrl, HttpServletRequest request){

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if(!profileUrl.contains("steamcommunity.com")){
            redirectAttrs.addFlashAttribute("invalidLink", "Please, enter valid link");
            return "redirect:/";
        }

        Profile tempProfile = null;

        if(profileUrl.contains("/id/")){

            long id = 0;
            id = steamApi.resolveProfileIdByUrl(user.getApiKey(), getIdPartOfUrl(profileUrl, "/id/"));

            if(id == 0) {
                redirectAttrs.addFlashAttribute("invalidLink", "Couldn't fetch profile data - invalid id or private profile");
                return "redirect:/";
            }

            tempProfile = steamApi.getProfileById(user.getApiKey(), id);
        }
        else if(profileUrl.contains("/profiles/")){

            try{
                long id = Long.parseLong(getIdPartOfUrl(profileUrl, "/profiles/"));
                tempProfile = steamApi.getProfileById(user.getApiKey(), id);
            } catch (NumberFormatException e){
                redirectAttrs.addFlashAttribute("invalidLink", "Invalid link format");
                return "redirect:/";
            }
        }

        if(tempProfile == null){
            redirectAttrs.addFlashAttribute("invalidLink", "Couldn't fetch profile data - invalid id or private profile");
            return "redirect:/";
        }

        saveProfile(tempProfile, user.getApiKey());
        return "redirect:/info?steamId=" + tempProfile.getSteamId();
    }

    @GetMapping("/info")
    public String showProfile(@RequestParam("steamId") String id, Model model){

        Profile profile = profileService.findById(Long.parseLong(id));

        if(profile == null)
            throw new RuntimeException("Profile of id " + id + " not found.");

        ProfileDto profileDto = new ProfileDto();
        profileDto.mapFromProfile(profile);

        List<ProfileGenre> profileGenres = profileGenreService.findByProfileIdByPlaytimeDesc(profile.getSteamId());
        List<ProfileGenreDto> profileGenreDtoList = formatToWebProfileGenres(profileGenres);

        model.addAttribute("profile", profileDto);
        model.addAttribute("profileGenres", profileGenreDtoList);

        return "profiles/profile";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("steamId") String id){

        profileGenreService.deleteByProfileId(Long.parseLong(id));
        profileService.deleteById(Long.parseLong(id));

        return "redirect:/";
    }

    private String getIdPartOfUrl(String url, String part){
        String profileId = url.substring(url.indexOf(part) + part.length());
        String[] splitString = profileId.split("/");
        return splitString[0];
    }

    private void saveProfile(Profile tempProfile, String apiKey) {

        List<GameDto> tempGames = steamApi.getGamesByProfileId(apiKey, tempProfile.getSteamId());

        if(tempGames == null)
            tempGames = new ArrayList<>();

        System.out.println("Retrieving data... ");

        long start = System.currentTimeMillis();

        ProfileGenresAndPlaytime result = fetchGenresMT(tempGames);

        long finish = System.currentTimeMillis();

        long timeElapsed = TimeUnit.MILLISECONDS.toSeconds(finish - start);
        System.out.println("Retrieved data! Elapsed time: " + timeElapsed + "s");

        List<ProfileGenre> profileGenreList = result.getProfileGenres();
        int playtime = result.getPlaytime();

        System.out.println("Results size: " + profileGenreList.size() + "\nPlayed time: " + playtime/60f);

        sortAndSetProfileGenresPlaytime(profileGenreList, tempProfile);

        tempProfile.setOverallPlaytime(playtime);
        profileService.save(tempProfile);

        profileGenreService.saveAll(profileGenreList);

        System.out.println("Saved!");
    }

    private ProfileGenresAndPlaytime fetchGenresMT(List<GameDto> tempGames){

        int sumPlaytime = 0;

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<List<ProfileGenre>>> futures = new ArrayList<>();
        for (GameDto game : tempGames) {

            Callable<List<ProfileGenre>> task = () -> {

                List<Genre> genres = steamApi.getGenresByGameId(game.getId());

                List<ProfileGenre> profileGenreList = new ArrayList<>();

                for (Genre genre : genres) {

                    Genre tempGenre = genreService.save(genre);

                    if(tempGenre != null)
                        genre = tempGenre;

                    ProfileGenre profileGenre = new ProfileGenre();
                    profileGenre.setGenre(genre);
                    profileGenre.setPlaytime(game.getPlaytime());

                    profileGenreList.add(profileGenre);
                }

                return profileGenreList;
            };
            futures.add(executor.submit(task));

            sumPlaytime += game.getPlaytime();
        }

        List<ProfileGenre> results = new ArrayList<>();

        for (Future<List<ProfileGenre>> future : futures) {
            try {
                results.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        return new ProfileGenresAndPlaytime(sumPlaytime, results);
    }


    private List<ProfileGenre> getGenres(GameDto game){

        List<ProfileGenre> profileGenreList = new ArrayList<>();

        List<Genre> genres = steamApi.getGenresByGameId(game.getId());

        for (Genre genre : genres) {

            Genre tempGenre = genreService.save(genre);

            if(tempGenre != null)
                genre = tempGenre;

            ProfileGenre profileGenre = new ProfileGenre();
            profileGenre.setGenre(genre);
            profileGenre.setPlaytime(game.getPlaytime());

            profileGenreList.add(profileGenre);
        }

        return profileGenreList;
    }

    private List<ProfileGenreDto> formatToWebProfileGenres(List<ProfileGenre> profileGenres) {

        List<ProfileGenreDto> profileGenreDtoList = new ArrayList<>();

        for (ProfileGenre profileGenre : profileGenres) {

            ProfileGenreDto profileGenreDto = new ProfileGenreDto();
            profileGenreDto.setGenreName(profileGenre.getGenre().getName());
            profileGenreDto.setPlaytime(profileGenre.getPlaytime());

            profileGenreDtoList.add(profileGenreDto);
        }

        return profileGenreDtoList;
    }

    private void sortAndSetProfileGenresPlaytime(List<ProfileGenre> profileGenreList, Profile profile){

        for (int i = 0; i < profileGenreList.size(); i++){
            for(int j = 0; j < profileGenreList.size(); j++){

                profileGenreList.get(i).setProfile(profile);

                if(i == j)
                    continue;

                if(Objects.equals(profileGenreList.get(i).getGenre().getName(), profileGenreList.get(j).getGenre().getName())){

                    profileGenreList.get(i).setPlaytime(
                            profileGenreList.get(i).getPlaytime() + profileGenreList.get(j).getPlaytime());

                    profileGenreList.remove(j);
                    j--;
                }
            }
        }

        for(int i = profileGenreList.size() - 1; i >= 0; i--){
            if(profileGenreList.get(i).getPlaytime()/60f <= 0.1f)
                profileGenreList.remove(i);
        }

    }

    @Data
    @AllArgsConstructor
    public class ProfileGenresAndPlaytime{

        int playtime;
        List<ProfileGenre> profileGenres;

    }

}
