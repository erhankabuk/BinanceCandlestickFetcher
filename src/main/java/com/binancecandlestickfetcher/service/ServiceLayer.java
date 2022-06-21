package com.binancecandlestickfetcher.service;

import com.binancecandlestickfetcher.controller.ApiController;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceLayer {

    @Autowired
    ApiController apiController;

    public void deneme(String symbol, long startTime, int interval, int limit) {

        //while response!=null;
        String convertedInterval = convertIntervalFromIntToString(interval);
        String response = apiController.GetDataFromAPI(symbol, startTime, convertedInterval, limit);

        String basePath = checkFolder(symbol, interval);
        String fileName = createFileNameByTimeAndInterval(basePath, symbol, interval);

        if (!isFileExist(fileName)&&response != null) {
              String createdFile = createFile(fileName);
             saveDataInFile(createdFile, response);

        } else {
            System.out.println(fileName + " existed");
            //Get file
            //Get content
            //convertJSONARRAY
            //get lastindex of endTime
            //String response = apiController.GetDataFromAPI(symbol, endTime, interval, limit);
            //add data as interval
        }

    }

    //Convert interval int to String
    public String convertIntervalFromIntToString(int interval) {
        try {
            if (interval == 1) {
                return "1m";
            } else if (interval == 5) {
                return "5m";
            } else if (interval == 30) {
                return "30m";
            } else if (interval == 60) {
                return "1h";
            } else if (interval == 240) {
                return "4h";
            } else if (interval == 1440) {
                return "1d";
            } else {
                return "Invalid interval";
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Create FileName
    public String createFileNameByTimeAndInterval(String basePath, String symbol, int interval) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String createdTime = LocalDateTime.now().format(formatter);
        return basePath + "\\" + symbol + "-" + interval + " " + createdTime;
    }

    //Check Folder is existed.
    public String checkFolder(String symbol, int interval) {
        String fileName = symbol;
        File file = new File(fileName);
        if (file.exists()) {
            if (interval == 1440 || interval == 240) {
                return file.getAbsolutePath();
            }
            String fileNameHourly = symbol + "-" + interval;
            File fileHourly = new File(file.getAbsolutePath(), fileNameHourly);
            if (!fileHourly.exists()) {
                fileHourly.mkdirs();
            }
            return fileHourly.getAbsolutePath();
        } else {
            file.mkdirs();
            if (interval == 1 || interval == 5 || interval == 30 || interval == 60) {
                String fileNameHourly = symbol + "-" + interval;
                File fileHourly = new File(file.getAbsolutePath(), fileNameHourly);
                fileHourly.mkdirs();
                return fileHourly.getAbsolutePath();
            }
            return file.getAbsolutePath();
        }
    }

    //todo: Check access modifier - static
    //Gets endTime from startTime as LocalDateTime
    public static LocalDateTime calculateEndTime(String interval, String startTime, int limit) {
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
    public static long convertLocalDateTimeToEpoch(LocalDateTime time) {
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
            // file exists and it is not a directory &&!Files.isDirectory(path)
            if (Files.exists(path)) {
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
    public String createFile(String fileName) {
        try {
            File file = new File(fileName);
            file.createNewFile();
            return file.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
