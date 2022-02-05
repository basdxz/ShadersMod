package shadersmodcore.client;

import java.io.InputStream;

public class ShaderPackNone implements IShaderPack {
    @Override
    public void close() {
    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        return null;
    }
}
