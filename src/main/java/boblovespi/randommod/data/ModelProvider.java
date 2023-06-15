package boblovespi.randommod.data;

import boblovespi.randommod.RandomMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.Models;

public class ModelProvider extends FabricModelProvider
{
	public ModelProvider(FabricDataOutput output)
	{
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
	{
		blockStateModelGenerator.registerSimpleCubeAll(RandomMod.PEGMATITE);
		blockStateModelGenerator.registerSimpleCubeAll(RandomMod.PURE_QUARTZ_BLOCK);
		blockStateModelGenerator.registerSimpleCubeAll(RandomMod.BUDDING_PURE_QUARTZ);
		blockStateModelGenerator.registerAmethyst(RandomMod.PURE_QUARTZ_CLUSTER);
		blockStateModelGenerator.registerAmethyst(RandomMod.LARGE_PURE_QUARTZ_BUD);
		blockStateModelGenerator.registerAmethyst(RandomMod.MEDIUM_PURE_QUARTZ_BUD);
		blockStateModelGenerator.registerAmethyst(RandomMod.SMALL_PURE_QUARTZ_BUD);
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator)
	{
		itemModelGenerator.method_43228(RandomMod.DEPTH_METER);
		itemModelGenerator.register(RandomMod.BAD_APPLE, Models.SINGLE_LAYER_ITEM);
		itemModelGenerator.register(RandomMod.PURE_QUARTZ_SHARD, Models.SINGLE_LAYER_ITEM);
		itemModelGenerator.register(RandomMod.QUARTZ_DISC, Models.SINGLE_LAYER_ITEM);
	}
}
