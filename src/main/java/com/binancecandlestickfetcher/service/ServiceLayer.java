package com.binancecandlestickfetcher.service;

import com.binancecandlestickfetcher.controller.ApiController;
import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
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

    public void updateData(String symbol, long startTime, int interval, int limit) throws BusinessIntegrityException {

        //while response!=null;
        String convertedInterval = convertIntervalFromIntToString(interval);
        String response = apiController.GetDataFromAPI(symbol, startTime, convertedInterval, limit);

        String basePath = checkFolder(symbol, interval);
        String fileName = createFileNameByTimeAndInterval(basePath, symbol, interval);

        if (!isFileExist(fileName) && response != null) {
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
    public String convertIntervalFromIntToString(int interval) throws BusinessIntegrityException {
        try {
            if (interval == 1) {
                return "1m";
            } else if (interval == 3) {
                return "3m";
            } else if (interval == 5) {
                return "5m";
            } else if (interval == 15) {
                return "15m";
            } else if (interval == 30) {
                return "30m";
            } else if (interval == 60) {
                return "1h";
            } else if (interval == 120) {
                return "2h";
            } else if (interval == 240) {
                return "4h";
            } else if (interval == 360) {
                return "6h";
            } else if (interval == 480) {
                return "8h";
            } else if (interval == 720) {
                return "12h";
            } else if (interval == 1440) {
                return "1d";
            } else if (interval == 4320) {
                return "3d";
            } else if (interval == 10080) {
                return "1w";
            } else if (interval == 40320) {
                return "1M";
            } else {
                return "Invalid interval";
            }

        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Create FileName
    public String createFileNameByTimeAndInterval(String basePath, String symbol, int interval) throws BusinessIntegrityException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String createdTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).format(formatter);
            return basePath + "\\" + symbol + "-" + interval + " " + createdTime;
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Check Folder is existed.
    public String checkFolder(String symbol, int interval) throws BusinessIntegrityException {
        try {
            String fileName = symbol;
            File file = new File(fileName);
            if (file.exists()) {
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
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //todo: Check access modifier - static
    //Gets endTime from startTime as LocalDateTime
    public static LocalDateTime calculateEndTime(String interval, String startTime, int limit) throws BusinessIntegrityException {
        try {
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

        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Converts any LocalDateTime to Epoch
    public long convertLocalDateTimeToEpoch(LocalDateTime time) throws BusinessIntegrityException {
        try {
            System.out.println("End : " + time);
            Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
            long convertedTime = instant.toEpochMilli();
            System.out.println("ConvertedTime : " + convertedTime);
            return convertedTime;
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Check files for lastCloseTime
    public void checkLastCloseTime(String filePath) throws BusinessIntegrityException {
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
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Check database for any file existed before
    public boolean isFileExist(String filePath) throws BusinessIntegrityException {
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
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Save HttpResponse Data in File
    public void saveDataInFile(String filePath, String content) throws BusinessIntegrityException {
        try {
            Path path = Paths.get(filePath);
            String editedContent = content.replaceAll("\\s+", "");
            Files.writeString(path, editedContent);
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Create File
    public String createFile(String fileName) throws BusinessIntegrityException {
        try {
            File file = new File(fileName);
            file.createNewFile();
            return file.getAbsolutePath();
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

}
