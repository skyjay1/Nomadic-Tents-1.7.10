package com.yurtmod.structure;

import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.main.Content;
import com.yurtmod.structure.StructureHelper.IYurtBlock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class StructureYurt extends DimensionStructureBase
{
	public static final int WALL_HEIGHT = 3;
	
	private static final int BARRIER_Y_SMALL = 4;
	private static final int BARRIER_Y_MED = 5;
	private static final int BARRIER_Y_LARGE = 6;
	
	public StructureYurt(StructureType type)
	{
		super(type);
	}

	/**
	 * Allots a space for a sized yurt in the Yurt Dimension.
	 * @param prevDimension the dimension id the player is leaving
	 * @param worldIn the world (in Yurt Dimension) to build in
	 * @param cornerX calculated by TileEntityYurtDoor
	 * @param cornerZ calculated by TileEntityYurtDoor
	 * @param overworldX the players x-pos before teleporting to Yurt
	 * @param overworldY the players y-pos before teleporting to Yurt
	 * @param overworldZ the players z-pos before teleporting to Yurt
	 **/
	public boolean generateInTentDimension(int prevDimension, World worldIn, int cornerX, int cornerZ, double overworldX, double overworldY, double overworldZ)
	{
		// check if the rest of the yurt has already been generated
		int doorZ = cornerZ;
		boolean ret = true;
		if(StructureHelper.generatePlatform(worldIn, cornerX, StructureHelper.FLOOR_Y, cornerZ, this.structure.getSqWidth()))
		{
			// make the yurt
			switch(this.structure)
			{
			case YURT_SMALL:
				ret = this.generateSmallInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
				break;
			case YURT_MEDIUM:
				ret = this.generateMedInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
				break;
			case YURT_LARGE:
				ret = this.generateLargeInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
				break;
			default: break;
			}			
		}

		// set tile entity door information
		if(ret)
		{
			doorZ = cornerZ + this.structure.getDoorPosition();
			TileEntity te = worldIn.getTileEntity(cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
			if(te != null && te instanceof TileEntityTentDoor)
			{
				TileEntityTentDoor teyd = (TileEntityTentDoor)te;
				int[] offsets = StructureHelper.getOffsetsFromXZ(cornerX, cornerZ);
				teyd.setStructureType(this.structure);
				teyd.setOffsetX(offsets[0]);
				teyd.setOffsetZ(offsets[1]);
				teyd.setOverworldXYZ(overworldX, overworldY, overworldZ);
				teyd.setPrevDimension(prevDimension);
				// debug:
				//System.out.println("OverworldXYZ = " + overworldX + "," + overworldY + "," + overworldZ);
				return ret;
			}
			else System.out.println("Error! Failed to retrive TileEntityYurtDoor at " + cornerX + ", " + (StructureHelper.FLOOR_Y + 1) + ", " + doorZ);
		}
		return false;
	}

	/** (Helper function) Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmallInOverworld(World worldIn, int doorX, int doorY, int doorZ, Block door, int dirForward)
	{
		return generateSmall(worldIn, doorX, doorY, doorZ, dirForward, door, Content.yurtWallFrame, Content.yurtRoofFrame);
	}

	/** Helper function */
	public static boolean deleteSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward)
	{
		boolean flag = generateSmall(worldIn, doorX, doorY, doorZ, dirForward, Blocks.air, Blocks.air, Blocks.air);
		if(worldIn.getTileEntity(doorX, doorY, doorZ) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(doorX, doorY, doorZ);
		}
		if(worldIn.getTileEntity(doorX, doorY + 1, doorZ) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(doorX, doorY + 1, doorZ);
		}
		return flag;
	}

	/** (Helper function) Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmallInDimension(World worldIn, int doorX, int doorY, int doorZ)
	{
		boolean success = generateSmall(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.yurtDoorSmall, Content.yurtInnerWall, Content.yurtRoof);
		// place barrier block
		int[] pos = StructureHelper.getPosFromDoor(new int[] {doorX, doorY, doorZ}, StructureHelper.yurtBarrierSmall[0], StructureHelper.yurtBarrierSmall[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlock(pos[0], doorY + BARRIER_Y_SMALL, pos[2], Content.barrier);
		// make dirt layer
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.yurtWallsSmall);
		return success;
	}

	/** Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{	
		int[] door = new int[] {doorX, doorY, doorZ};
		int[] pos;
		// make layer 1
		StructureHelper.buildLayer(worldIn, door, dirForward, wallBlock, StructureHelper.yurtWallsSmall, WALL_HEIGHT);
		// make layer 2
		StructureHelper.buildLayer(worldIn, doorX, doorY + WALL_HEIGHT, doorZ, dirForward, roofBlock, StructureHelper.yurtRoof1Small, 1);

		// make door
		int doorMeta = dirForward % 2 == 0 ? 4 : 0;
		worldIn.setBlock(doorX, doorY, doorZ, doorBlock, doorMeta, 3);
		worldIn.setBlock(doorX, doorY + 1, doorZ, doorBlock, doorMeta + 1, 3);
		return true;
	}

	/** Helper function */
	public static boolean generateMedInDimension(World worldIn, int doorX, int doorY, int doorZ)
	{
		boolean success = generateMedium(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.yurtInnerWall, Content.yurtRoof);
		// place barrier block
		int[] pos = StructureHelper.getPosFromDoor(new int[] {doorX, doorY, doorZ}, StructureHelper.yurtBarrierMed[0], StructureHelper.yurtBarrierMed[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlock(pos[0], doorY + BARRIER_Y_MED, pos[2], Content.barrier);
		// make dirt layer
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.yurtWallsMed);
		return success;
	}

	public static boolean generateMedium(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block wallBlock, Block roofBlock)
	{
		int[] door = new int[] {doorX, doorY, doorZ};
		int[] pos;
		// make layer 1
		StructureHelper.buildLayer(worldIn, door, dirForward, wallBlock, StructureHelper.yurtWallsMed, WALL_HEIGHT);
		// make layer 2
		StructureHelper.buildLayer(worldIn, doorX, doorY + WALL_HEIGHT, doorZ, dirForward, roofBlock, StructureHelper.yurtRoof1Med, 1);
		// make layer 3
		StructureHelper.buildLayer(worldIn, doorX, doorY + WALL_HEIGHT + 1, doorZ, dirForward, roofBlock, StructureHelper.yurtRoof2Med, 1);
		// make door
		int doorMeta = dirForward % 2 == 0 ? 4 : 0;
		worldIn.setBlock(doorX, doorY, doorZ, Content.yurtDoorMed, doorMeta, 3);
		worldIn.setBlock(doorX, doorY + 1, doorZ, Content.yurtDoorMed, doorMeta + 1, 3);
		return true;
	}

	/** Helper function */
	public static boolean generateLargeInDimension(World worldIn, int doorX, int doorY, int doorZ)
	{
		boolean success = generateLarge(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.yurtInnerWall, Content.yurtRoof);
		// place barrier block
		int[] pos = StructureHelper.getPosFromDoor(new int[] {doorX, doorY, doorZ}, StructureHelper.yurtBarrierLarge[0], StructureHelper.yurtBarrierLarge[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlock(pos[0], doorY + BARRIER_Y_LARGE, pos[2], Content.barrier);
		// make dirt layer
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.yurtWallsLarge);
		return success;
	}

	public static boolean generateLarge(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block wallBlock, Block roofBlock)
	{
		int[] door = new int[] {doorX, doorY, doorZ};
		int[] pos;
		// make layer 1
		StructureHelper.buildLayer(worldIn, door, dirForward, wallBlock, StructureHelper.yurtWallsLarge, WALL_HEIGHT);
		// make layer 2
		StructureHelper.buildLayer(worldIn, doorX, doorY + WALL_HEIGHT, doorZ, dirForward, roofBlock, StructureHelper.yurtRoof1Large, 1);
		// make layer 3
		StructureHelper.buildLayer(worldIn, doorX, doorY + WALL_HEIGHT + 1, doorZ, dirForward, roofBlock, StructureHelper.yurtRoof2Large, 1);
		// make layer 3
		StructureHelper.buildLayer(worldIn, doorX, doorY + WALL_HEIGHT + 2, doorZ, dirForward, roofBlock, StructureHelper.yurtRoof3Large, 1);
		// make door
		int doorMeta = dirForward % 2 == 0 ? 4 : 0;
		worldIn.setBlock(doorX, doorY, doorZ, Content.yurtDoorLarge, doorMeta, 3);
		worldIn.setBlock(doorX, doorY + 1, doorZ, Content.yurtDoorLarge, doorMeta + 1, 3);
		return true;

	}

	public static boolean canSpawnSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward)
	{
		int[] door = new int[] {doorX, doorY, doorZ};
		int[] pos = door;
		// check outer walls
		for(int layer = 0; layer < WALL_HEIGHT; layer++)
		{
			for(int[] coord : StructureHelper.yurtWallsSmall)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn.getBlock(pos[0], door[1] + layer, pos[2]).getMaterial()))
				{
					return false;
				}
			}
		}
		// check roof
		for(int[] coord : StructureHelper.yurtRoof1Small)
		{
			pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
			if(!StructureHelper.isReplaceableMaterial(worldIn.getBlock(pos[0], door[1] + WALL_HEIGHT, pos[2]).getMaterial()))
			{
				return false;
			}
		}
		// check barrier space
		pos = StructureHelper.getPosFromDoor(door, StructureHelper.yurtBarrierSmall[0], StructureHelper.yurtBarrierSmall[1], dirForward);
		if(!StructureHelper.isReplaceableMaterial(worldIn.getBlock(pos[0], doorY + BARRIER_Y_SMALL, pos[2]).getMaterial()))
		{
			return false;
		}
		// passed all checks, return true
		return true;
	}

	/** Returns -1 if not valid. Returns direction if is valid: 0=SOUTH=z++; 1=WEST=x--; 2=NORTH=z--; 3=EAST=x++  */
	public static int isValidSmall(World worldIn, int doorX, int doorBaseY, int doorZ)
	{
		int[] door = new int[] {doorX, doorBaseY, doorZ};
		int[] pos;
		// check each direction
		for(int dir = 0; dir < 4; dir++)
		{
			boolean isValid = true;
			for(int layer = 0; layer < WALL_HEIGHT; layer++)
			{
				for(int[] coord : StructureHelper.yurtWallsSmall)
				{
					pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
					Block at = worldIn.getBlock(pos[0], door[1] + layer, pos[2]);
					if(isValid && !(at instanceof IYurtBlock))
					{
						isValid = false;
					}
				}			
			}
			// check roof
			for(int[] coord : StructureHelper.yurtRoof1Small)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
				Block at = worldIn.getBlock(pos[0], door[1] + WALL_HEIGHT, pos[2]);
				if(isValid && !(at instanceof IYurtBlock))
				{
					isValid = false;
				}
			}
			
			// if it passed all the checks, it's a valid yurt
			if(isValid) return dir;
		}

		return -1;
	}
	
	public static void buildDoor(World worldIn, int doorX, int doorY, int doorZ, Block doorBlock, int dirForward)
	{
		int doorMeta = dirForward % 2 == 0 ? 4 : 0;
		worldIn.setBlock(doorX, doorY, doorZ, doorBlock, doorMeta, 3);
		worldIn.setBlock(doorX, doorY + 1, doorZ, doorBlock, doorMeta + 1, 3);
	}
}
