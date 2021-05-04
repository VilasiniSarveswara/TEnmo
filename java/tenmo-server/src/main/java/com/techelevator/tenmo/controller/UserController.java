package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDAO;
import com.techelevator.tenmo.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController ()
public class UserController {
    private JdbcUserDAO userDAO;

    public UserController(JdbcUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @ApiOperation("Method to list all the users")
    @RequestMapping (path = "users", method = RequestMethod.GET)
    public List<User> getAllUsers() {
      return userDAO.findAll();
    }

}
