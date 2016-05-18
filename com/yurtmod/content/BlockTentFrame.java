package com.yurtmod.content;

import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTentFrame extends BlockUnbreakable
{
	private final int CONSTRUCT_DAMAGE = 1;		// 1 in x hits damages the tool
	private final Block TO_BECOME;
	private IIcon[] frameIcons;

	protected BlockTentFrame(Block replace)
	{
		super(Material.wood);
		this.TO_BECOME = replace;
		this.setBlockTextureName(NomadicTents.MODID + ":yurt_frame");
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		return meta;
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if(player.getHeldItem() != null)
		{
			// check if it's a super mallet
			if(player.getHeldItem().getItem() == Content.itemSuperMallet)
			{
				return onSuperMalletUsed(worldIn, x, y, z, player.getHeldItem(), player);
			}
			else if(player.getHeldItem().getItem() == Content.itemMallet)
			{
				// debug:
				//System.out.print("Activated by Tent Tool\n");
				int meta = worldIn.getBlockMetadata(x, y, z);
				if(meta < frameIcons.length)
				{
					worldIn.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
					// debug:
					//System.out.print("Built up a frame block by 1 unit. My metadata is now " + worldIn.getBlockMetadata(x, y, z) + "\n");
					if(meta + 1 == frameIcons.length)
					{
						this.becomeReal(worldIn, x, y, z, player.getHeldItem(), player);
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if(meta == 0)
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
		}
		else if(meta < 6)
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
		else
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return this.frameIcons[meta % frameIcons.length];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		this.frameIcons = new IIcon[4];
		this.frameIcons[0] = reg.registerIcon(this.getTextureName() + "_0");
		this.frameIcons[1] = reg.registerIcon(this.getTextureName() + "_1");
		this.frameIcons[2] = reg.registerIcon(this.getTextureName() + "_2");
		this.frameIcons[3] = reg.registerIcon(this.getTextureName() + "_3");
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return 6;
	}

	public boolean becomeReal(World worldIn, int x, int y, int z, ItemStack mallet, EntityPlayer player)
	{
		mallet.damageItem(CONSTRUCT_DAMAGE, player);
		return !worldIn.isRemote && worldIn.setBlock(x, y, z, this.TO_BECOME);
	}

	public boolean onSuperMalletUsed(World worldIn, int myX, int myY, int myZ, ItemStack mallet, EntityPlayer player)
	{
		int metaToSet = frameIcons.length;
		worldIn.setBlockMetadataWithNotify(myX, myY, myZ, metaToSet, 3);
		this.becomeReal(worldIn, myX,myY,myZ,mallet,player);
		for(int i = -1; i < 2; i++)
		{
			for(int j = -1; j < 2; j++)
			{
				for(int k = -1; k < 2; k++)
				{
					int x = myX + i;
					int y = myY + j;
					int z = myZ + k;
					Block current = worldIn.getBlock(x,y,z);
					if(current instanceof BlockTentFrame && worldIn.getBlockMetadata(x,y,z) < metaToSet)
					{
						((BlockTentFrame) current).onSuperMalletUsed(worldIn,x,y,z,mallet,player);
					}
				}
			}
		}
		return true;
	}
}