package com.yurtmod.content;

import java.util.List;

import com.yurtmod.dimension.StructureHelper;
import com.yurtmod.dimension.StructureType;
import com.yurtmod.main.Config;
import com.yurtmod.main.NomadicTents;
import com.yurtmod.main.TentSaveData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemTent extends Item 
{
	public IIcon[] icons;
	public static final String OFFSET_X = "TentOffsetX";
	public static final String OFFSET_Z = "TentOffsetZ";

	public ItemTent()
	{
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) 
	{
		if(!world.isRemote)
		{
			if(itemStack.getTagCompound() == null) itemStack.setTagCompound(new NBTTagCompound());
			// determine new offset data
			adjustSaveData(itemStack, world, player);
		}
	}
	
	/**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void onUpdate(ItemStack stack, World world, Entity entity, int i0, boolean b0) 
    {
    	if(stack.getTagCompound() == null) 
    	{
    		stack.setTagCompound(new NBTTagCompound());
    	}
    	if(!stack.getTagCompound().hasKey(OFFSET_X))
    	{
    		stack.getTagCompound().setInteger(OFFSET_X, Short.MIN_VALUE);
    	}
    	if(!stack.getTagCompound().hasKey(OFFSET_Z))
    	{
    		stack.getTagCompound().setInteger(OFFSET_Z, Short.MIN_VALUE);
    	}
    }

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		return false;
	}

	@Override
	public boolean canItemEditBlocks()
	{
		return true;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) 
	{
	    return "item." + this.getStructureType(stack.getItemDamage()).toString().toLowerCase();
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) 
	{
		for(int i = 0, len = StructureType.values().length; i < len; i++)
		{
			subItems.add(new ItemStack(itemIn, 1, i));
		}
	}
	
	/**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        return this.icons[meta % this.icons.length];
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        this.icons = new IIcon[StructureType.values().length];
        for(int i = 0, len = icons.length; i < len; i++)
        {
        	this.icons[i] = reg.registerIcon(NomadicTents.MODID + ":" + this.getStructureType(i).toString().toLowerCase());
        }
    }

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player)
	{
		if(worldIn.provider.dimensionId != Config.DIMENSION_ID && !worldIn.isRemote)
		{
			MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(worldIn, player, true);

			if (movingobjectposition == null)
			{
				return stack;
			}
			else if(hasInvalidCoords(stack))
			{
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + StatCollector.translateToLocal("chat.no_structure_ln1")));
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + StatCollector.translateToLocal("chat.no_structure_ln2")));
				return stack;
			}
			else if(movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				int d = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360) + 0.50) & 3;
				int i = movingobjectposition.blockX;
				int j = movingobjectposition.blockY + 1;
				int k = movingobjectposition.blockZ;
				boolean hitTop = movingobjectposition.sideHit == 1;
				Block clicked = worldIn.getBlock(i, j, k);
				int meta = stack.getItemDamage();
				if(clicked == Blocks.snow_layer || clicked.getMaterial() == Material.plants)
				{
					hitTop = true;
					j--;
					// debug:
					//System.out.println("You clicked on a replaceable material, the yurt will replace it.");
				}

				if (!player.canPlayerEdit(i, j, k, hitTop ? 1 : movingobjectposition.sideHit, stack))
				{
					return stack;
				}
				else if(hitTop)
				{
					// debug:
					//System.out.println("Trying to generate a Yurt...");
					if(StructureHelper.canSpawnStructureHere(worldIn, this.getStructureType(meta), i,j,k, d))
					{
						Block door = this.getStructureType(meta).getDoorBlock();
						if(StructureHelper.generateSmallStructureOverworld(worldIn, this.getStructureType(meta), i,j,k, d))
						{
							// lower door:
							TileEntity te = worldIn.getTileEntity(i,j,k);
							if(te != null && te instanceof TileEntityTentDoor)
							{
								this.getStructureType(meta).applyToTileEntity(player, stack, (TileEntityTentDoor)te);
							}
							else System.out.println("Error! Failed to retrieve TileEntityTentDoor at " + i + ", " + j + ", " + k);
							// remove tent from inventory
							stack.stackSize--;
						}
					}
				}
			}
		}
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List par3List, boolean par4)
	{
		par3List.add(this.getTooltipColor(stack.getItemDamage()) + StatCollector.translateToLocal("tooltip.extra_dimensional_space"));
	}
	
	public boolean hasInvalidCoords(ItemStack stack)
	{
		if(stack.getTagCompound() != null) 
		{
			return stack.getTagCompound().getInteger(OFFSET_X) == Short.MIN_VALUE && stack.getTagCompound().getInteger(OFFSET_Z) == Short.MIN_VALUE;
		}
		return true;
	}

	public StructureType getStructureType(int meta)
	{
		return StructureType.values()[meta % StructureType.values().length];
	}

	public void adjustSaveData(ItemStack stack, World world, EntityPlayer player)
	{
		TentSaveData data = TentSaveData.forWorld(world);
		StructureType struct = getStructureType(stack.getItemDamage());
		stack.getTagCompound().setInteger(OFFSET_Z, struct.getTagOffsetZ());
		switch(struct)
		{
		case TEPEE_LARGE:	
			data.addCountTepeeLarge(1);
			data.addCountTepeeMed(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountTepeeLarge());		
			break;
		case TEPEE_MEDIUM:
			data.addCountTepeeMed(1);
			data.addCountTepeeSmall(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountTepeeMed());
			break;
		case TEPEE_SMALL:
			data.addCountTepeeSmall(1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountTepeeSmall());
			break;
		case YURT_LARGE:
			data.addCountYurtLarge(1);
			data.addCountYurtMed(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountYurtLarge());
			break;
		case YURT_MEDIUM:
			data.addCountYurtMed(1);
			data.addCountYurtSmall(-1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountYurtMed());
			break;
		case YURT_SMALL:
			data.addCountYurtSmall(1);
			stack.getTagCompound().setInteger(OFFSET_X, data.getCountYurtSmall());
			break;
		default:
			break;
		}
	}

	public EnumChatFormatting getTooltipColor(int meta)
	{
		switch(meta)
		{
		case 0: case 3: return EnumChatFormatting.RED;
		case 1: case 4: return EnumChatFormatting.BLUE;
		case 2: case 5: return EnumChatFormatting.GREEN;
		default: 		return EnumChatFormatting.GRAY;
		}
	}
}