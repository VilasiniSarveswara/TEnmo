package com.techelevator.tenmo.services;


import com.techelevator.tenmo.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class UserService {
    private String API_BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    public static String AUTH_TOKEN = "";


    public UserService(String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
    }
    public User[] getAllUsers(int user_id){
        HttpEntity authEntity = makeAuthEntity();
        ResponseEntity<User[]> userResponse = restTemplate.exchange(API_BASE_URL + "users", HttpMethod.GET, authEntity, User[].class);
        User[] users = userResponse.getBody();
        return users;
    }
    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}
