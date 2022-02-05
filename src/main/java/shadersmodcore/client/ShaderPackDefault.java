package shadersmodcore.client;

import java.io.InputStream;

public class ShaderPackDefault implements IShaderPack {
    @Override
    public void close() {
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        return ShaderPackDefault.class.getResourceAsStream(resName);
    }
}
