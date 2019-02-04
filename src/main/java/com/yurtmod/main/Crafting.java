package com.yurtmod.main;

import com.yurtmod.structure.StructureType;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class Crafting 
{
	public static void mainRegistry()
	{
		ItemStack yurtWall = new ItemStack(Content.itemYurtWall, Config.NUM_YURT_WALL_OUTPUT);
		ItemStack tepeeWall = new ItemStack(Content.itemTepeeWall, Config.NUM_TEPEE_WALL_OUTPUT);

		GameRegistry.addShapedRecipe(new ItemStack(Content.itemTentCanvas, 1), "X","X",'X',Item.getItemFromBlock(Blocks.wool));	
		GameRegistry.addShapedRecipe(new ItemStack(Content.itemMallet, 1), " IS"," CI","S  ",'I',Items.iron_ingot,'S',Items.stick,'C',Content.itemTentCanvas);

		// yurt wall
		if(Config.REQUIRE_MORE_CANVAS)
		{
			// 6 canvas recipe
			GameRegistry.addShapedRecipe(yurtWall, "FSF","FSF","FSF",'F',Content.itemTentCanvas,'S',Items.stick);
		}
		else
		{
			// 4 canvas recipe
			GameRegistry.addShapedRecipe(yurtWall, "FSF","FSF",'F',Content.itemTentCanvas,'S',Items.stick);
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
		// bedouin wall
		ItemStack wool = Config.REQUIRE_CARPET ? new ItemStack(Blocks.carpet, 1, OreDictionary.WILDCARD_VALUE): new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE);
		GameRegistry.addShapedRecipe(new ItemStack(Content.itemBedWall, Config.NUM_BED_WALL_OUTPUT), "FSF","FSF",'F',wool,'S',Items.stick);
		
		if(Config.ALLOW_CRAFT_SUPER_MALLET)
		{
			ItemStack gold = Config.REQUIRE_GOLD_BLOCKS ? new ItemStack(Blocks.gold_block, 1) : new ItemStack(Items.golden_apple, 1, 1);
			GameRegistry.addShapedRecipe(new ItemStack(Content.itemSuperMallet, 1), " IS"," CI","S  ",'I',gold,'S',Items.stick,'C',Content.itemTentCanvas);
		}
		if(Config.ALLOW_CRAFT_YURT_SMALL)
		{
			GameRegistry.addShapedRecipe(StructureType.YURT_SMALL.getDropStack(), " F ","F F",'F',Content.itemYurtWall);
		}
		if(Config.ALLOW_CRAFT_YURT_MED)
		{
			GameRegistry.addShapedRecipe(StructureType.YURT_MEDIUM.getDropStack(), " F ","FYF",'F',Content.itemYurtWall,'Y',StructureType.YURT_SMALL.getDropStack());
		}
		if(Config.ALLOW_CRAFT_YURT_LARGE)
		{
			GameRegistry.addShapedRecipe(StructureType.YURT_LARGE.getDropStack(), " F ","FYF",'F',Content.itemYurtWall,'Y',StructureType.YURT_MEDIUM.getDropStack());
		}
		if(Config.ALLOW_CRAFT_TEPEE_SMALL)
		{
			GameRegistry.addShapedRecipe(StructureType.TEPEE_SMALL.getDropStack(), " F ","FFF","F F",'F',Content.itemTepeeWall);
		}
		if(Config.ALLOW_CRAFT_TEPEE_MED)
		{
			GameRegistry.addShapedRecipe(StructureType.TEPEE_MEDIUM.getDropStack(), " F ","FFF","FTF",'F',Content.itemTepeeWall,'T',StructureType.TEPEE_SMALL.getDropStack());
		}
		if(Config.ALLOW_CRAFT_TEPEE_LARGE)
		{
			GameRegistry.addShapedRecipe(StructureType.TEPEE_LARGE.getDropStack(), " F ","FFF","FTF",'F',Content.itemTepeeWall,'T',StructureType.TEPEE_MEDIUM.getDropStack());
		}
		// bedouin
		if(Config.ALLOW_CRAFT_BED_SMALL)
		{
			GameRegistry.addShapedRecipe(StructureType.BEDOUIN_SMALL.getDropStack(), " F ","F F","FFF",'F',Content.itemBedWall);
		}
		if(Config.ALLOW_CRAFT_BED_MED)
		{
			GameRegistry.addShapedRecipe(StructureType.BEDOUIN_MEDIUM.getDropStack(), " F ","FTF","FFF",'F',Content.itemBedWall,'T',StructureType.BEDOUIN_SMALL.getDropStack());
		}
		if(Config.ALLOW_CRAFT_BED_LARGE)
		{
			GameRegistry.addShapedRecipe(StructureType.BEDOUIN_LARGE.getDropStack(), " F ","FTF","FFF",'F',Content.itemBedWall,'T',StructureType.BEDOUIN_MEDIUM.getDropStack());
		}
	}
}
