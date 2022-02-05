package shadersmodcore.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

public class ShadersRender {
    public static void setFrustrumPosition(Frustrum frustrum, double x, double y, double z) {
        frustrum.setPosition(x, y, z);
    }

    public static void clipRenderersByFrustrum(RenderGlobal renderGlobal, Frustrum frustrum, float par2) {
        Shaders.checkGLError("pre clip");
        if (!Shaders.isShadowPass) {
            WorldRenderer[] worldRenderers = renderGlobal.worldRenderers;

            for (int i = 0; i < worldRenderers.length; ++i) {
                if (!worldRenderers[i].skipAllRenderPasses()) {
                    worldRenderers[i].updateInFrustum(frustrum);
                }
            }
        } else {
            WorldRenderer[] worldRenderers = renderGlobal.worldRenderers;

            for (int i = 0; i < worldRenderers.length; ++i) {
                if (!worldRenderers[i].skipAllRenderPasses()) {
                    worldRenderers[i].isInFrustum = true;
                }
            }
        }

    }

    public static void renderHand0(EntityRenderer er, float par1, int par2) {
        if (!Shaders.isShadowPass) {
            Item item = Shaders.itemToRender != null ? Shaders.itemToRender.getItem() : null;
            Block block = item instanceof ItemBlock ? ((ItemBlock) item).field_150939_a : null;
            if (!(item instanceof ItemBlock) || !(block instanceof Block) || block.getRenderBlockPass() == 0) {
                Shaders.readCenterDepth();
                Shaders.beginHand();
                ReflectionHandler.renderHand(er, par1, par2);
                Shaders.endHand();
                Shaders.isHandRendered = true;
            }
        }

    }

    public static void renderHand1(EntityRenderer er, float par1, int par2) {
        if (!Shaders.isShadowPass && !Shaders.isHandRendered) {
            Shaders.readCenterDepth();
            GL11.glEnable(3042);
            Shaders.beginHand();
            ReflectionHandler.renderHand(er, par1, par2);
            Shaders.endHand();
            Shaders.isHandRendered = true;
        }

    }

    public static void renderItemFP(ItemRenderer itemRenderer, float par1) {
        GL11.glDepthMask(true);
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        IntBuffer drawBuffers = Shaders.activeDrawBuffers;
        Shaders.setDrawBuffers(Shaders.drawBuffersNone);
        Shaders.renderItemPass1DepthMask = true;
        itemRenderer.renderItemInFirstPerson(par1);
        Shaders.renderItemPass1DepthMask = false;
        Shaders.setDrawBuffers(drawBuffers);
        GL11.glPopMatrix();
        GL11.glDepthFunc(515);
        itemRenderer.renderItemInFirstPerson(par1);
    }

    public static void renderFPOverlay(EntityRenderer er, float par1, int par2) {
        if (!Shaders.isShadowPass) {
            Shaders.beginFPOverlay();
            ReflectionHandler.renderHand(er, par1, par2);
            Shaders.endFPOverlay();
        }

    }
}
