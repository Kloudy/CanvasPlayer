package com.antarescraft.canvasplayer.events;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.antarescraft.canvasplayer.main.CPMain;
import com.antarescraft.canvasplayer.main.CanvasPlayer;

public class OnPlayerInteractEntityEvent implements Listener
{
	@EventHandler
	public void playerInteract(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		
		if(player.hasPermission("cp.use") && CPMain.Creators.containsKey(player.getUniqueId()))
		{
			Entity clickedEntity = event.getRightClicked();
			
			if(clickedEntity.getType() == EntityType.ITEM_FRAME)
			{
				ItemFrame itemFrame = (ItemFrame) clickedEntity;
				
				World world = itemFrame.getLocation().getWorld();
				
				String canvasName = CPMain.Creators.get(player.getUniqueId());
				CanvasPlayer canvasPlayer = new CanvasPlayer(world, canvasName, itemFrame);
				
				itemFrame.setCustomName(canvasName);
				itemFrame.setCustomNameVisible(true);
				
				CPMain.CanvasPlayers.put(canvasName, canvasPlayer);
				CPMain.Creators.remove(player.getUniqueId());
				
				player.sendMessage(ChatColor.GREEN + "Successfully created " + ChatColor.AQUA + "'" + canvasName + "'");
			}
		}
	}
}
