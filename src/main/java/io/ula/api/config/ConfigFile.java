package io.ula.api.config;

import com.google.gson.*;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


public class ConfigFile {
    protected JsonObject jsonObject = new JsonObject();
    protected JsonObject defaultContent;
    protected Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    protected File file;
    protected String file_folder;
    protected String file_name;
    public ConfigFile(@NonNull String name, String folder, JsonObject content){
        file_name = name;
        file_folder = folder;
        defaultContent = content;
    }

    public void createDir(Path root){
        Path folderPath = Path.of(new File(root.toString() + "/" + file_folder).toURI());
        if(file_folder != null && !Files.exists(folderPath)){
            try{
                Files.createDirectory(folderPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create io.ula.config directory :" + e.getMessage());
            }
        }
    }
    public void createFile(Path root,String version){
        if(file_folder != null) {
            file = new File(String.format(root.toString() + "/%s/%s", file_folder, file_name));
        }else{
            file = new File(String.format(root.toString() + "/%s", file_name));
        }

        if(!Files.exists(Path.of(file.toURI()))){
            try {
                Files.createFile(Path.of(file.toURI()));
            } catch (IOException e) {
                throw new RuntimeException(String.format("Failed to create io.ula.config file \"%s\" : ", file_name) + e.getMessage());
            }
            if(defaultContent!=null){
                jsonObject = defaultContent;
            }else {
                addKey("version", Objects.requireNonNullElse(version, "Unknown"));
            }
            write();
        }
    }
    public void removeFile(){
        if(Files.exists(Path.of(file.toURI()))){
            try {
                Files.delete(Path.of(file.toURI()));
            } catch (IOException e) {
                throw new RuntimeException(String.format("Failed to remove io.ula.config file \"%s\" : ", file_name) + e.getMessage());
            }
        }
    }
    public void addKey(String name,String key){jsonObject.addProperty(name,key);}
    public void addKey(String name,Boolean key){jsonObject.addProperty(name,key);}
    public void addKey(String name,Number key){jsonObject.addProperty(name,key);}
    public void addKey(String name,JsonElement key){jsonObject.add(name,key);}

    public void removeKey(String name){jsonObject.remove(name);}

    public JsonElement getKey(String name){return jsonObject.get(name);}

    public Boolean has(String name){return  jsonObject.has(name);};

    public String getName(){return this.file_name;}

    public void write(){
        try {
            Files.write(Path.of(file.toURI()), gson.toJson(jsonObject).getBytes());
        }catch(IOException e){
            throw new RuntimeException(String.format("Failed to write io.ula.config file \"%s\" : ",file_name)+e.getMessage());
        }
    }

    public void reload(){
        String list;
        try {
            list = new String(Files.readAllBytes(Path.of(file.toURI())));
            jsonObject = JsonParser.parseString(list).getAsJsonObject();
        }catch(Exception e){
            throw new RuntimeException(String.format("Failed to read io.ula.config file \"%s\" : ",file_name) + e.getMessage());
        }
    }
}
