package boblovespi.randommod.common.item;

import boblovespi.randommod.common.entity.CoinProjectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class Coin extends Item
{
	public Coin(Item.Settings settings)
	{
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		var stack = user.getStackInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F,
				0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		if (!world.isClient)
		{
			var coinProj = new CoinProjectile(world, user);
			coinProj.setItem(stack);
			coinProj.setProperties(user, user.getPitch(), user.getYaw(), 0, 0.2f, 0.2f);
			coinProj.addVelocity(0, 0.3, 0);
			world.spawnEntity(coinProj);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode)
			stack.decrement(1);

		return TypedActionResult.success(stack, world.isClient());
	}
}
