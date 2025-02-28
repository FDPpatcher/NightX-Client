package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.modules.misc.Annoy;
import net.ccbluex.liquidbounce.features.module.modules.render.Rotate;
import net.ccbluex.liquidbounce.features.module.modules.render.SilentView;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

    @Shadow
    public ModelRenderer bipedRightArm;

    @Shadow
    public int heldItemRight;

    @Shadow
    public ModelRenderer bipedHead;

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void revertSwordAnimation(float p_setRotationAngles_1_, float p_setRotationAngles_2_, float p_setRotationAngles_3_, float p_setRotationAngles_4_, float p_setRotationAngles_5_, float p_setRotationAngles_6_, Entity p_setRotationAngles_7_, CallbackInfo callbackInfo) {
        if (heldItemRight == 3)
            this.bipedRightArm.rotateAngleY = 0F;

        if (p_setRotationAngles_7_ instanceof EntityPlayer && p_setRotationAngles_7_.equals(Minecraft.getMinecraft().thePlayer)) {
            final SilentView silentView = LiquidBounce.moduleManager.getModule(SilentView.class);
            final Rotate spinBot = LiquidBounce.moduleManager.getModule(Rotate.class);
            final KillAura killAura = LiquidBounce.moduleManager.getModule(KillAura.class);
            final Scaffold scaffold = LiquidBounce.moduleManager.getModule(Scaffold.class);
            final Annoy annoy = LiquidBounce.moduleManager.getModule(Annoy.class);
            if (spinBot.getState() && !spinBot.getPitchMode().get().equalsIgnoreCase("none"))
                this.bipedHead.rotateAngleX = spinBot.getPitch() / (180F / (float) Math.PI);
            if (silentView.getState() && silentView.getMode().get().equals("Normal") && killAura.getTarget() != null) {
                this.bipedHead.rotateAngleX = RotationUtils.serverRotation.getPitch() / (220F / (float) Math.PI);
            }
            if (silentView.getState() && silentView.getMode().get().equals("Normal") && scaffold.getState()) {
                this.bipedHead.rotateAngleX = RotationUtils.serverRotation.getPitch() / (220F / (float) Math.PI);
            }
            if (silentView.getState() && silentView.getMode().get().equals("Normal") && annoy.getState()) {
                this.bipedHead.rotateAngleX = RotationUtils.serverRotation.getPitch() / (220F / (float) Math.PI);
            }
        }
    }
}