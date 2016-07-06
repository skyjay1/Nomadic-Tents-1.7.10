package com.yurtmod.structure;

import com.yurtmod.blocks.TileEntityTentDoor;
import com.yurtmod.items.ItemTent;
import com.yurtmod.main.Content;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

public enum StructureType
{
	YURT_SMALL(5, 2),
	YURT_MEDIUM(7, 3),
	YURT_LARGE(9, 4),
	TEPEE_SMALL(5, 2),
	TEPEE_MEDIUM(7, 3),
	TEPEE_LARGE(9, 4),
	BEDOUIN_SMALL(5, 2),
	BEDOUIN_MEDIUM(7, 3),
	BEDOUIN_LARGE(9, 4);
	
	private int squareWidth;
	private int doorOffsetZ;
	private String registryName;
	
	private StructureType(int sqWidth, int doorZ)
	{
		this.squareWidth = sqWidth;
		this.doorOffsetZ = doorZ;
		this.registryName = this.toString().toLowerCase();
	}
	
	/** @return square width of the structure **/
	public int getSqWidth()
	{
		return this.squareWidth;
	}
	
	/** @return The door is this number of blocks right from the front-left corner block **/
	public int getDoorPosition()
	{
		// on z-axis in Tent Dimension
		return doorOffsetZ;
	}
	
	public ItemStack getDropStack()
	{
		return new ItemStack(Content.itemTent, 1, this.ordinal());
	}
	
	public ItemStack getDropStack(int tagChunkX, int tagChunkZ)
	{
		ItemStack stack = this.getDropStack();
		if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger(ItemTent.OFFSET_X, tagChunkX);
		stack.getTagCompound().setInteger(ItemTent.OFFSET_Z, tagChunkZ);
		return stack;
	}
	
	public void applyToTileEntity(EntityPlayer player, ItemStack stack, TileEntityTentDoor te)
	{
		int offsetx = stack.getTagCompound().getInteger(ItemTent.OFFSET_X);
		int offsetz = stack.getTagCompound().getInteger(ItemTent.OFFSET_Z);
		te.setStructureType(this.get(stack.getItemDamage()));
		te.setOffsetX(offsetx);
		te.setOffsetZ(offsetz);
		te.setOverworldXYZ(player.posX, player.posY, player.posZ);
	}
	
	public Block getDoorBlock()
	{
		switch(this)
		{
		case YURT_SMALL: return Content.yurtDoorSmall;
		case YURT_MEDIUM: return Content.yurtDoorMed;
		case YURT_LARGE: return Content.yurtDoorLarge;
		case TEPEE_SMALL: return Content.tepeeDoorSmall;
		case TEPEE_MEDIUM: return Content.tepeeDoorMed;
		case TEPEE_LARGE: return Content.tepeeDoorLarge;
		case BEDOUIN_SMALL: return Content.bedDoorSmall;
		case BEDOUIN_MEDIUM: return Content.bedDoorMed;
		case BEDOUIN_LARGE: return Content.bedDoorLarge;
		}
		return null;
	}
	
	/** @return the Z-offset of this structure type in the Tent Dimension **/
	public int getTagOffsetZ()
	{
		return this.ordinal();
	}
	
	public EnumChatFormatting getTooltipColor()
	{
		switch(this.getSqWidth())
		{
		case 5: return EnumChatFormatting.RED;
		case 7: return EnumChatFormatting.BLUE;
		case 9: return EnumChatFormatting.GREEN;
		case 11: return EnumChatFormatting.YELLOW;
		case 13: return EnumChatFormatting.AQUA;
		case 15: return EnumChatFormatting.LIGHT_PURPLE;
		default: return EnumChatFormatting.GRAY;
		}
	}
	
	public static DimensionStructureBase getGenFromStructureType(StructureType type)
	{
		switch(type)
		{
		case YURT_LARGE: case YURT_MEDIUM: case YURT_SMALL: return new StructureYurt(type);
		case TEPEE_LARGE: case TEPEE_MEDIUM: case TEPEE_SMALL: return new StructureTepee(type);
		case BEDOUIN_LARGE:	case BEDOUIN_MEDIUM: case BEDOUIN_SMALL: return new StructureBedouin(type);
		}
		return null;
	}
	
	public static String getName(ItemStack stack)
	{
		return getName(stack.getItemDamage());
	}
	
	public static String getName(int metadata)
	{
		return get(metadata).registryName;
	}
	
	public static StructureType get(int meta)
	{
		return StructureType.values()[meta % StructureType.values().length];
	}
}
