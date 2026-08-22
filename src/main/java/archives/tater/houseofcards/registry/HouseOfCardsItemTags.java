package archives.tater.houseofcards.registry;

import archives.tater.houseofcards.HouseOfCards;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface HouseOfCardsItemTags {

    TagKey<Item> FLIPPABLE = create("flippable");

    private static TagKey<Item> create(String path) {
        return TagKey.create(Registries.ITEM, HouseOfCards.id(path));
    }
}
