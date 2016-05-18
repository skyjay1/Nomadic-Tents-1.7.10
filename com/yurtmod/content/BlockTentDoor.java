package com.yurtmod.content;

import com.yurtmod.dimension.StructureHelper;
import com.yurtmod.dimension.StructureHelper.ITepeeBlock;
import com.yurtmod.dimension.StructureHelper.IYurtBlock;
import com.yurtmod.dimension.StructureType;
import com.yurtmod.main.Config;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTentDoor extends BlockUnbreakable implements ITileEntityProvider, ITepeeBlock, IYurtBlock
{
	private final int DECONSTRUCT_DAMAGE = 5;
	private IIcon[] doorIcons;
	private int structure;
	private String structureName;

	public BlockTentDoor(StructureType struct, String name)
	{
		super(Material.wood);
		this.structure = struct.ordinal();
		this.structureName = name;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		Material m1 = world.getBlock(x, y, z).getMaterial();
		Material m2 = world.getBlock(x, y + 1, z).getMaterial();
		return (m1 == Material.air || m1 == Material.water) && (m2 == Material.air || m2 == Material.water);
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			int meta = worldIn.getBlockMetadata(x, y, z);
			int baseY = meta % 4 == 0 ? y : y - 1;
			TileEntity te = worldIn.getTileEntity(x, baseY, z);
			int dimID = worldIn.provider.dimensionId;
			if(te != null && te instanceof TileEntityTentDoor)
			{
				TileEntityTentDoor teyd = (TileEntityTentDoor) te;
				StructureType struct = teyd.getStructureType();
				int dir = dimID == Config.DIMENSION_ID ? StructureHelper.STRUCTURE_DIR : StructureHelper.isValidStructure(worldIn, struct, x, baseY, z);
				// deconstruct the tent if the player uses a tentHammer on the door (and in overworld and with fully built tent)
				if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemTentMallet && worldIn.provider.dimensionId != Config.DIMENSION_ID)
				{
					if(dir == -1) return false;	
					// prepare a tent item to drop
					ItemStack toDrop = struct.getDropStack(teyd.getOffsetX(), teyd.getOffsetZ());
					if(toDrop != null)
					{
						// drop the tent item and damage the tool
						EntityItem dropItem = new EntityItem(worldIn, player.posX, player.posY, player.posZ, toDrop);
						dropItem.delayBeforeCanPickup = 0;
						worldIn.spawnEntityInWorld(dropItem);
						// remove the yurt structure
						StructureHelper.deleteSmallStructure(worldIn, teyd.getStructureType(), x, baseY, z, dir);
						// damage the item
						player.getHeldItem().damageItem(DECONSTRUCT_DAMAGE, player);

						return true;
					}	
				}
				else
				{
					if(dir == -1) return false;
					else return ((TileEntityTentDoor)te).onPlayerActivate(player);
				}
			}
			else System.out.println("Error! Failed to retrieve TileEntityYurtDoor at " + x + ", " + baseY + ", " + z);
		}
		return false;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		world.setBlock(x, y + 1, z, this, meta + 1, 3);
		return meta;
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta)
	{
		if(meta % 4 == 0)
		{
			// if it's on the bottom
			world.setBlockToAir(x, y + 1, z);
		}
		else
		{
			// if it's on the top
			world.setBlockToAir(x, y - 1, z);
		}
		
		if(world.getTileEntity(x, y, z) instanceof TileEntityTentDoor)
		{
			world.removeTileEntity(x, y, z);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return side < 2 ? (this.structure < 3 ? Content.yurtInnerWall.getIcon(side, 0) : Content.tepeeWall.getIcon(side, 0)) : this.doorIcons[meta % doorIcons.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		this.doorIcons = new IIcon[2];
		int suffix = StructureType.values()[this.structure].getDoorPosition() - 2;
		this.doorIcons[0] = reg.registerIcon(NomadicTents.MODID + ":" + this.structureName + "_door_lower_" + suffix);
		this.doorIcons[1] = reg.registerIcon(NomadicTents.MODID + ":" + this.structureName + "_door_upper_" + suffix);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityTentDoor();
	}
}
