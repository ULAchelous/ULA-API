package io.ula.api.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConfigManager {


    private Map<String,ConfigFile> configs = new HashMap<>();
    private ArrayList<ConfigFile> autoRemove = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Thread thread;
    private AutoSave autoSave = new AutoSave();
    private String programId;
    private String programVersion;
    private Path programRootPath;
    private Path configRootPath;
    private Logger LOGGER;

    private volatile int autoSavePeriod = 600;

    class AutoSave implements Runnable{
        private volatile Boolean pluginStopped = false;
        public void stop(){this.pluginStopped = true;}
        @Override
        public void run() {
            Logger LOGGER = LogManager.getLogger(programId + "/ConfigManager:autoSave");
            while(true){
                try {
                    int period = autoSavePeriod;
                    Thread.sleep(TimeUnit.MINUTES.toMillis(period));
                } catch (InterruptedException e) {
                    LOGGER.info("Config file auto save thread are stopping now.");
                    break;
                }
                if(pluginStopped == true) break;
                saveAll();
            }
        }
    }


    public ConfigManager(String id,String version,Path path){
        programId = id;
        programVersion = version;
        programRootPath = path;
        configRootPath = Path.of(new File(path.toString() + "/config/"+id).toURI());
        LOGGER = LogManager.getLogger(id + "/ConfigManager");
        init();
        thread = new Thread(autoSave,"save");
        thread.start();

    }

    public String getId(){return programId;}
    public String getVersion(){return programVersion;}
    public Path getRoot(){return configRootPath;}

    public void setAutoSavePeriod(int autoSavePeriod) {
        this.autoSavePeriod = autoSavePeriod;
    }

    public void register(String key, ConfigFile configFile){
        if(!(configFile instanceof InlineConfigFile)){
            configFile.createDir(configRootPath);
            configFile.createFile(configRootPath,programVersion);
        }
        configFile.reload();
        LOGGER.info(String.format("Loaded config file \"%s\"",configFile.getName()));
        configs.put(key, configFile);
    }
    public void setAutoRemove(String key){
        autoRemove.add(configs.get(key));
    }
    public ConfigFile getConfig(String key){
        return configs.get(key);
    }
    public void saveAll(){
        lock.readLock().lock();
        for (Map.Entry<String, ConfigFile> entry : configs.entrySet()) {
            entry.getValue().write();
            LOGGER.info(String.format("Saved changes to config file \"%s\"",entry.getValue().getName()));
        }
        lock.readLock().unlock();
    }
    public void reloadAll(){
        lock.writeLock().lock();
        for (Map.Entry<String, ConfigFile> entry : configs.entrySet()) {
            entry.getValue().reload();
            LOGGER.info(String.format("Loaded config file \"%s\"",entry.getValue().getName()));
        }
        lock.writeLock().unlock();
    }

    public void init(){
        if(!Files.exists(Path.of(new File(programRootPath.toString() + "/config").toURI()))){
            try{
                Files.createDirectory(Path.of(new File(programRootPath.toString() + "/config").toURI()));
            }catch(IOException e){
                throw new RuntimeException("Failed to create config directory :" + e.getMessage());
            }
        }
        if(!Files.exists(configRootPath)){
            try{
                Files.createDirectory(configRootPath);
            }catch(IOException e){
                throw new RuntimeException("Failed to create config directory :" + e.getMessage());
            }
        }
    }

    public void onDisabled(){
        autoSave.stop();
        thread.interrupt();
        saveAll();
        for(ConfigFile configFile : autoRemove){
            configFile.removeFile();
        }
    }
}
