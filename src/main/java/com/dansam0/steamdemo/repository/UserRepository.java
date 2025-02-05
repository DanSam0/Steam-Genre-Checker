package com.dansam0.steamdemo.repository;

import com.dansam0.steamdemo.entity.Profile;
import com.dansam0.steamdemo.entity.Role;
import com.dansam0.steamdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.name = ?1")
    User findByUserName(String name);

    List<User> findAllByOrderByIdAsc();

    List<User> findAllByRoleIdOrderByIdAsc(int id);
}
