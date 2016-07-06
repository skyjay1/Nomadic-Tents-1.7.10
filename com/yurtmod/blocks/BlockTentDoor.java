package com.yurtmod.blocks;

import com.yurtmod.dimension.TentDimension;
import com.yurtmod.items.ItemTentMallet;
import com.yurtmod.main.Config;
import com.yurtmod.main.Content;
import com.yurtmod.main.NomadicTents;
import com.yurtmod.structure.StructureHelper;
import com.yurtmod.structure.StructureHelper.IBedouinBlock;
import com.yurtmod.structure.StructureHelper.ITepeeBlock;
import com.yurtmod.structure.StructureHelper.IYurtBlock;
import com.yurtmod.structure.StructureType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTentDoor extends BlockUnbreakable implements ITileEntityProvider, ITepeeBlock, IYurtBlock, IBedouinBlock
{
	private final int DECONSTRUCT_DAMAGE = 5;
	private StructureType type;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] doorIcons;

	public BlockTentDoor(StructureType structure, String sPrefix)
	{
		super(Material.wood);
		this.setBlockTextureName(NomadicTents.MODID + ":" + sPrefix + "_door");
		this.type = structure;
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
			if(te != null && te instanceof TileEntityTentDoor)
			{
				TileEntityTentDoor teyd = (TileEntityTentDoor) te;
				StructureType struct = teyd.getStructureType();
				int dir = TentDimension.isTent(worldIn) ? StructureHelper.STRUCTURE_DIR : StructureHelper.isValidStructure(worldIn, struct, x, baseY, z);
				// deconstruct the tent if the player uses a tentHammer on the door (and in overworld and with fully built tent)
				if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemTentMallet && !TentDimension.isTent(worldIn))
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
		IIcon fallback = getSideIcon(side, meta);
		return side < 2 && fallback != null ? fallback : this.doorIcons[meta % doorIcons.length];
	}
	

	@SideOnly(Side.CLIENT)
	public IIcon getSideIcon(int side, int meta)
	{
		switch(this.type)
		{
		case YURT_SMALL: case YURT_MEDIUM: case YURT_LARGE: return Content.yurtInnerWall.getIcon(side, meta);
		case TEPEE_SMALL: case TEPEE_MEDIUM: case TEPEE_LARGE: return Content.tepeeWall.getIcon(side, meta);
		case BEDOUIN_SMALL: case BEDOUIN_MEDIUM: case BEDOUIN_LARGE: return Content.bedRoof.getIcon(side, meta);
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		this.doorIcons = new IIcon[2];
		this.doorIcons[0] = reg.registerIcon(this.getTextureName() + "_lower_" + this.type.ordinal() % 3);
		this.doorIcons[1] = reg.registerIcon(this.getTextureName() + "_upper_" + this.type.ordinal() % 3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileEntityTentDoor();
	}
}
