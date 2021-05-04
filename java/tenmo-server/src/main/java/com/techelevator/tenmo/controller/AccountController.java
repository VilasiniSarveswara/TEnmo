package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcAccountDAO;
import com.techelevator.tenmo.model.Account;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController()

public class AccountController {
    @Autowired
    private JdbcAccountDAO accountDAO;

    public AccountController(JdbcAccountDAO accountDAO){

        this.accountDAO = accountDAO;
    }


    @ApiOperation(value = "This feature allows users to view their current balance")
    @RequestMapping(path = "/accounts", method = RequestMethod.GET)
    public Account getBalance(Principal principal){
        System.out.println("Name is " +principal.getName());
        return accountDAO.getCurrentBalance(principal.getName());
    }
    @ApiOperation(value = "This feature allows users to view all accounts")
    @RequestMapping(path = "/viewallaccounts", method = RequestMethod.GET)
    public List<Account> getAllAccounts(){
     return accountDAO.getAccountList();
    }
}
