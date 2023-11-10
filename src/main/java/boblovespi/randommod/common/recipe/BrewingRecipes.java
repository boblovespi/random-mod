package boblovespi.randommod.common.recipe;

import boblovespi.randommod.RandomMod;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;

import static net.minecraft.recipe.BrewingRecipeRegistry.registerPotionRecipe;

public class BrewingRecipes
{
	public static void addRecipes()
	{
		// levitation
		registerPotionRecipe(Potions.SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, RandomMod.LEVITATION);
		registerPotionRecipe(Potions.LONG_SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, RandomMod.LONG_LEVITATION);
		registerPotionRecipe(RandomMod.LEVITATION, Items.REDSTONE, RandomMod.LONG_LEVITATION);
		registerPotionRecipe(RandomMod.LEVITATION, Items.GLOWSTONE_DUST, RandomMod.STRONG_LEVITATION);

		// nausea
		registerPotionRecipe(Potions.WATER_BREATHING, Items.FERMENTED_SPIDER_EYE, RandomMod.NAUSEA);
		registerPotionRecipe(Potions.LONG_WATER_BREATHING, Items.FERMENTED_SPIDER_EYE, RandomMod.LONG_NAUSEA);
		registerPotionRecipe(RandomMod.NAUSEA, Items.REDSTONE, RandomMod.LONG_NAUSEA);

		// glowing
		registerPotionRecipe(Potions.THICK, RandomMod.GLEAMING_BERRIES, RandomMod.GLOWING);
		registerPotionRecipe(RandomMod.GLOWING, Items.REDSTONE, RandomMod.LONG_GLOWING);
	}
}
