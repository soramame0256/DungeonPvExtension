package com.github.soramame0256.dungeonpvextension.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.MOD_NAME;

public class DataUtils {
    private static DataUtils INSTANCE;
    private static final String FILE = MOD_NAME + "/saves.json";
    public static DataUtils getInstance() {
        return INSTANCE;
    }
    public DataUtils() throws IOException {
        if(Files.notExists(Paths.get(MOD_NAME))) {
            Files.createDirectory(Paths.get(MOD_NAME));
        }
        if(Files.notExists(Paths.get(FILE))){
            FileWriter fw = new FileWriter(FILE);
            PrintWriter writer = new PrintWriter(fw);
            writer.print("{}");
            writer.flush();
            writer.close();
        }
        INSTANCE = this;
    }
    public String getStringData(String index){
        JsonObject jsonObject = convertFileToJSON(FILE);
        if (jsonObject.has(index)) {
            return jsonObject.get(index).getAsString();
        }else{
            return "";
        }
    }
    public void saveStringData(String index, String value) throws IOException {
        JsonObject jsonObject = convertFileToJSON(FILE);
        jsonObject.addProperty(index, value);
        FileWriter fileWriter = new FileWriter(FILE);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();
        fileWriter.close();
    }
    public void saveJsonData(String index, JsonElement value) throws IOException {
        JsonObject jsonObject = convertFileToJSON(FILE);
        jsonObject.add(index, value);
        FileWriter fileWriter = new FileWriter(FILE);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();
        fileWriter.close();
    }
    public JsonArray getJsonArrayData(String index){
        JsonObject jsonObject = convertFileToJSON(FILE);
        if (jsonObject.has(index)) {
            return jsonObject.get(index).getAsJsonArray();
        }else{
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(new JsonObject());
            return jsonArray;
        }
    }
    //From anywhere
    public static JsonObject convertFileToJSON (String fileName){
        // Read from File to String
        JsonObject jsonObject = new JsonObject();
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(fileName));
            jsonObject = jsonElement.getAsJsonObject();
        } catch (IOException ignored) {}
        return jsonObject;
    }
}
