package shadersmodcore.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;

public class DefaultTexture extends AbstractTexture {
    public DefaultTexture() {
        this.loadTexture(null);
    }

    @Override
    public void loadTexture(IResourceManager resourcemanager) {
        int[] aint = ShadersTex.createAIntImage(1, -1);
        ShadersTex.setupTexture(ReflectionHelper.getMultiTex(this), aint, 1, 1, false, false);
    }
}
