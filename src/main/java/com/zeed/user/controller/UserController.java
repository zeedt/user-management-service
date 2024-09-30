package com.zeed.user.controller;

import com.zeed.user.services.UserService;
import com.zeed.usermanagement.apimodels.ManagedUserModelApi;
import com.zeed.usermanagement.models.Authority;
import com.zeed.usermanagement.models.ManagedUser;
import com.zeed.usermanagement.repository.AuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserAuthorityRepository;
import com.zeed.usermanagement.repository.ManagedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserService userService;

    @ResponseBody
    @RequestMapping(value = "/getDetailsByUsername/{username}")
    public ManagedUserModelApi getUserInformation(@PathVariable String username, Principal principal) throws Exception {
        return userService.getUserModelByUsername(username);
    }

    @ResponseBody
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ManagedUserModelApi createUser(@RequestBody ManagedUser managedUser){
        return userService.addManagedUser(managedUser);
    }

}
