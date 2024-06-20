package boblovespi.randommod.data;

import boblovespi.randommod.RandomMod;
import boblovespi.randommod.common.block.CopperKettle;
import boblovespi.randommod.common.block.TeaBushCrop;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.ItemTags;

public class BlockLootTableProvider extends FabricBlockLootTableProvider
{

	protected BlockLootTableProvider(FabricDataOutput dataOutput)
	{
		super(dataOutput);
	}

	@Override
	public void generate()
	{
		addDrop(RandomMod.PEGMATITE);
		addDrop(RandomMod.PURE_QUARTZ_BLOCK);
		add(RandomMod.BUDDING_PURE_QUARTZ, dropsNothing());
		add(RandomMod.PURE_QUARTZ_CLUSTER, block -> dropsWithSilkTouch(block,
				ItemEntry.builder(RandomMod.PURE_QUARTZ_SHARD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F)))
						 .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
						 .conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.CLUSTER_MAX_HARVESTABLES))).alternatively(
								 this.applyExplosionDecay(block,
										 ItemEntry.builder(RandomMod.PURE_QUARTZ_SHARD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))))));
		addDrop(RandomMod.REINFORCED_GLASS);
		addDropWithSilkTouch(RandomMod.SMALL_PURE_QUARTZ_BUD);
		addDropWithSilkTouch(RandomMod.MEDIUM_PURE_QUARTZ_BUD);
		addDropWithSilkTouch(RandomMod.LARGE_PURE_QUARTZ_BUD);

		addDrop(RandomMod.COPPER_SINK);

		add(RandomMod.COPPER_KETTLE, block -> kettleDrop(RandomMod.COPPER_KETTLE));

		add(RandomMod.TEA_BUSH_CROP, block -> cropDrops(block, RandomMod.TEA_LEAF, RandomMod.TEA_BUSH_CROP.asItem(),
				BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(TeaBushCrop.AGE, 3))));
	}

	private LootTable.Builder kettleDrop(Block drop)
	{
		return LootTable.builder().pool(
				LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(
						applyExplosionDecay(
								drop, ItemEntry.builder(drop).apply(
										CopperKettle.Contents.values(), c -> {
											var nbt = new NbtCompound();
											nbt.putString("contents", c.asString());
											return SetNbtLootFunction.builder(nbt).conditionally(
													BlockStatePropertyLootCondition.builder(drop).properties(
															StatePredicate.Builder.create().exactMatch(CopperKettle.CONTENTS, c.asString())));
										}))));
	}
}
