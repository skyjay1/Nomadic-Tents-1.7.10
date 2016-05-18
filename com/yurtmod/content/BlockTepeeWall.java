package com.yurtmod.content;

import java.util.List;
import java.util.Random;

import com.yurtmod.dimension.StructureHelper.ITepeeBlock;
import com.yurtmod.main.Config;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockTepeeWall extends BlockUnbreakable implements ITepeeBlock
{
	private static final int NUM_TEXTURES = 15;		// how many textures can apply to this block
	private static final int NUM_PATTERNS = 5;		// the first X textures excluding texture 0 are patterns
	private IIcon[] icons;

	public BlockTepeeWall() 
	{
		super(Material.cloth);
		this.setBlockTextureName(NomadicTents.MODID + ":tepee_wall_0");
	}
	
	@Override
	public void onBlockAdded(World worldIn, int x, int y, int z) 
	{
		if(worldIn.getBlockMetadata(x, y, z) == 0)
		{
			int metaToSet;
			int flag = this.findDoorNearby(worldIn, x, y, z, 4);
			// debug:
			// System.out.println("searched for door. y=" + y + ", flag=" + flag);
			if(worldIn.provider.dimensionId != Config.DIMENSION_ID && flag > -999 && flag % 2 == 0)
			{
				// psuedo-random: hopefully guarantees all blocks on same y are same meta
				metaToSet = getMetaForRandomPattern(new Random(y * flag));
				worldIn.setBlockMetadataWithNotify(x, y, z, metaToSet, 2);
			}
			else if(worldIn.rand.nextInt(Config.TEPEE_DECORATED_RATIO) == 0)
			{
				metaToSet = getMetaForRandomDesign(worldIn.rand);
				worldIn.setBlockMetadataWithNotify(x, y, z, metaToSet, 2);
			}			
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(side < 2) 
		{
			return this.icons[0];
		}
		else return this.icons[meta];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		super.registerBlockIcons(reg);
		this.icons = new IIcon[NUM_TEXTURES];
		for(int i = 0; i < NUM_TEXTURES; i++)
		{
			this.icons[i] = reg.registerIcon(NomadicTents.MODID + ":tepee_wall_" + i);
		}
	}
	
	/**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
    	for(int i = 0; i < NUM_TEXTURES; i++)
    	{
    		list.add(new ItemStack(item, 1, i));
    	}
    }
    
    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }
    
    public static int getMetaForBase()
    {
    	return 0;
    }
    
    public static int getMetaForRandomPattern(Random rand)
    {
    	return rand.nextInt(NUM_PATTERNS) + 1;
    }
    
    public static int getMetaForRandomDesign(Random rand)
    {
    	return rand.nextInt(NUM_TEXTURES - NUM_PATTERNS - 1) + NUM_PATTERNS + 1;
    }
    
    /** @return y-difference or -999 if no door is found (must be lower door block) **/
    private int findDoorNearby(World world, int x, int y, int z, int radius)
    {
    	for(int i = -radius; i < radius; i++)
    	{
    		for(int j = -radius; j < radius; j++)
    		{
    			for(int k = -radius; k < radius; k++)
        		{
        			if(world.getBlock(x + i, y + j, z + k) instanceof BlockTentDoor && world.getBlock(x + i, y + j + 1, z + k) instanceof BlockTentDoor)
        			{
        				return j;
        			}
        		}
    		}
    	}
    	return -999;
    }
}
