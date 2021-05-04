package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcTransferDAOTest {

    private static SingleConnectionDataSource dataSource;
    private static JdbcTemplate jdbcTemplate = null;
    private Transfer expectedTransfer = new Transfer();
    private Transfer actualTransfer = new Transfer();
    private static UserDAO userDAO = null;
    private static TransferDAO transferDAO = null;
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
        transferDAO = new JdbcTransferDAO(jdbcTemplate);
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
    void makeATransfer() {
        //Arrange - Create an expected Transfer object without transfer_id because it doesn't exist in the db.
        expectedTransfer.setTransfer_type_id(2);
        expectedTransfer.setTransfer_status_id(2);
        expectedTransfer.setAccount_from(2001);
        expectedTransfer.setAccount_to(2006);
        expectedTransfer.setAmount(new BigDecimal(50).setScale(2, RoundingMode.CEILING));
        userDAO.create(FIRSTUSERNAME, FIRSTPASSWORD);

        String sqlGetFirstUserAccountId = "SELECT a.account_id, a.user_id, a.balance FROM users u JOIN accounts a ON u.user_id = a.user_id WHERE u.username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetFirstUserAccountId, FIRSTUSERNAME);
        int firstUserAccountId = 0;
        while(results.next()){
          firstUserAccountId = results.getInt("account_id");
          expectedTransfer.setAccount_from(firstUserAccountId);
        }
        userDAO.create(SECONDUSERNAME, SECONDPASSWORD);
        String sqlGetSecondUserAccountId = "SELECT a.account_id, a.user_id, a.balance FROM users u JOIN accounts a ON u.user_id = a.user_id WHERE u.username = ?";
        results = jdbcTemplate.queryForRowSet(sqlGetSecondUserAccountId, SECONDUSERNAME);
        int secondUserAccountId = 0;
        while(results.next()){
            secondUserAccountId = results.getInt("account_id");
            expectedTransfer.setAccount_to(secondUserAccountId);
        }
        String sqlGetSecondUserId = "SELECT user_id, username,password_hash FROM users WHERE username = ?";
        results = jdbcTemplate.queryForRowSet(sqlGetSecondUserId,SECONDUSERNAME);
        int secondUserId = 0;
        while(results.next()){
            secondUserId = results.getInt("user_id");
        }

        //Act
        actualTransfer = transferDAO.makeATransfer(FIRSTUSERNAME, secondUserId,new BigDecimal(50));

        //Getting the transfer_id from the db and setting that in the expected transfer object.
        String  sqlGetTransferId = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from,account_to, amount FROM transfers WHERE transfer_type_id = ? AND transfer_status_id = ? AND account_from = ? AND account_to = ? AND amount = ?";
        results = jdbcTemplate.queryForRowSet(sqlGetTransferId,2, 2, expectedTransfer.getAccount_from(), expectedTransfer.getAccount_to(),expectedTransfer.getAmount());
        /*while(results.next()){
            expectedTransfer.setTransfer_id(results.getInt("transfer_id"));
        }*/
        while(results.next()){
            int transfer_id = Integer.parseInt(results.getString("transfer_id"));
            expectedTransfer.setTransfer_id(transfer_id);
        }

        //Assert
        Assertions.assertNotNull(actualTransfer);
        compareTransferObjects(expectedTransfer,actualTransfer);
    }

    @Test
    void transferList() {
        //Arrange
        List<Transfer> expectedTransferList = new ArrayList<>();
        List<Transfer> actualTransferList = new ArrayList<>();
        userDAO.create(FIRSTUSERNAME,FIRSTPASSWORD);
        userDAO.create(SECONDUSERNAME,SECONDPASSWORD);
        String sqlGetSecondUserId = "SELECT user_id, username,password_hash FROM users WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetSecondUserId,SECONDUSERNAME);
        int secondUserId = 0;
        while(results.next()){
            secondUserId = results.getInt("user_id");
        }
        transferDAO.makeATransfer(FIRSTUSERNAME,secondUserId,new BigDecimal(500));
    }

    @Test
    void viewTransferById() {
        //Arrange
        userDAO.create(FIRSTUSERNAME,FIRSTPASSWORD);
        userDAO.create(SECONDUSERNAME,SECONDPASSWORD);
        String sqlGetSecondUserId = "SELECT user_id, username,password_hash FROM users WHERE username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetSecondUserId,SECONDUSERNAME);
        int secondUserId = 0;
        while(results.next()){
            secondUserId = results.getInt("user_id");
        }
        expectedTransfer = transferDAO.makeATransfer(FIRSTUSERNAME,secondUserId, new BigDecimal(500));
        int expectedTransferId = expectedTransfer.getTransfer_id();
        //Act
        actualTransfer = transferDAO.viewTransferById(expectedTransferId);
        Assertions.assertNotNull(actualTransfer);
        compareTransferObjects(expectedTransfer,actualTransfer);

    }

    //Helper Methods
    private void compareTransferObjects(Transfer expectedTransfer, Transfer actualTransfer){
        Assertions.assertEquals(expectedTransfer.getTransfer_id(), actualTransfer.getTransfer_id());
        Assertions.assertEquals(expectedTransfer.getTransfer_type_id(),actualTransfer.getTransfer_type_id());
        Assertions.assertEquals(expectedTransfer.getTransfer_status_id(),actualTransfer.getTransfer_status_id());
        Assertions.assertEquals(expectedTransfer.getAccount_from(),actualTransfer.getAccount_from());
        Assertions.assertEquals(expectedTransfer.getAccount_to(),actualTransfer.getAccount_to());
        Assertions.assertEquals(expectedTransfer.getAmount(),actualTransfer.getAmount());
    }
}