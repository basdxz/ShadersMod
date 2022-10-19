# ShadersMod

This is an old, unmaintained Forge mod for 1.7.10 which adds external GLSL support for Minecraft.

The [source code](https://www.dropbox.com/s/n0s6aitufwdhrdj/smc-2.3.18-mc1.7.10-src.7z) was released in
a [Minecraft Forums Post](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1286604-shaders-mod-updated-by-karyonix?page=285)
.
An Open Licence was found inside `java/shadersmodcore/client/Shaders.java` from the released source code. id_miner and
karyonix have been mentioned in the included MIT licence.

However, the provided sources were a few versions older than the latest release, and not known about at the start of the
project.
As such, I decompiled
the [last jar](https://web.archive.org/web/20151126055520/http://www.karyonix.net:80/shadersmod/files/ShadersModCore-v2.3.31-mc1.7.10-f.jar)
I could find from the way back machine and fixed any issues I could find.

This version of shader mod is behind the
latest [1.7.10 OptiFine release](https://optifine.net/adloadx?f=OptiFine_1.7.10_HD_U_E7.jar), for which I am unsure the
exact version of.
Some older versions of BSL shaders were found to work correctly with it outside of dev too.

As far as updating and maintaining this mod goes, I don't have any plans for it. Anyone is more than welcome to pick it up and make it their own.

If I had to work on it, this is what I would do:
- Parity with [OptiFine's](https://optifine.net/adloadx?f=OptiFine_1.7.10_HD_U_E7.jar) built-in shader mod
- Converting the legacy ASM into modern 1.7.10 [Mixins](https://github.com/FalsePattern/GasStation)
- Compatibility with modern performance mods like [FalseTweaks](https://github.com/FalsePattern/FalseTweaks)
  and [Neodymium](https://github.com/makamys/Neodymium).
- Creation of an external API, perhaps additional GLSL uniforms?
