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


@SpringBootApplication
public class BinanceCandlestickFetcherApplication {


    public static void main(String[] args) throws BusinessIntegrityException {
        //Inject ServiceLayer in main method
        ApplicationContext applicationContext = SpringApplication.run(BinanceCandlestickFetcherApplication.class, args);
        ServiceLayer service = applicationContext.getBean(ServiceLayer.class);
        String symbol = "EOSUSDT";
        String start = "2018-05-28T03:00:00";
        String interval = "1d";
        int limit = 10;
        //startTime==endTime of start
        long startTime =service.ConvertLocalTimeToEpoch(service.CalculateEndTime(interval,start,limit));
       service.GetDataFromAPI(symbol,startTime,interval,limit);

    }


}
