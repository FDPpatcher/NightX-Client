package net.ccbluex.liquidbounce.injection.forge.mixins.entity;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.StrafeEvent;
import net.ccbluex.liquidbounce.features.module.modules.combat.HitBox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public double posX;

    @Shadow
    public double posY;

    @Shadow
    public double posZ;
    @Shadow
    public float rotationPitch;
    @Shadow
    public float rotationYaw;
    @Shadow
    public Entity ridingEntity;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public boolean onGround;
    @Shadow
    public boolean isAirBorne;
    @Shadow
    public boolean noClip;
    @Shadow
    public World worldObj;
    @Shadow
    public boolean isInWeb;
    @Shadow
    public float stepHeight;
    @Shadow
    public boolean isCollidedHorizontally;
    @Shadow
    public boolean isCollidedVertically;
    @Shadow
    public boolean isCollided;
    @Shadow
    public float distanceWalkedModified;
    @Shadow
    public float distanceWalkedOnStepModified;
    @Shadow
    public int fireResistance;
    @Shadow
    public int timeUntilPortal;
    @Shadow
    public float width;
    @Shadow
    public float prevRotationPitch;
    @Shadow
    public float prevRotationYaw;
    @Shadow
    protected Random rand;
    @Shadow
    protected boolean inPortal;
    @Shadow
    private int nextStepDistance;
    @Shadow
    private int fire;
    @Shadow(remap = false)
    private CapabilityDispatcher capabilities;

    @Shadow
    public abstract boolean isSprinting();

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    @Shadow
    public abstract void setEntityBoundingBox(AxisAlignedBB bb);

    @Shadow
    public abstract float getDistanceToEntity(Entity entityIn);

    @Shadow
    public void moveEntity(double x, double y, double z) {
    }

    @Shadow
    public abstract boolean isInWater();

    @Shadow
    public abstract boolean isRiding();

    @Shadow
    protected abstract void dealFireDamage(int amount);

    @Shadow
    public abstract boolean isWet();

    @Shadow
    public abstract void addEntityCrashInfo(CrashReportCategory category);

    @Shadow
    protected abstract void doBlockCollisions();

    @Shadow
    protected abstract void playStepSound(BlockPos pos, Block blockIn);

    @Shadow
    protected abstract Vec3 getVectorForRotation(float pitch, float yaw);

    @Shadow
    public abstract UUID getUniqueID();

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public abstract boolean isInsideOfMaterial(Material materialIn);

    public int getNextStepDistance() {
        return nextStepDistance;
    }

    public void setNextStepDistance(int nextStepDistance) {
        this.nextStepDistance = nextStepDistance;
    }

    public int getFire() {
        return fire;
    }

    @Shadow
    public abstract void setFire(int seconds);

    @Inject(method = "getCollisionBorderSize", at = @At("HEAD"), cancellable = true)
    private void getCollisionBorderSize(final CallbackInfoReturnable<Float> callbackInfoReturnable) {
        final HitBox hitBox = LiquidBounce.moduleManager.getModule(HitBox.class);

        if (hitBox.getState())
            callbackInfoReturnable.setReturnValue(0.1F + hitBox.getSizeValue().get());
    }

    /*
        @Inject(method = "setAngles", at = @At("HEAD"), cancellable = true)
        private void setAngles(final float yaw, final float pitch, final CallbackInfo callbackInfo) {
            if (LiquidBounce.moduleManager.getModule(NoPitchLimit.class).getState()) {
                callbackInfo.cancel();

                float f = this.rotationPitch;
                float f1 = this.rotationYaw;
                this.rotationYaw = (float) ((double) this.rotationYaw + (double) yaw * 0.15D);
                this.rotationPitch = (float) ((double) this.rotationPitch - (double) pitch * 0.15D);
                this.prevRotationPitch += this.rotationPitch - f;
                this.prevRotationYaw += this.rotationYaw - f1;
            }
        }
    */
    @Inject(method = "moveFlying", at = @At("HEAD"), cancellable = true)
    private void handleRotations(float strafe, float forward, float friction, final CallbackInfo callbackInfo) {
        if ((Object) this != Minecraft.getMinecraft().thePlayer)
            return;

        final StrafeEvent strafeEvent = new StrafeEvent(strafe, forward, friction);
        LiquidBounce.eventManager.callEvent(strafeEvent);

        if (strafeEvent.isCancelled())
            callbackInfo.cancel();
    }

    @Redirect(method = "getBrightnessForRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockLoaded(Lnet/minecraft/util/BlockPos;)Z"))
    public boolean alwaysReturnTrue(World world, BlockPos pos) {
        return true;
    }

    @Inject(method = "spawnRunningParticles", at = @At("HEAD"), cancellable = true)
    private void checkGroundState(CallbackInfo ci) {
        if (!this.onGround) ci.cancel();
    }

    /**
     * @author asbyth
     * @reason Faster capability check
     */
    @Overwrite(remap = false)
    public boolean hasCapability(Capability<?> capability, EnumFacing direction) {
        return this.capabilities != null && this.capabilities.hasCapability(capability, direction);
    }
}