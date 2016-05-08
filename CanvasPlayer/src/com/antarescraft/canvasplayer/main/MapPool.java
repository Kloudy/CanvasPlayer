package com.antarescraft.canvasplayer.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

public class MapPool implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int usedMaps;
	private ArrayList<Short> availableMaps;
	
	public MapPool(UUID worldUID)
	{
		usedMaps = 0;
		availableMaps = new ArrayList<Short>();//keeps track of maps that were once used, but are now avaiable
	}
	
	@SuppressWarnings("deprecation")
	public ArrayList<MapView> getMaps(int amount)
	{
		int poolSize = 0;
		try
		{
			poolSize = CPMain.PluginInstance.getConfig().getInt("mappool-size");
		}
		catch(Exception e)
		{
			poolSize = 200;
		}
		
		if(usedMaps + amount < poolSize)
		{
			ArrayList<MapView> maps = new ArrayList<MapView>();
			int amountLeft = amount;
			
			//create deep copied temp list to iterate over
			ArrayList<Short> tempList = new ArrayList<Short>();
			for(short id : availableMaps)
			{
				tempList.add(id);
			}
			
			for(short id : tempList)
			{
				if(amountLeft > 0)
				{
					MapView map = Bukkit.getMap(id);
					map.getRenderers().clear();
					
					maps.add(map);
					amountLeft--;
					
					int idIndex = getIdIndex(id);
					if(idIndex > -1)
					{
						availableMaps.remove(idIndex);
					}
				}
				else
				{
					break;
				}
			}
			
			World world = Bukkit.getWorlds().get(0);
			
			for(int i = 0; i < amountLeft; i++)
			{
				MapView map = Bukkit.createMap(world);
				map.getRenderers().clear();
				
				maps.add(map);
			}
			
			usedMaps += amount;
			
			return maps;
		}
		else
		{
			return null;
		}
	}
	
	private int getIdIndex(short id)
	{
		for(int i = 0; i < availableMaps.size(); i++)
		{
			if(availableMaps.get(i) == id)
			{
				return i;
			}
		}
		
		return -1;
	}
	
	@SuppressWarnings("deprecation")
	public void freeMaps(ArrayList<MapView> maps)
	{
		for(MapView map : maps)
		{
			availableMaps.add(map.getId());
		}
		
		usedMaps -= maps.size();
	}
}
