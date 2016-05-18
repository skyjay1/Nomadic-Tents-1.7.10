package com.yurtmod.content;

import com.yurtmod.dimension.StructureHelper;
import com.yurtmod.dimension.StructureType;
import com.yurtmod.dimension.TentTeleporter;
import com.yurtmod.main.Config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTentDoor extends TileEntity
{
	private static final String KEY_OFFSET_X = "TentOffsetX";
	private static final String KEY_OFFSET_Z = "TentOffsetZ";
	private static final String KEY_LAST_X = "PlayerPreviousX";
	private static final String KEY_LAST_Y = "PlayerPreviousY";
	private static final String KEY_LAST_Z = "PlayerPreviousZ";
	private static final String KEY_PREV_DIM = "PlayerPreviousDimension";
	private static final String KEY_STRUCTURE_TYPE = "StructureTypeOrdinal";
	private StructureType structure;
	private int offsetX;
	private int offsetZ;	
	private double prevX, prevY, prevZ;
	private int prevDimID;

	public TileEntityTentDoor()
	{
		//super();
		if(structure == null)
		{
			this.structure = StructureType.YURT_SMALL;
			// debug:
			//System.out.println("Warning:  structure has defaulted to YURT_SMALL");
		}
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
		int structureOrdinal = nbt.getInteger(KEY_STRUCTURE_TYPE);
		this.structure = StructureType.values()[structureOrdinal];
		this.offsetX = nbt.getInteger(KEY_OFFSET_X);
		this.offsetZ = nbt.getInteger(KEY_OFFSET_Z);
		this.prevX = nbt.getDouble(KEY_LAST_X);
		this.prevY = nbt.getDouble(KEY_LAST_Y);
		this.prevZ = nbt.getDouble(KEY_LAST_Z);
		this.prevDimID = nbt.getInteger(KEY_PREV_DIM);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setInteger(KEY_STRUCTURE_TYPE, this.structure.ordinal());
		nbt.setInteger(KEY_OFFSET_X, this.offsetX);
		nbt.setInteger(KEY_OFFSET_Z, this.offsetZ);
		nbt.setDouble(KEY_LAST_X, prevX);
		nbt.setDouble(KEY_LAST_Y, prevY);
		nbt.setDouble(KEY_LAST_Z, prevZ);
		nbt.setInteger(KEY_PREV_DIM, this.getPrevDimension());
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
			else if(playerMP.worldObj.provider.dimensionId != Config.DIMENSION_ID)
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
