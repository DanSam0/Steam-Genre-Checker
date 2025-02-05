package com.dansam0.steamdemo.repository;

import com.dansam0.steamdemo.entity.Genre;
import com.dansam0.steamdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

    //@Query("SELECT u FROM Genre u WHERE u.name = ?1")
    Genre findByName(String name);
}
