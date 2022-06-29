package com.binancecandlestickfetcher;

import com.binancecandlestickfetcher.service.ServiceLayer;
import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@SpringBootApplication
public class BinanceCandlestickFetcherApplication {


    public static void main(String[] args) throws BusinessIntegrityException {
        try {
            ApplicationContext applicationContext = SpringApplication.run(BinanceCandlestickFetcherApplication.class, args);
            ServiceLayer service = applicationContext.getBean(ServiceLayer.class);

            //Get startTime
            String begin = "2018-05-28T03:00:00";
            LocalDateTime start = LocalDateTime.parse(begin);
            long startTime = service.convertLocalDateTimeToEpoch(start);
            //service.updateData("EOSUSDT", startTime,1 , 1000);
            //service.updateData("EOSUSDT",startTime,5,1000);
            //service.updateData("EOSUSDT",startTime,30,1000);
            //service.updateData("EOSUSDT", startTime, 60, 1000);
            //service.updateData("EOSUSDT",startTime,240,1000);
            service.updateData("EOSUSDT", startTime, 1440, 1000);

            System.exit(0);
        } catch (BeansException e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }


}
