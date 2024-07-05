package boblovespi.randommod.common.entity;

import boblovespi.randommod.RandomMod;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class CoinProjectile extends ThrownItemEntity
{
	public CoinProjectile(EntityType<? extends CoinProjectile> entityType, World world)
	{
		super(entityType, world);
	}

	public CoinProjectile(World world, LivingEntity owner)
	{
		super(RandomMod.COIN_PROJECTILE, owner, world);
	}

	public CoinProjectile(World world, double x, double y, double z)
	{
		super(RandomMod.COIN_PROJECTILE, x, y, z, world);
	}

	@Override
	protected Item getDefaultItem()
	{
		return RandomMod.COIN;
	}

	private ParticleEffect getParticleParameters()
	{
		return new ItemStackParticleEffect(ParticleTypes.ITEM, getStack());
	}

	@Override
	public void handleStatus(byte status)
	{
		if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
		{
			var particleEffect = getParticleParameters();
			for (int i = 0; i < 8; ++i)
				getWorld().addParticle(particleEffect, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
			getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.NEUTRAL, 0.9f, 1.5f, false);
		}
	}

	@Override
	protected void onCollision(HitResult hitResult)
	{
		super.onCollision(hitResult);
		if (!getWorld().isClient)
		{
			getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
			dropItem(getDefaultItem());
			discard();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
		super.onEntityHit(entityHitResult);
		var entity = entityHitResult.getEntity();
		entity.damage(getDamageSources().thrown(this, getOwner()), 10 * (float) getVelocity().length());
	}
}
