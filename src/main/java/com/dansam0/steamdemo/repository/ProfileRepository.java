package com.dansam0.steamdemo.repository;

import com.dansam0.steamdemo.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    public List<Profile> findAllByOrderByNameAsc();
    public List<Profile> findAllByNameContainingOrderByNameAsc(String name);
}
