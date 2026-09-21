package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.registry.DealWithItItems;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        return new RecipeProvider(registries, output) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.MISC, DealWithItItems.BLANK_CARD_BOX)
                        .pattern("###")
                        .pattern("#%#")
                        .pattern("###")
                        .define('#', Items.PAPER)
                        .define('%', Items.BARREL)
                        .unlockedBy(getHasName(Items.PAPER), has(Items.PAPER))
                        .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
