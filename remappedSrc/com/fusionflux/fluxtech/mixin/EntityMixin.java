package com.fusionflux.fluxtech.mixin;

import com.fusionflux.fluxtech.entity.IsRollingAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements IsRollingAccessor {

    private static final TrackedData<Boolean> IS_ROLLING = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Direction> DIRECTION = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.FACING);

    @Shadow
    @Final
    protected DataTracker dataTracker;

    @Shadow private float standingEyeHeight;

    @Shadow protected abstract float getEyeHeight(EntityPose pose, EntityDimensions dimensions);

    @Shadow public abstract EntityPose getPose();

    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);

    @Override
    public boolean isRolling() {
        return dataTracker.get(IS_ROLLING);
    }

    @Override
    public void setRolling(boolean rolling) {
        dataTracker.set(IS_ROLLING, rolling);
    }

    @Override
    public Direction getDirection() {
        return dataTracker.get(DIRECTION);
    }

    @Override
    public void setDirection(Direction direction) {
        dataTracker.set(DIRECTION, direction);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;initDataTracker()V"))
    public void Entity(EntityType<?> type, World world, CallbackInfo callbackInfo) {
        this.dataTracker.startTracking(IS_ROLLING, false);
        this.dataTracker.startTracking(DIRECTION, Direction.UP);
    }


    /*@Inject(method = "calculateDimensions", at = @At("TAIL"))
public void calculateDimensions(CallbackInfo ci){
        EntityPose entityPose2 = this.getPose();
        EntityDimensions entityDimensions3 = this.getDimensions(entityPose2);
        this.standingEyeHeight = this.getEyeHeight(entityPose2, entityDimensions3)-1;
    }*/
}