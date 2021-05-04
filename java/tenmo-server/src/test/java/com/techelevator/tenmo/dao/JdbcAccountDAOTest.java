package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcAccountDAOTest {

    private static SingleConnectionDataSource dataSource;
    private static JdbcTemplate jdbcTemplate = null;
    private static AccountDAO accountDAO = null;
    private static UserDAO userDAO = null;
    private Account actualAccount = new Account();
    private static final String FIRSTUSERNAME = "testFirstUserName";
    private static final String FIRSTPASSWORD = "testFirstPassword";

    private static final String SECONDUSERNAME = "testSecondUserName";
    private static final String SECONDPASSWORD = "testSecondPassword";

    @BeforeAll
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("tenmo_appuser");
        dataSource.setPassword("tebucks");

        //Must have this in order to rollback
        dataSource.setAutoCommit(false);
        jdbcTemplate = new JdbcTemplate(dataSource);
        userDAO = new JdbcUserDAO(jdbcTemplate);
        accountDAO = new JdbcAccountDAO(jdbcTemplate);
    }

    /* After all tests have finished running, this method will close the DataSource */
    //This method runs once after the last TEST method is executed
    @AfterAll
    public static void closeDataSource() throws SQLException {
        dataSource.destroy();
    }

    @BeforeEach
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    }

    @AfterEach
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Test
    void getCurrentBalance() {
        userDAO.create(FIRSTUSERNAME, FIRSTPASSWORD);
      actualAccount = accountDAO.getCurrentBalance(FIRSTUSERNAME);
        BigDecimal expectedBalance = new BigDecimal(1000).setScale(2, RoundingMode.CEILING);
        Assertions.assertEquals(expectedBalance, actualAccount.getBalance());

    }

    @Test
    void getAccountList() {
        List<Account> expectedAccountList = new ArrayList<>();
        List<Account> actualAccountList = new ArrayList<>();
        userDAO.create(FIRSTUSERNAME,FIRSTPASSWORD);
        expectedAccountList = accountDAO.getAccountList();
        userDAO.create(SECONDUSERNAME,SECONDPASSWORD);
        actualAccountList = accountDAO.getAccountList();
        Assertions.assertEquals(expectedAccountList.size()+ 1, actualAccountList.size());

    }
}