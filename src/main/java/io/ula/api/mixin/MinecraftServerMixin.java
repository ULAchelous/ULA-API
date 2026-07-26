package io.ula.api.mixin;

import io.ula.api.motd.CustomMotdHolder;
import io.ula.api.scheduler.ServerScheduler;
import io.ula.api.scheduler.ServerSchedulerHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ServerSchedulerHolder , CustomMotdHolder {
    @Shadow
    private @Nullable String motd;

    @Shadow
    protected abstract ServerStatus buildServerStatus();

    private final ServerScheduler serverScheduler = new ServerScheduler();

    private ServerStatus status;

    String CUSTOM_MOTD = null;
    Type MOTD_TYPE = null;
    Boolean ENABLE_CUSTOM_MOTD = false;


    @Override
    public ServerScheduler drng$getServerSchedule(){
        return  this.serverScheduler;
    }

    @Inject(method = "tickChildren",at = @At("TAIL"))
    private void tickableInject(BooleanSupplier booleanSupplier, CallbackInfo ci){
        serverScheduler.tickable();
    }

    @ModifyArg(method = "buildServerStatus",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/status/ServerStatus;<init>(Lnet/minecraft/network/chat/Component;Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;Z)V"),index = 0)
    private Component modifyMotd(Component motd){
        if(ENABLE_CUSTOM_MOTD){
            switch(MOTD_TYPE) {
                case  DIRECT_REPLACE -> motd = Component.literal(CUSTOM_MOTD);
                case FORMARTTED_STRING -> {
                    String[] strs = CUSTOM_MOTD.split("\\{motd\\}",-1);
                    MutableComponent customMotd = Component.literal(strs[0]);
                    for(int i = 0;i < strs.length -1;i++){
                        customMotd.append(motd);
                        customMotd.append(strs[i+1]);
                    }
                    motd = customMotd;
                }
            }
        }
        return motd;
    }

    public void setMotd(String motd, Type type){
        if(motd != null && type != null) {
            CUSTOM_MOTD = motd;
            MOTD_TYPE = type;
            ENABLE_CUSTOM_MOTD = true;
        }
        //status = buildServerStatus();
    }

    public void unsetMotd(String motd, Type type){
        CUSTOM_MOTD = null;
        MOTD_TYPE = null;
        ENABLE_CUSTOM_MOTD = false;
    }
}
