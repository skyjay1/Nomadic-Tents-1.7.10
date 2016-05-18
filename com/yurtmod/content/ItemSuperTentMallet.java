package com.yurtmod.content;

import com.yurtmod.main.NomadicTents;

public class ItemSuperTentMallet extends ItemTentMallet
{	
	public ItemSuperTentMallet(ToolMaterial material)
	{
		super(material);
		this.setTextureName(NomadicTents.MODID + ":super_mallet");
	}
}
