package com.binancecandlestickfetcher.service;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceLayer {
    //Service methods

    public static long CreateStartTime() {

        //The date for which start of day needs to be found
        //todo:now will be change with expired time of currency
        LocalDate localDate = LocalDate.now();
        //Local date time
        LocalDateTime startOfDay = localDate.atStartOfDay();
        //Convert startOfDay to Epoch Time in Local Time Zone
        Instant instant = startOfDay.atZone(ZoneId.systemDefault()).toInstant();
        long convertedStartOfDay = instant.toEpochMilli();
        System.out.println("Start of Day: " + startOfDay);
        System.out.println("Converted Start of Day: " + convertedStartOfDay);
        return convertedStartOfDay;
    }

    public static long CreateEndTime(long startTime, String interval,int limit) {

        //Get interval Time as Epoch Time
        long intervalTime = ConvertIntervalToEpochTime(interval,limit);
        //Add intervalTime to startTime
        long endTime = startTime + intervalTime;
        return endTime;
    }

    private static long ConvertIntervalToEpochTime(String interval,int limit) {

        HashMap<String, Integer> intervalMap = new HashMap<String, Integer>();
        intervalMap.put("1m", 60000);
        intervalMap.put("5m", 300000);
        intervalMap.put("30m", 1800000);
        intervalMap.put("1h", 3600000);
        intervalMap.put("4h", 14400000);
        intervalMap.put("1d", 86400000);

        long convertedInterval = intervalMap.get(interval)*limit+1000;
        return convertedInterval;
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
            String fileName = LocalDateTime.now() + symbol + interval;
            File file = new File(fileName);

            file.createNewFile();

            //return file.getName();
            return file.getAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }


}
