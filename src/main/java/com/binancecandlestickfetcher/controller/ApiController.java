package com.binancecandlestickfetcher.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
