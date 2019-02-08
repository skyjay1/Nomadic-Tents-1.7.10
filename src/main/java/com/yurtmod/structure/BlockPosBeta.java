package com.yurtmod.structure;

import com.sun.javafx.geom.Vec3d;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPosBeta extends Vec3d {
	
	public BlockPosBeta(int x, int y, int z) {
		super(x, y, z);
	}
	
	public int getX() {
		return (int)this.x;
	}
	
	public int getY() {
		return (int)this.y;
	}
	
	public int getZ() {
		return (int)this.z;
	}
	
	public boolean setBlockToAir(final World world) {
		return world.setBlockToAir(getX(), getY(), getZ());
	}
	
	public boolean isAirBlock(final World world) {
		return world.isAirBlock(getX(), getY(), getZ());
	}
	
	public Block getBlock(final World world) {
		return world.getBlock(getX(), getY(), getZ());
	}
	
	public TileEntity getTileEntity(final World world) {
		return world.getTileEntity(getX(), getY(), getZ());
	}
	
	public void removeTileEntity(final World world) {
		world.removeTileEntity(getX(), getY(), getZ());
	}
	
	public boolean setBlock(final World world, final Block block) {
		return setBlock(world, block, 0);
	}
	
	public boolean setBlock(final World world, final Block block, final int meta) {
		return setBlock(world, block, meta, 3);
	}
	
	public boolean setBlock(final World world, final Block block, final int meta, final int updateFlag) {
		return world.setBlock(getX(), getY(), getZ(), block, meta, updateFlag);
	}
	
	public BlockPosBeta up(int i) {
		return new BlockPosBeta(this.getX(), this.getY() + i, this.getZ());
	}
	
	public BlockPosBeta down(int i) {
		return new BlockPosBeta(this.getX(), this.getY() - i, this.getZ());
	}

	public BlockPosBeta add(int x, int y, int z) {
		return new BlockPosBeta(getX() + x, getY() + y, getZ() + z);
	}
}
