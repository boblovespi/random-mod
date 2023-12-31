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
		ShapedRecipeJsonFactory.create(RecipeCategory.BUILDING_BLOCKS, RandomMod.REINFORCED_GLASS, 2)
							   .ingredient('G', Blocks.GLASS)
							   .ingredient('S', RandomMod.PURE_QUARTZ_SHARD)
							   .pattern(" S ")
							   .pattern("SGS")
							   .pattern(" S ")
							   .criterion("has_pure_quartz", conditionsFromItem(RandomMod.PURE_QUARTZ_SHARD))
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

		// we are a furniture mod
		ShapedRecipeJsonFactory.create(RecipeCategory.DECORATIONS, RandomMod.COPPER_SINK)
							   .ingredient('c', Blocks.CALCITE)
							   .ingredient('l', Blocks.STRIPPED_DARK_OAK_LOG)
							   .ingredient('*', Items.COPPER_INGOT)
							   .ingredient('#', Blocks.COPPER_BLOCK)
							   .pattern("c*c")
							   .pattern("l#l")
							   .pattern("lll")
							   .criterion("has_copper", conditionsFromItem(Items.COPPER_INGOT))
							   .offerTo(exporter);

		ShapedRecipeJsonFactory.create(RecipeCategory.BREWING, RandomMod.GLEAMING_BERRIES)
							   .ingredient('n', Items.GOLD_NUGGET)
							   .ingredient('b', Items.GLOW_BERRIES)
							   .pattern("nnn")
							   .pattern("nbn")
							   .pattern("nnn")
							   .criterion("has_glow_berries", conditionsFromItem(Items.GLOW_BERRIES))
							   .offerTo(exporter);
	}
}
