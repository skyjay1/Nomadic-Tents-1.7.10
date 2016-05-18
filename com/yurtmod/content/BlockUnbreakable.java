package com.yurtmod.content;

import com.yurtmod.main.NomadicTents;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public abstract class BlockUnbreakable extends Block
{
	public BlockUnbreakable(Material material)
	{
		super(material);
		this.setBlockUnbreakable();
		this.setHardness(100F);
		this.setResistance(6000000.0F);
		this.setCreativeTab(NomadicTents.tab);
	}
}
