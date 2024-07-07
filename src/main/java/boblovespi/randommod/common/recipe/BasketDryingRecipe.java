package boblovespi.randommod.common.recipe;

import boblovespi.randommod.RandomMod;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BasketDryingRecipe implements Recipe<Inventory>
{
	private final Identifier id;
	private final Ingredient input;
	private final ItemStack result;
	private final int dryingTime;

	public BasketDryingRecipe(Identifier id, Ingredient input, ItemStack result, int dryingTime)
	{
		this.id = id;
		this.input = input;
		this.result = result;
		this.dryingTime = dryingTime;
	}

	@Override
	public boolean matches(Inventory inventory, World world)
	{
		return input.test(inventory.getStack(0));
	}

	@Override
	public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager)
	{
		return result.copy();
	}

	@Override
	public boolean fits(int width, int height)
	{
		return true;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager registryManager)
	{
		return result;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients()
	{
		var ings = DefaultedList.<Ingredient>of();
		ings.add(input);
		return ings;
	}

	@Override
	public Identifier getId()
	{
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return RandomMod.BASKET_DRYING_RECIPE_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType()
	{
		return RandomMod.BASKET_DRYING_RECIPE;
	}

	@Override
	public ItemStack createIcon()
	{
		return new ItemStack(RandomMod.BAMBOO_BASKET);
	}

	public int getDryingTime()
	{
		return dryingTime;
	}

	public Ingredient getInput()
	{
		return input;
	}
}
