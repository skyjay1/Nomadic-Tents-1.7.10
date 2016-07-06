package com.yurtmod.blocks;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.main.Config;
import com.yurtmod.structure.StructureHelper;
import com.yurtmod.structure.StructureType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTentDoor extends TileEntity
{
	private static final String KEY_OFFSET_X = "TentOffsetX";
	private static final String KEY_OFFSET_Z = "TentOffsetZ";
	private static final String KEY_LAST_COORDS = "PlayerPreviousCoords";
	private static final String KEY_STRUCTURE_TYPE = "StructureTypeOrdinal";
	private StructureType structure = StructureType.YURT_SMALL;
	private int offsetX = 0;
	private int offsetZ = 0;	
	private double prevX = 0.0D, prevY = 64.0D, prevZ = 0.0D;
	private int prevDimID = 0;

	public TileEntityTentDoor()
	{
		super();
	}

	public TileEntityTentDoor(StructureType type)
	{
		this();
		this.setStructureType(type);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.structure = StructureType.get(nbt.getInteger(KEY_STRUCTURE_TYPE));
		this.offsetX = nbt.getInteger(KEY_OFFSET_X);
		this.offsetZ = nbt.getInteger(KEY_OFFSET_Z);
		NBTTagCompound nbtCoords = nbt.getCompoundTag(KEY_LAST_COORDS);
		this.prevX = nbtCoords.getDouble(KEY_LAST_COORDS.concat(".x"));
		this.prevY = nbtCoords.getDouble(KEY_LAST_COORDS.concat(".y"));
		this.prevZ = nbtCoords.getDouble(KEY_LAST_COORDS.concat(".z"));
		this.prevDimID = nbtCoords.getInteger(KEY_LAST_COORDS.concat(".dim"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger(KEY_STRUCTURE_TYPE, this.structure.ordinal());
		nbt.setInteger(KEY_OFFSET_X, this.offsetX);
		nbt.setInteger(KEY_OFFSET_Z, this.offsetZ);
		NBTTagCompound nbtCoords = new NBTTagCompound();
		nbtCoords.setDouble(KEY_LAST_COORDS.concat(".x"), prevX);
		nbtCoords.setDouble(KEY_LAST_COORDS.concat(".y"), prevY);
		nbtCoords.setDouble(KEY_LAST_COORDS.concat(".z"), prevZ);
		nbtCoords.setInteger(KEY_LAST_COORDS.concat(".dim"), this.getPrevDimension());
		nbt.setTag(KEY_LAST_COORDS, nbtCoords);
	}

	public void setStructureType(StructureType type)
	{
		this.structure = type;
		// debug:
		//System.out.print("structure at (" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ") now set to " + type + "\n");
	}

	public StructureType getStructureType()
	{
		return this.structure;
	}

	public void setOffsetX(int toSet)
	{
		this.offsetX = toSet;
	}

	public int getOffsetX()
	{
		return this.offsetX;
	}

	public void setOffsetZ(int toSet)
	{
		this.offsetZ = toSet;
	}

	public int getOffsetZ()
	{
		return this.offsetZ;
	}

	public void setOverworldXYZ(double x, double y, double z)
	{
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
	}

	public void setPrevDimension(int dimID)
	{
		this.prevDimID = dimID;
	}

	public int getPrevDimension()
	{
		return this.prevDimID;
	}

	public double[] getOverworldXYZ()
	{
		return new double[] {this.prevX, this.prevY, this.prevZ};
	}

	private int[] getXYZFromOffsets()
	{
		int x = this.offsetX * (StructureHelper.MAX_SQ_WIDTH);
		int y = StructureHelper.FLOOR_Y + 1;
		int z = this.offsetZ * (StructureHelper.MAX_SQ_WIDTH);
		return new int[] {x,y,z};
	}

	public boolean onPlayerActivate(EntityPlayer player)
	{
		if(player.ridingEntity == null && player.riddenByEntity == null && player instanceof EntityPlayerMP)
		{
			MinecraftServer mcServer = MinecraftServer.getServer();
			EntityPlayerMP playerMP = (EntityPlayerMP)player;
			// where the corresponding structure is in Tent dimension
			int[] corners = getXYZFromOffsets();
			int dimensionFrom = playerMP.worldObj.provider.dimensionId;

			if(playerMP.timeUntilPortal > 0)
			{
				playerMP.timeUntilPortal = 10;
			}
			else if(!TentDimension.isTent(playerMP.worldObj))
			{
				// remember the player's coordinates from the previous dimension
				this.setOverworldXYZ(playerMP.posX, playerMP.posY, playerMP.posZ);
				this.setPrevDimension(dimensionFrom);

				TentTeleporter tel = new TentTeleporter(
						dimensionFrom, mcServer.worldServerForDimension(Config.DIMENSION_ID), 
						corners[0], corners[1], corners[2],
						this.prevX, this.prevY, this.prevZ, this.structure);
				// teleport the player to Tent Dimension
				playerMP.timeUntilPortal = 10;	
				mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, Config.DIMENSION_ID, tel);	
			}
			else if(playerMP.worldObj.provider.dimensionId == Config.DIMENSION_ID)
			{
				TentTeleporter tel = new TentTeleporter(
						dimensionFrom, mcServer.worldServerForDimension(this.getPrevDimension()), 
						corners[0], corners[1], corners[2],
						this.prevX, this.prevY, this.prevZ, this.structure);
				// teleport player to overworld
				playerMP.timeUntilPortal = 10;
				mcServer.getConfigurationManager().transferPlayerToDimension(playerMP, this.getPrevDimension(), tel);
			}
			return true;
		}
		return false;
	}
}
