package boblovespi.randommod.client;

import boblovespi.randommod.RandomMod;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

@ClientOnly
public class ClientEntry implements ClientModInitializer
{
	@Override
	public void onInitializeClient(ModContainer mod)
	{
		ModelPredicateProviderRegistry.register(RandomMod.DEPTH_METER, new Identifier("depth"), (stack, world, entity, val) -> {
			if (entity == null)
			{
				return 0.0F;
			}
			var seaLevel = world.getSeaLevel();
			var bottom = world.getBottomY();
			return (float) MathHelper.clampedMap(entity.getY(), bottom, seaLevel, 0, 1);
		});

		BlockRenderLayerMap.put(RenderLayer.getCutout(), RandomMod.PURE_QUARTZ_CLUSTER, RandomMod.LARGE_PURE_QUARTZ_BUD, RandomMod.MEDIUM_PURE_QUARTZ_BUD,
				RandomMod.SMALL_PURE_QUARTZ_BUD, RandomMod.REMEMBERER);
		BlockRenderLayerMap.put(RenderLayer.getTranslucent(), RandomMod.REINFORCED_GLASS);
	}
}
