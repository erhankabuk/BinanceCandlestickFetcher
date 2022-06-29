package com.binancecandlestickfetcher.service;

import com.binancecandlestickfetcher.controller.ApiController;
import com.binancecandlestickfetcher.utility.BusinessIntegrityException;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Service
public class ServiceLayer {

    @Autowired
    ApiController apiController;


    public void updateData(String symbol, long startTime, int interval, int limit) throws BusinessIntegrityException {

        //Check folder created before
        String basePath = checkFolder(symbol, interval);
        //Check File created before
        String fileName = getFileNameByTimeAndInterval(basePath, symbol, interval, startTime);
        //Converted interval for GET request
        String convertedInterval = convertIntervalFromIntToString(interval);

            boolean checkLoop = true;
            while (checkLoop) {
                if (!isFileExist(fileName)) {
                    //Call request
                    String response = apiController.GetDataFromAPI(symbol, startTime, convertedInterval, limit);
                    if (response != "[]") {
                        saveDataInFile(createFile(fileName), response);
                    } else {
                        // If currency has smaller limit than 1000 response returns "[]". So call request without limit.
                        String responseWithoutLimit = apiController.GetDataFromAPIWithoutLimit(symbol, startTime, convertedInterval);
                        //Save data in file
                        saveDataInFile(createFile(fileName), responseWithoutLimit);
                    }
                } else {
                    //If there is already some data in file...
                    //Find endTime of last value
                    long endTime = getLastEndTimeAsNewStartTime(fileName);
                    //Find present time
                    long beginningOfToday = convertLocalDateTimeToEpoch(
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MIN)
                    );

                    while (endTime <= beginningOfToday) {
                        String response = apiController.GetDataFromAPI(symbol, endTime, convertedInterval, limit);
                        if (response != "[]") {
                            saveDataInFile(fileName, response);
                            //Update endtime as next startTime
                            endTime = getLastEndTimeAsNewStartTime(fileName);
                        }
                    }
                    checkLoop = false;
                }
            }

    }


    //Save HttpResponse Data in File
    public void saveDataInFile(String filePath, String response) throws BusinessIntegrityException {
        try {
            //Get path
            Path path = Paths.get(filePath);
            //Get file content
            String contentInFiles = Files.readString(path);
            //Convert string to JsonArray
            JsonArray jsonArrayFromContentInFiles = new Gson().fromJson(contentInFiles, JsonArray.class);
            JsonArray jsonArrayOfResponse = new Gson().fromJson(response, JsonArray.class);

            //Write data to file
            if (jsonArrayFromContentInFiles != null) {
                for (JsonElement data : jsonArrayOfResponse) {
                    jsonArrayFromContentInFiles.add(data);
                }
                writeDataToFile(path, jsonArrayFromContentInFiles);
            } else {
                writeDataToFile(path, jsonArrayOfResponse);
            }
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Write data to file
    public void writeDataToFile(Path path, JsonArray jsonArray) throws BusinessIntegrityException {
        try {
            String content = String.valueOf(jsonArray);
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Check files for lastEndTime
    public long getLastEndTimeAsNewStartTime(String filePath) throws BusinessIntegrityException {
        try {
            //Get file content
            String content = Files.readString(Paths.get(filePath));
            //Convert string to JsonArray
            JsonArray jsonArray = new Gson().fromJson(content, JsonArray.class);
            //Get last index of JsonArray
            int lastIndexOfJsonArray = jsonArray.size() - 1;
            //Get endTime of last index of JsonArray
            JsonElement jsonElement = jsonArray.get(lastIndexOfJsonArray).getAsJsonArray().get(6);
            //Convert jsonelement to long
            long startTime = jsonElement.getAsLong();
            return startTime;
        } catch (IOException e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Convert interval int to String
    public String convertIntervalFromIntToString(int interval) throws BusinessIntegrityException {
        try {
            HashMap<Integer, String> intervalList = new HashMap<Integer, String>() {
                {
                    put(1, "1m");
                    put(3, "3m");
                    put(5, "5m");
                    put(15, "15m");
                    put(30, "30m");
                    put(60, "1h");
                    put(120, "2h");
                    put(240, "4h");
                    put(360, "6h");
                    put(480, "8h");
                    put(720, "12h");
                    put(1440, "1d");
                    put(4320, "3d");
                    put(10080, "1w");
                    put(40320, "1M");
                }
            };
            return intervalList.get(interval);
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Converts Epoch to DateTime
    public LocalDateTime convertEpochToLocalDateTime(long epochTime) throws BusinessIntegrityException {
        try {
            LocalDateTime convertedEpochToLocalDateTime = Instant.ofEpochMilli(epochTime)
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            return convertedEpochToLocalDateTime;
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Create FileName
    public String getFileNameByTimeAndInterval(String basePath, String symbol, int interval, long startTime) throws BusinessIntegrityException {
        try {
            LocalDateTime start = convertEpochToLocalDateTime(startTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String createdTime = start.atZone(ZoneId.systemDefault()).format(formatter);
            return basePath + "\\" + symbol + "-" + interval + " " + createdTime;
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Check Folder is existed.
    public String checkFolder(String symbol, int interval) throws BusinessIntegrityException {
        try {
            String databaseFolderPath = "database//" + symbol;
            File file = new File(databaseFolderPath);
            if (!file.exists()) {
                file.mkdirs();
                String fileNameHourly = symbol + "-" + interval;
                File fileHourly = new File(file.getAbsolutePath(), fileNameHourly);
                fileHourly.mkdirs();
                return fileHourly.getAbsolutePath();
            }

            String fileNameHourly = symbol + "-" + interval;
            File fileHourly = new File(file.getAbsolutePath(), fileNameHourly);
            if (!fileHourly.exists()) {
                fileHourly.mkdirs();
            }
            return fileHourly.getAbsolutePath();
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Converts any LocalDateTime to Epoch
    public long convertLocalDateTimeToEpoch(LocalDateTime time) throws BusinessIntegrityException {
        try {
            Instant instant = time.atZone(ZoneId.systemDefault()).toInstant();
            long convertedTime = instant.toEpochMilli();
            return convertedTime;
        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Check database for any file existed before
    public boolean isFileExist(String filePath) throws BusinessIntegrityException {
        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) ? true : false;
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
