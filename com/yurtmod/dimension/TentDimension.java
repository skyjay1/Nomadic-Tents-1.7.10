package com.yurtmod.dimension;

import com.yurtmod.main.Config;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class TentDimension 
{
	public static final String DIM_NAME = "Tent Dimension";
	
	public static void mainRegistry()
	{
		DimensionManager.registerProviderType(Config.DIMENSION_ID, WorldProviderTent.class, false);
		DimensionManager.registerDimension(Config.DIMENSION_ID, Config.DIMENSION_ID);
	}	
	
	public static boolean isTent(World world)
	{
		return isTent(world.provider.dimensionId);
	}
	
	public static boolean isTent(int dimID)
	{
		return dimID == Config.DIMENSION_ID;
	}
}
