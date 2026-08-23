package archives.tater.dealwithit.item;

import archives.tater.dealwithit.registry.DealWithItComponents;

import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CardBoxItem extends Item {
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);

    public CardBoxItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        var contents = stack.get(DealWithItComponents.DECK_CONTENTS);
        return contents == null || !contents.isComplete();
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var contents = stack.get(DealWithItComponents.DECK_CONTENTS);
        if (contents == null) return 0;
        return contents.cardCount() == 0 ? 0 : 12 * contents.cardCount() / contents.deck().value().size() + 1;
    }
}
