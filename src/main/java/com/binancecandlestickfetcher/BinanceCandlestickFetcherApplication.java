package com.binancecandlestickfetcher;

import com.binancecandlestickfetcher.service.ServiceLayer;
import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
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
        service.deneme("EOSUSDT",startTime,1,10);
        //service.deneme("EOSUSDT",startTime,5,10);
        //service.deneme("EOSUSDT",startTime,30,10);
        //service.deneme("EOSUSDT",startTime,60,10);
        //service.deneme("EOSUSDT",startTime,240,10);
        //service.deneme("EOSUSDT",startTime,1440,10);

        System.exit(0);
    }


}
