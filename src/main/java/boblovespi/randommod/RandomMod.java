package boblovespi.randommod;

import boblovespi.randommod.common.block.Rememberer;
import boblovespi.randommod.common.item.BuddingPureQuartz;
import boblovespi.randommod.common.item.DepthMeter;
import boblovespi.randommod.common.worldgen.PureQuartzSpikeFeature;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class RandomMod implements ModInitializer
{
	public static final String MODID = "boblovespirandommod";
	public static final Logger LOGGER = LoggerFactory.getLogger("Random Mod");

	// Items

	public static final Item DEPTH_METER = item("depth_meter", DepthMeter::new, new QuiltItemSettings());
	public static final Item BAD_APPLE = item("bad_apple", Item::new, new Item.Settings().food(FoodComponents.APPLE));
	public static final Item PURE_QUARTZ_SHARD = item("pure_quartz_shard", Item::new, new Item.Settings());
	public static final Item QUARTZ_DISC = item("quartz_disc", Item::new, new Item.Settings().maxCount(1).rarity(Rarity.RARE));

	// Blocks

	public static final Block PEGMATITE = block("pegmatite", Block::new, QuiltBlockSettings.copyOf(Blocks.SMOOTH_BASALT));
	public static final Block PURE_QUARTZ_BLOCK = block("pure_quartz_block", AmethystBlock::new,
			QuiltBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).mapColor(MapColor.WOOL));
	public static final Block BUDDING_PURE_QUARTZ = block("budding_pure_quartz", BuddingPureQuartz::new,
			QuiltBlockSettings.copyOf(Blocks.BUDDING_AMETHYST).mapColor(MapColor.WOOL));
	public static final Block PURE_QUARTZ_CLUSTER = block("pure_quartz_cluster", s -> new AmethystClusterBlock(7, 2, s),
			QuiltBlockSettings.copyOf(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.WOOL));
	public static final Block LARGE_PURE_QUARTZ_BUD = block("large_pure_quartz_bud", s -> new AmethystClusterBlock(5, 3, s),
			QuiltBlockSettings.copyOf(Blocks.LARGE_AMETHYST_BUD).mapColor(MapColor.WOOL));
	public static final Block MEDIUM_PURE_QUARTZ_BUD = block("medium_pure_quartz_bud", s -> new AmethystClusterBlock(4, 4, s),
			QuiltBlockSettings.copyOf(Blocks.MEDIUM_AMETHYST_BUD).mapColor(MapColor.WOOL));
	public static final Block SMALL_PURE_QUARTZ_BUD = block("small_pure_quartz_bud", s -> new AmethystClusterBlock(3, 5, s),
			QuiltBlockSettings.copyOf(Blocks.SMALL_AMETHYST_BUD).mapColor(MapColor.WOOL));

	public static final Block REMEMBERER = block("rememberer", Rememberer::new, QuiltBlockSettings.copyOf(Blocks.REPEATER));

	// Features

	public static final Feature<DefaultFeatureConfig> PURE_QUARTZ_SPIKE = Registry.register(Registries.FEATURE, new Identifier(MODID, "pure_quartz_spike"),
			new PureQuartzSpikeFeature(DefaultFeatureConfig.CODEC));

	public static final RegistryKey<PlacedFeature> PURE_QUARTZ_SPIKE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
			new Identifier(MODID, "pure_quartz_spike"));

	private static <T extends Item.Settings> Item item(String name, Function<T, Item> itemProvider, T settings)
	{
		return Registry.register(Registries.ITEM, new Identifier(MODID, name), itemProvider.apply(settings));
	}

	private static <T extends AbstractBlock.Settings> Block block(String name, Function<T, Block> blockProvider, T settings)
	{
		var block = Registry.register(Registries.BLOCK, new Identifier(MODID, name), blockProvider.apply(settings));
		item(name, s -> new BlockItem(block, s), new Item.Settings());
		return block;
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void onInitialize(ModContainer mod)
	{
		LOGGER.debug("Registering feature...");
		BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.BASALT_DELTAS), GenerationStep.Feature.UNDERGROUND_DECORATION,
				PURE_QUARTZ_SPIKE_PLACED_KEY);

		LOGGER.debug("Adding items to item groups...");
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(c -> {
			c.addAfter(Blocks.AMETHYST_BLOCK, PURE_QUARTZ_BLOCK);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL_BLOCKS).register(c -> {
			c.addAfter(Blocks.SMOOTH_BASALT, PEGMATITE);
			c.addAfter(Blocks.AMETHYST_CLUSTER, PURE_QUARTZ_BLOCK, BUDDING_PURE_QUARTZ, SMALL_PURE_QUARTZ_BUD, MEDIUM_PURE_QUARTZ_BUD, LARGE_PURE_QUARTZ_BUD, PURE_QUARTZ_CLUSTER);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE_BLOCKS).register(c -> {
			c.addAfter(Blocks.COMPARATOR, REMEMBERER);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS_AND_UTILITIES).register(c -> {
			c.addAfter(Items.CLOCK, DEPTH_METER);
			c.addAfter(Items.MUSIC_DISC_RELIC, QUARTZ_DISC);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINKS).register(c -> {
			c.addAfter(Items.ENCHANTED_GOLDEN_APPLE, BAD_APPLE);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(c -> {
			c.addAfter(Items.AMETHYST_SHARD, PURE_QUARTZ_SHARD);
		});
	}
}