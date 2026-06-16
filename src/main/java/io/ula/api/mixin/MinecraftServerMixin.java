package io.ula.api.mixin;

import io.ula.api.scheduler.ServerScheduler;
import io.ula.api.scheduler.ServerSchedulerHolder;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements ServerSchedulerHolder {
    private final ServerScheduler serverScheduler = new ServerScheduler();

    @Override
    public ServerScheduler drng$getServerSchedule(){
        return  this.serverScheduler;
    }

    @Inject(method = "tickChildren",at = @At("TAIL"))
    private void tickableInject(BooleanSupplier booleanSupplier, CallbackInfo ci){
        serverScheduler.tickable();
    }

}
