package boblovespi.randommod.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.sound.BlockSoundGroup;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
abstract class BlocksMixin
{
	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$Settings;sounds(Lnet/minecraft/sound/BlockSoundGroup;)Lnet/minecraft/block/AbstractBlock$Settings;"),
			slice = @Slice(
					from = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;DANDELION:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC),
					to = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;TORCHFLOWER:Lnet/minecraft/block/Block;", opcode = Opcodes.PUTSTATIC)
			))
	private static AbstractBlock.Settings makeTorchflowerEmitLight(AbstractBlock.Settings instance, BlockSoundGroup soundGroup)
	{
		return instance.sounds(soundGroup).luminance(u -> 3);
	}
}
