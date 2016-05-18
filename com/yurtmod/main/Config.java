package com.yurtmod.main;

import net.minecraftforge.common.config.Configuration;

public class Config 
{
	public static boolean ALLOW_CRAFT_SUPER_MALLET;
	public static boolean ALLOW_CRAFT_YURT_SMALL;
	public static boolean ALLOW_CRAFT_YURT_MED;
	public static boolean ALLOW_CRAFT_YURT_LARGE;
	public static boolean ALLOW_CRAFT_TEPEE_SMALL;
	public static boolean ALLOW_CRAFT_TEPEE_MED;
	public static boolean ALLOW_CRAFT_TEPEE_LARGE;
	public static boolean REQUIRE_MORE_CANVAS;
	public static boolean REQUIRE_MORE_LEATHER;
	public static boolean REQUIRE_GOLD_BLOCKS;
	//public static boolean ALLOW_BUILD_TENTS_IN_TENT_DIM;
	public static int DIMENSION_ID;
	public static int TEPEE_DECORATED_RATIO;

	private static final String CATEGORY_CRAFTING = "crafting";

	public static void mainRegistry(Configuration config)
	{
		config.load();

		ALLOW_CRAFT_SUPER_MALLET = config.getBoolean("Craft Super Mallet", CATEGORY_CRAFTING, true, 
				"Whether the Super Mallet is craftable");
		ALLOW_CRAFT_YURT_SMALL = config.getBoolean("Craft Small Yurt", CATEGORY_CRAFTING, true, 
				"Whether the Small Yurt can be crafted");
		ALLOW_CRAFT_YURT_MED = config.getBoolean("Craft Medium Yurt", CATEGORY_CRAFTING, true, 
				"Whether the Medium Yurt can be crafted");
		ALLOW_CRAFT_YURT_LARGE = config.getBoolean("Craft Large Yurt", CATEGORY_CRAFTING, true, 
				"Whether the Large Yurt can be crafted");
		ALLOW_CRAFT_TEPEE_SMALL = config.getBoolean("Craft Small Tepee", CATEGORY_CRAFTING, true, 
				"Whether the Small Tepee can be crafted");
		ALLOW_CRAFT_TEPEE_MED = config.getBoolean("Craft Medium Tepee", CATEGORY_CRAFTING, true, 
				"Whether the Medium Tepee can be crafted");
		ALLOW_CRAFT_TEPEE_LARGE = config.getBoolean("Craft Large Tepee", CATEGORY_CRAFTING, true, 
				"Whether the Large Tepee can be crafted");
		REQUIRE_GOLD_BLOCKS	= config.getBoolean("Use Gold Blocks for Super Mallet", CATEGORY_CRAFTING, false, 
				"When true, the Super Tent Mallet recipe will use gold blocks instead of enchanted golden apples");
		REQUIRE_MORE_CANVAS = config.getBoolean("Require More Canvas", CATEGORY_CRAFTING, false, 
				"When true, the Yurt Wall recipe requires 6 canvas instead of 4");
		REQUIRE_MORE_LEATHER = config.getBoolean("Require More Leather", CATEGORY_CRAFTING, false, 
				"When true, the Tepee Wall recipe requires 6 leather instead of 4");
		DIMENSION_ID = config.getInt("Tent Dimension ID", Configuration.CATEGORY_GENERAL, 68, -255, 255, 
				"The ID for the Tent Dimension. (Warning: changing this will reset all tents!)");
		TEPEE_DECORATED_RATIO = config.getInt("Tepee Design Ratio", Configuration.CATEGORY_GENERAL, 3, 1, 64, 
				"Number of plain Tepee Blocks generated for every decorated tepee block (does not affect horizontal patterns)");
		//ALLOW_BUILD_TENTS_IN_TENT_DIM = config.getBoolean("Allow Nested Tents", Configuration.CATEGORY_GENERAL, true, 
		//		"Whether new tents can be built in the Tent Dimension");

		config.save();
	}
}
