package com.binancecandlestickfetcher.service;

import com.binancecandlestickfetcher.controller.ApiController;
import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ServiceLayer {

    @Autowired
    ApiController apiController;

    //GetData unneccessary
    public String GetData(String symbol, long startTime, String interval, int limit) throws BusinessIntegrityException {
        String response = apiController.GetDataFromAPI(symbol, startTime, interval, limit);
        System.out.println(response);
        return response;
    }

    public void deneme(String symbol, long startTime, String interval, int limit) {
        //String response = apiController.GetDataFromAPI(symbol, startTime, interval, limit);
        //while response!=null;
        String basePath="C:\\Users\\erhan\\IdeaProjects\\BinanceCandlestickFetcher\\";
        String fileName =basePath+ symbol + interval; // filePAth i bulmak gerek
        if (!isFileExist(fileName)) {
            String createdFilePath = createFile(symbol, interval);
            String response = apiController.GetDataFromAPI(symbol, startTime, interval, limit);
            saveDataInFile(createdFilePath, response);
        } else {

            //Get file
            //Get content
            //convertJSONARRAY
            //get lastindex of endTime
            //String response = apiController.GetDataFromAPI(symbol, endTime, interval, limit);
            //add data as interval
        }

    }

    //todo: Check access modifier - static
    //Gets endTime from startTime as LocalDateTime
    public static LocalDateTime CalculateEndTime(String interval, String startTime, int limit) {
        LocalDateTime endTime = LocalDateTime.parse(startTime);
        System.out.println("Start : " + endTime);
        if (interval == "1m") {
            return endTime = endTime.plusMinutes(limit);
        } else if (interval == "5m") {
            return endTime = endTime.plusMinutes(limit * 5);
        } else if (interval == "30m") {
            return endTime = endTime.plusMinutes(limit * 30);
        } else if (interval == "1h") {
            return endTime = endTime.plusHours(limit);
        } else if (interval == "4h") {
            return endTime = endTime.plusHours(limit * 4);
        } else if (interval == "1d") {
            return endTime = endTime.plusDays(limit);
        } else {
            System.out.println("End : Invalid internal...");
            //todo: return BusinessIntegrityException
            return null;
        }
    }

    //todo: Check access modifier - static
    //Converts any LocalDateTime to Epoch
    public static long ConvertLocalDateTimeToEpoch(LocalDateTime time) {
        try {
            System.out.println("End : " + time);
            Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
            long convertedTime = instant.toEpochMilli();
            System.out.println("ConvertedTime : " + convertedTime);
            return convertedTime;
        } catch (Exception e) {
            //todo: return BusinessIntegrityException
            throw new RuntimeException(e);
        }

    }


    //Check files for lastCloseTime
    public void checkLastCloseTime(String filePath) {

        try {
            String content = Files.readString(Paths.get(filePath));

            //convert string to Json with gson
            Gson gson = new Gson();
            gson.toJson(content);

            // get closetime from last item
            //todo: model kısmından dolayı nasıl yapacağımı kestiremiyorum
            //todo: jsonArray olarak çevirmem gerekebilir.

            //return closetime as startDate;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Check database for any file existed before
    public boolean isFileExist(String filePath) {

        try {
            Path path = Paths.get(filePath);
            // file exists and it is not a directory
            if (Files.exists(path) && !Files.isDirectory(path)) {
                //todo: check flowchart
                return true;
            } else {
                //todo: check flowchart
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //Save HttpResponse Data in File
    public void saveDataInFile(String filePath, String content) {
        try {
            Path path = Paths.get(filePath);
            String editedContent = content.replaceAll("\\s+", "");
            Files.writeString(path, editedContent);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    //Create File
    public String createFile(String symbol, String interval) {

        try {
            // todo: Needs a base folder path
            // slug gibi - koy aralarına
            //eosusdt
            //String fileName = LocalDateTime.now() + symbol + interval;
            String fileName = symbol + interval;
            File file = new File(fileName);

            file.createNewFile();

            //return file.getName();
            return file.getAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }


}
