package shadersmodcore.client;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.WeakHashMap;

public final class ReflectionHandler {
    private ReflectionHandler() {
    }

    private static final Map<ITextureObject, Method> cachedMethods = new WeakHashMap<>();

    // region Vanilla
    public static ByteBuffer getTessellatorByteBuffer() {
        //System.out.println("getTessellatorByteBuffer");
        return ReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, "byteBuffer", "field_78394_d");
    }

    public static IntBuffer getTessellatorIntBuffer() {
        //System.out.println("getTessellatorIntBuffer");
        return ReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, "intBuffer", "field_147568_c");
    }

    public static FloatBuffer getTessellatorFloatBuffer() {
        //System.out.println("getTessellatorFloatBuffer");
        return ReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, "floatBuffer", "field_147566_d");
    }

    public static ShortBuffer getTessellatorShortBuffer() {
        //System.out.println("getTessellatorShortBuffer");
        return ReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, "shortBuffer", "field_147567_e");
    }

    public static void reset(Tessellator tessellator) {
        //System.out.println("reset");
        Method method = ReflectionHelper.findMethod(Tessellator.class, tessellator, new String[]{"reset", "func_78379_d"});
        try {
            method.invoke(tessellator);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void renderHand(EntityRenderer er, float par1, int par2) {
        //System.out.println("renderHand");
        Method method = ReflectionHelper.findMethod(EntityRenderer.class, er, new String[]{"renderHand", "func_78476_b"}, float.class, int.class);
        try {
            method.invoke(er, par1, par2);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    // endregion

    public static ResourceLocation completeResourceLocation(TextureMap textureMap, ResourceLocation resourceLocation, int mipMapLevel) {
        //System.out.println("completeResourceLocation");
        Method method = ReflectionHelper.findMethod(TextureMap.class, textureMap, new String[]{"completeResourceLocation", "func_147634_a"}, ResourceLocation.class, int.class);
        try {
            return (ResourceLocation) method.invoke(textureMap, resourceLocation, mipMapLevel);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // region Transformed
    public static MultiTexID getMultiTexID(DynamicTexture texture) {
        return getMultiTexID((ITextureObject) texture);
    }

    public static MultiTexID getMultiTexID(ITextureObject texture) {
        Method method = cachedMethods.computeIfAbsent(texture, texture1 -> {
            //System.out.println("getMultiTexID");
            return ReflectionHelper.findMethod(ITextureObject.class, texture1, new String[]{"getMultiTexID"});
        });

        try {
            return (MultiTexID) method.invoke(texture);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultiTexID getMultiTexID(AbstractTexture texture) {
        //System.out.println("getMultiTexID");
        return ReflectionHelper.getPrivateValue(AbstractTexture.class, texture, "multiTex");
    }

    public static void setMultiTex(AbstractTexture texture, MultiTexID multiTexID) {
        //System.out.println("setMultiTex");
        ReflectionHelper.setPrivateValue(AbstractTexture.class, texture, multiTexID, "multiTex");
    }

    public static void resetDisplayList(ModelRenderer modelRenderer) {
        //System.out.println("resetDisplayList");
        Method method = ReflectionHelper.findMethod(ModelRenderer.class, modelRenderer, new String[]{"resetDisplayList"});
        try {
            method.invoke(modelRenderer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static ShadersTess getShadersTess(Tessellator tessellator) {
        //System.out.println("getShadersTess");
        return ReflectionHelper.getPrivateValue(Tessellator.class, tessellator, "shadersTess");
    }

    public static int getAtlasWidth(TextureMap textureMap) {
        //System.out.println("getAtlasWidth");
        return ReflectionHelper.getPrivateValue(TextureMap.class, textureMap, "atlasWidth");
    }

    public static void setAtlasWidth(TextureMap textureMap, int atlasWidth) {
        //System.out.println("setAtlasWidth");
        ReflectionHelper.setPrivateValue(TextureMap.class, textureMap, atlasWidth, "atlasWidth");
    }

    public static int getAtlasHeight(TextureMap textureMap) {
        //System.out.println("getAtlasHeight");
        return ReflectionHelper.getPrivateValue(TextureMap.class, textureMap, "atlasHeight");
    }

    public static void setAtlasHeight(TextureMap textureMap, int atlasHeight) {
        //System.out.println("setAtlasHeight");
        ReflectionHelper.setPrivateValue(TextureMap.class, textureMap, atlasHeight, "atlasHeight");
    }
    // endregion
}
