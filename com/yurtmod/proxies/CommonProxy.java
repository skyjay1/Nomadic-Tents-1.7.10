package com.yurtmod.proxies;

public class CommonProxy 
{
	public void registerRenders() {}

	/*
	public Path getOrCreateSaveBackupsFolder() throws IOException 
	{
		Path universeFolder = FMLServerHandler.instance().getSavesDirectory().toPath();
		Path backupsFolder = universeFolder.resolve(YurtMain.MODID);
		if (!Files.exists(backupsFolder)) {
			Files.createDirectory(backupsFolder);
		}
		return backupsFolder;
	}
	*/
}
