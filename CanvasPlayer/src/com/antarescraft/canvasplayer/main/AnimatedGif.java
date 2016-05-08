package com.antarescraft.canvasplayer.main;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class AnimatedGif implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	public int currentFrame;
	public ArrayList<int[]> imageBytes;
	public ArrayList<Short> mapIds;
	private int width, height;
	
	public AnimatedGif(String name, int width, int height)
	{
		this.name = name;
		this.width = width;
		this.height = height;
		imageBytes = new ArrayList<int[]>();
		mapIds = new ArrayList<Short>();
		currentFrame = 0;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ArrayList<BufferedImage> getFrames()
	{
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		
		for(int[] bytes : imageBytes)
		{
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			image.setRGB(0, 0, width, height, bytes, 0, width);
			
			images.add(image);
		}
		
		//CPMain.GifImages.put(name, images);
		
		return images;
	}
	
	public int getFrameCount()
	{
		return imageBytes.size();
	}
	
	public short getCurrentMapId()
	{
		return mapIds.get(currentFrame);
	}
	
	@SuppressWarnings("deprecation")
	public ArrayList<MapView> getMapViews()
	{
		ArrayList<MapView> maps = new ArrayList<MapView>();
		
		for(short id : mapIds)
		{
			MapView map = Bukkit.getMap(id);

			if(map != null)
			{
				maps.add(map);
			}
		}
		
		return maps;
	}
	
	public ArrayList<ItemStack> getMapItemStacks()
	{
		ArrayList<ItemStack> mapItems = new ArrayList<ItemStack>();
		
		for(short id : mapIds)
		{
			ItemStack mapItem = new ItemStack(Material.MAP, 1, id);
			mapItems.add(mapItem);
		}
		
		return mapItems;
	}
}
