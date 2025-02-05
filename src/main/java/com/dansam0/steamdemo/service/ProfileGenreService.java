package com.dansam0.steamdemo.service;

import com.dansam0.steamdemo.entity.ProfileGenre;

import java.util.List;

public interface ProfileGenreService {

    void save(ProfileGenre profileGenre);

    void saveAll(List<ProfileGenre> profileGenres);

    List<ProfileGenre> findByProfileId(long profileId);

    List<ProfileGenre> findByProfileIdByPlaytimeDesc(long profileId);

    List<ProfileGenre> findByGenreId(long genreId);

    void deleteByProfileId(long profileId);

}
