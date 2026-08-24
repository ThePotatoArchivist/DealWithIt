package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.DealWithItAtlases;
import archives.tater.dealwithit.client.atlas.CartesianComposite;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.resources.model.sprite.AtlasManager.AtlasConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class ModAtlasProvider extends FabricCodecDataProvider<List<SpriteSource>> {

    public ModAtlasProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture, PackOutput.Target.RESOURCE_PACK, "atlases", SpriteSources.FILE_CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, List<SpriteSource>> provider, HolderLookup.Provider registryLookup) {
        accept(provider, DealWithItAtlases.CARDS, List.of(
                new DirectoryLister(DealWithIt.MOD_ID + "/deck_back", DealWithIt.MOD_ID + "/deck_back/"),
                new DirectoryLister(DealWithIt.MOD_ID + "/card", DealWithIt.MOD_ID + "/card/"),
                compositeCards("black", "clubs", "spades"),
                compositeCards("red", "hearts", "diamonds"),
                new CartesianComposite(
                        DealWithIt.id(DealWithIt.MOD_ID + "/card/playing_cards/ace_"),
                        new CartesianComposite.Layer(DealWithIt.id(DealWithIt.MOD_ID + "/composite/playing_cards/base")),
                        new CartesianComposite.Layer(Map.of(
                                "clubs", DealWithIt.id(DealWithIt.MOD_ID + "/composite/playing_cards/ace_clubs"),
                                "spades", DealWithIt.id(DealWithIt.MOD_ID + "/composite/playing_cards/ace_spades"),
                                "hearts", DealWithIt.id(DealWithIt.MOD_ID + "/composite/playing_cards/ace_hearts"),
                                "diamonds", DealWithIt.id(DealWithIt.MOD_ID + "/composite/playing_cards/ace_diamonds")
                        ))
                )
        ));
    }

    private static CartesianComposite compositeCards(String color, String... suits) {
        return new CartesianComposite(
                DealWithIt.id(DealWithIt.MOD_ID + "/card/playing_cards/"),
                new CartesianComposite.Layer(DealWithIt.id(DealWithIt.MOD_ID + "/composite/playing_cards/base")),
                new CartesianComposite.Layer(Stream.of(
                        "two",
                        "three",
                        "four",
                        "five",
                        "six",
                        "seven",
                        "eight",
                        "nine",
                        "ten",
                        "jack",
                        "queen",
                        "king"
                ).collect(toMap(
                        identity(),
                        name -> DealWithIt.id(DealWithIt.MOD_ID + "/composite/playing_cards/" + color + "_" + name)
                ))),
                new CartesianComposite.Layer(Arrays.stream(suits).collect(toMap(
                        name -> "_" + name,
                        name -> DealWithIt.id(DealWithIt.MOD_ID + "/composite/playing_cards/" + name)
                )))
        );
    }

    private static void accept(BiConsumer<Identifier, List<SpriteSource>> provider, AtlasConfig atlas, List<SpriteSource> sources) {
        provider.accept(atlas.definitionLocation(), sources);
    }

    @Override
    public String getName() {
        return "Atlases";
    }
}
