package com.yurtmod.blocks;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.yurtmod.blocks.Categories.IFrameBlock;
import com.yurtmod.blocks.Categories.ITepeeBlock;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.main.Config;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTepeeWall extends BlockUnbreakable implements ITepeeBlock {
	private static final int NUM_TEXTURES = 15; // how many textures can apply to this block
	private static final int NUM_PATTERNS = 5; // the first X textures excluding texture 0 are patterns
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public BlockTepeeWall() {
		super(Material.cloth);
		this.setBlockTextureName(NomadicTents.MODID + ":tepee_wall_0");
	}

	@Override
	public void onBlockAdded(World worldIn, int x, int y, int z) {
		if (!TentDimension.isTent(worldIn) && worldIn.getBlockMetadata(x, y, z) == 0) {
			int metaToSet;
			BlockPos doorLoc = this.findDoorNearby(worldIn, x, y, z);
			// debug:
			// System.out.println("searched for door. y=" + y + ", flag=" + flag);
			if (doorLoc != null && Math.abs(y - doorLoc.y) % 2 == 0) {
				TileEntityTentDoor te = (TileEntityTentDoor) worldIn.getTileEntity(doorLoc.x, doorLoc.y, doorLoc.z);
				// psuedo-random seed guarantees all attached blocks that are same y-dis from
				// door get the same seed
				int randSeed = y + doorLoc.x + doorLoc.z + te.getOffsetX() * 123 + te.getOffsetZ() * 321;
				metaToSet = getMetaForRandomPattern(new Random(randSeed));
				worldIn.setBlockMetadataWithNotify(x, y, z, metaToSet, 2);
			} else if (worldIn.rand.nextInt(100) < Config.TEPEE_DECORATED_CHANCE) {
				metaToSet = getMetaForRandomDesign(worldIn.rand);
				worldIn.setBlockMetadataWithNotify(x, y, z, metaToSet, 2);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (side < 2) {
			return this.icons[0];
		} else
			return this.icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		this.icons = new IIcon[NUM_TEXTURES];
		for (int i = 0; i < NUM_TEXTURES; i++) {
			this.icons[i] = reg.registerIcon(NomadicTents.MODID + ":tepee_wall_" + i);
		}
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood
	 * returns 4 blocks)
	 */
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < NUM_TEXTURES; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	/**
	 * Determines the damage on the item the block drops. Used in cloth and wood.
	 */
	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	public static int getMetaForBase() {
		return 0;
	}

	public static int getMetaForRandomPattern(Random rand) {
		return rand.nextInt(NUM_PATTERNS) + 1;
	}

	public static int getMetaForRandomDesign(Random rand) {
		return rand.nextInt(NUM_TEXTURES - NUM_PATTERNS - 1) + NUM_PATTERNS + 1;
	}

	public static int getMetaForRandomDesignWithChance(Random rand) {
		return rand.nextInt(Config.TEPEE_DECORATED_CHANCE) == 0 ? rand.nextInt(NUM_TEXTURES - NUM_PATTERNS - 1) + NUM_PATTERNS + 1 : 0;
	}

	/**
	 * Traces all connected ITepeeBlock blocks (frames and tepee walls) until it
	 * finds the lower door of the tepee.
	 * 
	 * @param world the world
	 * @param pos   BlockPos to begin searching from
	 * @return BlockPos of lower tepee door if found, else null
	 **/
	private BlockPos findDoorNearby(World world, int myX, int myY, int myZ) {
		List<BlockPos> checked = new LinkedList();
		BlockPos pos = new BlockPos(myX, myY, myZ);
		while (pos != null && !(world.getBlock(pos.x, pos.y, pos.z) instanceof BlockTentDoor)) {
			System.out.println("checking for door at " + pos);
			pos = getNextTepeeBlock(world, checked, pos);
		}
		if (pos == null)
			return null;
		boolean isLower = world.getBlockMetadata(pos.x, pos.x, pos.x) % 4 == 0;
		// debug:
		System.out.println("Found a door at " + pos);
		System.out.println("isLower = " + isLower);
		return isLower ? pos : new BlockPos(pos.x, pos.y - 1, pos.z);
	}

	/**
	 * Searches a 3x3x3 box for an ITepeeBlock that has not been added to the list
	 * already.
	 * 
	 * @param worldIn the world
	 * @param exclude list of BlockPos already checked
	 * @param pos     center of the 3x3x3 box
	 **/
	private BlockPos getNextTepeeBlock(World worldIn, List<BlockPos> exclude, BlockPos pos) {
		int radius = 1;
		// favor blocks below this one - useful because most tepee blocks will be above
		// the door
		for (int y = -radius; y <= radius; y++) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					int x0 = pos.x + x;
					int y0 = pos.y + y;
					int z0 = pos.z + z;
					BlockPos checkPos = new BlockPos(x0, y0, z0);
					Block at = worldIn.getBlock(x0, y0, z0);
					if (!exclude.contains(checkPos)) {
						if (at instanceof ITepeeBlock || at instanceof IFrameBlock) {
							exclude.add(checkPos);
							return checkPos;
						}
					}
				}
			}
		}
		return null;
	}

	private static class BlockPos {
		public final int x, y, z;

		BlockPos(int x0, int y0, int z0) {
			this.x = x0;
			this.y = y0;
			this.z = z0;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof BlockPos) {
				BlockPos p = (BlockPos) o;
				return p.x == this.x && p.y == this.y && p.z == this.z;
			}
			return false;
		}

		@Override
		public String toString() {
			return "[x=" + x + ", y=" + y + ", z=" + z + "]";
		}
	}
}
