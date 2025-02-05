package com.dansam0.steamdemo.service;

import com.dansam0.steamdemo.entity.Genre;

import java.util.List;

public interface GenreService {

    Genre save(Genre genre);

    void saveAll(List<Genre> genres);

    List<Genre> findAll();

    Genre findById(int id);
}
