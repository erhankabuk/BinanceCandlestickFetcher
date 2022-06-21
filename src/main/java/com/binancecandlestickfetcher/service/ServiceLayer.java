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
            HashMap<Integer,String> intervalList = new HashMap<Integer,String>(){
                {
                    put(1,"1m");
                    put(3,"3m");
                    put(5,"5m");
                    put(15,"15m");
                    put(30,"30m");
                    put(60,"1h");
                    put(120,"2h");
                    put(240,"4h");
                    put(360,"6h");
                    put(480,"8h");
                    put(720,"12h");
                    put(1440,"1d");
                    put(4320,"3d");
                    put(10080,"1w");
                    put(40320,"1M");
                }
            };
            return intervalList.get(interval);
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
            File file = new File(symbol);
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

    //todo: Add All Intervals
    //Gets endTime from startTime as LocalDateTime
    public  LocalDateTime calculateEndTime(int interval, String startTime, int limit) throws BusinessIntegrityException {
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
