package boblovespi.randommod.data;

import boblovespi.randommod.RandomMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class ModelProvider extends FabricModelProvider
{
	public ModelProvider(FabricDataOutput output)
	{
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator bsmg)
	{
		bsmg.registerSimpleCubeAll(RandomMod.PEGMATITE);
		bsmg.registerSimpleCubeAll(RandomMod.PURE_QUARTZ_BLOCK);
		bsmg.registerSimpleCubeAll(RandomMod.BUDDING_PURE_QUARTZ);
		bsmg.registerAmethyst(RandomMod.PURE_QUARTZ_CLUSTER);
		bsmg.registerAmethyst(RandomMod.LARGE_PURE_QUARTZ_BUD);
		bsmg.registerAmethyst(RandomMod.MEDIUM_PURE_QUARTZ_BUD);
		bsmg.registerAmethyst(RandomMod.SMALL_PURE_QUARTZ_BUD);

		createRemembererBlockState(bsmg);
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator)
	{
		itemModelGenerator.method_43228(RandomMod.DEPTH_METER);
		itemModelGenerator.register(RandomMod.BAD_APPLE, Models.SINGLE_LAYER_ITEM);
		itemModelGenerator.register(RandomMod.PURE_QUARTZ_SHARD, Models.SINGLE_LAYER_ITEM);
		itemModelGenerator.register(RandomMod.QUARTZ_DISC, Models.SINGLE_LAYER_ITEM);
	}

	private void createRemembererBlockState(BlockStateModelGenerator bsmg)
	{
		bsmg.excludeFromSimpleItemModelGeneration(RandomMod.REMEMBERER);
		var dOff = new Identifier(RandomMod.MODID, "block/rememberer");
		var dOn = new Identifier(RandomMod.MODID, "block/rememberer_high");
		var wOff = new Identifier(RandomMod.MODID, "block/rememberer_write_off");
		var wOn = new Identifier(RandomMod.MODID, "block/rememberer_write_on");
		var states = MultipartBlockStateSupplier.create(RandomMod.REMEMBERER);
		Map.of(Direction.NORTH, VariantSettings.Rotation.R180,
				Direction.EAST, VariantSettings.Rotation.R270,
				Direction.SOUTH, VariantSettings.Rotation.R0,
				Direction.WEST, VariantSettings.Rotation.R90).forEach((direction, rotation) ->
		{
			var rotCon = When.create().set(Properties.HORIZONTAL_FACING, direction);
			// states.with(rotCon, BlockStateVariant.create().put(VariantSettings.Y, rotation));
			var powerLow = When.create().set(Properties.POWER, 0);
			states.with(When.allOf(rotCon, powerLow), BlockStateVariant.create().put(VariantSettings.MODEL, dOff).put(VariantSettings.Y, rotation));
			var powerHigh = When.create().setNegated(Properties.POWER, 0);
			states.with(When.allOf(rotCon, powerHigh), BlockStateVariant.create().put(VariantSettings.MODEL, dOn).put(VariantSettings.Y, rotation));
			var writeOff = When.create().set(Properties.POWERED, false);
			states.with(When.allOf(rotCon, writeOff), BlockStateVariant.create().put(VariantSettings.MODEL, wOff).put(VariantSettings.Y, rotation));
			var writeOn = When.create().set(Properties.POWERED, true);
			states.with(When.allOf(rotCon, writeOn), BlockStateVariant.create().put(VariantSettings.MODEL, wOn).put(VariantSettings.Y, rotation));
		});
		bsmg.blockStateCollector.accept(states);
	}
}
