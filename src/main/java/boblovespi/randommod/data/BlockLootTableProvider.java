package boblovespi.randommod.data;

import boblovespi.randommod.RandomMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
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
	}
}
