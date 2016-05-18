package com.yurtmod.content;

import com.yurtmod.dimension.StructureType;
import com.yurtmod.main.NomadicTents;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

public class Content 
{
	// begin blocks
	public static Block barrier;
	public static Block yurtRoof;
	public static Block indestructibleDirt;
	public static Block yurtOuterWall;
	public static Block yurtInnerWall;
	public static Block yurtDoorSmall;
	public static Block yurtDoorMed;
	public static Block yurtDoorLarge;
	public static Block tepeeDoorSmall;
	public static Block tepeeDoorMed;
	public static Block tepeeDoorLarge;
	public static Block yurtWallFrame;
	public static Block yurtRoofFrame;
	public static Block tepeeFrame;
	public static Block tepeeWall;
	// begin items
	public static Item itemTent;
	public static Item itemMallet;
	public static Item itemSuperMallet;
	public static Item itemTentCanvas;
	public static Item itemYurtWall;
	public static Item itemTepeeWall;

	public static void mainRegistry()
	{
		registerBlocks();
		registerFrameBlocks();
		registerItems();
		registerTileEntitys();
	}

	private static void registerBlocks() 
	{
		// yurt door blocks
		yurtDoorSmall = new BlockTentDoor(StructureType.YURT_SMALL, "yurt").setBlockName("yurt_door_0");
		register(yurtDoorSmall);
		yurtDoorMed = new BlockTentDoor(StructureType.YURT_MEDIUM, "yurt").setBlockName("yurt_door_1");
		register(yurtDoorMed);
		yurtDoorLarge = new BlockTentDoor(StructureType.YURT_LARGE, "yurt").setBlockName("yurt_door_2");
		register(yurtDoorLarge);
		// tepee door blocks
		tepeeDoorSmall = new BlockTentDoor(StructureType.TEPEE_SMALL, "tepee").setBlockName("tepee_door_0");
		register(tepeeDoorSmall);
		tepeeDoorMed = new BlockTentDoor(StructureType.TEPEE_MEDIUM, "tepee").setBlockName("tepee_door_1");
		register(tepeeDoorMed);
		tepeeDoorLarge = new BlockTentDoor(StructureType.TEPEE_LARGE, "tepee").setBlockName("tepee_door_2");
		register(tepeeDoorLarge);
		// yurt construction blocks
		yurtOuterWall = new BlockYurtWall("yurt_wall", "yurt_wall_inner_upper").setBlockName("yurt_wall_outer");
		register(yurtOuterWall);
		yurtInnerWall = new BlockYurtWall("yurt_wall_inner", "yurt_wall_inner_upper").setBlockName("yurt_wall_inner");
		register(yurtInnerWall);
		yurtRoof = new BlockYurtRoof().setBlockName("yurt_roof");
		register(yurtRoof);
		// tepee construction blocks
		tepeeWall = new BlockTepeeWall().setBlockName("tepee_wall");
		register(tepeeWall);
		// other blocks
		indestructibleDirt = new BlockUnbreakable(Material.ground){}.setBlockName("yurt_floor").setBlockTextureName("minecraft:dirt");
		register(indestructibleDirt);
		barrier = new BlockBarrier().setBlockName("yurtmod_barrier");
		register(barrier);
	}

	private static void registerFrameBlocks() 
	{
		yurtWallFrame = new BlockTentFrame(yurtOuterWall).setBlockName("yurt_frame_wall");
		register(yurtWallFrame);
		yurtRoofFrame = new BlockTentFrame(yurtRoof).setBlockName("yurt_frame_roof");
		register(yurtRoofFrame);
		tepeeFrame = new BlockTentFrame(tepeeWall).setBlockName("tepee_frame_wall");
		register(tepeeFrame);
	}

	private static void registerItems() 
	{
		itemTent = new ItemTent().setUnlocalizedName("tent");
		register(itemTent);
		itemMallet = new ItemTentMallet(ToolMaterial.IRON).setUnlocalizedName("tent_hammer");
		register(itemMallet);
		itemSuperMallet = new ItemSuperTentMallet(ToolMaterial.IRON).setUnlocalizedName("super_tent_hammer");
		register(itemSuperMallet);
		// init crafting-only items
		itemTentCanvas = new Item().setUnlocalizedName("tent_canvas").setTextureName(NomadicTents.MODID + ":tent_canvas").setCreativeTab(NomadicTents.tab);
		register(itemTentCanvas);
		itemYurtWall = new Item().setUnlocalizedName("yurt_wall_piece").setTextureName(NomadicTents.MODID + ":yurt_wall_piece").setCreativeTab(NomadicTents.tab);
		register(itemYurtWall);
		itemTepeeWall = new Item().setUnlocalizedName("tepee_wall_piece").setTextureName(NomadicTents.MODID + ":tepee_wall_piece").setCreativeTab(NomadicTents.tab);
		register(itemTepeeWall);
	}

	private static void registerTileEntitys() 
	{
		GameRegistry.registerTileEntity(TileEntityTentDoor.class, NomadicTents.MODID + "_TileEntityTentDoor");
	}

	private static void register(Item in)
	{
		GameRegistry.registerItem(in, in.getUnlocalizedName());
	}

	private static void register(Block in)
	{
		GameRegistry.registerBlock(in, in.getUnlocalizedName());
	}
}
