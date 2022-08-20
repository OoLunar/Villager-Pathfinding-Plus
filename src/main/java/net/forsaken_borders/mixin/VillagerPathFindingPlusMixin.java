package net.forsaken_borders.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(EntityNavigation.class)
public abstract class VillagerPathFindingPlusMixin {
    @Shadow
    @Final
    public World world;

    @Shadow
    @Final
    public MobEntity entity;

    @Inject(at = @At("HEAD"), method = "canPathDirectlyThrough", cancellable = true)
    private void canPathDirectlyThrough(Vec3d origin, Vec3d target, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (entity instanceof VillagerEntity) {
            BlockState blockState = world.getBlockState(new BlockPos(target));
            Block block = blockState.getBlock();

            if (block instanceof LadderBlock) {
                callbackInfo.setReturnValue(true);
            } else if (block instanceof TrapdoorBlock trapdoorBlock) {
                BlockPos belowTrapdoorPos = new BlockPos(target).down();
                BlockState belowTrapdoor = world.getBlockState(belowTrapdoorPos);
                boolean isBelowTrapdoorSolid = belowTrapdoor.isOpaqueFullCube(world, belowTrapdoorPos);

                BlockState aboveTrapdoor = world.getBlockState(new BlockPos(target).up());
                boolean isAboveTrapdoorAirOrLadder = aboveTrapdoor.isAir() || aboveTrapdoor.isOf(Blocks.LADDER);
                boolean trapdoorIsOpen = blockState.get(TrapdoorBlock.OPEN);

                if (trapdoorIsOpen && isBelowTrapdoorSolid)
                // Block is below an open trapdoor
                {
                    callbackInfo.setReturnValue(true);
                }
                // Block above is a ladder or air (climbing or jumping)
                else if (trapdoorIsOpen && isAboveTrapdoorAirOrLadder) {
                    callbackInfo.setReturnValue(true);
                } else if (!trapdoorIsOpen)
                // The trapdoor isn't open (flat)
                {
                    callbackInfo.setReturnValue(true);
                } else
                // We know the block is a trapdoor but isn't safe to cross
                {
                    callbackInfo.setReturnValue(false);
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "isValidPosition", cancellable = true)
    private void isValidPosition(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (entity instanceof VillagerEntity) {
            BlockState blockState = world.getBlockState(new BlockPos(pos));
            Block block = blockState.getBlock();

            if (block instanceof LadderBlock) {
                callbackInfo.setReturnValue(true);
            } else if (block instanceof TrapdoorBlock trapdoorBlock) {
                BlockPos belowTrapdoorPos = new BlockPos(pos).down();
                BlockState belowTrapdoor = world.getBlockState(belowTrapdoorPos);
                boolean isBelowTrapdoorSolid = belowTrapdoor.isOpaqueFullCube(world, belowTrapdoorPos);

                BlockState aboveTrapdoor = world.getBlockState(new BlockPos(pos).up());
                boolean isAboveTrapdoorAirOrLadder = aboveTrapdoor.isAir() || aboveTrapdoor.isOf(Blocks.LADDER);
                boolean trapdoorIsOpen = blockState.get(TrapdoorBlock.OPEN);

                if (trapdoorIsOpen && isBelowTrapdoorSolid)
                // Block is below an open trapdoor
                {
                    callbackInfo.setReturnValue(true);
                }
                // Block above is a ladder or air (climbing or jumping)
                else if (trapdoorIsOpen && isAboveTrapdoorAirOrLadder) {
                    callbackInfo.setReturnValue(true);
                } else if (!trapdoorIsOpen)
                // The trapdoor isn't open (flat)
                {
                    callbackInfo.setReturnValue(true);
                } else
                // We know the block is a trapdoor but isn't safe to cross
                {
                    callbackInfo.setReturnValue(false);
                }

            }
        }
    }
}
