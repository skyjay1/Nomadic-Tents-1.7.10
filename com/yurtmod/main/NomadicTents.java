package com.yurtmod.main;

import com.yurtmod.content.Content;
import com.yurtmod.dimension.TentDimension;
import com.yurtmod.proxies.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = NomadicTents.MODID, name = NomadicTents.NAME, version = NomadicTents.VERSION, acceptedMinecraftVersions = NomadicTents.MCVERSION)
public class NomadicTents 
{
	public static final String MODID = "yurtmod";
	public static final String NAME = "Nomadic Tents Mod";
	public static final String VERSION = "1.10";
	public static final String MCVERSION = "1.7.10";
	public static final String CLIENT = "com." + MODID + ".proxies.ClientProxy";
	public static final String SERVER = "com." + MODID + ".proxies.CommonProxy";
	
	@SidedProxy(clientSide = CLIENT, serverSide = SERVER)
	public static CommonProxy proxy;
	
	public static CreativeTabs tab;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		tab = new NomadicTents.YurtTab("yurtMain");
		Config.mainRegistry(new Configuration(event.getSuggestedConfigurationFile()));
		Content.mainRegistry();
		Crafting.mainRegistry();
		TentDimension.mainRegistry();
	}
	
	@EventHandler
    public void init(FMLInitializationEvent event)
    {        
        proxy.registerRenders();
    }
	
	public static class YurtTab extends CreativeTabs
	{
		public YurtTab(String label)
		{
			super(label);
		}
		
		@Override
		public Item getTabIconItem() 
		{
			return Content.itemTent;
		}
		
	}
}
