package boblovespi.randommod.client;

import boblovespi.randommod.common.block.BambooBasketBE;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Axis;

public class BambooBasketRenderer implements BlockEntityRenderer<BambooBasketBE>
{
	private static final float SCALE = 0.375F;
	private final ItemRenderer itemRenderer;

	public BambooBasketRenderer(BlockEntityRendererFactory.Context ctx)
	{
		this.itemRenderer = ctx.getItemRenderer();
	}

	@Override
	public void render(BambooBasketBE basket, float delta, MatrixStack stack, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		var items = basket.getStacks();
		int k = (int) basket.getPos().asLong();

		for (int i = 0; i < 4; ++i)
		{
			var item = items.get(i);
			if (item != ItemStack.EMPTY)
			{
				stack.push();
				stack.translate(0.5, 1 / 16f + 2.5 / 256, 0.5);
				stack.multiply(Axis.X_POSITIVE.rotationDegrees(90.0F));
				stack.translate(3.5 / 16f * (1 - 2 * (i >> 1)), 3.5 / 16f * (1 - 2 * (i & 0b1)), 0.0F);
				stack.scale(5 / 16f, 5 / 16f, 5 / 16f);
				itemRenderer.renderItem(item, ModelTransformationMode.FIXED, light, overlay, stack, vertexConsumers, basket.getWorld(), k + i);
				stack.pop();
			}
		}
		var item = items.get(4);
		if (item != ItemStack.EMPTY)
		{
			stack.push();
			stack.translate(0.5, 1 / 16f + 2.5 / 256, 0.5);
			stack.multiply(Axis.X_POSITIVE.rotationDegrees(-90.0F));
			stack.scale(5 / 16f, 5 / 16f, 5 / 16f);
			itemRenderer.renderItem(item, ModelTransformationMode.FIXED, light, overlay, stack, vertexConsumers, basket.getWorld(), k + 4);
			stack.pop();
		}
	}
}
