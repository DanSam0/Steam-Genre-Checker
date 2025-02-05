package com.dansam0.steamdemo.service;

import com.dansam0.steamdemo.dao.RoleDao;
import com.dansam0.steamdemo.entity.Role;
import com.dansam0.steamdemo.entity.User;
import com.dansam0.steamdemo.repository.UserRepository;
import com.dansam0.steamdemo.dto.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@EnableMethodSecurity
@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    private RoleDao roleDao;
    private BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(User user){
        userRepository.save(user);
    }

    @Override
    public void saveUserDto(UserDto userDto) {

        User tempUser = userRepository.findByUserName(userDto.getName());

        if(tempUser == null){

            tempUser = new User();
            tempUser.setName(userDto.getName());
            tempUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            tempUser.setEnabled(true);
            tempUser.setRole(roleDao.findRoleByName("ROLE_USER"));
        }
        else {

            if(userDto.getPassword() != null)
                tempUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userRepository.save(tempUser);
    }

    @Override
    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllByOrderByIdAsc() {
        return userRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<User> findAllByRoleIdByOrderByIdAsc(int id) {
        return userRepository.findAllByRoleIdOrderByIdAsc(id);
    }

    @Override
    public User findById(int id){

        Optional<User> result = userRepository.findById(id);

        if(!result.isPresent())
            throw new RuntimeException("Didn't find user id - " + id);

        User user = result.get();
        return user;
    }

    @Override
    public User findByUserName(String name) {
        return userRepository.findByUserName(name);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void deleteById(int id){

        Optional<User> result = userRepository.findById(id);

        if(!result.isPresent())
            throw new RuntimeException("Didn't find user id - " + id);

        User user = result.get();
        userRepository.delete(user);
    }

    @Override
    public boolean hasRole(int id, String role) {

        Optional<User> result = userRepository.findById(id);

        if(!result.isPresent())
            throw new RuntimeException("Didn't find user id - " + id);

        User user = result.get();
        return user.getRole().getName().equals(role);
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(name);

        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password");
        }

        Collection<SimpleGrantedAuthority> authorities = mapRoleToAuthorities(user.getRole());
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(),
                authorities);
    }

    private Collection<SimpleGrantedAuthority> mapRoleToAuthorities(Role role) {

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(role.getName());
        authorities.add(tempAuthority);
        return authorities;
    }
}
