package boblovespi.randommod;

import boblovespi.randommod.common.block.*;
import boblovespi.randommod.common.entity.CoinProjectile;
import boblovespi.randommod.common.item.BuddingPureQuartz;
import boblovespi.randommod.common.item.Coin;
import boblovespi.randommod.common.item.CopperKettleItem;
import boblovespi.randommod.common.item.DepthMeter;
import boblovespi.randommod.common.recipe.BrewingRecipes;
import boblovespi.randommod.common.worldgen.PureQuartzSpikeFeature;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;
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
	public static final Item GLEAMING_BERRIES = item("gleaming_berries", Item::new, new Item.Settings());
	public static final Item COIN = item("coin", Coin::new, new Item.Settings().maxCount(16));

	public static final Item TEA_LEAF = item("tea_leaf", Item::new, new Item.Settings());
	public static final Item PANNED_TEA_LEAF = item("panned_tea_leaf", Item::new, new Item.Settings());
	public static final Item GREEN_TEA_LEAF = item("green_tea_leaf", Item::new, new Item.Settings());

	// Blocks

	public static final Block PEGMATITE = block("pegmatite", Block::new, QuiltBlockSettings.copyOf(Blocks.SMOOTH_BASALT));
	public static final Block PURE_QUARTZ_BLOCK = block("pure_quartz_block", AmethystBlock::new,
			QuiltBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).mapColor(MapColor.WOOL).strength(3, 9));
	public static final Block BUDDING_PURE_QUARTZ = block("budding_pure_quartz", BuddingPureQuartz::new,
			QuiltBlockSettings.copyOf(Blocks.BUDDING_AMETHYST).mapColor(MapColor.WOOL).strength(3, 9));
	public static final Block PURE_QUARTZ_CLUSTER = block("pure_quartz_cluster", s -> new AmethystClusterBlock(7, 2, s),
			QuiltBlockSettings.copyOf(Blocks.AMETHYST_CLUSTER).mapColor(MapColor.WOOL).strength(3, 9));
	public static final Block LARGE_PURE_QUARTZ_BUD = block("large_pure_quartz_bud", s -> new AmethystClusterBlock(5, 3, s),
			QuiltBlockSettings.copyOf(Blocks.LARGE_AMETHYST_BUD).mapColor(MapColor.WOOL).strength(3, 9));
	public static final Block MEDIUM_PURE_QUARTZ_BUD = block("medium_pure_quartz_bud", s -> new AmethystClusterBlock(4, 4, s),
			QuiltBlockSettings.copyOf(Blocks.MEDIUM_AMETHYST_BUD).mapColor(MapColor.WOOL).strength(3, 9));
	public static final Block SMALL_PURE_QUARTZ_BUD = block("small_pure_quartz_bud", s -> new AmethystClusterBlock(3, 5, s),
			QuiltBlockSettings.copyOf(Blocks.SMALL_AMETHYST_BUD).mapColor(MapColor.WOOL).strength(3, 9));
	public static final Block REINFORCED_GLASS = block("reinforced_glass", GlassBlock::new, QuiltBlockSettings.copyOf(Blocks.TINTED_GLASS).strength(10, 100));

	public static final Block REMEMBERER = block("rememberer", Rememberer::new, QuiltBlockSettings.copyOf(Blocks.REPEATER));

	public static final Block COPPER_SINK = block("copper_sink", CopperSink::new, QuiltBlockSettings.copyOf(Blocks.COPPER_BLOCK));

	public static final Block COPPER_KETTLE = block("copper_kettle", CopperKettle::new, CopperKettleItem::new,
			QuiltBlockSettings.copyOf(Blocks.COPPER_BLOCK).strength(0.1f, 3.5f).nonOpaque().pistonBehavior(PistonBehavior.DESTROY).requiresTool(false));
	public static final Block BAMBOO_BASKET = block("bamboo_basket", BambooBasket::new, QuiltBlockSettings.copyOf(Blocks.BAMBOO_PLANKS));

	public static final Block TEA_BUSH_CROP = block("tea_bush_crop", "tea_seeds", TeaBushCrop::new, QuiltBlockSettings.copyOf(Blocks.WHEAT)); // TODO: make model + texture
	public static final Block TEA_BUSH = block("tea_bush", TeaBush::new, QuiltBlockSettings.copyOf(Blocks.AZALEA).noCollision());

	// BE types

	public static final BlockEntityType<BambooBasketBE> BAMBOO_BASKET_BE = beType("bamboo_basket", BambooBasketBE::new, BAMBOO_BASKET);

	// Features

	public static final Feature<DefaultFeatureConfig> PURE_QUARTZ_SPIKE = Registry.register(Registries.FEATURE, new Identifier(MODID, "pure_quartz_spike"),
			new PureQuartzSpikeFeature(DefaultFeatureConfig.CODEC));

	// Potions

	public static final Potion LEVITATION = potion("levitation", new Potion("levitation", new StatusEffectInstance(StatusEffects.LEVITATION, 20 * 45)));
	public static final Potion LONG_LEVITATION = potion("long_levitation",
			new Potion("levitation", new StatusEffectInstance(StatusEffects.LEVITATION, 20 * 90)));
	public static final Potion STRONG_LEVITATION = potion("strong_levitation",
			new Potion("levitation", new StatusEffectInstance(StatusEffects.LEVITATION, 20 * 22, 1)));

	public static final Potion NAUSEA = potion("nausea", new Potion("nausea", new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 60 * 3)));
	public static final Potion LONG_NAUSEA = potion("long_nausea", new Potion("nausea", new StatusEffectInstance(StatusEffects.NAUSEA, 20 * 60 * 8)));

	public static final Potion GLOWING = potion("glowing", new Potion("glowing", new StatusEffectInstance(StatusEffects.GLOWING, 20 * 60 * 3)));
	public static final Potion LONG_GLOWING = potion("long_glowing", new Potion("glowing", new StatusEffectInstance(StatusEffects.GLOWING, 20 * 60 * 8)));

	public static final RegistryKey<PlacedFeature> PURE_QUARTZ_SPIKE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE,
			new Identifier(MODID, "pure_quartz_spike"));

	// Entities

	public static final EntityType<CoinProjectile> COIN_PROJECTILE = Registry.register(Registries.ENTITY_TYPE, new Identifier(MODID, "coin_projectile"),
			QuiltEntityTypeBuilder.<CoinProjectile>create().entityFactory(CoinProjectile::new).setDimensions(EntityDimensions.fixed(0.25f, 0.25f))
								  .maxChunkTrackingRange(4).trackingTickInterval(10).makeFireImmune().build());

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

	private static <T extends AbstractBlock.Settings> Block block(String name, String itemName, Function<T, Block> blockProvider, T settings)
	{
		var block = Registry.register(Registries.BLOCK, new Identifier(MODID, name), blockProvider.apply(settings));
		item(itemName, s -> new AliasedBlockItem(block, s), new Item.Settings());
		return block;
	}

	private static <T extends AbstractBlock.Settings, B extends Block> B block(String name, Function<T, B> blockProvider,
																			   BiFunction<B, Item.Settings, BlockItem> blockItemProvider, T settings)
	{
		var block = Registry.register(Registries.BLOCK, new Identifier(MODID, name), blockProvider.apply(settings));
		item(name, s -> blockItemProvider.apply(block, s), new Item.Settings());
		return block;
	}

	private static <T extends BlockEntity> BlockEntityType<T> beType(String name, BlockEntityType.BlockEntityFactory<T> bef, Block b)
	{
		var id = new Identifier(MODID, name);
		var type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id.toString());
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.create(bef, b).build(type));
	}

	private static Potion potion(String name, Potion potion)
	{
		return Registry.register(Registries.POTION, new Identifier(MODID, name), potion);
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
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register(c -> {
			c.addAfter(Blocks.TINTED_GLASS, REINFORCED_GLASS);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL_BLOCKS).register(c -> {
			c.addAfter(Blocks.SMOOTH_BASALT, PEGMATITE);
			c.addAfter(Blocks.AMETHYST_CLUSTER, PURE_QUARTZ_BLOCK, BUDDING_PURE_QUARTZ, SMALL_PURE_QUARTZ_BUD, MEDIUM_PURE_QUARTZ_BUD, LARGE_PURE_QUARTZ_BUD,
					PURE_QUARTZ_CLUSTER);
			c.addAfter(Blocks.CACTUS, TEA_BUSH);
			c.addAfter(Items.PITCHER_POD, TEA_BUSH_CROP);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL_BLOCKS).register(c -> {
			c.addAfter(Blocks.CAULDRON, COPPER_SINK, COPPER_KETTLE, BAMBOO_BASKET);
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
			c.addAfter(Items.WHEAT, TEA_LEAF);
			c.addAfter(Items.GLISTERING_MELON_SLICE, GLEAMING_BERRIES);
		});

		LOGGER.debug("Adding brewing recipes...");
		BrewingRecipes.addRecipes();

		LOGGER.debug("Modifying loot tables...");
		LootTableEvents.MODIFY.register((r, l, n, t, s) -> {
			if (LootTables.SNIFFER_DIGGING_GAMEPLAY.equals(n))
				t.modifyPools(p -> p.with(ItemEntry.builder(TEA_BUSH_CROP)));
		});
	}
}