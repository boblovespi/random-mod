package boblovespi.randommod.common.item;

import boblovespi.randommod.common.block.CopperKettle;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CopperKettleItem extends BlockItem
{
	public CopperKettleItem(CopperKettle block, Settings settings)
	{
		super(block, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		var nbt = stack.getNbt();
		var contents = nbt == null ? "empty" : nbt.getString("contents");
		contents = CopperKettle.Contents.fromString(contents).asString();
		tooltip.add(Text.translatable("block.boblovespirandommod.copper_kettle." + contents).formatted(Formatting.GRAY));
	}
}
