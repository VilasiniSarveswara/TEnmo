package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Account;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


public class AccountService {

    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountService(String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
    }

    public Account getCurrentBalance(String token) {
        Account account = new Account();
        try {
            HttpEntity entity = makeAuthEntity(token);
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "accounts", HttpMethod.GET, entity, Account.class);
            account = response.getBody();

        } catch (RestClientResponseException ex) {
            System.out.println(ex.getRawStatusCode() + " :" + ex.getMessage());
        } catch (ResourceAccessException ex) {
            ex.getMessage();
        }
        return account;
    }
    public Account[] accountList(String token) {
        Account[] accountList = new Account[0];
        try {
            HttpEntity entity = makeAuthEntity(token);
            ResponseEntity<Account[]> response = restTemplate.exchange(API_BASE_URL + "viewallaccounts", HttpMethod.GET, entity, Account[].class);
            accountList = response.getBody();

        } catch (RestClientResponseException ex) {
            System.out.println(ex.getRawStatusCode() + " :" + ex.getMessage());
        } catch (ResourceAccessException ex) {
            ex.getMessage();
        }
        return accountList;
    }

    private HttpEntity makeAuthEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }


}
