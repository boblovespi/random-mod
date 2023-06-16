package boblovespi.randommod.data;

import boblovespi.randommod.RandomMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.HolderLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider
{
	public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture)
	{
		super(output, registriesFuture);
	}

	private static TagKey<Block> cTag(String name)
	{
		return TagKey.of(RegistryKeys.BLOCK, new Identifier("c", name));
	}

	private static TagKey<Block> mcTag(String name)
	{
		return TagKey.of(RegistryKeys.BLOCK, new Identifier("minecraft", name));
	}

	private static TagKey<Block> tag(String name)
	{
		return TagKey.of(RegistryKeys.BLOCK, new Identifier(RandomMod.MODID, name));
	}

	@Override
	protected void configure(HolderLookup.Provider arg)
	{
		getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(RandomMod.PEGMATITE, RandomMod.PURE_QUARTZ_BLOCK, RandomMod.BUDDING_PURE_QUARTZ,
				RandomMod.PURE_QUARTZ_CLUSTER, RandomMod.LARGE_PURE_QUARTZ_BUD, RandomMod.MEDIUM_PURE_QUARTZ_BUD, RandomMod.SMALL_PURE_QUARTZ_BUD,
				RandomMod.REINFORCED_GLASS, RandomMod.COPPER_SINK);
	}
}
