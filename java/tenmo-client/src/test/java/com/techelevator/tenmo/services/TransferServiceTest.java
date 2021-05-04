/*
package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.view.ConsoleService;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.*;

import javax.sql.DataSource;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class TransferServiceTest {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private static BasicDataSource dataSource = null;
    private static JdbcTemplate jdbcTemplate = null;
    private TransferService transferService = new TransferService(API_BASE_URL);
    private ConsoleService console = new ConsoleService(System.in, System.out);
    private AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AccountService accountService = new AccountService(API_BASE_URL);
    private UserService userService = new UserService(API_BASE_URL);
    private static User user = null;
    private static final String FIRSTUSERNAME = "testFirstUserName";
    private static final String FIRSTPASSWORD = "testFirstPassword";
    private static final String SECONDUSERNAME = "testSecondUserName";
    private static final String SECONDPASSWORD = "testSecondPassword";

    @BeforeClass
    public static void setUpDataSource(){
*/
/*        dataSource = new ;
        dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        dataSource.setUsername("tenmo_appuser");
        dataSource.setPassword("tebucks");

        //Must have this in order to rollback
        dataSource.setAutoCommit(false);
        jdbcTemplate = new JdbcTemplate(dataSource);*//*

    }
    @AfterClass
    @Before


    @After

    @Test
    public void makeTransfer() {
        user = new User();
        App mainApp = new App(console, authenticationService, accountService, transferService, userService);
        //register two users


        //Creating Transfer object to call makeService()
        Transfer transfer = new Transfer();
        transfer.setTransfer_type_id(2);
        transfer.setTransfer_status_id(2);
        transfer.setAmount(new BigDecimal(100));

        //Fetching first user and 2nd user's account ids
*/
/*        String sqlGetFirstUserAccountId = "SELECT a.account_id, a.user_id, a.balance FROM users u JOIN accounts a ON u.user_id = a.user_id WHERE u.username = ?";
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
            expectedTransfer.setAccount_to(secondUserAccountId);*//*

        }


        String sqlGetSecondUserId = "SELECT user_id,username,password_hash FROM users u WHERE u.username = ?";
*/
/*        results = jdbcTemplate.queryForRowSet(sqlGetSecondUserId, SECONDUSERNAME);
        int secondUserId = 0;
        while(results.next()){
            secondUserId = results.getInt("user_id");
        }

        //Set the first user's acct id
        //Set the 2nd user's account id

        transferService.makeTransfer(transfer,);*//*


    }

    @Test
    public void viewTransfers() {

    }

    @Test
    public void viewTransferById() {

    }
}*/
