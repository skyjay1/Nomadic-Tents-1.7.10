package com.yurtmod.blocks;

import com.yurtmod.blocks.Categories.IYurtBlock;
import com.yurtmod.main.Content;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockYurtRoof extends BlockUnbreakable implements IYurtBlock
{
	private IIcon[] icons;

	public BlockYurtRoof() 
	{
		super(Material.cloth);
		this.setBlockTextureName(NomadicTents.MODID + ":yurt_roof_upper");
		
	}
	
	@Override
	public void onBlockAdded(World worldIn, int x, int y, int z) 
	{
		updateMetadata(worldIn, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, int myX, int myY, int myZ, Block neighbor) 
	{
		updateMetadata(worldIn, myX, myY, myZ);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(side == 0) 
		{
			return this.icons[meta % 3];
		}
		else return super.getIcon(side, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		super.registerBlockIcons(reg);
		this.icons = new IIcon[3];
		final String innerTexture = NomadicTents.MODID + ":yurt_roof_inner";
		this.icons[0] = reg.registerIcon(innerTexture + "_0");
		this.icons[1] = reg.registerIcon(innerTexture + "_1");
		this.icons[2] = reg.registerIcon(innerTexture + "_2");
	}
	
	private void updateMetadata(World worldIn, int myX, int myY, int myZ)
	{
		int metaToSet = 0;
		if(worldIn.getBlock(myX, myY, myZ + 1) == Content.barrier)
		{
			metaToSet = 2;
		}
		else if(worldIn.getBlock(myX, myY, myZ - 1) == Content.barrier)
		{
			metaToSet = 1;
		}
		else if(worldIn.getBlock(myX + 1, myY, myZ) == this)
		{
			metaToSet = worldIn.getBlockMetadata(myX + 1, myY, myZ);
		}
		else if(worldIn.getBlock(myX - 1, myY, myZ) == this)
		{
			metaToSet = worldIn.getBlockMetadata(myX - 1, myY, myZ);
		}
		worldIn.setBlockMetadataWithNotify(myX, myY, myZ, metaToSet, 3);
	}

}
