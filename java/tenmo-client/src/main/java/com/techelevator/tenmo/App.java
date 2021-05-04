package com.techelevator.tenmo;

import com.techelevator.tenmo.models.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";

    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private TransferService transferService;
    private UserService userService;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL), new TransferService(API_BASE_URL), new UserService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService, TransferService transferService, UserService userService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = accountService;
        this.transferService = transferService;
        this.userService = userService;
    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                Account theAccount = accountService.getCurrentBalance(currentUser.getToken());
                System.out.println("Your current account balance is: $" + theAccount.getBalance());


            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private User[] displayAvailableUsers() {
        User[] userList = userService.getAllUsers(currentUser.getUser().getId());
        System.out.println("-------------------------------------------");
        System.out.printf("%-20s \n", "Users");
        System.out.printf("%-20s %-20s \n", "ID", "Name");
        System.out.println("-------------------------------------------");
        for (User user : userList) {
            if (!user.getId().equals(currentUser.getUser().getId()))
                System.out.printf("%-20s %-20s \n", user.getId(), user.getUsername().toUpperCase());
        }
        System.out.println("");
        return userList;
    }


    private void viewTransferHistory() {
        Transfer[] transferHistory = new Transfer[0];
        transferHistory = transferService.viewTransfers(currentUser.getToken());
        printTransfers(transferHistory);
        int transfer_id = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel) ");
        boolean isValidTransferId = false;
        if(transfer_id == 0){
            mainMenu();
        }
        else{
            isValidTransferId = isValidTransferID(transferHistory,transfer_id);
            if (isValidTransferId) {
                Transfer transferDetails = transferService.viewTransferById(transfer_id);
                printSpecificTransfer(transferDetails);
            }
        }
    }

    private void printSpecificTransfer(Transfer transferDetails) {
        String sourceName = "";
        String beneficiaryName = "";
        User[] userList = allOtherUsers();
        Account[] accountList = allAccounts();
        sourceName = getName(transferDetails.getAccount_from());
        beneficiaryName = getName(transferDetails.getAccount_to());
        String transferType = "";
        if (transferDetails.getTransfer_type_id() == 1) {
            transferType = "Request";
        } else {
            transferType = "Send";
        }
        String transferStatus = "";
        if (transferDetails.getTransfer_status_id() == 1) {
            transferStatus = "Pending";
        } else if (transferDetails.getTransfer_status_id() == 2) {
            transferStatus = "Approved";
        } else {
            transferStatus = "Rejected";
        }
        System.out.println("\n\n----------------------------------");
        System.out.printf("%-20s \n", "Transfer Details");
        System.out.println("----------------------------------");
        System.out.printf("%-20s %-20s \n", "Id:", transferDetails.getTransfer_id());
        System.out.printf("%-20s %-20s \n", "From:", sourceName);
        System.out.printf("%-20s %-20s \n", "To:", beneficiaryName);
        System.out.printf("%-20s %-20s \n", "Type:", transferType);
        System.out.printf("%-20s %-20s \n", "Status:", transferStatus);
        System.out.printf("%-20s $ %-5s \n", "Amount: ", transferDetails.getAmount());
    }

    private void viewPendingRequests() {
        Transfer[] pendingTransfer = new Transfer[0];
        pendingTransfer = transferService.viewPendingTransfers(currentUser.getToken());

        int fromAccountId = getSourceAccountNumber();
        //Checking to see if the user does not have any pending requests
        int count = 0;
        for (Transfer transfer : pendingTransfer) {
            if (transfer.getAccount_from() == fromAccountId) {
                count++;
            }
        }
        if (count == 0) {
            System.out.println("You have no pending requests!");
        } else {
            User[] userList = allOtherUsers(); //Call to helper method
            Account[] accountList = allAccounts();
            System.out.println("\n----------------------------------------------------");
            System.out.printf("%-20s \n", "Pending Requests");
            System.out.printf("%-10s %-10s %20s \n", "ID", "To", "Amount");
            System.out.println("----------------------------------------------------");

            for (Transfer transfer : pendingTransfer) {
                if (transfer.getAccount_from() == fromAccountId) {
                    String beneficiaryName = "";
                    for (Account account : accountList) {
                        if (transfer.getAccount_to() == account.getAccount_id()) {
                            int beneficiaryId = account.getUser_id();
                            for (User user : userList) {
                                if (user.getId() == beneficiaryId) {
                                    beneficiaryName = user.getUsername();
                                    System.out.printf("%-10s %-10s %-20s $%-10s \n \n", transfer.getTransfer_id(), "To: ", beneficiaryName, transfer.getAmount());
                                }
                            }
                        }
                    }
                }
            }
            int transfer_id = console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel) ");
            if(transfer_id == 0){
                mainMenu();
            }else{
                boolean isValidTransferId = false;
                isValidTransferId = isValidTransferID(pendingTransfer,transfer_id);
                if (isValidTransferId) {
                    System.out.println("1. Approve");
                    System.out.println("2. Reject");
                    System.out.println("0. Don't approve or reject");
                    System.out.println("--------------------------------");
                    int status = console.getUserInputInteger("Please choose an option");
                    if (status != 0) {
                        Transfer updatedTransfer = new Transfer();
                        updatedTransfer = transferService.approveRejectTransfer(transfer_id, status, currentUser.getToken());
                        //Print the updated transfer to the user
                        printSpecificTransfer(updatedTransfer);
                    } else {
                        mainMenu();
                    }
                }
            }
        }
    }

    private void sendBucks() {
        User[] userList = displayAvailableUsers();
        int user_id = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
        //Checking to see if a valid account id has been provided
        boolean validUserId = false;
        validUserId = isValidUserID(userList,user_id);
        if (validUserId) {
            int amt = console.getUserInputInteger("Enter amount");
            if (amt == 0) {
                System.out.println("Invalid Amount!");
            } else {
                BigDecimal amount = new BigDecimal(amt);
                Transfer transfer = new Transfer(2, amount);
                Transfer receivedTransfer = new Transfer();
                receivedTransfer = transferService.makeTransfer(transfer, user_id, currentUser.getToken());
                //Validating that the user has provided right data
                if (receivedTransfer.getTransfer_id() == 0) {
                    System.out.println("Invalid Data!");
                } else {
                    System.out.println("\n----------------------------------------------------");
                    System.out.printf("%-20s \n", "Transfers");
                    System.out.printf("%-20s %-20s %-20s \n", "ID", "To", "Amount");
                    System.out.println("----------------------------------------------------");
                    String name = "";
                    for (User user : userList) {
                        if (user.getId().equals(user_id)) {
                            name = user.getUsername();
                        }
                    }
                    System.out.printf("%-20s %-20s $%-20s \n", receivedTransfer.getTransfer_id(), name.toUpperCase(), receivedTransfer.getAmount());
                }
            }
        }
    }

    private boolean isValidUserID(User[] userList, int user_id) {
        boolean validUserId = false;
        int counter = 0;
        for (User user : userList) {
            if (user.getId() != user_id) {
                counter++;
                continue;

            } else {
                if (currentUser.getUser().getId() != user_id) {
                    validUserId = true;
                    break;
                } else {
                    System.out.println("You have provided your ID!");
                    break;
                }
            }
        }
        if (counter == userList.length) {
            System.out.println("Invalid user ID!");
            mainMenu();
        }
        return validUserId;
    }

    private boolean isValidTransferID(Transfer[] pendingTransfer, int transfer_id) {
        boolean validTransferId = false;
        int counter = 0;
        for (Transfer transfer : pendingTransfer) {
            if (transfer.getTransfer_id() != transfer_id) {
                counter++;
                continue;

            } else {
                validTransferId = true;
                break;
            }
        }
        if (counter == pendingTransfer.length) {
            System.out.println("Invalid transfer ID!");
            mainMenu();
        }
        return validTransferId;
    }

    private void requestBucks() {
        User[] userList = displayAvailableUsers();
        Integer user_id = console.getUserInputInteger("Enter ID of user you are requesting from (0 to cancel)");
        if(user_id == 0){
            mainMenu();
        }
        else{
            boolean isValidUserId = false;
            isValidUserId = isValidUserID(userList,user_id);
            if (isValidUserId) {
                Integer amt = console.getUserInputInteger("Enter amount");
                BigDecimal amount = new BigDecimal(amt);
                Transfer transfer = new Transfer(1, amount);
                Transfer receivedTransfer = new Transfer();
                receivedTransfer = transferService.requestTransfer(transfer, user_id, currentUser.getToken());
                String name = "";
                for (User user : userList) {
                    if (user.getId().equals(user_id)) {
                        name = user.getUsername();
                    }
                }
                System.out.printf("%-20s %-20s $%-20s \n", receivedTransfer.getTransfer_id(), name.toUpperCase(), receivedTransfer.getAmount());
            } else {
                mainMenu();
            }
        }
    }

    private int getSourceAccountNumber() {
        Account[] accountList = allAccounts();
        //Finding out the Source Account ID
        int fromAccountId = 0;
        for (Account account : accountList) {
            if (currentUser.getUser().getId().equals(account.getUser_id())) {
                fromAccountId = account.getAccount_id();
            }
        }
        return fromAccountId;
    }

    private User[] allOtherUsers() {
        User[] userList = new User[0];
        userList = userService.getAllUsers(currentUser.getUser().getId());
        return userList;
    }

    private Account[] allAccounts() {
        Account[] accountList = new Account[0];
        accountList = accountService.accountList(currentUser.getToken());
        return accountList;
    }

    private String getName(int account_id) {
        User[] userList = allOtherUsers(); //Call to helper method
        Account[] accountList = allAccounts();
        String name = "";
        for (Account account : accountList) {
            if (account.getAccount_id() == account_id) {
                int userId = account.getUser_id();
                for (User user : userList) {
                    if (user.getId() == userId) {
                        name = user.getUsername();
                    }
                }
            }
        }
        return name;
    }


    private void printTransfers(Transfer[] transferHistory) {
        int fromAccountId = getSourceAccountNumber();
        User[] userList = allOtherUsers(); //Call to helper method
        Account[] accountList = allAccounts();
        System.out.println("\n---------------------------------------------------------------------");
        System.out.printf("%-20s \n", "Transfers");
        System.out.printf("%-10s %-25s %-10s %-10s \n", "ID", "From/To", "Status", "Amount");
        System.out.println("---------------------------------------------------------------------");
        for (Transfer transfer : transferHistory) {
            String status = "";
            if (transfer.getTransfer_status_id() == 1) {
                status = "Pending";
            } else if (transfer.getTransfer_status_id() == 2) {
                status = "Approved";
            } else if (transfer.getTransfer_status_id() == 3) {
                status = "Rejected";
            }
            if (transfer.getAccount_from() == fromAccountId) {
                String beneficiaryName = "";
                for (Account account : accountList) {
                    if (transfer.getAccount_to() == account.getAccount_id()) {
                        int beneficiaryId = account.getUser_id();
                        for (User user : userList) {
                            if (user.getId() == beneficiaryId) {
                                beneficiaryName = user.getUsername();
                                System.out.printf("%-10s %-7s %-18s %-10s $%-10s \n", transfer.getTransfer_id(), "To: ", beneficiaryName, status, transfer.getAmount());
                            }
                        }
                    }
                }
            } else {
                String sourceName = "";
                for (Account account : accountList) {
                    if (transfer.getAccount_to() == account.getAccount_id()) {
                        int beneficiaryId = account.getUser_id();
                        for (User user : userList) {
                            if (user.getId() == beneficiaryId) {
                                sourceName = user.getUsername();
                                System.out.printf("%-10s %-7s %-18s %-10s $%-10s \n", transfer.getTransfer_id(), "From: ", sourceName, status, transfer.getAmount());
                            }
                        }
                    }
                }
            }
        }
    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
                transferService.AUTH_TOKEN = currentUser.getToken();

            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
