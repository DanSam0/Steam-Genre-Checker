package com.dansam0.steamdemo.service;

import com.dansam0.steamdemo.entity.ProfileGenre;
import com.dansam0.steamdemo.repository.ProfileGenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileGenreServiceImpl implements ProfileGenreService{

    private ProfileGenreRepository profileGenreRepository;

    public ProfileGenreServiceImpl(ProfileGenreRepository profileGenreRepository) {
        this.profileGenreRepository = profileGenreRepository;
    }

    @Override
    @Transactional
    public void save(ProfileGenre profileGenre) {
        profileGenreRepository.save(profileGenre);
    }

    @Override
    @Transactional
    public void saveAll(List<ProfileGenre> profileGenres) {
        profileGenreRepository.saveAll(profileGenres);
    }

    @Override
    public List<ProfileGenre> findByProfileId(long profileId) {

        Optional<List<ProfileGenre>> result = profileGenreRepository.findByProfileId(profileId);

        if(!result.isPresent())
            throw new RuntimeException("Didn't find profile games with id - " + profileId);

        List<ProfileGenre> profileGenres = new ArrayList<>(result.get());

        return profileGenres;
    }

    @Override
    public List<ProfileGenre> findByProfileIdByPlaytimeDesc(long profileId) {

        Optional<List<ProfileGenre>> result = profileGenreRepository.findByProfileIdOrderByPlaytimeDesc(profileId);

        if(!result.isPresent())
            throw new RuntimeException("Didn't find profile games with id - " + profileId);

        List<ProfileGenre> profileGenres = new ArrayList<>(result.get());

        return profileGenres;
    }

    @Override
    public List<ProfileGenre> findByGenreId(long genreId) {

        Optional<List<ProfileGenre>> result = profileGenreRepository.findByGenreId(genreId);

        if(!result.isPresent())
            throw new RuntimeException("Didn't find profile games with id - " + genreId);

        List<ProfileGenre> profileGenres = new ArrayList<>(result.get());

        return profileGenres;
    }

    @Override
    @Transactional
    public void deleteByProfileId(long profileId) {
        profileGenreRepository.deleteByProfileId(profileId);
    }
}
