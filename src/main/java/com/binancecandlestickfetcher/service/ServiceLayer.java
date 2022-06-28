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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Service
public class ServiceLayer {

    @Autowired
    ApiController apiController;


    public void updateData(String symbol, long startTime, int interval, int limit) throws BusinessIntegrityException {

        String basePath = checkFolder(symbol, interval);
        String fileName = getFileNameByTimeAndInterval(basePath, symbol, interval, startTime);
        String convertedInterval = convertIntervalFromIntToString(interval);


        if (interval > 1 && interval < 240) {
//ilk önce dosya yoksa ilk günü ekleyecek.
            //Sorguna 1000 adet veri var bunu gün başlarına göre bölmem lazım.
            //Eğer sonraki gün başına gelmişse yeni dosyada kaydetmeli.


        } else if (interval >= 240) {

            boolean checkLoop = true;
            while (checkLoop) {
                if (!isFileExist(fileName)) {
                    String createdFile = createFile(fileName);
                    String response = apiController.GetDataFromAPI(symbol, startTime, convertedInterval, limit);
                    if (response != "[]") {
                        saveDataInFile(createdFile, response);
                    } else {
                       String responseWithoutLimit = apiController.GetDataFromAPIWithoutLimit(symbol, startTime, convertedInterval);
                        saveDataInFile(createdFile, responseWithoutLimit);
                    }
                } else {
                    long endTime = getLastEndTimeAsNewStartTime(fileName);
                    long beginningOfToday = convertLocalDateTimeToEpoch(
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toLocalDateTime().with(LocalTime.MIN)
                    );
                    while (endTime <= beginningOfToday) {
                        String response = apiController.GetDataFromAPI(symbol, endTime, convertedInterval, limit);
                        if (response != "[]") {
                            saveDataInFile(fileName, response);
                            endTime = getLastEndTimeAsNewStartTime(fileName);
                        }
                    }

                checkLoop = false;
                }

            }
        }
/*
        if (!isFileExist(fileName)) {
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
            //String convertedInterval = convertIntervalFromIntToString(interval);

            String response = apiController.GetDataFromAPI(symbol, getLastEndTimeAsNewStartTime(fileName), convertedInterval, limit);
            if (response != "[]") {
                saveDataInFile(fileName, response);
            }
        }
  */
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
            System.out.println("Data in file: " + jsonArrayFromContentInFiles);
            System.out.println("Response: " + jsonArrayOfResponse);
            //Write data to file
            if (jsonArrayFromContentInFiles != null) {
                for (JsonElement data : jsonArrayOfResponse) {
                    jsonArrayFromContentInFiles.add(data);
                }
                writeDataToFile(path, jsonArrayFromContentInFiles);
            } else {

                writeDataToFile(path, jsonArrayOfResponse);
            }
            System.out.println("Updated data in file: " + jsonArrayFromContentInFiles);
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
    //C:\Users\erhan\IdeaProjects\BinanceCandlestickFetcher\EOSUSDT\EOSUSDT-240\EOSUSDT-240 2022-06-22
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

    //Converts Epoch to DateTime
    public LocalDateTime convertEpochToLocalDateTime(long epochTime) {
        System.out.println("gelen long epoch" + epochTime);
        //todo longdan locale çevirmiyor aşağısı onu çevir

        LocalDateTime ldt = Instant.ofEpochMilli(epochTime)
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println("converted" + ldt);
        return ldt;
    }

    //Create FileName
    public String getFileNameByTimeAndInterval(String basePath, String symbol, int interval, long startTime) throws BusinessIntegrityException {
        try {
            LocalDateTime start = convertEpochToLocalDateTime(startTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String createdTime = start.atZone(ZoneId.systemDefault()).format(formatter);
            System.out.println(createdTime);
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

            LocalDateTime base = LocalDateTime.parse(startTime);
            LocalDateTime start = base.with(LocalDateTime.MIN);
            LocalDateTime end = base.with(LocalDateTime.MAX);
            System.out.println("Start : " + start);
            System.out.println("End : " + end);

            if (interval == 1) {
                //Add 1.44
                return start.plusMinutes(limit);
            }

            return base.with(LocalDateTime.MAX);
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
