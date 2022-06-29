package com.binancecandlestickfetcher.controller;

import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class ApiController {

    //GET method as limit
    @GetMapping
    public String GetDataFromAPI(String symbol, long startTime, String interval, int limit) throws BusinessIntegrityException {
        try {
            String url = "https://api.binance.com/api/v3/klines?symbol=" + symbol
                    + "&interval=" + interval
                    + "&limit=" + limit
                    + "&startTime=" + startTime;
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Get method without limit
    public String GetDataFromAPIWithoutLimit(String symbol, long startTime, String interval) throws BusinessIntegrityException {
        try {
            String url = "https://api.binance.com/api/v3/klines?symbol=" + symbol
                    + "&interval=" + interval
                    + "&startTime=" + startTime;
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, String.class);
        } catch (RestClientException e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

}
