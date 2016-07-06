package com.yurtmod.main;

import com.yurtmod.dimension.TentDimension;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;

public class SleepHandler 
{
	@SubscribeEvent
	public void onPlayerWake(PlayerWakeUpEvent event)
	{
		if(!event.entityPlayer.worldObj.isRemote && TentDimension.isTent(event.entityPlayer.worldObj))
		{
			// debug:
			// System.out.println("player sleep in bed in Tent Dimension: shouldSetSpawn=" + event.shouldSetSpawn());
			MinecraftServer server = MinecraftServer.getServer();
			WorldServer overworld = server.worldServerForDimension(0);
			WorldServer tentDim = server.worldServerForDimension(Config.DIMENSION_ID);
			handleSleepIn(overworld, event.setSpawn);
			handleSleepIn(tentDim, event.setSpawn);
		}
	}
	
	public void handleSleepIn(WorldServer s, boolean reset)
	{
		// debug:
		// System.out.println("dimid=" + s.provider.getDimension() + "; reset=" + reset);
		if(reset && s.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
        {
            long i = s.getWorldInfo().getWorldTime() + 24000L;
            s.getWorldInfo().setWorldTime(i - i % 24000L);
            s.updateAllPlayersSleepingFlag();
        }
	}
}
