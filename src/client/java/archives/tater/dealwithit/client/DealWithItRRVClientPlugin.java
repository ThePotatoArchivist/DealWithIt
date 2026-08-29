package archives.tater.dealwithit.client;

import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static cc.cassian.rrv.common.recipe.ItemViewRecipes.CHECKS;

public class DealWithItRRVClientPlugin implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        ItemView.excludeItemStack(new ItemStackTemplate(DealWithItItems.CARD));
        ItemView.excludeItemStack(new ItemStackTemplate(DealWithItItems.CARD_BOX));
        ItemView.addItemCheck(DealWithItComponents.CARD);
        CHECKS.add((stack1, stack2) -> Objects.equals(getDeck(stack1), getDeck(stack2)));
    }

    private static @Nullable Holder<Deck> getDeck(ItemStack stack) {
        var contents = stack.get(DealWithItComponents.DECK_CONTENTS);
        return contents == null ? null : contents.deck();
    }
}
