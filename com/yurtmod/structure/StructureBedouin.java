package com.yurtmod.structure;

import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.main.Content;
import com.yurtmod.structure.StructureHelper.IBedouinBlock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class StructureBedouin extends DimensionStructureBase
{
	public static final int WALL_HEIGHT = 2;

	public StructureBedouin(StructureType type)
	{
		super(type);
	}

	/**
	 * Allots a space for a sized tepee in the Tent Dimension.
	 * @param prevDimension the dimension id the player is leaving
	 * @param worldIn the world (in Tent Dimension) to build in
	 * @param cornerX calculated by TileEntityYurtDoor
	 * @param cornerZ calculated by TileEntityYurtDoor
	 * @param prevX the players x-pos before teleporting to Tepee
	 * @param prevY the players y-pos before teleporting to Tepee
	 * @param prevZ the players z-pos before teleporting to Tepee
	 **/
	public boolean generateInTentDimension(int prevDimension, World worldIn, int cornerX, int cornerZ, double prevX, double prevY, double prevZ)
	{
		// debug:
		// System.out.println("generating in dimension " + worldIn.provider.getDimensionId() + "; cornerX=" + cornerX + "; cornerZ=" + cornerZ);
		// check if the rest of the yurt has already been generated
		int doorZ = cornerZ + this.structure.getDoorPosition();
		boolean success = true;
		if(StructureHelper.generatePlatform(worldIn, cornerX, StructureHelper.FLOOR_Y, cornerZ, this.structure.getSqWidth()))
		{
			// make the yurt
			switch(this.structure)
			{
			case BEDOUIN_SMALL:
				success = this.generateSmallInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
				break;
			case BEDOUIN_MEDIUM:
				success = this.generateMedInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
				break;
			case BEDOUIN_LARGE:
				success = this.generateLargeInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
				break;
			default:
				System.out.println("Error: Tried to generate a StructureBedouin with an unsupported structure type");
				break;
			}			
		}

		// set tile entity door information
		if(success)
		{
			TileEntity te = worldIn.getTileEntity(cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
			if(te != null && te instanceof TileEntityTentDoor)
			{
				TileEntityTentDoor teyd = (TileEntityTentDoor)te;
				int[] offsets = StructureHelper.getOffsetsFromXZ(cornerX, cornerZ);
				teyd.setStructureType(this.structure);;
				teyd.setOffsetX(offsets[0]);
				teyd.setOffsetZ(offsets[1]);
				teyd.setOverworldXYZ(prevX, prevY, prevZ);
				teyd.setPrevDimension(prevDimension);
				// debug:
				//System.out.println("OverworldXYZ = " + overworldX + "," + overworldY + "," + overworldZ);
				return success;
			}
			else System.out.println("Error! Failed to retrive TileEntityTentDoor at " + cornerX + ", " + (StructureHelper.FLOOR_Y + 1) + ", " + doorZ);
		}
		return false;
	}

	public static boolean generateSmallInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		boolean flag = generateSmall(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.bedDoorSmall, Content.bedWall, Content.bedRoof);
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.bedWallsSmall);
		StructureHelper.buildFire(worldIn, Blocks.glowstone, Blocks.air, doorX + 2, doorY - 1, doorZ); 
		return flag;
	}

	/** (Helper function) Warning: does not check canSpawnSmallTepee before generating */
	public static boolean generateSmallInOverworld(World worldIn, int doorX, int doorY, int doorZ, Block door, int dirForward)
	{
		return generateSmall(worldIn, doorX, doorY, doorZ, dirForward, door, Content.bedWallFrame, Content.bedRoofFrame);
	}

	public static boolean deleteSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward)
	{
		boolean flag = generateSmall(worldIn, doorX, doorY, doorZ, dirForward, Blocks.air, Blocks.air, Blocks.air);
		// delete door TileEntity if found
		if(worldIn.getTileEntity(doorX, doorY, doorZ) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(doorX, doorY, doorZ);
		}
		if(worldIn.getTileEntity(doorX, doorY+1, doorZ) instanceof TileEntityTentDoor)
		{
			worldIn.removeTileEntity(doorX, doorY+1, doorZ);
		}
		return flag;
	}

	public static boolean generateMedInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		boolean flag = generateMedium(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.bedDoorMed, Content.bedWall, Content.bedRoof);
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.bedWallsMed);
		StructureHelper.buildFire(worldIn, Blocks.glowstone, Blocks.air, doorX + 3, doorY - 1, doorZ); 
		return flag;
	}

	public static boolean generateLargeInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		boolean flag = generateLarge(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.bedDoorLarge, Content.bedWall, Content.bedRoof);
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.bedWallsLarge);
		StructureHelper.buildFire(worldIn, Blocks.glowstone, Blocks.air, doorX + 4, doorY - 1, doorZ); 
		return flag;
	}

	/** Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{	
		int[] pos = new int[] {doorX, doorY, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, wallBlock, StructureHelper.bedWallsSmall, WALL_HEIGHT);
		pos = new int[] {doorX, doorY + WALL_HEIGHT, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof1Small, 1);
		pos = new int[] {doorX, doorY + WALL_HEIGHT + 1, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof2Small, 1);
		// door
		StructureHelper.buildDoor(worldIn, doorX, doorY, doorZ, doorBlock);
		return true;
	}

	public static boolean generateMedium(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{
		int[] pos = new int[] {doorX, doorY, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, wallBlock, StructureHelper.bedWallsMed, WALL_HEIGHT);
		pos = new int[] {doorX, doorY + WALL_HEIGHT, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof1Med, 1);
		pos = new int[] {doorX, doorY + WALL_HEIGHT + 1, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof2Med, 1);
		pos = new int[] {doorX, doorY + WALL_HEIGHT + 2, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof3Med, 1);
		// make door
		StructureHelper.buildDoor(worldIn, doorX, doorY, doorZ, doorBlock);
		return true;
	}

	public static boolean generateLarge(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
	{
		int[] pos = new int[] {doorX, doorY, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, wallBlock, StructureHelper.bedWallsLarge, WALL_HEIGHT);
		pos = new int[] {doorX, doorY + WALL_HEIGHT, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof1Large, 1);
		pos = new int[] {doorX, doorY + WALL_HEIGHT + 1, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof2Large, 1);
		pos = new int[] {doorX, doorY + WALL_HEIGHT + 2, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof3Large, 1);
		pos = new int[] {doorX, doorY + WALL_HEIGHT + 3, doorZ};
		StructureHelper.buildLayer(worldIn, pos, dirForward, roofBlock, StructureHelper.bedRoof4Large, 1);
		// make door
		StructureHelper.buildDoor(worldIn, doorX, doorY, doorZ, doorBlock);
		return true;
	}

	public static boolean canSpawnSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward)
	{
		final int[] doorPos = new int[] {doorX, doorY, doorZ};
		int[] pos;
		// check each direction
		boolean isValid = true;
		for(int layer = 0; isValid && layer < WALL_HEIGHT; layer++)
		{	
			for(int[] coord : StructureHelper.bedWallsSmall)
			{
				pos = StructureHelper.getPosFromDoor(doorPos, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn, pos))
				{
					return false;
				}
			}			
		}
		// check layer 1 of roof
		for(int[] coord : StructureHelper.bedRoof1Small)
		{
			pos = StructureHelper.getPosFromDoor(doorPos, coord[0], coord[1], dirForward);
			pos[1] += WALL_HEIGHT;
			if(!StructureHelper.isReplaceableMaterial(worldIn, pos))
			{
				return false;
			}
		}
		// check layer 2 of roof
		for(int[] coord : StructureHelper.bedRoof2Small)
		{
			pos = StructureHelper.getPosFromDoor(doorPos, coord[0], coord[1], dirForward);
			pos[1] += WALL_HEIGHT + 1;
			if(!StructureHelper.isReplaceableMaterial(worldIn, pos))
			{
				return false;
			}
		}

		// if it passed all the checks, it's a valid position
		return true;
	}

	public static int isValidSmall(World worldIn, int doorX, int doorBaseY, int doorZ)
	{
		final int[] door = new int[] {doorX, doorBaseY, doorZ};
		int[] pos;
		// check each direction
	loopDirections:
		for(int dir = 0; dir < 4; dir++)
		{
			boolean isValid = true;
			for(int layer = 0; isValid && layer < WALL_HEIGHT; layer++)
			{	
				for(int[] coord : StructureHelper.bedWallsSmall)
				{
					// debug:
					// System.out.println("checking walls layer " + layer + " at y=" + (doorBase.up(layer).getY()) + ", dir=" + dir);
					pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
					Block at = worldIn.getBlock(pos[0], pos[1] + layer, pos[2]);
					if(!(at instanceof IBedouinBlock))
					{
						isValid = false;
						continue loopDirections;
					}
				}			
			}
			// check layer 1 of roof
			for(int[] coord : StructureHelper.bedRoof1Small)
			{
				// debug:
				// System.out.println("checking roof 1 at y=" + doorBase.up(WALL_HEIGHT).getY() + ", dir=" + dir);
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
				Block at = worldIn.getBlock(pos[0], pos[1] + WALL_HEIGHT, pos[2]);
				if(!(at instanceof IBedouinBlock))
				{
					isValid = false;
					continue loopDirections;
				}
			}
			// check layer 2 of roof
			for(int[] coord : StructureHelper.bedRoof2Small)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
				Block at = worldIn.getBlock(pos[0], pos[1] + WALL_HEIGHT + 1, pos[2]);
				if(isValid && !(at instanceof IBedouinBlock))
				{
					isValid = false;
					continue loopDirections;
				}
			}

			// if it passed all the checks, it's a valid structure
			if(isValid) return dir;
		}
		return -1;
	}
}
