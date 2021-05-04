package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDAO implements TransferDAO {

    private AccountDAO accountDAO;
    private JdbcTemplate jdbcTemplate;


    public JdbcTransferDAO(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
        accountDAO = new JdbcAccountDAO(jdbcTemplate);

    }

    @Override
    public Transfer makeATransfer(String username, int user_id, BigDecimal amount) {
        Account fromAccount = new Account();
        Account toAccount = new Account();
        Transfer transfer = new Transfer();

       //Get the Source account Id using the username
        //Calling helper method to get the fromAccountId
        fromAccount = getAccountIdByUsername(username);

        //Check if current balance is greater than or equal to the amount to be transferred
        int result = fromAccount.getBalance().compareTo(amount);

        if (result >= 0){   //If current balance is greater than or equal to amount to be transferred, get account_to from the accounts table.
            //Calling helper method to get the toAccountId
            toAccount = getAccountIdByUserId(user_id);
            int toAccount_id = toAccount.getAccount_id(); //Got the 'to account id'

            //Check if the account ID is valid


            //Call to helper method to insert a row in the Transfers table in the Db.
            insertIntoTransferTable(2,2,fromAccount.getAccount_id(), toAccount_id,amount);

            //Query to get back the TRANSFER object
            //Call to helper method(fetchRowFromTransferTable()) to fetch/get the new row that was created using insertIntoTransferTable() on line 64
            transfer = fetchRowFromTransferTable(2, 2, fromAccount.getAccount_id(), toAccount_id,amount);

            //The sender's account balance is decreased by the amount of the transfer.
            //Calculating changed balance of the FROM account
            BigDecimal decreasedBalance = decreaseAccountBalance(fromAccount,amount);

            //Call to helper method to update the source account's balance
            updateAccountBalance(fromAccount,decreasedBalance);

            //The receiver's account balance is increased by the amount of the transfer.
            //Calculating changed balance of the TO account and updating in the transfers table.
            BigDecimal increasedBalance = increaseAccountBalance(toAccount,amount);
            updateAccountBalance(toAccount,increasedBalance);
        }
        return transfer;
    }

    @Override
    public List<Transfer> transferList(String username) {
        List<Transfer> transferList = new ArrayList<>();
        String getTransferList = "SELECT t.transfer_id, t.transfer_type_id,t.transfer_status_id,t.account_from,t.account_to,t.amount FROM transfers t JOIN accounts a ON t.account_from = a.account_id OR t.account_to = a.account_id JOIN users u ON a.user_id = u.user_id WHERE username = ?";
        SqlRowSet results= jdbcTemplate.queryForRowSet(getTransferList, username);
        while(results.next()) {
          Transfer theTransfer = mapRowToTransfer(results);
         transferList.add(theTransfer);
        }
        return transferList;
    }



    @Override
    public Transfer viewTransferById(int transfer_id) {
        Transfer theTransfer = new Transfer();
        String getTransferById = "SELECT t.transfer_id, t.transfer_type_id,t.transfer_status_id,t.account_from,t.account_to,t.amount FROM transfers t WHERE t.transfer_id = ?";
        SqlRowSet results= jdbcTemplate.queryForRowSet(getTransferById,transfer_id);
        while(results.next()) {
          theTransfer = mapRowToTransfer(results);
        }
        return theTransfer;
    }

    @Override
    public Transfer requestTransfer(String username, int user_id, BigDecimal amount) {
        Transfer theTransfer = new Transfer();
        Account fromAccount = new Account();
        Account toAccount = new Account();
        fromAccount = getAccountIdByUserId(user_id);
        toAccount = getAccountIdByUsername(username);
        insertIntoTransferTable(1,1, fromAccount.getAccount_id(), toAccount.getAccount_id(),amount);
        theTransfer = fetchRowFromTransferTable(1,1, fromAccount.getAccount_id(), toAccount.getAccount_id(),amount);
        return theTransfer;
    }

    @Override
    public List<Transfer> pendingTransferList(String username) {
        List<Transfer> transferList = new ArrayList<>();
        String getTransferList = "SELECT t.transfer_id, t.transfer_type_id,t.transfer_status_id,t.account_from,t.account_to,t.amount FROM transfers t JOIN accounts a ON t.account_from = a.account_id OR t.account_to = a.account_id JOIN users u ON a.user_id = u.user_id WHERE username = ? AND t.transfer_status_id= ?";
        SqlRowSet results= jdbcTemplate.queryForRowSet(getTransferList, username, 1);
        while(results.next()) {
            Transfer theTransfer = mapRowToTransfer(results);
            transferList.add(theTransfer);
        }
        return transferList;
    }

    @Override
    public Transfer approveRejectTransfer(int transfer_id, int transfer_status_id, String username) {
        Account account = getAccountIdByUsername(username);
        Transfer existingTransfer = new Transfer();
        Transfer updatedTransfer = new Transfer();
        existingTransfer = getTransferRowByTransferId(transfer_id);
        int comparison = account.getBalance().compareTo(existingTransfer.getAmount());
        if (comparison >= 0) {
            updatedTransfer = new Transfer();
            String getUpdatedTransfer = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
            jdbcTemplate.update(getUpdatedTransfer, transfer_status_id, transfer_id);
            updatedTransfer = viewTransferById(transfer_id);
        }
        return updatedTransfer;
    }

    //Helper method

/*    private boolean checkValidityOfAccountId(int account_id) {
        boolean checkValidityOfAccountId = false;
        String
    }*/

    private Transfer getTransferRowByTransferId(int transfer_id){
        Transfer transfer = new Transfer();
        String sqlGetTransferRow = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from,account_to, amount FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetTransferRow, transfer_id);
        while (results.next()){
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    private Account getAccountIdByUsername(String username){
        Account theAccount = new Account();
        String sqlGetSourceAccountId = "SELECT a.account_id, a.user_id, a.balance FROM users u JOIN accounts a ON u.user_id = a.user_id WHERE u.username = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetSourceAccountId,username);
        while(results.next()){
            theAccount = mapRowToAccount(results);
        }
        return theAccount;
    }

    private Account getAccountIdByUserId(int user_id){
        Account theAccount = new Account();
        String sqlGetPayeeAccountId = "SELECT account_id,user_id,balance FROM accounts WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetPayeeAccountId,user_id);
        while(results.next()){
            theAccount = mapRowToAccount(results);
        }
        return theAccount;
    }

    private void insertIntoTransferTable(int transfer_type_id, int transfer_status_id, int account_from,int account_to, BigDecimal amount){
        String sqlTransferAmount = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from,account_to, amount) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlTransferAmount,transfer_type_id,transfer_status_id,account_from,account_to,amount);
    }

    private Transfer fetchRowFromTransferTable(int transfer_type_id, int transfer_status_id, int account_from,int account_to, BigDecimal amount){
        Transfer transfer = new Transfer();
        String sqlGetLastTransfer = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from,account_to, amount FROM transfers WHERE transfer_type_id = ? AND transfer_status_id = ? AND account_from = ? AND account_to = ? AND amount = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetLastTransfer,transfer_type_id,transfer_status_id,account_from,account_to,amount);
        while(results.next()){
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    private BigDecimal decreaseAccountBalance(Account fromAccount, BigDecimal amountTobeDecreased){
        BigDecimal decreasedBalance = fromAccount.getBalance().subtract(amountTobeDecreased);
        return decreasedBalance;
    }

    private BigDecimal increaseAccountBalance(Account toAccount, BigDecimal amountTobeIncreased){
        BigDecimal increasedBalance = toAccount.getBalance().add(amountTobeIncreased);
        return increasedBalance;
    }

    private void updateAccountBalance(Account account,BigDecimal amount){
        String sqlDecreaseBalance = "UPDATE accounts SET balance = ? WHERE account_id = ? ";
        jdbcTemplate.update(sqlDecreaseBalance, amount, account.getAccount_id());
    }



    private Account mapRowToAccount(SqlRowSet results){
        Account theAccount = new Account();
        theAccount.setAccount_id(results.getInt("account_id"));
        theAccount.setUser_id(results.getInt("user_id"));
        theAccount.setBalance(results.getBigDecimal("balance"));
        return theAccount;
    }

    private Transfer mapRowToTransfer(SqlRowSet results){
        Transfer theTransfer = new Transfer();
        theTransfer.setTransfer_id(results.getInt("transfer_id"));
        theTransfer.setTransfer_type_id(results.getInt("transfer_type_id"));
        theTransfer.setTransfer_status_id(results.getInt("transfer_status_id"));
        theTransfer.setAccount_from(results.getInt("account_from"));
        theTransfer.setAccount_to(results.getInt("account_to"));
        theTransfer.setAmount(results.getBigDecimal("amount"));
        return theTransfer;
    }
}











