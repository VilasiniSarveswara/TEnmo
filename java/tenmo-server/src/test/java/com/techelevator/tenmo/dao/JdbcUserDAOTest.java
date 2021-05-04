package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcUserDAOTest {

    private static SingleConnectionDataSource dataSource;
    private static JdbcTemplate jdbcTemplate = null;
    private static UserDAO userDAO = null;

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
    void findIdByUsername() {
        userDAO.create(FIRSTUSERNAME, FIRSTPASSWORD);
        User expectedUser = new User();
        int actualUserId = 0;
        String sqlGetUser = "SELECT user_id, username,password_hash FROM users WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetUser, FIRSTUSERNAME);
        while (results.next()) {
            expectedUser.setId(results.getLong("user_id"));
            expectedUser.setUsername(results.getString("username"));
        }
        actualUserId = userDAO.findIdByUsername(expectedUser.getUsername());
        Assertions.assertEquals(expectedUser.getId(), actualUserId);
    }

    @Test
    void findAll() {
        userDAO.create(FIRSTUSERNAME, FIRSTPASSWORD);
       List<User> existingUsers = userDAO.findAll();
        userDAO.create(SECONDUSERNAME,SECONDPASSWORD);
        List<User> changedUsers = userDAO.findAll();
        Assertions.assertEquals(existingUsers.size()+1, changedUsers.size());

    }

    @Test
    void findByUsername() {
        userDAO.create(FIRSTUSERNAME, FIRSTPASSWORD);
        User expectedUser = new User();
        User actualUser = new User();
        String sqlGetUser = "SELECT user_id, username,password_hash FROM users WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetUser, FIRSTUSERNAME);
        while (results.next()) {
            expectedUser.setId(results.getLong("user_id"));
            expectedUser.setUsername(results.getString("username"));
        }
        actualUser = userDAO.findByUsername(expectedUser.getUsername());
        compareTwoUsers(expectedUser, actualUser);
    }

    @Test
    void create() {
        List<User> existingUserList = userDAO.findAll();
        userDAO.create(FIRSTUSERNAME, FIRSTPASSWORD);
        List<User> changedUserList = userDAO.findAll();
        Assertions.assertEquals(existingUserList.size()+1, changedUserList.size());
    }

    private void compareTwoUsers(User expectedUser, User actualUser) {
        Assertions.assertEquals(expectedUser.getId(), actualUser.getId());
        Assertions.assertEquals(expectedUser.getUsername(), actualUser.getUsername());
    }
}