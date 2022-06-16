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

        /*
        Epoch timestamp: 1654041600
        Timestamp in milliseconds: 1654041600000
        Date and time (GMT): Wednesday, 1 June 2022 00:00:00
*/
        //long start =1654041600;
        long startTime= service.CreateStartTime();
        long endTime= service.CreateEndTime(startTime,"30m",1000);
        System.out.println("EndTime: "+endTime);
    }

}
