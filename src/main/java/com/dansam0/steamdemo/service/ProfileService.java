package com.dansam0.steamdemo.service;

import com.dansam0.steamdemo.entity.Profile;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProfileService {

    List<Profile> findAll();

    List<Profile> findAllByOrderByNameAsc();

    Page<Profile> findAllWithPagination(int offset, int pageSize);

    Page<Profile> findAllWithPaginationAndSort(int offset, int pageSize);

    List<Profile> findAllByNameContainingOrderByNameAsc(String name);

    Profile findById(long id);

    void save(Profile profile);

    void deleteById(long id);

}
