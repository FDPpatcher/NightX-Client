package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;


@Mixin(GuiButton.class)
public abstract class MixinGuiButton extends Gui {

    @Shadow
    @Final
    protected static ResourceLocation buttonTextures;
    private final float bright = 0F;
    @Shadow
    public boolean visible;
    @Shadow
    public int xPosition;
    @Shadow
    public int yPosition;
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    public boolean enabled;
    @Shadow
    public String displayString;
    @Shadow
    protected boolean hovered;
    private float moveX = 0F;
    private float cut;
    private float alpha;

    @Shadow
    protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

    @Shadow
    protected abstract int getHoverState(boolean mouseOver);

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            final FontRenderer fontRenderer =
                    mc.getLanguageManager().isCurrentLocaleUnicode() ? mc.fontRendererObj : Fonts.font40;
            hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition &&
                    mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);

            final int delta = RenderUtils.deltaTime;
            final float speedDelta = 0.01F * delta;

            final HUD hud = LiquidBounce.moduleManager.getModule(HUD.class);

            if (hud == null) return;

            if (enabled && hovered) {
                // LiquidBounce
                cut += 0.05F * delta;
                if (cut >= 4) cut = 4;
                alpha += 0.3F * delta;
                if (alpha >= 210) alpha = 210;

                // LiquidBounce+
                moveX = AnimationUtils.animate(this.width - 2.4F, moveX, speedDelta);
            } else {
                // LiquidBounce
                cut -= 0.05F * delta;
                if (cut <= 0) cut = 0;
                alpha -= 0.3F * delta;
                if (alpha <= 120) alpha = 120;

                // LiquidBounce+
                moveX = AnimationUtils.animate(0F, moveX, speedDelta);
            }

            float roundCorner = Math.max(0F, 2.4F + moveX - (this.width - 2.4F));

            switch (hud.getGuiButtonStyle().get().toLowerCase()) {
                case "minecraft":
                    mc.getTextureManager().bindTexture(buttonTextures);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                    int i = this.getHoverState(this.hovered);
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.blendFunc(770, 771);
                    this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height);
                    this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
                    this.mouseDragged(mc, mouseX, mouseY);
                    int j = 14737632;

                    if (!this.enabled) {
                        j = 10526880;
                    } else if (this.hovered) {
                        j = 16777120;
                    }

                    this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
                    break;
                case "liquidbounce":
                    Gui.drawRect(this.xPosition + (int) this.cut, this.yPosition,
                            this.xPosition + this.width - (int) this.cut, this.yPosition + this.height,
                            this.enabled ? new Color(0F, 0F, 0F, this.alpha / 255F).getRGB() :
                                    new Color(0.5F, 0.5F, 0.5F, 0.5F).getRGB());
                    break;
                case "rounded":
                    RenderUtils.originalRoundedRect(this.xPosition, this.yPosition,
                            this.xPosition + this.width, this.yPosition + this.height, 2F,
                            this.enabled ? new Color(0F, 0F, 0F, this.alpha / 255F).getRGB() :
                                    new Color(0.5F, 0.5F, 0.5F, 0.5F).getRGB());
                    break;
                case "liquidbounce+":
                    RenderUtils.drawRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 2.4F, new Color(0, 0, 0, 150).getRGB());
                    RenderUtils.customRounded(this.xPosition, this.yPosition, this.xPosition + 2.4F + moveX, this.yPosition + this.height, 2.4F, roundCorner, roundCorner, 2.4F, (this.enabled ? new Color(0, 111, 255) : new Color(71, 71, 71)).getRGB());
                    break;
            }

            if (hud.getGuiButtonStyle().get().equalsIgnoreCase("minecraft")) return;

            mc.getTextureManager().bindTexture(buttonTextures);
            mouseDragged(mc, mouseX, mouseY);

            AWTFontRenderer.Companion.setAssumeNonVolatile(true);

            fontRenderer.drawStringWithShadow(displayString,
                    (float) ((this.xPosition + this.width / 2) -
                            fontRenderer.getStringWidth(displayString) / 2),
                    this.yPosition + (this.height - 5) / 2F - 2, 14737632);

            AWTFontRenderer.Companion.setAssumeNonVolatile(false);

            GlStateManager.resetColor();
        }
    }
}