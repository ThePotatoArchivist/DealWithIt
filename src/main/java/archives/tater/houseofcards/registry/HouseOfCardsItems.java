package archives.tater.houseofcards.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface HouseOfCardsItems {
    private static Item register(ResourceKey<Item> id, Function<Item.Properties, Item> item, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, id, item.apply(properties.setId(id)));
    }

    private static Item register(ResourceKey<Item> id, Item.Properties properties) {
        return register(id, Item::new, properties);
    }

    Item CARD = register(HouseOfCardsItemIds.CARD, new Item.Properties().stacksTo(1));
    Item CARD_BOX = register(HouseOfCardsItemIds.CARD_BOX, new Item.Properties().stacksTo(1));

    static void init() {

    }
}
