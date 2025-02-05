package com.dansam0.steamdemo.repository;

import com.dansam0.steamdemo.entity.ProfileGenre;
import com.dansam0.steamdemo.entity.ProfileGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileGenreRepository extends JpaRepository<ProfileGenre, ProfileGenreId> {

    @Query("SELECT p FROM ProfileGenre p WHERE p.primaryKey.profile.steamId = :profileId")
    Optional<List<ProfileGenre>> findByProfileId(long profileId);

    @Modifying
    @Query("DELETE FROM ProfileGenre p WHERE p.primaryKey.profile.steamId = :profileId")
    void deleteByProfileId(long profileId);

    @Query("SELECT p FROM ProfileGenre p WHERE p.primaryKey.genre.id = :genreId")
    Optional<List<ProfileGenre>> findByGenreId(long genreId);

    @Modifying
    @Query("DELETE FROM ProfileGenre p WHERE p.primaryKey.genre.id = :genreId")
    void deleteByGenreId(long genreId);

    @Query("SELECT p FROM ProfileGenre p WHERE p.primaryKey.profile.steamId = :profileId ORDER BY p.playtime DESC")
    Optional<List<ProfileGenre>> findByProfileIdOrderByPlaytimeDesc(long profileId);
}
