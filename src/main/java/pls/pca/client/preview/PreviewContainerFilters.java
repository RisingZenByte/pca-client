package pls.pca.client.preview;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import org.jetbrains.annotations.Nullable;

public final class PreviewContainerFilters
{
	private PreviewContainerFilters()
	{
	}

	public static boolean isBlockEntitySupported(@Nullable BlockEntity blockEntity)
	{
		return blockEntity instanceof AbstractFurnaceBlockEntity
				|| blockEntity instanceof DispenserBlockEntity
				|| blockEntity instanceof HopperBlockEntity
				|| blockEntity instanceof ShulkerBoxBlockEntity
				|| blockEntity instanceof BarrelBlockEntity
				|| blockEntity instanceof BrewingStandBlockEntity
				|| blockEntity instanceof ChestBlockEntity
				|| blockEntity instanceof ComparatorBlockEntity
				|| blockEntity instanceof BeehiveBlockEntity;
	}

	public static boolean isBlockContainerSupported(@Nullable Object container)
	{
		return container instanceof AbstractFurnaceBlockEntity
				|| container instanceof DispenserBlockEntity
				|| container instanceof HopperBlockEntity
				|| container instanceof ShulkerBoxBlockEntity
				|| container instanceof BarrelBlockEntity
				|| container instanceof BrewingStandBlockEntity
				|| container instanceof ChestBlockEntity
				|| container instanceof CompoundContainer
				|| container instanceof ComparatorBlockEntity
				|| container instanceof BeehiveBlockEntity;
	}

	public static boolean isEntitySupported(Entity entity)
	{
		return entity instanceof ContainerEntity
				|| entity instanceof AbstractMinecartContainer
				|| entity instanceof Container
				|| entity instanceof AbstractVillager
				|| entity instanceof AbstractHorse
				|| entity instanceof Player;
	}
}
