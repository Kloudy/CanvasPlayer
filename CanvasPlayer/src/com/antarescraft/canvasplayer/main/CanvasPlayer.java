package com.antarescraft.canvasplayer.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;

public class CanvasPlayer implements Serializable
{
	private static final long serialVersionUID = 1L;
	public ArrayList<UUID> itemFrameUUIDs;
	//public ArrayList<Short> mapIds;
	private double x, y, z;
	private String name;
	private boolean isRunning;
	public ArrayList<String> gifNames;
	private int currentMapIndex;
	private UUID worldUUID;
	public int currentGifIndex;
	
	public CanvasPlayer(World world, String name, ItemFrame itemFrame)
	{
		this.worldUUID = world.getUID();
		this.name = name;
		this.isRunning = false;
		currentMapIndex = 0;
		currentGifIndex = 0;
		
		Location loc = itemFrame.getLocation();
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		
		itemFrameUUIDs = new ArrayList<UUID>();
		gifNames = new ArrayList<String>();
		//mapIds = new ArrayList<Short>();
		
		initPlayer(world, itemFrame);
	}
	
	//@SuppressWarnings("deprecation")
	public void initPlayer(World world, ItemFrame itemFrame)
	{
		itemFrameUUIDs.add(itemFrame.getUniqueId());
	}
	
	public ItemFrame[] getItemFrames()
	{
		World world = Bukkit.getWorld(worldUUID);
		ItemFrame[] itemFrames = new ItemFrame[itemFrameUUIDs.size()];
		
		Location loc = new Location(world, x, y, z);
		Chunk chunk = world.getChunkAt(loc);
		
		for(Entity entity : chunk.getEntities())
		{
			if(entity.getType() == EntityType.ITEM_FRAME)
			{
				ItemFrame itemFrame = (ItemFrame) entity;
				int index = itemFrameIndex(itemFrame);
				
				if(index >= 0)
				{
					itemFrames[index] = itemFrame;
				}
			}
		}
		
		return itemFrames;
	}
	
	private int itemFrameIndex(ItemFrame itemFrame)
	{
		int index = -1;
		for(int i = 0; i < itemFrameUUIDs.size(); i++)
		{
			if(itemFrame.getUniqueId().equals(itemFrameUUIDs.get(i)))
			{
				index = i;
				return index;
			}
		}
		
		return index;
	}
	
	public void removePlayer()
	{
		
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public double getZ()
	{
		return z;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isRunning()
	{
		return isRunning;
	}
	
	public int getCurrentMapIndex()
	{
		return currentMapIndex;
	}
	
	public UUID getWorldUUID()
	{
		return worldUUID;
	}
	
	public void setIsRunning(boolean isRunning)
	{
		this.isRunning = isRunning;
	}
	
	public void setCurrentMapIndex(int currentMapIndex)
	{
		this.currentMapIndex = currentMapIndex;
	}
	
	public Location getLocation()
	{
		return new Location(Bukkit.getWorld(worldUUID), x, y ,z);
	}
	
	public ArrayList<AnimatedGif> getAnimatedGifs()
	{
		ArrayList<AnimatedGif> gifs = new ArrayList<AnimatedGif>();
		
		for(String gifName : gifNames)
		{
			AnimatedGif gif = CPMain.Gifs.get(gifName);
			
			if(gif != null)
			{
				gifs.add(gif);
			}
		}
		
		return gifs;
	}
}
