package com.yurtmod.main;

import com.yurtmod.content.Content;
import com.yurtmod.dimension.StructureType;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Crafting 
{
	public static void mainRegistry()
	{
		GameRegistry.addShapedRecipe(new ItemStack(Content.itemTentCanvas, 1), "X","X",'X',Item.getItemFromBlock(Blocks.wool));	
		GameRegistry.addShapedRecipe(new ItemStack(Content.itemMallet, 1), " IS"," CI","S  ",'I',Items.iron_ingot,'S',Items.stick,'C',Content.itemTentCanvas);
		// yurt wall
		if(Config.REQUIRE_MORE_CANVAS)
		{
			// 6 canvas recipe
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemYurtWall, 1), "FSF","FSF","FSF",'F',Content.itemTentCanvas,'S',Items.stick);
		}
		else
		{
			// 4 canvas recipe
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemYurtWall, 1), "FSF","FSF",'F',Content.itemTentCanvas,'S',Items.stick);
		}
		// tepee wall
		if(Config.REQUIRE_MORE_LEATHER)
		{
			// 6 canvas recipe
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemTepeeWall, 1), "FSF","FSF","FSF",'F',Items.leather,'S',Items.stick);
		}
		else
		{
			// 4 canvas recipe
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemTepeeWall, 1), "FSF","FSF",'F',Items.leather,'S',Items.stick);
		}
		
		if(Config.ALLOW_CRAFT_SUPER_MALLET)
		{
			ItemStack gold = Config.REQUIRE_GOLD_BLOCKS ? new ItemStack(Blocks.gold_block, 1) : new ItemStack(Items.golden_apple, 1, 1);
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemSuperMallet, 1), " IS"," CI","S  ",'I',gold,'S',Items.stick,'C',Content.itemTentCanvas);
		}
		if(Config.ALLOW_CRAFT_YURT_SMALL)
		{
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemTent, 1, StructureType.YURT_SMALL.ordinal()), " F ","F F",'F',Content.itemYurtWall);
		}
		if(Config.ALLOW_CRAFT_YURT_MED)
		{
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemTent, 1, StructureType.YURT_MEDIUM.ordinal()), " F ","FYF",'F',Content.itemYurtWall,'Y',new ItemStack(Content.itemTent, 1, StructureType.YURT_SMALL.ordinal()));
		}
		if(Config.ALLOW_CRAFT_YURT_LARGE)
		{
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemTent, 1, StructureType.YURT_LARGE.ordinal()), " F ","FYF",'F',Content.itemYurtWall,'Y',new ItemStack(Content.itemTent, 1, StructureType.YURT_MEDIUM.ordinal()));
		}
		if(Config.ALLOW_CRAFT_TEPEE_SMALL)
		{
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemTent, 1, StructureType.TEPEE_SMALL.ordinal()), " F ","FFF","F F",'F',Content.itemTepeeWall);
		}
		if(Config.ALLOW_CRAFT_TEPEE_MED)
		{
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemTent, 1, StructureType.TEPEE_MEDIUM.ordinal()), " F ","FFF","FTF",'F',Content.itemTepeeWall,'T',new ItemStack(Content.itemTent, 1, StructureType.TEPEE_SMALL.ordinal()));
		}
		if(Config.ALLOW_CRAFT_TEPEE_LARGE)
		{
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemTent, 1, StructureType.TEPEE_LARGE.ordinal()), " F ","FFF","FTF",'F',Content.itemTepeeWall,'T',new ItemStack(Content.itemTent, 1, StructureType.TEPEE_MEDIUM.ordinal()));
		}
	}
}
