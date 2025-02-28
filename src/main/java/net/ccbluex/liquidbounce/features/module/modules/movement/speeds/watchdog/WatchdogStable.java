package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.watchdog;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class WatchdogStable extends SpeedMode {

    public WatchdogStable() {
        super("WatchdogStable");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
        mc.timer.timerSpeed = 1F;
        final Speed speed = LiquidBounce.moduleManager.getModule(Speed.class);
        if (speed == null) return;

        if (MovementUtils.isMoving() && !(mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) && !mc.gameSettings.keyBindJump.isKeyDown()) {
            double moveSpeed = Math.max(MovementUtils.getBaseMoveSpeed() * speed.baseStrengthValue.get(), MovementUtils.getSpeed());

            if (mc.thePlayer.onGround) {
                if (speed.sendJumpValue.get()) mc.thePlayer.jump();
                if (speed.recalcValue.get())
                    moveSpeed = Math.max(MovementUtils.getBaseMoveSpeed() * speed.baseStrengthValue.get(), MovementUtils.getSpeed());
                event.setY(mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier((mc.thePlayer.isCollidedHorizontally ? 0.42 : speed.jumpYValue.get())));
                moveSpeed *= speed.moveSpeedValue.get();
            } else if (speed.glideStrengthValue.get() > 0 && event.getY() < 0) {
                event.setY(mc.thePlayer.motionY += speed.glideStrengthValue.get());
            }
        }
    }
}
