package boblovespi.randommod.common.item;

import boblovespi.randommod.RandomMod;
import boblovespi.randommod.common.block.Teacup;
import boblovespi.randommod.common.block.TeapotBE;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class TeacupItem extends BlockItem
{
	public TeacupItem(Teacup block, Settings settings)
	{
		super(block, settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		var nbt = user.getStackInHand(hand).getNbt();
		if (nbt != null && nbt.getInt("teaTypeId") > 0)
		{
			user.setCurrentHand(hand);
			return TypedActionResult.consume(user.getStackInHand(hand));
		}
		return super.use(world, user, hand);
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user)
	{
		var nbt = stack.getNbt();
		if (nbt != null && nbt.getInt("teaTypeId") > 0)
		{
			var type = TeapotBE.TeaTypes.values()[nbt.getInt("teaTypeId")];
			nbt.putInt("teaTypeId", 0);
			nbt.putString("teaType", TeapotBE.TeaTypes.EMPTY.toString());
			switch (type)
			{
				case EMPTY, WATER -> {
				}
				case GREEN -> user.addStatusEffect(new StatusEffectInstance(RandomMod.REFRESHED, 20 * 30));
			}
			user.emitGameEvent(GameEvent.DRINK);
		}
		return stack;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.DRINK;
	}

	@Override
	public int getMaxUseTime(ItemStack stack)
	{
		var nbt = stack.getNbt();
		if (nbt != null && nbt.getInt("teaTypeId") > 0)
			return 32;
		return 0;
	}
}
