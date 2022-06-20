package com.binancecandlestickfetcher.controller;

import com.binancecandlestickfetcher.service.ServiceLayer;
import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;

@RestController
public  class ApiController {
//Convert JSON to string in get method
    @GetMapping
    public String GetDataFromAPI(String symbol, long startTime, String interval, int limit)  {
        try {
            String url = "https://api.binance.com/api/v3/klines?symbol=" + symbol
                    + "&interval=" + interval
                    + "&limit=" + limit
                    + "&startTime=" + startTime;
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException(e.getMessage());
        }


    }


}
