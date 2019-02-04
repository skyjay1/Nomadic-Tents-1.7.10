package com.yurtmod.dimension;

import com.yurtmod.structure.DimensionStructureBase;
import com.yurtmod.structure.StructureBedouin;
import com.yurtmod.structure.StructureHelper;
import com.yurtmod.structure.StructureTepee;
import com.yurtmod.structure.StructureType;
import com.yurtmod.structure.StructureYurt;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TentTeleporter extends Teleporter
{	
	public final StructureType structure;
	public final int yurtCornerX, yurtCornerY, yurtCornerZ;
	public final double prevX, prevY, prevZ;
	public final int prevDimID;
	public final WorldServer worldServer;

	public TentTeleporter(int dimensionFrom, WorldServer worldTo, int cornerX, int cornerY, int cornerZ, double lastX, double lastY, double lastZ, StructureType structureType) 
	{
		super(worldTo);
		this.prevDimID = dimensionFrom;
		this.worldServer = worldTo;
		this.yurtCornerX = cornerX;
		this.yurtCornerY = cornerY;
		this.yurtCornerZ = cornerZ;
		this.prevX = lastX;
		this.prevY = lastY;
		this.prevZ = lastZ;
		this.structure = structureType;
	}

	@Override
	public void placeInPortal(Entity entity, double x, double y, double z, float f)
	{
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			
			double entityX;
			double entityY;
			double entityZ;
			float yaw;
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;

			if(TentDimension.isTent(this.worldServer))
			{				
				entityX = this.yurtCornerX + 1.5D;
				entityY = this.yurtCornerY + 0.01D;
				entityZ = this.yurtCornerZ + this.structure.getDoorPosition() + 0.5D;
				yaw = -90F;
				
				// generate the structure - each tent should check if it already exists before generating
				DimensionStructureBase gen = StructureType.getGenFromStructureType(this.structure);				
				if(gen != null)
				{
					gen.generateInTentDimension(prevDimID, worldServer, yurtCornerX, yurtCornerZ, prevX, prevY, prevZ);
				}
				else
				{
					StructureHelper.generatePlatform(worldServer, yurtCornerX, yurtCornerY, yurtCornerZ, 8);
					System.out.println("Error: unhandled structure type resulted in empty platform");
				}
			}
			else
			{	
				entityX = this.prevX;
				entityY = this.prevY;
				entityZ = this.prevZ;
				yaw = entity.rotationYaw;
			}
			entity.setLocationAndAngles(entityX, entityY, entityZ, yaw, entity.rotationPitch);
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float f)
	{
		return true;
	}
	
	public String toString()
	{
		String out = "\n[TentTeleporter]\n";
		out += "structure=" + this.structure + "\n";
		out += "yurtCornerX=" + this.yurtCornerX + "\n";
		out += "yurtCornerZ=" + this.yurtCornerZ + "\n";
		out += "prevX=" + this.prevX + "\n";
		out += "prevY=" + this.prevY + "\n";
		out += "prevZ=" + this.prevZ + "\n";
		out += "prevDimID=" + this.prevDimID + "\n";
		return out;
	}
}
