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
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Service
public class ServiceLayer {

    @Autowired
    ApiController apiController;

    public void updateData(String symbol, long startTime, int interval, int limit) throws BusinessIntegrityException {

        //while response!=null;
        // String convertedInterval = convertIntervalFromIntToString(interval);
        // String response = apiController.GetDataFromAPI(symbol, startTime, convertedInterval, limit);

        String basePath = checkFolder(symbol, interval);
        String fileName = getFileNameByTimeAndInterval(basePath, symbol, interval);

        if (!isFileExist(fileName)) {

            String convertedInterval = convertIntervalFromIntToString(interval);
            String response = apiController.GetDataFromAPI(symbol, startTime, convertedInterval, limit);
            if (response != "[]") {
                String createdFile = createFile(fileName);
                saveDataInFile(createdFile, response);
            }
            //How loop wil be stoped?


        } else {
            // Data Integrity
            // Önce veriyi çek
            // Verinin son indexinin endTimeına bak.
            // ilk verinin startTimeını al calculateendtime ını al
            //eğer calculatedEndTime-1 ==endtime ise yeni dosyada endTime ile veriyi çeksin
            // değilse endtimedan calculatedtime-1 e kadarki veriyi o günkü dosyaya kaydet
            //1m de 1440 veri var 4h ye kadar günlük tek sorgu dosyaya kaydediyor.4h sonrası tek dosyada

            //4h sonrası için

            System.out.println(fileName + " existed");
            String convertedInterval = convertIntervalFromIntToString(interval);
            String response = apiController.GetDataFromAPI(symbol, getLastEndTimeAsNewStartTime(fileName), convertedInterval, limit);
            if (response != "[]") {
                //override on file!! append data
                //Dosya adını getir.
                saveDataInFile(fileName, response);
            }

            //add data as interval 1d 4h first
        }

    }

    //Save HttpResponse Data in File
    public void saveDataInFile(String filePath, String content) throws BusinessIntegrityException {
        try {

            Path path = Paths.get(filePath);
/*
            //Get file content
            String content2 = Files.readString(path);
            //Convert string to JsonArray
            JsonArray jsonArray = new Gson().fromJson(content2, JsonArray.class);

            System.out.println("Dosyadaki jsonArray" + jsonArray);
*/
            //Added ","
            // String editedContent = content.replaceAll("\\s+", "")+",";
            String editedContent = content.replaceAll("\\s+", "");
            //Convert string to json Array
            //get content in file and append with new content
            //Convert jsonarray to string
            //write string

            Files.writeString(path, editedContent, StandardOpenOption.APPEND);
            //Files.writeString(path, editedContent);


        } catch (Exception e) {
            throw new BusinessIntegrityException(e.getMessage());
        }
    }

    //Check files for lastEndTime
    //C:\Users\erhan\IdeaProjects\BinanceCandlestickFetcher\EOSUSDT\EOSUSDT-240\EOSUSDT-240 2022-06-22
    public long getLastEndTimeAsNewStartTime(String filePath) throws BusinessIntegrityException {
        try {
            //Get file content
            String content = Files.readString(Paths.get(filePath));
            System.out.println("hatalı" + content);
            //Convert string to JsonArray
            JsonArray jsonArray = new Gson().fromJson(content, JsonArray.class);
            //Get last index of JsonArray
            int lastIndexOfJsonArray = jsonArray.size() - 1;
            //Get endTime of last index of JsonArray
            JsonElement jsonElement = jsonArray.get(lastIndexOfJsonArray).getAsJsonArray().get(6);
            //Convert jsonelement to long
            long startTime = jsonElement.getAsLong();
            System.out.println("Last: " + startTime);
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

    //Create FileName
    public String getFileNameByTimeAndInterval(String basePath, String symbol, int interval) throws BusinessIntegrityException {
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

    //Gets endTime from startTime as LocalDateTime
    public LocalDateTime calculateEndTime(int interval, String startTime, int limit) throws BusinessIntegrityException {
        try {
            LocalDateTime endTime = LocalDateTime.parse(startTime);
            System.out.println("Start : " + endTime);
            if (interval == 1) {
                return endTime = endTime.plusMinutes(limit);
            } else if (interval == 5) {
                return endTime = endTime.plusMinutes(limit * 5);
            } else if (interval == 30) {
                return endTime = endTime.plusMinutes(limit * 30);
            } else if (interval == 60) {
                return endTime = endTime.plusHours(limit);
            } else if (interval == 240) {
                return endTime = endTime.plusHours(limit * 4);
            } else if (interval == 1440) {
                return endTime = endTime.plusDays(limit);
            } else {
                System.out.println("End : Invalid internal...");
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
