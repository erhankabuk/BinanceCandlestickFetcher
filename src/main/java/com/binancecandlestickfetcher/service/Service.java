package com.binancecandlestickfetcher.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.json.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {
    //Service methods

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
