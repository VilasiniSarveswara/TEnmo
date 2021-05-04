package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import io.cucumber.java.bs.A;
import okhttp3.Response;
import org.apiguardian.api.API;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TransferService {
    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    public static String AUTH_TOKEN = "";
    private UserService userService = new UserService(API_BASE_URL);

    public TransferService(String API_BASE_URL) {

        this.API_BASE_URL = API_BASE_URL;
    }

    public Transfer makeTransfer(Transfer transfer, int user_id, String token) {
        Transfer newTransfer = new Transfer();
        HttpEntity<Transfer> transferEntity = makeTransferEntity(transfer);
        try {
            newTransfer = restTemplate.postForObject(API_BASE_URL + "maketransfer/" + user_id, transferEntity, Transfer.class);

        } catch (RestClientResponseException ex) {
            System.out.println(ex.getRawStatusCode() + " : " + ex.getMessage());

        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return newTransfer;
    }

    public Transfer[] viewTransfers(String token) {
        Transfer[] transferList = new Transfer[0];

        try {
            HttpEntity entity = makeAuthEntity();
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "viewtransfers", HttpMethod.GET, entity, Transfer[].class);
            transferList = response.getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getRawStatusCode() + " : " + ex.getMessage());

        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return transferList;
    }
    public Transfer[] viewPendingTransfers(String token) {
        Transfer[] transferList = new Transfer[0];

        try {
            HttpEntity entity = makeAuthEntity();
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "pendingtransfers", HttpMethod.GET, entity, Transfer[].class);
            transferList = response.getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getRawStatusCode() + " : " + ex.getMessage());

        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return transferList;
    }


    public Transfer viewTransferById(int transfer_id) {
        Transfer transfer = new Transfer();
        try {
            HttpEntity entity = makeAuthEntity();
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "transfer/" + transfer_id, HttpMethod.GET, entity, Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getRawStatusCode() + " : " + ex.getMessage());

        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return transfer;
    }

    public Transfer requestTransfer (Transfer transfer, int user_id, String token) {
        Transfer newTransfer = new Transfer();
        HttpEntity<Transfer> transferEntity = makeTransferEntity(transfer);
        try {
           newTransfer = restTemplate.postForObject(API_BASE_URL + "requesttransfer/" + user_id, transferEntity, Transfer.class);
            System.out.println("\n----------------------------------------------------");
            System.out.printf("%-20s \n", "Transfers");
            System.out.printf("%-20s %-20s %-20s \n", "ID", "From", "Amount");
            System.out.println("----------------------------------------------------");
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getRawStatusCode() + " : " + ex.getMessage());

        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        }
        return newTransfer;
    }

    public Transfer approveRejectTransfer (int transfer_id, int status, String token) {
        Transfer transfer = new Transfer();
        Transfer updatedTransfer = new Transfer();
        transfer.setTransfer_id(transfer_id);
        transfer.setTransfer_status_id(status+1);
        HttpEntity entity = makeTransferEntity(transfer);

        try {
          ResponseEntity<Transfer> responseEntity = restTemplate.exchange(API_BASE_URL + "approverejecttransfers/", HttpMethod.PUT, entity, Transfer.class);
          updatedTransfer = responseEntity.getBody();
        } catch (RestClientResponseException ex) {
            System.out.println(ex.getRawStatusCode() + " : " + ex.getMessage());

        } catch (ResourceAccessException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return updatedTransfer;
    }



    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity(transfer, headers);
        return entity;
    }

}
