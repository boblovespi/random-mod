package boblovespi.randommod.common.block;

import boblovespi.randommod.RandomMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class TeacupBE extends BlockEntity
{
	private TeapotBE.TeaTypes teaType = TeapotBE.TeaTypes.EMPTY; // TODO: add logic to set on place, same with teapot

	public TeacupBE(BlockPos pos, BlockState state)
	{
		super(RandomMod.TEACUP_BE, pos, state);
	}

	public boolean pour(@Nullable Entity user, ItemStack item)
	{
		if (teaType == TeapotBE.TeaTypes.EMPTY && item.isOf(RandomMod.TERRACOTTA_TEAPOT.asItem())) // TODO: replace with tag or smth
		{
			var nbt = item.getSubNbt("teapot");
			if (nbt != null)
			{
				var amt = nbt.getInt("amount");
				var type = TeapotBE.TeaTypes.values()[nbt.getInt("teaTypeId")];
				var brewed = nbt.getBoolean("brewed");
				if (brewed && type != TeapotBE.TeaTypes.EMPTY && amt > 0)
				{
					teaType = type;
					nbt.putInt("amount", amt - 1);
					world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Context.create(user, this.getCachedState()));
					markDirty();
					world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		teaType = TeapotBE.TeaTypes.values()[nbt.getInt("teaTypeId")];
	}

	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putInt("teaTypeId", teaType.ordinal());
		nbt.putString("teaType", teaType.toString());
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket()
	{
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	public NbtCompound toSyncedNbt()
	{
		var nbt = new NbtCompound();
		writeNbt(nbt);
		return nbt;
	}

	public void drink(LivingEntity user)
	{
		switch (teaType)
		{
			case EMPTY, WATER -> {}
			case GREEN -> user.addStatusEffect(new StatusEffectInstance(RandomMod.REFRESHED, 20 * 30));
		}
		teaType = TeapotBE.TeaTypes.EMPTY;
		user.emitGameEvent(GameEvent.DRINK);
	}
}
