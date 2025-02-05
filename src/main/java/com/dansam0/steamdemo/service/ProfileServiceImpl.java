package com.dansam0.steamdemo.service;

import com.dansam0.steamdemo.entity.Profile;
import com.dansam0.steamdemo.repository.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@EnableMethodSecurity
@Service
public class ProfileServiceImpl implements ProfileService{

    private ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository){
        this.profileRepository = profileRepository;
    }

    @Override
    public List<Profile> findAll(){
        return profileRepository.findAll();
    }

    @Override
    public List<Profile> findAllByOrderByNameAsc(){
        return profileRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Override
    public Page<Profile> findAllWithPagination(int offset, int pageSize){
        return profileRepository.findAll(PageRequest.of(offset, pageSize));
    }

    @Override
    public Page<Profile> findAllWithPaginationAndSort(int offset, int pageSize){
        return profileRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.Direction.ASC, "name"));
    }

    @Override
    public List<Profile> findAllByNameContainingOrderByNameAsc(String name) {
        return profileRepository.findAllByNameContainingOrderByNameAsc(name);
    }

    @Override
    public Profile findById(long id){

        Optional<Profile> result = profileRepository.findById(id);

        if(!result.isPresent())
            throw new RuntimeException("Didn't find profile id - " + id);

        Profile profile = result.get();

        return profile;
    }

    @Override
    @Transactional
    public void save(Profile profile) {

        Instant instant = Instant.now();
        Timestamp timestamp = Timestamp.from(instant);
        profile.setLastUpdateTime(timestamp);

        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        profileRepository.deleteById(id);
    }
}
