package com.yurtmod.structure;

import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.main.Content;
import com.yurtmod.structure.StructureHelper.ITepeeBlock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class StructureTepee extends DimensionStructureBase
{
	public static final int LAYER_DEPTH = 2;

	public StructureTepee(StructureType type) 
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
		//System.out.println("generating in dimension " + worldIn.provider.dimensionId + "; cornerX=" + cornerX + "; cornerZ=" + cornerZ);
		// check if the rest of the yurt has already been generated
		int doorZ = cornerZ + this.structure.getDoorPosition();
		boolean ret = true;
		if(StructureHelper.generatePlatform(worldIn, cornerX, StructureHelper.FLOOR_Y, cornerZ, this.structure.getSqWidth()))
		{
			// make the yurt
			switch(this.structure)
			{
			case TEPEE_SMALL:
				ret = this.generateSmallInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
				break;
			case TEPEE_MEDIUM:
				ret = this.generateMedInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
				break;
			case TEPEE_LARGE:
				ret = this.generateLargeInDimension(worldIn, cornerX, StructureHelper.FLOOR_Y + 1, doorZ);
				break;
			default: 
				System.out.println("Error: Tried to generate a StructureBedouin with an unsupported structure type");
				break;
			}			
		}

		// set tile entity door information
		if(ret)
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
				return ret;
			}
			else System.out.println("Error! Failed to retrive TileEntityTentDoor at " + cornerX + ", " + (StructureHelper.FLOOR_Y + 1) + ", " + doorZ);
		}
		return false;
	}

	public static boolean generateSmallInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		boolean success = generateSmall(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.tepeeDoorSmall, Content.tepeeWall, null);
		// place barrier block
		int[] pos = StructureHelper.getPosFromDoor(new int[] {doorX, doorY, doorZ}, StructureHelper.tepeeBarrierSmall[0], StructureHelper.tepeeBarrierSmall[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlock(pos[0], doorY + LAYER_DEPTH * 3, pos[2], Content.barrier);
		// dirt floor
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.tepeeLayer1Small);
		return success;

	}

	/** (Helper function) Warning: does not check canSpawnSmallTepee before generating */
	public static boolean generateSmallInOverworld(World worldIn, int doorX, int doorY, int doorZ, Block door, int dirForward)
	{
		return generateSmall(worldIn, doorX, doorY, doorZ, dirForward, door, Content.tepeeFrame, null);
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

	public static boolean generateMedInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		boolean success = generateMedium(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.tepeeDoorMed, Content.tepeeWall, null);
		// place barrier block
		int[] pos = StructureHelper.getPosFromDoor(new int[] {doorX, doorY, doorZ}, StructureHelper.tepeeBarrierMed[0], StructureHelper.tepeeBarrierMed[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlock(pos[0], doorY + LAYER_DEPTH * 3, pos[2], Content.barrier);
		// make dirt layer
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.tepeeLayer1Med);
		return success;
	}

	public static boolean generateLargeInDimension(World worldIn, int doorX, int doorY, int doorZ) 
	{
		boolean flag = generateLarge(worldIn, doorX, doorY, doorZ, StructureHelper.STRUCTURE_DIR, Content.tepeeDoorLarge, Content.tepeeWall, null);
		// place barrier block
		int[] pos = StructureHelper.getPosFromDoor(new int[] {doorX, doorY, doorZ}, StructureHelper.tepeeBarrierLarge[0], StructureHelper.tepeeBarrierLarge[1], StructureHelper.STRUCTURE_DIR);
		worldIn.setBlock(pos[0], doorY + LAYER_DEPTH * 5, pos[2], Content.barrier);
		// make dirt layer if required
		StructureHelper.refinePlatform(worldIn, doorX, doorY, doorZ, StructureHelper.tepeeLayer1Large);
		StructureHelper.buildFire(worldIn, Blocks.netherrack, doorX + 4, doorY - 1, doorZ);
		return flag;
	}

	/** Warning: does not check canSpawnSmallYurt before generating */
	public static boolean generateSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
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
		// door
		StructureHelper.buildDoor(worldIn, doorX, doorY, doorZ, doorBlock);
		return true;
	}

	public static boolean generateMedium(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
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
		// make door
		StructureHelper.buildDoor(worldIn, doorX, doorY, doorZ, doorBlock);
		return true;
	}

	public static boolean generateLarge(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block doorBlock, Block wallBlock, Block roofBlock)
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
		// make door
		StructureHelper.buildDoor(worldIn, doorX, doorY, doorZ, doorBlock);
		return true;
	}

	public static boolean canSpawnSmall(World worldIn, int doorX, int doorY, int doorZ, int dirForward)
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
		// debug:
		//System.out.print("canSpawnHere returned true\n");
		return true;
	}

	public static int isValidSmall(World worldIn, int doorX, int doorBaseY, int doorZ) 
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
