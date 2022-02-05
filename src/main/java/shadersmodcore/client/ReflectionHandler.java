package shadersmodcore.client;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.WeakHashMap;

public final class ReflectionHandler {
    private static final Map<Tessellator, Method> tessResetMethods = new WeakHashMap<>();
    private static final Map<EntityRenderer, Method> entRenderHandMethods = new WeakHashMap<>();
    private static final Map<TextureMap, Method> texMapCompResLocMethods = new WeakHashMap<>();
    private static final Map<ITextureObject, Method> getMultiTextIDMethods = new WeakHashMap<>();

    private static Field byteBufferField;
    private static Field intBufferField;
    private static Field floatBufferField;
    private static Field shortBufferField;

    private ReflectionHandler() {
    }

    // region Vanilla
    public static ByteBuffer getTessellatorByteBuffer() {
        if (byteBufferField == null)
            byteBufferField = ReflectionHelper.findField(Tessellator.class, "byteBuffer", "field_78394_d");
        try {
            return (ByteBuffer) byteBufferField.get(Tessellator.instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static IntBuffer getTessellatorIntBuffer() {
        if (intBufferField == null)
            intBufferField = ReflectionHelper.findField(Tessellator.class, "intBuffer", "field_147568_c");
        try {
            return (IntBuffer) intBufferField.get(Tessellator.instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static FloatBuffer getTessellatorFloatBuffer() {
        if (floatBufferField == null)
            floatBufferField = ReflectionHelper.findField(Tessellator.class, "floatBuffer", "field_147566_d");
        try {
            return (FloatBuffer) floatBufferField.get(Tessellator.instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ShortBuffer getTessellatorShortBuffer() {
        if (shortBufferField == null)
            shortBufferField = ReflectionHelper.findField(Tessellator.class, "shortBuffer", "field_147567_e");
        try {
            return (ShortBuffer) shortBufferField.get(Tessellator.instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void reset(Tessellator tessellator) {
        try {
            tessResetMethods.computeIfAbsent(tessellator, tessellator1 -> ReflectionHelper.findMethod(Tessellator.class, tessellator1, new String[]{"reset", "func_78379_d"})).invoke(tessellator);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void renderHand(EntityRenderer entityRenderer, float par1, int par2) {
        try {
            entRenderHandMethods.computeIfAbsent(entityRenderer, entityRenderer1 -> ReflectionHelper.findMethod(EntityRenderer.class, entityRenderer1, new String[]{"renderHand", "func_78476_b"}, float.class, int.class)).invoke(entityRenderer, par1, par2);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    // endregion

    public static ResourceLocation completeResourceLocation(TextureMap textureMap, ResourceLocation resourceLocation, int mipMapLevel) {
        try {
            return (ResourceLocation) texMapCompResLocMethods.computeIfAbsent(textureMap, textureMap1 -> ReflectionHelper.findMethod(TextureMap.class, textureMap1, new String[]{"completeResourceLocation", "func_147634_a"}, ResourceLocation.class, int.class)).invoke(textureMap, resourceLocation, mipMapLevel);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // region Transformed
    public static MultiTexID getMultiTexIDByMethod(ITextureObject texture) {
        try {
            return (MultiTexID) getMultiTextIDMethods.computeIfAbsent(texture, texture1 -> ReflectionHelper.findMethod(ITextureObject.class, texture1, new String[]{"getMultiTexID"})).invoke(texture);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultiTexID getMultiTexIDByField(AbstractTexture texture) {
        return ReflectionHelper.getPrivateValue(AbstractTexture.class, texture, "multiTex");
    }

    public static void setMultiTex(AbstractTexture texture, MultiTexID multiTexID) {
        ReflectionHelper.setPrivateValue(AbstractTexture.class, texture, multiTexID, "multiTex");
    }

    public static void resetDisplayList(ModelRenderer modelRenderer) {
        Method method = ReflectionHelper.findMethod(ModelRenderer.class, modelRenderer, new String[]{"resetDisplayList"});
        try {
            method.invoke(modelRenderer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static ShadersTess getShadersTess(Tessellator tessellator) {
        return ReflectionHelper.getPrivateValue(Tessellator.class, tessellator, "shadersTess");
    }

    public static int getAtlasWidth(TextureMap textureMap) {
        return ReflectionHelper.getPrivateValue(TextureMap.class, textureMap, "atlasWidth");
    }

    public static void setAtlasWidth(TextureMap textureMap, int atlasWidth) {
        ReflectionHelper.setPrivateValue(TextureMap.class, textureMap, atlasWidth, "atlasWidth");
    }

    public static int getAtlasHeight(TextureMap textureMap) {
        return ReflectionHelper.getPrivateValue(TextureMap.class, textureMap, "atlasHeight");
    }

    public static void setAtlasHeight(TextureMap textureMap, int atlasHeight) {
        ReflectionHelper.setPrivateValue(TextureMap.class, textureMap, atlasHeight, "atlasHeight");
    }
    // endregion
}
