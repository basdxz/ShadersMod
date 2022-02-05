package shadersmodcore.client;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ShaderPackZip implements IShaderPack {
    protected File packFile;
    protected ZipFile packZipFile;

    public ShaderPackZip(String name, File file) {
        this.packFile = file;
        this.packZipFile = null;
    }

    @Override
    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            } catch (Exception var2) {
            }

            this.packZipFile = null;
        }

    }

    @Override
    public InputStream getResourceAsStream(String resName) {
        if (this.packZipFile == null) {
            try {
                this.packZipFile = new ZipFile(this.packFile);
            } catch (Exception var4) {
            }
        }

        if (this.packZipFile != null) {
            try {
                ZipEntry entry = this.packZipFile.getEntry(resName.substring(1));
                if (entry != null) {
                    return this.packZipFile.getInputStream(entry);
                }
            } catch (Exception var3) {
            }
        }

        return null;
    }
}
