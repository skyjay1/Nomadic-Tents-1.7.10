package com.yurtmod.dimension;

import com.yurtmod.content.Content;
import com.yurtmod.content.TileEntityTentDoor;
import com.yurtmod.main.Config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
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

			if(worldServer.provider.dimensionId == Config.DIMENSION_ID)
			{				
				entityX = this.yurtCornerX + 1.5D;
				entityY = this.yurtCornerY + 0.01D;
				entityZ = this.yurtCornerZ + this.structure.getDoorPosition() + 0.5D;
				yaw = -90F;
				
				// generate the structure - each tent should check if it already exists before generating
				switch(this.structure)
				{
				case YURT_LARGE: case YURT_MEDIUM: case YURT_SMALL:
					new StructureYurt(this.structure).generateInTentDimension(prevDimID, worldServer, yurtCornerX, yurtCornerZ, prevX, prevY, prevZ);
					break;
				case TEPEE_LARGE: case TEPEE_MEDIUM: case TEPEE_SMALL:
					new StructureTepee(this.structure).generateInTentDimension(prevDimID, worldServer, yurtCornerX, yurtCornerZ, prevX, prevY, prevZ);
					break;
				default:
					StructureHelper.generatePlatform(worldServer, yurtCornerX, yurtCornerY, yurtCornerZ, 16);
					break;
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
