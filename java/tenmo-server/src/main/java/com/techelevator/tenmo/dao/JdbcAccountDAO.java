package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class JdbcAccountDAO implements AccountDAO {
    JdbcTemplate jdbcTemplate;

    public JdbcAccountDAO(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getCurrentBalance(String username) {
        Account theAccount = new Account();

        String sqlGetCurrentBalance = "SELECT a.account_id, a.user_id, a.balance FROM users u JOIN accounts a ON u.user_id = a.user_id WHERE u.username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetCurrentBalance,username);
        while (results.next()) {
            theAccount = mapRowToAccount(results);
        }
        return theAccount;
    }

    @Override
    public List<Account> getAccountList() {
        List<Account> accountList = new ArrayList<>();
       // String sqlGetCurrentBalance = "SELECT a.account_id, a.user_id, a.balance FROM users u JOIN accounts a ON u.user_id = a.user_id";
        String sqlGetAccountList = "SELECT account_id, user_id, balance FROM accounts";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAccountList);
        while (results.next()) {
           Account theAccount = new Account();
            theAccount = mapRowToAccount(results);
            accountList.add(theAccount);
        }
        return accountList;
    }

    private Account mapRowToAccount(SqlRowSet results) {
        Account theAccount = new Account();
        theAccount.setAccount_id(results.getInt("account_id"));
        theAccount.setUser_id(results.getInt("user_id"));
        theAccount.setBalance(results.getBigDecimal("balance"));
        return theAccount;
    }
}
