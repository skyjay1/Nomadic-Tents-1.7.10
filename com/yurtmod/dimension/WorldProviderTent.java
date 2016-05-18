package com.yurtmod.dimension;

import com.yurtmod.main.Config;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderTent extends WorldProvider
{
	@Override
	public void registerWorldChunkManager()
	{
		this.setDimension(Config.DIMENSION_ID);
		this.setAllowedSpawnTypes(false, false);
		this.worldChunkMgr = new WorldChunkManager(worldObj.getSeed(), terrainType);
		this.hasNoSky = false;
	}
	
	@Override
	public String getDimensionName() 
	{
		return TentDimension.DIM_NAME;
	}
	
	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new TentChunkProvider(this.worldObj, this.dimensionId, false);
	}
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z)
    {
        return BiomeGenBase.ocean;//super.getBiomeGenForCoords(x, z);
    }
	
	@Override
	public boolean canRespawnHere()
	{
		return true;
	}
	
	@Override
	public int getRespawnDimension(EntityPlayerMP player)
	{
		return Config.DIMENSION_ID;
	}
	
	@Override
	public boolean isSurfaceWorld()
    {
        return true;
    }
}
