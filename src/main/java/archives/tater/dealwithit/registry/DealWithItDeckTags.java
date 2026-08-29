package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.data.Deck;

import net.minecraft.tags.TagKey;

public interface DealWithItDeckTags {
    TagKey<Deck> CREATIVE_TAB_ORDER = create("creative_tab_order");

    private static TagKey<Deck> create(String path) {
        return TagKey.create(DealWithItRegistries.DECK, DealWithIt.id(path));
    }
}
