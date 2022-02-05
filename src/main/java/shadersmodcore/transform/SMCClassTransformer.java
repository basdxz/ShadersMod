package shadersmodcore.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.HashMap;
import java.util.Map;

public class SMCClassTransformer implements IClassTransformer {
   Names names = new Names();
   protected Map ctMap;

   public void put(Names.Clas clas, IClassTransformer ct) {
      this.ctMap.put(clas.clas.replace('/', '.'), ct);
   }

   public SMCClassTransformer() {
      InitNames.init();
      this.ctMap = new HashMap();
      this.put(Names.block_, new SMCCTBlock());
      this.put(Names.itemBlock_, new SMCCTItemBlock());
      this.put(Names.minecraft_, new SMCCTMinecraft());
      this.put(Names.guiOptions_, new SMCCTGuiOptions());
      this.put(Names.modelRenderer_, new SMCCTModelRenderer());
      this.put(Names.openGlHelper_, new SMCCTOpenGlHelper());
      this.put(Names.tessellator_, new SMCCTTessellator());
      this.put(Names.renderBlocks_, new SMCCTRenderBlocks());
      this.put(Names.renderGlobal_, new SMCCTRenderGlobal());
      this.put(Names.entityRenderer_, new SMCCTEntityRenderer());
      this.put(Names.render_, new SMCCTRender());
      this.put(Names.renderManager_, new SMCCTRenderManager());
      this.put(Names.rendererLivingE_, new SMCCTRendererLivingEntity());
      this.put(Names.renderDragon_, new SMCCTRenderSpider());
      this.put(Names.renderEnderman_, new SMCCTRenderSpider());
      this.put(Names.renderSpider_, new SMCCTRenderSpider());
      this.put(Names.renderItemFrame_, new SMCCTRenderItemFrame());
      this.put(Names.itemRenderer_, new SMCCTItemRenderer());
      this.put(Names.textureDownload_, new SMCCTTextureDownload());
      this.put(Names.abstractTexture_, new SMCCTTextureAbstract());
      this.put(Names.iTextureObject_, new SMCCTTextureObject());
      this.put(Names.simpleTexture_, new SMCCTTextureSimple());
      this.put(Names.layeredTexture_, new SMCCTTextureLayered());
      this.put(Names.dynamicTexture_, new SMCCTTextureDynamic());
      this.put(Names.textureMap_, new SMCCTTextureMap());
      this.put(Names.textureAtlasSpri_, new SMCCTTextureAtlasSprite());
      this.put(Names.textureClock_, new SMCCTTextureClock());
      this.put(Names.textureCompass_, new SMCCTTextureCompass());
      this.put(Names.textureManager_, new SMCCTTextureManager());
      this.ctMap.put("mrtjp.projectred.illumination.RenderHalo$", new SMCCTPrjRedIlluRenderHalo());
      this.ctMap.put("mrtjp.projectred.core.RenderHalo$", new SMCCTPrjRedIlluRenderHalo());
      this.ctMap.put("net.smart.render.ModelRotationRenderer", new SMCCTSmartMoveModelRotationRenderer());
   }

   public byte[] transform(String par1, String par2, byte[] par3) {
      byte[] bytecode = par3;
      IClassTransformer ct = (IClassTransformer)this.ctMap.get(par2);
      if (ct != null) {
         bytecode = ct.transform(par1, par2, par3);
         int len1 = par3.length;
         int len2 = bytecode.length;
         SMCLog.fine(" %d (%+d)", len2, len2 - len1);
      }

      return bytecode;
   }
}
