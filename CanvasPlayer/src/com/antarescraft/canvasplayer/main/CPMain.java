/*
 * Author: Kloudy
 * antarescraft.com
 * 
 * Copyright 2015 (c) All rights reserved
 */

package com.antarescraft.canvasplayer.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.antarescraft.canvasplayer.animation.AnimationLauncher;
import com.antarescraft.canvasplayer.animation.FramePlayer;
import com.antarescraft.canvasplayer.events.OnCommandEvent;
import com.antarescraft.canvasplayer.events.OnPlayerInteractEntityEvent;

public class CPMain extends JavaPlugin
{
	public static Hashtable<String, AnimatedGif> Gifs;
	public static Hashtable<String, CanvasPlayer> CanvasPlayers;
	public static Hashtable<String, FramePlayer> ActiveFramePlayers;
	//public static Hashtable<String, ArrayList<BufferedImage>> GifImages;
	public static Plugin PluginInstance;
	public static Logger logger;
	
	//<UUID of players creating CanvasPlayers, name of the CanvasPlayer>
	public static Hashtable<UUID, String> Creators;
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable()
	{
		logger = getLogger();
		PluginInstance = this;
		Gifs = new Hashtable<String, AnimatedGif>();
		CanvasPlayers = new Hashtable<String, CanvasPlayer>();
		ActiveFramePlayers = new Hashtable<String, FramePlayer>();
		Creators = new Hashtable<UUID, String>();
		//GifImages = new Hashtable<String, ArrayList<BufferedImage>>();
		
		getCommand("cp").setExecutor(new OnCommandEvent());
		getServer().getPluginManager().registerEvents(new OnPlayerInteractEntityEvent(), this);
		
		saveDefaultConfig();
		
		try
		{
			File folder = new File("plugins/CanvasPlayer");
			
			if(!folder.exists())
			{
				folder.mkdir();
			}
			
			folder = new File("plugins/CanvasPlayer/images");
			
			if(!folder.exists())
			{
				folder.mkdir();
			}
			
			File canvasPlayers = new File("plugins/CanvasPlayer/CanvasPlayers.dat");
			
			if(!canvasPlayers.exists())
			{
				canvasPlayers.createNewFile();
			}
			
			//load LEDArrays
			FileInputStream fileIn = new FileInputStream(canvasPlayers);
			ObjectInputStream in = null;
			
			if(canvasPlayers.length() > 0)
			{				
				in = new ObjectInputStream(fileIn);
				CanvasPlayers = (Hashtable<String, CanvasPlayer>) in.readObject();
				in.close();
				
				//starts up any displays that were previously running before server reboot
				for(CanvasPlayer cp : CanvasPlayers.values())
				{
					if(cp.isRunning())
					{			
						if(imagesExist(cp.gifNames))
						{
							AnimationLauncher anim = new AnimationLauncher(null, cp);
							anim.runTaskAsynchronously(PluginInstance);
						}
						else
						{
							cp.setIsRunning(false);
						}
					}
				}
			}
		}
		catch(Exception e){}
		
		//PlayerRange range = new PlayerRange();
		//range.runTaskTimer(PluginInstance, 0, 2);
	}
	
	@Override
	public void onDisable()
	{
		try
		{
			File folder = new File("plugins/CanvasPlayer");
			
			if(!folder.exists())
			{
				folder.mkdir();
			}
			
			File canvasPlayers = new File("plugins/CanvasPlayer/CanvasPlayers.dat");
			
			if(!canvasPlayers.exists())
			{
				canvasPlayers.createNewFile();
			}
			
			FileOutputStream fileOut = new FileOutputStream("plugins/CanvasPlayer/CanvasPlayers.dat");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			//double check that the running variables are correct before righting them
			for(CanvasPlayer cp : CanvasPlayers.values())
			{
				if(ActiveFramePlayers.containsKey(cp.getName()))
				{
					cp.setIsRunning(true);
				}
				else
				{
					cp.setIsRunning(false);
				}
			}
			
			out.writeObject(CanvasPlayers);
			out.close();
		}
		catch(Exception e){}
	}
	
	public static boolean gifIsRunning(String gifName)
	{
		for(CanvasPlayer cp : CanvasPlayers.values())
		{
			if(cp.isRunning())
			{
				for(String name : cp.gifNames)
				{
					if(name.equals(gifName))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean imagesExist(ArrayList<String> imgNames)
	{
		ArrayList<String> imageNamesFromFile = getImageNames();
		
		for(String imgName : imgNames)
		{
			boolean exists = false;
			for(String fileName : imageNamesFromFile)
			{
				if(fileName.equals(imgName))
				{
					exists = true;
				}
			}
			
			if(!exists)
			{
				return false;
			}
		}
		
		return true;
	}
	
	/*return ArrayList of all image files located in /plugins/LEDArrayData/images*/
	public static ArrayList<String> getImageNames()
	{
		ArrayList<String> names = new ArrayList<String>();
		File folder = new File("plugins/CanvasPlayer/images");
		File[] files = null;
		
		if(folder.exists())
		{
			files = folder.listFiles();
			
			for(File file : files)
			{
				if(file.isFile())
				{
					names.add(file.getName().replaceAll(".dat", ""));
				}
			}
		}
		
		return names;
	}
}
