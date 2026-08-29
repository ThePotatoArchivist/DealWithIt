package archives.tater.dealwithit.client;

import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import net.minecraft.world.item.ItemStackTemplate;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;

public class DealWithItRRVClientPlugin implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        ItemView.excludeItemStack(new ItemStackTemplate(DealWithItItems.CARD));
        ItemView.excludeItemStack(new ItemStackTemplate(DealWithItItems.CARD_BOX));
        ItemView.addItemCheck(DealWithItComponents.CARD);
        ItemView.addItemCheck(DealWithItComponents.DECK_CONTENTS);
    }
}
