package io.ula.api.dialog;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class DialogHelper {
    private static MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    @FunctionalInterface
    public interface CustomEvent{
        public void behaviour(ServerPlayer player,ServerboundCustomClickActionPacket packet);
    }

    private static Map<Identifier, CustomEvent> customClicks = new HashMap<>();
    public static void addCustomClick(Identifier identifier,CustomEvent event){
        customClicks.put(identifier,event);
    }


    public static void onHandleCustomClick(ServerPlayer player, ServerboundCustomClickActionPacket packet){
        Identifier identifier = packet.id();
        customClicks.get(identifier).behaviour(player,packet);
    }
}
