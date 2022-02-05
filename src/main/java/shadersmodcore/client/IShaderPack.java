package shadersmodcore.client;

import java.io.InputStream;

public interface IShaderPack {
    void close();

    InputStream getResourceAsStream(String var1);
}
