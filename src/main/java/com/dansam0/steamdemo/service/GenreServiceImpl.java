package com.dansam0.steamdemo.service;

import com.dansam0.steamdemo.entity.Genre;
import com.dansam0.steamdemo.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService{

    private GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public Genre save(Genre genre) {

        Genre tempGenre = genreRepository.findByName(genre.getName());

        if(tempGenre == null){
            genreRepository.save(genre);
            return null;
        }

        return tempGenre;
    }

    @Override
    @Transactional
    public void saveAll(List<Genre> genres) {
        genreRepository.saveAll(genres);
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre findById(int id) {

        Optional<Genre> result = genreRepository.findById(id);

        if(!result.isPresent())
            throw new RuntimeException("Didn't find genre id - " + id);

        Genre genre = result.get();

        return genre;
    }
}
