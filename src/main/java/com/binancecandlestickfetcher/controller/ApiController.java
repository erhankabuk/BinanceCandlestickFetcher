package com.binancecandlestickfetcher.controller;

import com.binancecandlestickfetcher.service.ServiceLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;

@RestController
public  class ApiController {
    //HttpRequests
//Convert JSOn to string in get method
    @Autowired
    ServiceLayer serviceLayer;
    @GetMapping
    public  HttpRequest GetData( ) {

        String url = "https://api.binance.com/api/v3/klines?symbol=EOSUSDT&interval=1d&limit=1000&startTime=1527465600000";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url,HttpRequest.class);
        /*
        try {
            String symbol ="EOSUSDT";
            URI getUri = new URI("https://api.binance.com/api/v3/klines?symbol="+symbol+"&interval="+interval+"&limit="+limit+"$startTime="+startTime);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(getUri)
                    .GET()
                    .build();

            return request;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        */

    }


}
