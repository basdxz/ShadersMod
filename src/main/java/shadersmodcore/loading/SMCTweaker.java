package shadersmodcore.loading;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SMCTweaker implements ITweaker {
    public List args;
    public File gameDir;
    public File assetsDir;
    public String version;

    public void acceptOptions(List args, File gameDir, File assetsDir, String version) {
        this.args = args;
        this.gameDir = gameDir;
        this.assetsDir = assetsDir;
        this.version = version;
    }

    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader) {
        launchClassLoader.addTransformerExclusion("shadersmodcore.loading.");
        launchClassLoader.addTransformerExclusion("shadersmodcore.transform.");
        launchClassLoader.registerTransformer("shadersmodcore.transform.SMCClassTransformer");
    }

    public String[] getLaunchArguments() {
        ArrayList argumentList = (ArrayList) Launch.blackboard.get("ArgumentList");
        if (argumentList.isEmpty()) {
            new ArrayList();
            if (this.gameDir != null) {
                argumentList.add("--gameDir");
                argumentList.add(this.gameDir.getPath());
            }

            if (this.assetsDir != null) {
                argumentList.add("--assetsDir");
                argumentList.add(this.assetsDir.getPath());
            }

            argumentList.add("--version");
            argumentList.add(this.version);
            argumentList.addAll(this.args);
        }

        return new String[0];
    }

    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }
}
