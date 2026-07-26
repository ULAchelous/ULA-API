package io.ula.api.motd;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class MotdManager {
    public static String CUSTOM_MOTD = null;
    public static Type MOTD_TYPE = null;
    public static Boolean ENABLE_CUSTOM_MOTD = false;
    public static enum Type{
        DIRECT_REPLACE,
        FORMARTTED_STRING,
    }

    public static void setMotd(String motd, Type type){
        if(motd != null && type != null) {
            CUSTOM_MOTD = motd;
            MOTD_TYPE = type;
            ENABLE_CUSTOM_MOTD = true;
        }
    }

    public static void unsetMotd(String motd, Type type){
        CUSTOM_MOTD = null;
        MOTD_TYPE = null;
        ENABLE_CUSTOM_MOTD = false;
    }
}