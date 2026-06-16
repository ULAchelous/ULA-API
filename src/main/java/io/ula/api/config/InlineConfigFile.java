package io.ula.api.config;

import com.google.gson.JsonParser;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class InlineConfigFile extends ConfigFile{

    private String path;
    public InlineConfigFile(@NonNull String path,@NonNull String name){
        super(name,null,null);
        this.path = path + '/' + name;
    }

    @Override
    public void createDir(Path root) {}
    @Override
    public void createFile(Path root,String version) {}

    @Override
    public void write() {}

    @Override
    public void reload() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader == null)
            classLoader = ConfigManager.class.getClassLoader();

        InputStream is = classLoader.getResourceAsStream(path);
        String content = "";
        if(is != null) content = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        try {
            jsonObject = JsonParser.parseString(content).getAsJsonObject();
        }catch(Exception e){
            throw new RuntimeException(String.format("Failed to read config file \"%s\" : ",file_name) + e.getMessage());
        }
    }
}
