package com.dansam0.steamdemo.controller;

import com.dansam0.steamdemo.entity.Role;
import com.dansam0.steamdemo.entity.User;
import com.dansam0.steamdemo.dao.RoleDao;
import com.dansam0.steamdemo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController {

    private UserService userService;
    private RoleDao roleDao;

    public UsersController(UserService userService, RoleDao roleDao) {
        this.userService = userService;
        this.roleDao = roleDao;
    }

    @GetMapping("")
    public String getUsers(Model model, HttpSession session){

        User user = (User) session.getAttribute("user");

        if (user.getRole().getName().equals("ROLE_ADMIN")){

            Role role = roleDao.findRoleByName("ROLE_USER");
            List<User> userList = userService.findAllByRoleIdByOrderByIdAsc(role.getId());
            model.addAttribute("users", userList);
            return "users/users-list";
        }

        List<User> userList = userService.findAllByOrderByIdAsc();
        userList.remove(user);
        model.addAttribute("users", userList);
        return "users/users-list";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("user") User user){
        userService.save(user);
        return "redirect:/users";
    }

    @PostMapping("/updateRole")
    public String updateRole(@RequestParam("userId") int id,
                             @RequestParam("role") String role, HttpSession session){

        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser.getId() == id || sessionUser.getRole().getName().equals("ROLE_ADMIN"))
            return "error/403";

        User user = userService.findById(id);
        Role newRole = roleDao.findRoleByName(role);

        user.setRole(newRole);
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") int id, HttpSession session){

        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser.getId() == id)
            return "error/403";

        if((userService.hasRole(id, "ROLE_ADMIN") || userService.hasRole(id, "ROLE_CREATOR"))
                && sessionUser.getRole().getName().equals("ROLE_ADMIN"))
            return "error/403";

        userService.deleteById(id);
        return "redirect:/users";
    }

}
