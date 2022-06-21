package com.binancecandlestickfetcher;

import com.binancecandlestickfetcher.controller.ApiController;
import com.binancecandlestickfetcher.service.ServiceLayer;
import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;


@SpringBootApplication
public class BinanceCandlestickFetcherApplication {


    public static void main(String[] args) throws BusinessIntegrityException {
        //Inject ServiceLayer in main method
        ApplicationContext applicationContext = SpringApplication.run(BinanceCandlestickFetcherApplication.class, args);
        ServiceLayer service = applicationContext.getBean(ServiceLayer.class);
        //ApiController apiController = applicationContext.getBean(ApiController.class);
        /*
        String symbol = "EOSUSDT";
        String start = "2018-05-28T03:00:00";
        String interval = "1d";
        int limit = 10;
        //startTime==endTime of start
        long startTime = service.ConvertLocalDateTimeToEpoch(service.CalculateEndTime(interval, start, limit));
        service.GetData(symbol,startTime,interval,limit);
        //apiController.GetDataFromAPI(symbol, startTime, interval, limit);

         */
        // String path=service.createFile("deneme1","asdfasdf");
        // System.out.println(path);

        //Get Request
        String begin = "2018-05-28T03:00:00";
        LocalDateTime start = LocalDateTime.parse(begin);
        long startTime = service.convertLocalDateTimeToEpoch(start);
        service.deneme("EOSUSDT",startTime,"1m",10);
        //service.deneme("EOSUSDT",startTime,"5m",10);
        //service.deneme("EOSUSDT",startTime,"30m",10);
        //service.deneme("EOSUSDT",startTime,"1h",10);
        //service.deneme("EOSUSDT",startTime,"4h",10);
        //service.deneme("EOSUSDT",startTime,"1d",10);

       // String s= service.checkFolder("EOSUSDT","1m");
       // System.out.println(s);



        System.exit(0);
    }


}
