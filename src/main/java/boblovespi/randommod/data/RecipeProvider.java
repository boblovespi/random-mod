package boblovespi.randommod.data;

import boblovespi.randommod.RandomMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeCategory;

import java.util.function.Consumer;

public class RecipeProvider extends FabricRecipeProvider
{
	public RecipeProvider(FabricDataOutput output)
	{
		super(output);
	}

	@Override
	public void generateRecipes(Consumer<RecipeJsonProvider> exporter)
	{
		ShapedRecipeJsonFactory.create(RecipeCategory.TOOLS, RandomMod.DEPTH_METER)
							   .ingredient('#', Items.COPPER_INGOT)
							   .ingredient('X', Items.REDSTONE)
							   .pattern(" # ")
							   .pattern("#X#")
							   .pattern(" # ")
							   .criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
							   .offerTo(exporter);
		ShapedRecipeJsonFactory.create(RecipeCategory.MISC, RandomMod.QUARTZ_DISC)
							   .ingredient('#', RandomMod.PURE_QUARTZ_SHARD)
							   .ingredient('X', Items.NETHERITE_INGOT)
							   .pattern(" # ")
							   .pattern("#X#")
							   .pattern(" # ")
							   .criterion("has_netherite", conditionsFromItem(Items.NETHERITE_INGOT))
							   .offerTo(exporter);
		RecipesProvider.offerTwoByTwoCompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, RandomMod.PURE_QUARTZ_BLOCK.asItem(), RandomMod.PURE_QUARTZ_SHARD);
		ShapedRecipeJsonFactory.create(RecipeCategory.REDSTONE, RandomMod.REMEMBERER)
							   .ingredient('#', Blocks.REDSTONE_TORCH)
							   .ingredient('X', RandomMod.PURE_QUARTZ_SHARD)
							   .ingredient('I', Blocks.STONE)
							   .pattern(" # ")
							   .pattern("#X#")
							   .pattern("III")
							   .criterion("has_pure_quartz", conditionsFromItem(RandomMod.PURE_QUARTZ_SHARD))
							   .offerTo(exporter);
	}
}
