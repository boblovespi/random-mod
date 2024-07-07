package boblovespi.randommod.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BasketDryingRecipeSerializer implements RecipeSerializer<BasketDryingRecipe>
{
	@Override
	public BasketDryingRecipe read(Identifier id, JsonObject json)
	{
		var inputJson = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
		var input = Ingredient.method_8102(inputJson, false);
		var resultName = new Identifier(JsonHelper.getString(json, "result"));
		var result = new ItemStack(
				Registries.ITEM.getOrEmpty(resultName).orElseThrow(() -> new IllegalStateException("Item: " + resultName + " does not exist")));
		var dryingTime = JsonHelper.getInt(json, "dryingTime", 20 * 10);
		return new BasketDryingRecipe(id, input, result, dryingTime);
	}

	@Override
	public BasketDryingRecipe read(Identifier id, PacketByteBuf buf)
	{
		var input = Ingredient.fromPacket(buf);
		var result = buf.readItemStack();
		var dryingTime = buf.readInt();
		return new BasketDryingRecipe(id, input, result, dryingTime);
	}

	@Override
	public void write(PacketByteBuf buf, BasketDryingRecipe recipe)
	{
		recipe.getInput().write(buf);
		buf.writeItemStack(recipe.getResult(null));
		buf.writeInt(recipe.getDryingTime());
	}
}
