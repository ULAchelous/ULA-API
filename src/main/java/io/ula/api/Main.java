package io.ula.api;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main implements ModInitializer {
    Logger LOGGER = LogManager.getLogger("ulapi");
    @Override
    public void onInitialize() {
        LOGGER.info("Thanks you for using ULA-API !");
    }
}
