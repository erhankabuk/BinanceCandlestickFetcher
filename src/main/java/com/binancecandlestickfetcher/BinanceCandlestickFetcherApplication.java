package com.binancecandlestickfetcher;

import com.binancecandlestickfetcher.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BinanceCandlestickFetcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinanceCandlestickFetcherApplication.class, args);
    }


}
