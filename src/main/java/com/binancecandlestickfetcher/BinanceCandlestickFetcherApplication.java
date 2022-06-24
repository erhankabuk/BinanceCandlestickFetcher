package com.binancecandlestickfetcher;

import com.binancecandlestickfetcher.service.ServiceLayer;
import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;

@SpringBootApplication
public class BinanceCandlestickFetcherApplication {


    public static void main(String[] args) throws BusinessIntegrityException {
        try {
            //Inject ServiceLayer in main method
            ApplicationContext applicationContext = SpringApplication.run(BinanceCandlestickFetcherApplication.class, args);
            ServiceLayer service = applicationContext.getBean(ServiceLayer.class);
            //ApiController apiController = applicationContext.getBean(ApiController.class);

            //Get Request
            String begin = "2018-05-28T03:00:00";
            LocalDateTime start = LocalDateTime.parse(begin);
            long startTime = service.convertLocalDateTimeToEpoch(start);
            //System.out.println(service.convertIntervalFromIntToString(31));
           // service.updateData("EOSUSDT", startTime,240 , 10);
            //service.updateData("EOSUSDT",startTime,5,10);
            //service.updateData("EOSUSDT",startTime,30,10);
            //service.updateData("EOSUSDT",startTime,60,10);
            //service.updateData("EOSUSDT",startTime,240,10);
            //service.updateData("EOSUSDT",startTime,1440,10);

            //  String lastEndTime = "C:\\Users\\erhan\\IdeaProjects\\BinanceCandlestickFetcher\\SHIBUSDT\\SHIBUSDT-240\\SHIBUSDT-240 2022-06-23";
            // long newStartTime = service.getLastEndTimeAsNewStartTime(lastEndTime);
             String lastEndTime = "database\\XRPUSDT\\XRPUSDT-1440\\XRPUSDT-1440 2022-06-24";
             long newStartTime = service.getLastEndTimeAsNewStartTime(lastEndTime);

            service.updateData("XRPUSDT", newStartTime, 1440, 10);

            System.exit(0);
        } catch (BeansException e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }


}
