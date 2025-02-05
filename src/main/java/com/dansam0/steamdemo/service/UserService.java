package com.dansam0.steamdemo.service;

import com.dansam0.steamdemo.entity.User;
import com.dansam0.steamdemo.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService{

    void save(User user);

    void saveUserDto(UserDto userDto);

    List<User> findAll();

    List<User> findAllByOrderByIdAsc();

    List<User> findAllByRoleIdByOrderByIdAsc(int id);

    User findById(int id);

    User findByUserName(String name);

    void deleteById(int id);

    boolean hasRole(int id, String role);

}
