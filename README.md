# ShadersMod

ShadersMod is an old, unmaintained Forge mod for Minecraft 1.7.10 that adds external GLSL support.
The [source code](https://www.dropbox.com/s/n0s6aitufwdhrdj/smc-2.3.18-mc1.7.10-src.7z) was released in a [Minecraft Forums Post](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1286604-shaders-mod-updated-by-karyonix?page=285).
It includes an Open License, with id_miner and karyonix mentioned in the accompanying MIT license.

The provided sources were a few versions older than the latest release and not known at the project's inception.
Therefore, the [last jar](https://web.archive.org/web/20151126055520/http://www.karyonix.net:80/shadersmod/files/ShadersModCore-v2.3.31-mc1.7.10-f.jar) from the Wayback Machine was decompiled, and any issues were resolved.

This version of ShadersMod is behind the latest [1.7.10 OptiFine release](https://optifine.net/adloadx?f=OptiFine_1.7.10_HD_U_E7.jar).
Some older versions of BSL shaders have been confirmed to work correctly with it outside of development.

There are currently no plans to update or maintain this mod, but anyone is welcome to take over the project.

If future development were to occur, these goals might be considered:
- Achieve parity with [OptiFine's](https://optifine.net/adloadx?f=OptiFine_1.7.10_HD_U_E7.jar) built-in shader mod
- Convert the legacy ASM to modern 1.7.10 [Mixins](https://github.com/FalsePattern/GasStation)
- Ensure compatibility with modern performance mods like [FalseTweaks](https://github.com/FalsePattern/FalseTweaks) and [Neodymium](https://github.com/makamys/Neodymium)
- Develop an external API, potentially including additional GLSL uniforms
