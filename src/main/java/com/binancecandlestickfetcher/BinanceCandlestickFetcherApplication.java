package com.binancecandlestickfetcher;

import com.binancecandlestickfetcher.service.ServiceLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BinanceCandlestickFetcherApplication {

    private static ServiceLayer service;


    public static void main(String[] args) {
        SpringApplication.run(BinanceCandlestickFetcherApplication.class, args);

        String startTime= "2018-05-28T03:00:00";
        String interval ="30m";
        int limit=1000;
          service.ConvertLocalTimeToEpoch(service.CalculateEndTime(interval,startTime,limit));
    }

}
