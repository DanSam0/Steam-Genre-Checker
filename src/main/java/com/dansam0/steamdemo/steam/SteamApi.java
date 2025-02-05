package com.dansam0.steamdemo.steam;

import com.dansam0.steamdemo.dto.GameDto;
import com.dansam0.steamdemo.entity.Genre;
import com.dansam0.steamdemo.entity.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
public class SteamApi {

    private final SteamDataProcessor steamDataProcessor;
    private final String resolvePlayerIdURL = "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/";
    private final String getPlayerSummariesURL = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/";
    private final String getPlayerGameInfoURL = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";
    private final String getGameTagsURL = "https://steamspy.com/api.php?request=appdetails";

    public SteamApi(SteamDataProcessor steamDataProcessor) {
        this.steamDataProcessor = steamDataProcessor;
    }

    public long resolveProfileIdByUrl(String apiKey, String vanityUrl){

        HttpURLConnection con = null;
        long id = 0;

        try {

            URL url = new URL(resolvePlayerIdURL + "?key=" + apiKey + "&vanityurl=" + vanityUrl);
            con = (HttpURLConnection) url.openConnection();

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
                return 0;

            return steamDataProcessor.readProfileId(con.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }

        return 0;
    }

    public Profile getProfileById(String apiKey, long id) {

        HttpURLConnection con = null;
        Profile profile = null;

        try {

            URL url = new URL(getPlayerSummariesURL + "?key=" + apiKey + "&steamids=" + id);
            con = (HttpURLConnection) url.openConnection();

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            profile = steamDataProcessor.readProfile(con.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }

        return profile;
    }

    public List<GameDto> getGamesByProfileId(String apiKey, long id){

        HttpURLConnection con = null;
        List<GameDto> games = new ArrayList<>();

        try {

            URL url = new URL(getPlayerGameInfoURL + "?key=" + apiKey + "&steamid=" + id + "&include_appinfo=true&format=json");
            con = (HttpURLConnection) url.openConnection();

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            games = steamDataProcessor.readGames(con.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                con.disconnect();
        }

        return games;
    }

    public List<Genre> getGenresByGameId(int id){

        List<Genre> genreList = new ArrayList<>();

        for(int i = 0; i < 5; i++){

            HttpURLConnection con = null;

            try {

                URL url = new URL(getGameTagsURL + "&appid=" + id);
                con = (HttpURLConnection) url.openConnection();

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
                    continue;

                genreList = steamDataProcessor.readGenres(con.getInputStream());

                return genreList;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null)
                    con.disconnect();
            }
        }

        throw new RuntimeException("Failed to retrieve genre data after 5 tries.");
    }

}
