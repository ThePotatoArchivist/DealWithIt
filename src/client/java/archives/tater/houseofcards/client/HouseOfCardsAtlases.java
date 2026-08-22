package archives.tater.houseofcards.client;

import archives.tater.houseofcards.HouseOfCards;

import net.fabricmc.fabric.api.client.rendering.v1.AtlasRegistry;

import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.resources.model.sprite.AtlasManager.AtlasConfig;
import net.minecraft.resources.Identifier;

public interface HouseOfCardsAtlases {
    AtlasConfig CARD = register("card");
    AtlasConfig DECK_BACK = register("deck_back");

    SpriteMapper CARD_MAPPER = new SpriteMapper(CARD.textureId(), HouseOfCards.MOD_ID + "/card");
    SpriteMapper DECK_BACK_MAPPER = new SpriteMapper(CARD.textureId(), HouseOfCards.MOD_ID + "/deck_back");

    private static AtlasConfig register(Identifier id) {
        var config = new AtlasConfig(AtlasRegistry.generateTextureLocation(id), id, false);
        AtlasRegistry.register(config);
        return config;
    }

    private static AtlasConfig register(String path) {
        return register(HouseOfCards.id(path));
    }

    static void init() {

    }
}
