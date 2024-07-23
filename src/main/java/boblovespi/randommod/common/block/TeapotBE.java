package boblovespi.randommod.common.block;

import boblovespi.randommod.RandomMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class TeapotBE extends BlockEntity
{
	private TeaTypes teaType = TeaTypes.EMPTY;
	private boolean brewed = false;
	private int brewTime = 0;
	private int amount = 0;

	public TeapotBE(BlockPos pos, BlockState state)
	{
		super(RandomMod.TEAPOT_BE, pos, state);
	}

	public static void tick(World world, BlockPos pos, BlockState state, TeapotBE be)
	{
		if (!be.brewed && be.brewTime > 0)
		{
			be.brewTime--;
			if (be.brewTime <= 0)
			{
				be.brewed = true;
				world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
			}
			be.markDirty();
		}
	}

	public boolean addItem(@Nullable Entity user, ItemStack item)
	{
		if (teaType == TeaTypes.EMPTY && item.isOf(RandomMod.COPPER_KETTLE.asItem()))
		{
			if (item.hasNbt() && "boiling".equals(item.getNbt().getString("contents")))
			{
				item.getNbt().putString("contents", "empty");
				teaType = TeaTypes.WATER;
				amount = 2;
				world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Context.create(user, this.getCachedState()));
				markDirty();
				world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
				return true;
			}
			return false;
		}
		if (teaType == TeaTypes.WATER)
		{
			if (item.isOf(RandomMod.GREEN_TEA_LEAF))
			{
				teaType = TeaTypes.GREEN;
				brewTime = 20 * 1;
				item.decrement(1);
			}
			else
				return false;
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Context.create(user, this.getCachedState()));
			markDirty();
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
			return true;
		}
		return false;
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		teaType = TeaTypes.values()[nbt.getInt("teaTypeId")];
		brewed = nbt.getBoolean("brewed");
		brewTime = nbt.getInt("brewTime");
		amount = nbt.getInt("amount");
	}

	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putInt("teaTypeId", teaType.ordinal());
		nbt.putString("teaType", teaType.name);
		nbt.putBoolean("brewed", brewed);
		nbt.putInt("brewTime", brewTime);
		nbt.putInt("amount", amount);
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
		return nbt;
	}

	public enum TeaTypes
	{
		EMPTY, WATER, GREEN;
		private final String name;

		TeaTypes()
		{
			name = name().toLowerCase(Locale.ROOT);
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
