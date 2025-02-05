package com.dansam0.steamdemo.controller;

import com.dansam0.steamdemo.dto.UserDto;
import com.dansam0.steamdemo.entity.User;
import com.dansam0.steamdemo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
public class SecurityController {

    @Value("${steam.api_key_length}")
    private int apiKeyLength;

    private UserService userService;

    public SecurityController(UserService userService){
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/login")
    public String showLogin(){
        return "security/login";
    }

    //Registration

    @GetMapping("/register")
    public String showRegister(Model theModel) {
        theModel.addAttribute("userDto", new UserDto());
        return "security/register";
    }

    @GetMapping("/forbidden")
    public String showForbiddenPage(){
        return "error/403";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("userDto") UserDto userDto,
            BindingResult theBindingResult, Model theModel) {

        String userName = userDto.getName();

        if (theBindingResult.hasErrors())
            return "security/register";

        if(!Objects.equals(userDto.getPassword(), userDto.getConfirmPassword())){

            theModel.addAttribute("userDto", new UserDto());
            theModel.addAttribute("registrationError", "Passwords must match");
            return "security/register";
        }

        User existing = userService.findByUserName(userName);

        if (existing != null){

            theModel.addAttribute("userDto", new UserDto());
            theModel.addAttribute("registrationError", "Username already exists.");
            return "security/register";
        }

        userService.saveUserDto(userDto);
        return "security/login";
    }

    @GetMapping("/my")
    public String showMyAccount(Model theModel, HttpSession session){

        System.out.println(">>>> IN showMyAccount()");

        User user = (User) session.getAttribute("user");

        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        theModel.addAttribute("userDto", userDto);
        return "security/my-account";
    }

    @PostMapping("/my/saveApiKey")
    public String saveUserApiKey(
            @ModelAttribute("userDto") UserDto userDto,
            @RequestParam(value = "apiKey", required = false) String apiKey,
            HttpSession session, Model theModel){

        if(apiKey == null)
            apiKey = "";

        if (apiKey.length() != apiKeyLength){

            theModel.addAttribute("userDto", userDto);
            theModel.addAttribute("keyError", "Steam api key must be 32 characters long");
            return "security/my-account";
        }

        User user = (User) session.getAttribute("user");
        user.setApiKey(apiKey);
        userService.save(user);

        theModel.addAttribute("userDto", userDto);
        theModel.addAttribute("keySuccess", "Steam api key saved!");
        return "security/my-account";
    }

    @PostMapping("/my/removeApiKey")
    public String deleteUserApiKey(
            @ModelAttribute("userDto") UserDto userDto,
            HttpSession session, Model theModel){

        User user = (User) session.getAttribute("user");
        user.setApiKey(null);
        userService.save(user);

        theModel.addAttribute("userDto", userDto);
        theModel.addAttribute("keyRemove", "Steam api key removed!");
        return "security/my-account";
    }

    @PostMapping("/my/updatePassword")
    public String updateUserPassword(
            @Valid @ModelAttribute("userDto") UserDto userDto,
            BindingResult theBindingResult, Model theModel){

        //System.out.println(">>>> IN updateUserPassword()");

        if (theBindingResult.hasErrors())
            return "security/my-account";

        if(!Objects.equals(userDto.getPassword(), userDto.getConfirmPassword())){

            theModel.addAttribute("userDto", userDto);
            theModel.addAttribute("passwordError", "Passwords must match");
            return "security/my-account";
        }

        userService.saveUserDto(userDto);

        theModel.addAttribute("userDto", userDto);
        theModel.addAttribute("passwordSuccess", "Successfully changed password!");
        return "security/my-account";
    }

}
