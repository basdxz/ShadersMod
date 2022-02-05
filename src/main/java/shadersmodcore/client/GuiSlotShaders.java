package shadersmodcore.client;

import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;

class GuiSlotShaders extends GuiSlot {
    private ArrayList shaderslist;
    final GuiShaders shadersGui;

    public GuiSlotShaders(GuiShaders par1GuiShaders) {
        super(par1GuiShaders.getMc(), par1GuiShaders.width / 2 + 20, par1GuiShaders.height, 40, par1GuiShaders.height - 70, 16);
        this.shadersGui = par1GuiShaders;
        this.shaderslist = Shaders.listofShaders();
    }

    public void updateList() {
        this.shaderslist = Shaders.listofShaders();
    }

    protected int getSize() {
        return this.shaderslist.size();
    }

    protected void elementClicked(int par1, boolean par2, int par3, int par4) {
        Shaders.setShaderPack((String) this.shaderslist.get(par1));
        this.shadersGui.needReinit = false;
        Shaders.loadShaderPack();
        Shaders.uninit();
    }

    protected boolean isSelected(int par1) {
        return this.shaderslist.get(par1).equals(Shaders.currentshadername);
    }

    protected int getScrollBarX() {
        return this.width - 6;
    }

    protected int getContentHeight() {
        return this.getSize() * 18;
    }

    protected void drawBackground() {
    }

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5, int par6, int par7) {
        this.shadersGui.drawCenteredString((String) this.shaderslist.get(par1), this.shadersGui.width / 4 + 10, par3 + 1, 16777215);
    }
}
