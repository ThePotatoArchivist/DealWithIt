package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.DealWithItAtlases;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.resources.model.sprite.AtlasManager.AtlasConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModAtlasProvider extends FabricCodecDataProvider<List<SpriteSource>> {

    public ModAtlasProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture, PackOutput.Target.RESOURCE_PACK, "atlases", SpriteSources.FILE_CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, List<SpriteSource>> provider, HolderLookup.Provider registryLookup) {
        accept(provider, DealWithItAtlases.CARDS, List.of(new DirectoryLister(DealWithIt.MOD_ID, DealWithIt.MOD_ID + "/")));
    }

    private static void accept(BiConsumer<Identifier, List<SpriteSource>> provider, AtlasConfig atlas, List<SpriteSource> sources) {
        provider.accept(atlas.definitionLocation(), sources);
    }

    @Override
    public String getName() {
        return "Atlases";
    }
}
