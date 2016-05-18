package com.yurtmod.dimension;

import com.yurtmod.content.BlockTepeeWall;
import com.yurtmod.content.Content;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class StructureHelper 
{
	public static final int MAX_SQ_WIDTH = 16;
	public static final int FLOOR_Y = 70;
	public static final int ERR_Y = 10;
	public static final int STRUCTURE_DIR = 3;
	public static interface IYurtBlock {}
	public static interface ITepeeBlock {}
	
	/** Handles the structure type to call the correct {@code canSpawnHere} functions. Currently only handles *_SMALL **/
	public static boolean canSpawnStructureHere(World world, StructureType structure, int doorX, int doorY, int doorZ, int dir)
	{
		switch(structure)
		{
		case TEPEE_LARGE: case TEPEE_MEDIUM: case TEPEE_SMALL:	return StructureTepee.canSpawnSmallTepee(world, doorX, doorY, doorZ, dir);
		case YURT_LARGE: case YURT_MEDIUM: case YURT_SMALL: 	return StructureYurt.canSpawnSmallYurt(world, doorX, doorY, doorZ, dir);
		default: return false;
		}
	}
	
	/** Handles the structure type to call the correct {@code isValidStructure} functions. Currently only handles *_SMALL **/
	public static int isValidStructure(World world, StructureType structure, int doorX, int doorY, int doorZ)
	{
		switch(structure)
		{
		case TEPEE_LARGE: case TEPEE_MEDIUM: case TEPEE_SMALL:	return StructureTepee.isValidSmallTepee(world, doorX, doorY, doorZ);
		case YURT_LARGE: case YURT_MEDIUM: case YURT_SMALL: 	return StructureYurt.isValidSmallYurt(world, doorX, doorY, doorZ);
		default: return -1;
		}
	}
	
	/** Handles the structure type to call the correct {@code generateSmallInOverworld} function **/
	public static boolean generateSmallStructureOverworld(World world, StructureType structure, int doorX, int doorY, int doorZ, int dir)
	{
		switch(structure)
		{
		case TEPEE_LARGE: case TEPEE_MEDIUM: case TEPEE_SMALL:	return StructureTepee.generateSmallInOverworld(world, doorX, doorY, doorZ, dir, structure.getDoorBlock());
		case YURT_LARGE: case YURT_MEDIUM: case YURT_SMALL: 	return StructureYurt.generateSmallInOverworld(world, doorX, doorY, doorZ, dir, structure.getDoorBlock());
		default: return false;
		}
	}
	
	/** Handles the structure type to call the correct {@code generateSmallInOverworld} function **/
	public static boolean deleteSmallStructure(World world, StructureType structure, int doorX, int doorY, int doorZ, int dir)
	{
		switch(structure)
		{
		case TEPEE_LARGE: case TEPEE_MEDIUM: case TEPEE_SMALL:	return StructureTepee.deleteSmall(world, doorX, doorY, doorZ, dir);
		case YURT_LARGE: case YURT_MEDIUM: case YURT_SMALL: 	return StructureYurt.deleteSmall(world, doorX, doorY, doorZ, dir);
		default: return false;
		}
	}
	
	/** Structure blocks are allowed to replace blocks of these materials */
	public static boolean isReplaceableMaterial(Material m)
	{
		return m == Material.air || m == Material.plants 
			|| m == Material.lava || m == Material.water 
			|| m == Material.leaves || m == Material.snow 
			|| m == Material.vine;
	}
	
	/** Fill the locations given by an array {{x1,z1}} with given block and given metadata **/
	public static void buildLayer(World worldIn, int[] door, int dirForward, Block block, int metadata, int[][] coordinates, int numLayers)
	{
		for(int layer = 0; layer < numLayers; layer++)
		{
			for(int[] coord : coordinates)
			{
				int[] pos = getPosFromDoor(door, coord[0], coord[1], dirForward);
				worldIn.setBlock(pos[0], pos[1] + layer, pos[2], block, metadata, 3);
			}
		}
	}
	
	/** Call buildLayer using x,y,z for door and specific metadata **/
	public static void buildLayer(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block block, int meta, int[][] coordinates, int numLayers)
	{
		buildLayer(worldIn, new int[] {doorX, doorY, doorZ}, dirForward, block, meta, coordinates, numLayers);
	}
	
	/** Call buildLayer using x,y,z for door and no specified metadata **/
	public static void buildLayer(World worldIn, int doorX, int doorY, int doorZ, int dirForward, Block block, int[][] coordinates, int numLayers)
	{
		buildLayer(worldIn, doorX, doorY, doorZ, dirForward, block, 0, coordinates, numLayers);
	}
	
	/** Call buildLayer using int[] for door and no specified metadata **/
	public static void buildLayer(World worldIn, int[] door, int dirForward, Block block, int[][] coordinates, int numLayers)
	{
		buildLayer(worldIn, door, dirForward, block, 0, coordinates, numLayers);
	}
	
	public static void build2TepeeLayers(World worldIn, int doorX, int layerY, int doorZ, int dirForward, Block wallBlock, int[][] coordinates)
	{
		int meta = wallBlock instanceof BlockTepeeWall ? BlockTepeeWall.getMetaForRandomPattern(worldIn.rand) : 0;
		buildLayer(worldIn, doorX, layerY, doorZ, dirForward, wallBlock, meta, coordinates, 1);
		meta = 0;
		buildLayer(worldIn, doorX, layerY + 1, doorZ, dirForward, wallBlock, meta, coordinates, 1);
	}
	
	/** Builds a 2-block-deep platform from (cornerX, cornerY - 1, cornerZ) 
	 * to (cornerX + sqWidth, cornerY, cornerZ + sqWidth), with the top layer
	 * regular dirt and the bottom layer indestructible dirt
	 **/
	public static boolean generatePlatform(World worldIn, int cornerX, int cornerY, int cornerZ, int sqWidth)
	{
		if(worldIn.isAirBlock(cornerX, cornerY, cornerZ) && worldIn.isAirBlock(cornerX, cornerY - 1, cornerZ))
		{
			// make a base from corner x,y,z to +x,y,+z
			for(int i = 0; i < sqWidth; i++)
			{
				for(int j = 0; j < sqWidth; j++)
				{
					if(worldIn.isAirBlock(cornerX + i, cornerY, cornerZ + j)) worldIn.setBlock(cornerX + i, cornerY, cornerZ + j, Blocks.dirt, 0, 2);
					worldIn.setBlock(cornerX + i, cornerY - 1, cornerZ + j, Content.indestructibleDirt, 0, 2);
				}
			}
			return true;
		}
		return false;
	}
	
	public static void refinePlatform(World worldIn, int doorX, int doorY, int doorZ, int[][] layer0)
	{
		for(int[] coord : layer0)
		{
			worldIn.setBlock(doorX + coord[0], doorY - 1, doorZ + coord[1], Content.indestructibleDirt);
		}
	}
	
	/** Sets the passed coordinates to {@link fuel} and places fire above it **/
	public static void buildFire(World world, Block fuel, int fuelX, int fuelY, int fuelZ)
	{
		world.setBlock(fuelX, fuelY, fuelZ, fuel);
		world.setBlock(fuelX, fuelY + 1, fuelZ, Blocks.fire);
	}
	
	/** Calculates what offset x, z to give a door or item **/
	public static int[] getOffsetsFromXZ(int actualX, int actualZ)
	{
		int offsetX = actualX / (StructureHelper.MAX_SQ_WIDTH);
		int offsetZ = actualZ / (StructureHelper.MAX_SQ_WIDTH);
		return new int[] {offsetX, offsetZ};
	}
	
	/** dirForward 0=SOUTH=z++; 1=WEST=x--; 2=NORTH=z--; 3=EAST=x++ */
	public static int[] getPosFromDoor(int[] doorPos, int disForward, int disRight, int dirForward)
	{
		int[] ret = {doorPos[0], doorPos[1], doorPos[2]};
		// get position forward of doorPos
		switch(dirForward)
		{
		case 0: ret[2] += disForward; break;
		case 1: ret[0] -= disForward; break;
		case 2: ret[2] -= disForward; break;
		case 3: ret[0] += disForward; break;
		}
		// get position to right of doorPos
		switch(dirForward)
		{
		case 0: ret[0] -= disRight; break;
		case 1: ret[2] -= disRight; break;
		case 2: ret[0] += disRight; break;
		case 3: ret[2] += disRight; break;
		}
		return ret;
	}
	
	/** Adds passed values to the array. Requires an int[][] in format {{x1, z1}} **/
	private static int[][] getShiftedArray(int[][] original, int dX, int dZ)
	{
		int[][] clone = new int[original.length][2];
		for(int i = 0, len = original.length; i < len; i++)
		{
			clone[i][0] = original[i][0] + dX;
			clone[i][1] = original[i][1] + dZ;
		}
		return clone;
	}
	
	/////////////////////////////////////////////////////////
	///////////////////// BLUEPRINTS ////////////////////////
	/////////////////////////////////////////////////////////
	
	/// format: {{numForward=dX, numRight=dZ}} from door ///
		
	/********************* SMALL YURT **********************/
	public static final int[][] yurtWallsSmall = new int[][]
	{
		{0,1},{0,0},{0,-1},{1,-2},{2,-2},{3,-2},{4,-1},{4,0},{4,1},{3,2},{2,2},{1,2}
	};
	public static final int[][] yurtRoof1Small = new int[][]
	{
		{1,1},{1,0},{1,-1},{2,-1},{3,-1},{3,0},{3,1},{2,1},{0,0},{2,-2},{4,0},{2,2}
	};
	// middle 1 block
	public static final int[] yurtBarrierSmall = new int[] {2,0};
	
	/******************** MEDIUM YURT **********************/
	public static final int[][] yurtWallsMed = new int[][]
	{
		{0,-1},{0,0},{0,1},{1,2},{2,3},{3,3},{4,3},{5,2},{6,1},{6,0},{6,-1},{5,-2},{4,-3},{3,-3},{2,-3},{1,-2}
	};	
	public static final int[][] yurtRoof1Med = new int[][]
	{
		{1,-1},{1,0},{1,1},{2,2},{3,2},{4,2},{5,1},{5,0},{5,-1},{4,-2},{3,-2},{2,-2}
	};
	public static final int[][] yurtRoof2Med = new int[][] 
	{
		{2,-1},{2,0},{2,1},{3,1},{4,1},{4,0},{4,-1},{3,-1}
	};
	public static final int[] yurtBarrierMed = new int[] {3,0};
	
	/******************** LARGE YURT **********************/
	public static final int[][] yurtWallsLarge = new int[][]
	{
		{0,-2},{0,-1},{0,0},{0,1},{0,2},{1,3},{2,4},{3,4},{4,4},{5,4},{6,4},{7,3},{8,2},{8,1},{8,0},{8,-1},{8,-2},
		{7,-3},{6,-4},{5,-4},{4,-4},{3,-4},{2,-4},{1,-3}
	};	
	public static final int[][] yurtRoof1Large = new int[][]
	{
		{1,-2},{1,-1},{1,0},{1,1},{1,2},{2,2},{2,3},{3,3},{4,3},{5,3},{6,3},{6,2},{7,2},{7,1},{7,0},
		{7,-1},{7,-2},{6,-2},{6,-3},{5,-3},{4,-3},{3,-3},{2,-3},{2,-2}
	};
	public static final int[][] yurtRoof2Large = new int[][] 
	{
		{2,-1},{2,0},{2,1},{3,1},{3,2},{4,2},{5,2},{5,1},{6,1},{6,0},{6,-1},{5,-1},{5,-2},{4,-2},{3,-2},{3,-1},
	};
	public static final int[][] yurtRoof3Large = new int[][]
	{
		{3,0},{4,1},{5,0},{4,-1}
	};
	public static final int[] yurtBarrierLarge = new int[] {4,0};
	
	/******************** SMALL TEPEE **********************/
	public static final int[][] tepeeLayer1Small = yurtWallsSmall;
	public static final int[][] tepeeLayer2Small = new int[][]
	{
		{1,-1},{1,0},{1,1},{2,1},{3,1},{3,0},{3,-1},{2,-1}
	};
	public static final int[][] tepeeLayer3Small = new int[][]
	{
		{1,0},{2,1},{3,0},{2,-1}
	};
	public static final int[] tepeeBarrierSmall = new int[] {2,0};
	
	/******************** MEDIUM TEPEE **********************/
	public static final int[][] tepeeLayer1Med = yurtWallsMed;
	public static final int[][] tepeeLayer2Med = getShiftedArray(tepeeLayer1Small, 1, 0);
	public static final int[][] tepeeLayer3Med = getShiftedArray(tepeeLayer2Small, 1, 0);
	public static final int[] tepeeBarrierMed = new int[] {3,0};
	
	/******************** LARGE TEPEE **********************/
	public static final int[][] tepeeLayer1Large = yurtWallsLarge;
	public static final int[][] tepeeLayer2Large = new int[][]
	{
		{0,-1},{0,0},{0,1},{1,2},{2,3},{3,4},{4,4},{5,4},{6,3},{7,2},{8,1},{8,0},{8,-1},{7,-2},{6,-3},{5,-4},{4,-4},{3,-4},{2,-3},{1,-2}
	};
	public static final int[][] tepeeLayer3Large = getShiftedArray(tepeeLayer1Med, 1, 0);
	public static final int[][] tepeeLayer4Large = getShiftedArray(tepeeLayer2Med, 1, 0);
	public static final int[][] tepeeLayer5Large = getShiftedArray(tepeeLayer3Med, 1, 0);
	public static final int[] tepeeBarrierLarge = new int[] {4,0};
	
}
