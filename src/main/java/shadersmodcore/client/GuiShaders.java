package shadersmodcore.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.Util;
import org.lwjgl.Sys;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class GuiShaders extends GuiScreen {
    protected GuiScreen parentGui;
    private int updateTimer = -1;
    public boolean needReinit;
    private GuiSlotShaders shaderList;

    public GuiShaders(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
        this.parentGui = par1GuiScreen;
    }

    private static String toStringOnOff(boolean value) {
        return value ? "On" : "Off";
    }

    @Override
    public void initGui() {
        if (Shaders.shadersConfig == null) {
            Shaders.loadConfig();
        }

        List buttonList = this.buttonList;
        int width = this.width;
        int height = this.height;
        buttonList.add(new GuiButton(17, width * 3 / 4 - 60, 30, 160, 18, "NormapMap: " + toStringOnOff(Shaders.configNormalMap)));
        buttonList.add(new GuiButton(18, width * 3 / 4 - 60, 50, 160, 18, "SpecularMap: " + toStringOnOff(Shaders.configSpecularMap)));
        buttonList.add(new GuiButton(15, width * 3 / 4 - 60, 70, 160, 18, "RenderResMul: " + String.format("%.04f", Shaders.configRenderResMul)));
        buttonList.add(new GuiButton(16, width * 3 / 4 - 60, 90, 160, 18, "ShadowResMul: " + String.format("%.04f", Shaders.configShadowResMul)));
        buttonList.add(new GuiButton(10, width * 3 / 4 - 60, 110, 160, 18, "HandDepth: " + String.format("%.04f", Shaders.configHandDepthMul)));
        buttonList.add(new GuiButton(9, width * 3 / 4 - 60, 130, 160, 18, "CloudShadow: " + toStringOnOff(Shaders.configCloudShadow)));
        buttonList.add(new GuiButton(4, width * 3 / 4 - 60, 170, 160, 18, "tweakBlockDamage: " + toStringOnOff(Shaders.configTweakBlockDamage)));
        buttonList.add(new GuiButton(19, width * 3 / 4 - 60, 190, 160, 18, "OldLighting: " + toStringOnOff(Shaders.configOldLighting)));
        buttonList.add(new GuiButton(6, width * 3 / 4 - 60, height - 25, 160, 20, "Done"));
        buttonList.add(new GuiButton(5, width / 4 - 80, height - 25, 160, 20, "Open shaderpacks folder"));
        this.shaderList = new GuiSlotShaders(this);
        this.shaderList.registerScrollButtons(7, 8);
        this.needReinit = false;
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            switch (par1GuiButton.id) {
                case 4:
                    Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                    par1GuiButton.displayString = "tweakBlockDamage: " + toStringOnOff(Shaders.configTweakBlockDamage);
                    break;
                case 5:
                    switch (Util.getOSType()) {
                        case OSX:
                            try {
                                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath()});
                                return;
                            } catch (IOException var81) {
                                var81.printStackTrace();
                                break;
                            }
                        case WINDOWS:
                            String var2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderpacksdir.getAbsolutePath());

                            try {
                                Runtime.getRuntime().exec(var2);
                                return;
                            } catch (IOException var7) {
                                var7.printStackTrace();
                            }
                    }

                    boolean var8 = false;

                    try {
                        Class var3 = Class.forName("java.awt.Desktop");
                        Object var4 = var3.getMethod("getDesktop").invoke(null);
                        var3.getMethod("browse", URI.class).invoke(var4, (new File(this.mc.mcDataDir, Shaders.shaderpacksdirname)).toURI());
                    } catch (Throwable var6) {
                        var6.printStackTrace();
                        var8 = true;
                    }

                    if (var8) {
                        System.out.println("Opening via system class!");
                        Sys.openURL("file://" + Shaders.shaderpacksdir.getAbsolutePath());
                    }
                    break;
                case 6:
                    new File(Shaders.shadersdir, "current.cfg");

                    try {
                        Shaders.storeConfig();
                    } catch (Exception var5) {
                    }

                    if (this.needReinit) {
                        this.needReinit = false;
                        Shaders.loadShaderPack();
                        Shaders.uninit();
                        this.mc.renderGlobal.loadRenderers();
                    }

                    this.mc.displayGuiScreen(this.parentGui);
                    break;
                case 7:
                case 8:
                default:
                    this.shaderList.actionPerformed(par1GuiButton);
                    break;
                case 9:
                    Shaders.configCloudShadow = !Shaders.configCloudShadow;
                    par1GuiButton.displayString = "CloudShadow: " + toStringOnOff(Shaders.configCloudShadow);
                    break;
                case 10: {
                    float val = Shaders.configHandDepthMul;
                    float[] choices = new float[]{0.0625F, 0.125F, 0.25F, 0.5F, 1.0F};
                    int i;
                    if (!isShiftKeyDown()) {
                        i = 0;

                        while (i < choices.length && choices[i] <= val) {
                            ++i;
                        }

                        if (i == choices.length) {
                            i = 0;
                        }
                    } else {
                        i = choices.length - 1;

                        while (i >= 0 && val <= choices[i]) {
                            --i;
                        }

                        if (i < 0) {
                            i = choices.length - 1;
                        }
                    }

                    Shaders.configHandDepthMul = choices[i];
                    par1GuiButton.displayString = "HandDepth: " + String.format("%.4f", Shaders.configHandDepthMul);
                    break;
                }
                case 11:
                    Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                    Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
                    par1GuiButton.displayString = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                    ShadersTex.updateTextureMinMagFilter();
                    break;
                case 12:
                    Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                    par1GuiButton.displayString = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
                    ShadersTex.updateTextureMinMagFilter();
                    break;
                case 13:
                    Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                    par1GuiButton.displayString = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
                    ShadersTex.updateTextureMinMagFilter();
                    break;
                case 14:
                    Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                    par1GuiButton.displayString = "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum);
                    ShadersTex.updateTextureMinMagFilter();
                    break;
                case 15: {
                    float val = Shaders.configRenderResMul;
                    float[] choices = new float[]{0.25F, 0.33333334F, 0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F};
                    int i;
                    if (!isShiftKeyDown()) {
                        i = 0;

                        while (i < choices.length && choices[i] <= val) {
                            ++i;
                        }

                        if (i == choices.length) {
                            i = 0;
                        }
                    } else {
                        i = choices.length - 1;

                        while (i >= 0 && val <= choices[i]) {
                            --i;
                        }

                        if (i < 0) {
                            i = choices.length - 1;
                        }
                    }

                    Shaders.configRenderResMul = choices[i];
                    par1GuiButton.displayString = "RenderResMul: " + String.format("%.4f", Shaders.configRenderResMul);
                    Shaders.scheduleResize();
                    break;
                }
                case 16:
                    float val = Shaders.configShadowResMul;
                    float[] choices = new float[]{0.25F, 0.33333334F, 0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F, 3.0F, 4.0F};
                    int i;
                    if (!isShiftKeyDown()) {
                        i = 0;

                        while (i < choices.length && choices[i] <= val) {
                            ++i;
                        }

                        if (i == choices.length) {
                            i = 0;
                        }
                    } else {
                        i = choices.length - 1;

                        while (i >= 0 && val <= choices[i]) {
                            --i;
                        }

                        if (i < 0) {
                            i = choices.length - 1;
                        }
                    }

                    Shaders.configShadowResMul = choices[i];
                    par1GuiButton.displayString = "ShadowResMul: " + String.format("%.4f", Shaders.configShadowResMul);
                    Shaders.scheduleResizeShadow();
                    break;
                case 17:
                    Shaders.configNormalMap = !Shaders.configNormalMap;
                    par1GuiButton.displayString = "NormapMap: " + toStringOnOff(Shaders.configNormalMap);
                    this.mc.scheduleResourcesRefresh();
                    break;
                case 18:
                    Shaders.configSpecularMap = !Shaders.configSpecularMap;
                    par1GuiButton.displayString = "SpecularMap: " + toStringOnOff(Shaders.configSpecularMap);
                    this.mc.scheduleResourcesRefresh();
                    break;
                case 19:
                    Shaders.configOldLighting = !Shaders.configOldLighting;
                    par1GuiButton.displayString = "OldLighting: " + toStringOnOff(Shaders.configOldLighting);
                    Shaders.updateBlockLightLevel();
                    this.mc.renderGlobal.loadRenderers();
            }
        }

    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        this.shaderList.drawScreen(par1, par2, par3);
        if (this.updateTimer <= 0) {
            this.shaderList.updateList();
            this.updateTimer += 20;
        }

        this.drawCenteredString(this.fontRendererObj, "Shaders ", this.width / 2, 16, 16777215);
        this.drawCenteredString(this.fontRendererObj, " v2.3.31", this.width - 40, 10, 8421504);
        super.drawScreen(par1, par2, par3);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        --this.updateTimer;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public void drawCenteredString(String par1, int par2, int par3, int par4) {
        this.drawCenteredString(this.fontRendererObj, par1, par2, par3, par4);
    }
}
