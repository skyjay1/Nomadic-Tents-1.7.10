package com.yurtmod.dimension;

import com.yurtmod.content.Content;
import com.yurtmod.content.TileEntityTentDoor;
import com.yurtmod.dimension.StructureHelper.ITepeeBlock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class StructureTepee 
{
	public static final int LAYER_DEPTH = 2;
	
	private final StructureType structure;
		
	public StructureTepee(StructureType type)
	{
		this.structure = type;
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
		//System.out.println("generating in dimension " + worldIn.provider.dimensionId + "; cornerX=" + cornerX + "; cornerZ=" + cornerZ);
		// check if the rest of the yurt has already been generated
		int doorZ = cornerZ;
		boolean ret = true;
		if(StructureHelper.generatePlatform(worldIn, cornerX, StructureHelper.FLOOR_Y, cornerZ, this.structure.getSqWidth()))
		{
			// make the yurt
			switch(this.structure)
			{
			case TEPEE_SMALL:
				ret = this.generateSmallInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
				break;
			case TEPEE_MEDIUM:
				ret = this.generateMedInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, cornerZ + this.structure.getDoorPosition());
				break;
			case TEPEE_LARGE:
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
				teyd.setStructureType(this.structure);;
				teyd.setOffsetX(offsets[0]);
				teyd.setOffsetZ(offsets[1]);
				teyd.setOverworldXYZ(prevX, prevY, prevZ);
				teyd.setPrevDimension(prevDimension);
				// debug:
				//System.out.println("OverworldXYZ = " + overworldX + "," + overworldY + "," + overworldZ);
				return ret;
			}
			else System.out.println("Error! Failed to retrive TileEntityTentDoor at " + cornerX + ", " + (StructureHelper.FLOOR_Y + 1) + ", " + doorZ);
		}
		return false;
	}

	private static boolean generateSmallInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		return generateSmall(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.tepeeDoorSmall, Content.tepeeWall, Content.barrier, true);
	}
	
	/** (Helper function) Warning: does not check canSpawnSmallTepee before generating */
	public static boolean generateSmallInOverworld(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block door)
	{
		return generateSmall(worldIn, doorX, doorY, doorZ, dirForward, door, Content.tepeeFrame, Blocks.air, false);
	}

	/** Helper function */
	public static boolean deleteSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward)
	{
		boolean flag = generateSmall(worldIn, doorX, doorY, doorZ, dirForward, Blocks.air, Blocks.air, Blocks.air, false);
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

	public static boolean generateMedInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		return generateMedium(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.tepeeDoorMed, Content.tepeeWall, Content.barrier, true);
	}

	public static boolean generateLargeInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		boolean flag = generateLarge(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.tepeeDoorLarge, Content.tepeeWall, Content.barrier, true);
		StructureHelper.buildFire(worldIn, Blocks.netherrack, doorX + 4, doorY - 1, doorZ);
		return flag;
	}
	
	/** Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block barrier, boolean hasNegativeFloor)
	{	
		if(!worldIn.isRemote)
		{
			// debug:
			//System.out.println("generating Small Tepee");
			int[] door = new int[] {doorX, doorY, doorZ};
			int[] pos;
			// make layer 1 and 2
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 0, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer1Small);
			// make layer 3 and 4
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 1, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer2Small);
			// make layer 4 and 5
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 2, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer3Small);
			// place barrier block
			pos = StructureHelper.getPosFromDoor(door, StructureHelper.tepeeBarrierSmall[0], StructureHelper.tepeeBarrierSmall[1], dirForward);
			worldIn.setBlock(pos[0], doorY + LAYER_DEPTH * 3, pos[2], barrier);
			// make dirt layer if required
			if(hasNegativeFloor && dirForward == StructureHelper.STRUCTURE_DIR)
			{
				StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.tepeeLayer1Small);
			}
			// make door
			worldIn.setBlock(doorX, doorY, doorZ, doorBlock, 0, 3);
			worldIn.setBlock(doorX, doorY + 1, doorZ, doorBlock, 1, 3);
			return true;
		}
		return false;
	}
	
	public static boolean generateMedium(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block barrier, boolean hasNegativeFloor)
	{
		if(!worldIn.isRemote)
		{
			// debug:
			//System.out.println("generating Medium Tepee");
			int[] door = new int[] {doorX, doorY, doorZ};
			int[] pos;
			// make layer 1 and 2
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 0, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer1Med);
			// make layer 3 and 4
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 1, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer2Med);
			// make layer 5 and 6
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 2, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer3Med);
			// place barrier block
			pos = StructureHelper.getPosFromDoor(door, StructureHelper.tepeeBarrierMed[0], StructureHelper.tepeeBarrierMed[1], dirForward);
			worldIn.setBlock(pos[0], doorY + LAYER_DEPTH * 3, pos[2], barrier);
			// make dirt layer if required
			if(hasNegativeFloor && dirForward == StructureHelper.STRUCTURE_DIR)
			{
				StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.tepeeLayer1Med);
			}
			// make door
			worldIn.setBlock(doorX, doorY, doorZ, doorBlock, 0, 3);
			worldIn.setBlock(doorX, doorY + 1, doorZ, doorBlock, 1, 3);
			return true;
		}
		return false;
	}
	
	public static boolean generateLarge(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block barrier, boolean hasNegativeFloor)
	{
		if(!worldIn.isRemote)
		{
			// debug:
			//System.out.println("generating Large Tepee");
			int[] door = new int[] {doorX, doorY, doorZ};
			int[] pos;
			// make layer 1 and 2
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 0, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer1Large);
			// make layer 3 and 4
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 1, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer2Large);
			// make layer 5 and 6
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 2, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer3Large);
			// make layer 7 and 8
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 3, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer4Large);
			// make layer 7 and 8
			StructureHelper.build2TepeeLayers(worldIn, doorX, doorY + LAYER_DEPTH * 4, doorZ, dirForward, wallBlock, StructureHelper.tepeeLayer5Large);
			// place barrier block
			pos = StructureHelper.getPosFromDoor(door, StructureHelper.tepeeBarrierLarge[0], StructureHelper.tepeeBarrierLarge[1], dirForward);
			worldIn.setBlock(pos[0], doorY + LAYER_DEPTH * 5, pos[2], barrier);
			// make dirt layer if required
			if(hasNegativeFloor && dirForward == StructureHelper.STRUCTURE_DIR)
			{
				StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.tepeeLayer1Large);
			}
			// make door
			worldIn.setBlock(doorX, doorY, doorZ, doorBlock, 0, 3);
			worldIn.setBlock(doorX, doorY + 1, doorZ, doorBlock, 1, 3);
			return true;
		}
		return false;
	}
	
	public static boolean canSpawnSmallTepee(World worldIn, int doorX, int doorY, int doorZ, int dirForward)
	{
		int[] door = new int[] {doorX, doorY, doorZ};
		int[] pos = door;
		// check outer walls
		for(int layer = 0; layer < LAYER_DEPTH; layer++)
		{
			// coord = {{numForward,numRight}}
			for(int[] coord : StructureHelper.tepeeLayer1Small)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn.getBlock(pos[0], door[1] + layer, pos[2]).getMaterial()))
				{
					// debug:
					//System.out.println("canSpawnHere failed on wallsSmall: x=" + coord[0] + ", y=" + (door[1] + layer) + ", z=" + pos[2]);
					return false;
				}
			}
			for(int[] coord : StructureHelper.tepeeLayer2Small)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn.getBlock(pos[0], door[1] + LAYER_DEPTH + layer, pos[2]).getMaterial()))
				{
					// debug:
					//System.out.println("canSpawnHere failed on roof1Small: x=" + coord[0] + ", y=" + (door[1] + WALL_HEIGHT) + ", z=" + pos[2]);
					return false;
				}
			}
			for(int[] coord : StructureHelper.tepeeLayer3Small)
			{
				pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dirForward);
				if(!StructureHelper.isReplaceableMaterial(worldIn.getBlock(pos[0], door[1] + LAYER_DEPTH * 2 + layer, pos[2]).getMaterial()))
				{
					// debug:
					//System.out.println("canSpawnHere failed on roof1Small: x=" + coord[0] + ", y=" + (door[1] + WALL_HEIGHT) + ", z=" + pos[2]);
					return false;
				}
			}
		}		
		// check barrier space
		pos = StructureHelper.getPosFromDoor(door, StructureHelper.tepeeBarrierSmall[0], StructureHelper.tepeeBarrierSmall[1], dirForward);
		if(!StructureHelper.isReplaceableMaterial(worldIn.getBlock(pos[0], doorY + LAYER_DEPTH * 3, pos[2]).getMaterial()))
		{
			// debug:
			//System.out.println("canSpawnHere failed on roof1Small: x=" + coord[0] + ", y=" + (door[1] + WALL_HEIGHT) + ", z=" + pos[2]);
			return false;
		}
		// debug:
		//System.out.print("canSpawnHere returned true\n");
		return true;
	}

	public static int isValidSmallTepee(World worldIn, int doorX, int doorBaseY, int doorZ) 
	{
		int[] door = new int[] {doorX, doorBaseY, doorZ};
		int[] pos;
		// check each direction
		loopDirections:
		for(int dir = 0; dir < 4; dir++)
		{
			boolean isValid = true;
			for(int layer = 0; isValid && layer < LAYER_DEPTH; layer++)
			{
				// debug:
				//System.out.println("Checking layer1 for y = " + (doorBaseY + layer) + " for dir = " + dir + "... isValid = " + isValid);
				for(int[] coord : StructureHelper.tepeeLayer1Small)
				{
					pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
					Block at = worldIn.getBlock(pos[0], door[1] + layer, pos[2]);
					if(isValid && !(at instanceof ITepeeBlock))
					{
						isValid = false;
						continue loopDirections;
					}
				}			
			}
			for(int layer = 0; isValid && layer < LAYER_DEPTH; layer++)
			{
				for(int[] coord : StructureHelper.tepeeLayer2Small)
				{
					// debug:
					//System.out.println("Checking layer2 for y = " + (doorBaseY + layer + LAYER_HEIGHT) + " for dir = " + dir + "... isValid = " + isValid);
					pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
					Block at = worldIn.getBlock(pos[0], door[1] + layer + LAYER_DEPTH, pos[2]);
					if(isValid && !(at instanceof ITepeeBlock))
					{
						isValid = false;
						continue loopDirections;
					}
				}			
			}
			for(int layer = 0; isValid && layer < LAYER_DEPTH; layer++)
			{
				for(int[] coord : StructureHelper.tepeeLayer3Small)
				{
					// debug:
					//System.out.println("Checking layer2 for y = " + (doorBaseY + layer + LAYER_HEIGHT) + " for dir = " + dir + "... isValid = " + isValid);
					pos = StructureHelper.getPosFromDoor(door, coord[0], coord[1], dir);
					Block at = worldIn.getBlock(pos[0], door[1] + layer + LAYER_DEPTH * 2, pos[2]);
					if(isValid && !(at instanceof ITepeeBlock))
					{
						isValid = false;
						continue loopDirections;	
					}
				}			
			}
			// debug:
			//System.out.println("isValid=" + isValid + "; dir=" + dir);
			// if it passed all the checks, it's a valid yurt
			if(isValid) return dir;
		}

		return -1;
	}
}
