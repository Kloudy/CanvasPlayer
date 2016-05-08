package com.antarescraft.canvasplayer.events;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.antarescraft.canvasplayer.animation.AnimationLauncher;
import com.antarescraft.canvasplayer.animation.FramePlayer;
import com.antarescraft.canvasplayer.imageprocessing.ImageURL;
import com.antarescraft.canvasplayer.main.AnimatedGif;
import com.antarescraft.canvasplayer.main.CPMain;
import com.antarescraft.canvasplayer.main.CanvasPlayer;

public class OnCommandEvent implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("cp") && sender.hasPermission("cp.use"))
		{
			Player player = null;
			
			if(sender instanceof Player)
			{
				player = (Player)sender;
			}
			
			//led help
			if(args.length >= 1 && args[0].equalsIgnoreCase("help"))
			{
				String message = ChatColor.GOLD + "=========================\n";
				message += ChatColor.GOLD + "LED Array Commands\n";
				message += ChatColor.GREEN + "/cp" + ChatColor.GOLD + " - Credits, Author, Version\n";
				message += ChatColor.GREEN + "/cp create <canvas_name>" + ChatColor.GOLD + " - Creates new canvas player above the player's head\n";
				message += ChatColor.GREEN + "/cp image <image_name> <image_url>" + ChatColor.GOLD + " - Process new image\n";
				message += ChatColor.GREEN + "/cp start <image_name> <canvas_name>" + ChatColor.GOLD + " - Starts image on the canvas\n";
				message += ChatColor.GREEN + "/cp stop <canvas_name>" + ChatColor.GOLD + " - Stops animation on the canvas\n";
				message += ChatColor.GREEN + "/cp list [page]" + ChatColor.GOLD + " - Lists all of the created canvas player names\n";
				message += ChatColor.GREEN + "/cp imagelist [page]" + ChatColor.GOLD + " - Lists all of the created image names\n";
				message += ChatColor.GREEN + "/cp removeall" + ChatColor.GOLD + " - Deletes all canvas players\n";
				message += ChatColor.GREEN + "/cp remove player <canvas_name>" + ChatColor.GOLD + " - Deletes the canvas player\n";
				message += ChatColor.GREEN + "/cp remove image <image_name>" + ChatColor.GOLD + " - Deletes the image (image will still run if playing)\n";
				message += ChatColor.GREEN + "/cp load <image_name>" + ChatColor.GOLD + " - Loads an image into memory (for quicker start times)\n";
				message += ChatColor.GREEN + "/cp mem" + ChatColor.GOLD + " - Lists all images currently loaded in memory and memory usage\n";
				message += ChatColor.GREEN + "/cp memclean [image_name]" + ChatColor.GOLD + " - Removes images from memory\n";
				message += ChatColor.GREEN + "/cp running" + ChatColor.GOLD + " - Lists the names of canvases that are currently runnnig\n";
				message += ChatColor.GREEN + "/cp tp <canvas_name>" + ChatColor.GOLD + " - Teleport to the array\n";
				message += ChatColor.GOLD + "=========================\n";
				
				sender.sendMessage(message);
			}
			
			//cp
			else if(args.length == 0)
			{
				String message = ChatColor.GOLD + "=========================\n";
				message += ChatColor.GOLD + "CanvasPlayer - v1.0\n";
				message += ChatColor.GRAY + "Author: " + ChatColor.RED + "Kloudy\n" + ChatColor.GRAY;
				message += ChatColor.UNDERLINE + "www.antarescraft.com\n";
				message += ChatColor.GOLD + "=========================\n";
				
				sender.sendMessage(message);
			}
			
			//cp create <canvas_name>
			else if(args.length == 2 && args[0].equalsIgnoreCase("create") && player != null)
			{
				CPMain.Creators.put(player.getUniqueId(), args[1]);
				sender.sendMessage(ChatColor.GOLD + "Right click an item frame to set the display.");
			}
			
			//led start <image_name> <canvas_name>
			else if(args.length == 3 && args[0].equalsIgnoreCase("start"))
			{
				if(CPMain.CanvasPlayers.containsKey(args[2]))
				{
					ArrayList<String> gifNames = new ArrayList<String>();
					gifNames.add(args[1]);
					if(CPMain.imagesExist(gifNames))
					{
						if(!CPMain.gifIsRunning(gifNames.get(0)))
						{
							CanvasPlayer cp = CPMain.CanvasPlayers.get(args[2]);
							AnimatedGif gif = CPMain.Gifs.get(args[1]);
							
							cp.setIsRunning(true);
							cp.gifNames = new ArrayList<String>();
							cp.gifNames.add(args[1]);
							
							sender.sendMessage(ChatColor.GOLD + "Starting image...");
							
							if(CPMain.Gifs.containsKey(args[1]))
							{	
								AnimationLauncher anim = new AnimationLauncher(sender, cp);	
								
								ArrayList<AnimatedGif> gifs = new ArrayList<AnimatedGif>();
								gifs.add(gif);
								
								anim.start(gifs);
							}
							else
							{	
								//load gifs from file async
								AnimationLauncher anim = new AnimationLauncher(sender, cp);
								anim.runTaskAsynchronously(CPMain.PluginInstance);
							}
						}
						else
						{
							player.sendMessage(ChatColor.RED + "That image is already running on another canvas player!");
						}
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "That image id does not exist.");
					}
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "That canvas id does not exist.");
				}
			}
			
			//cp image <image_name> <image_url>
			else if(args.length == 3 && args[0].equalsIgnoreCase("image") && player != null)
			{
				sender.sendMessage(ChatColor.GOLD + "Processing Image...");
				
				ImageURL imageUrl = new ImageURL(player, args[2], args[1]);
				imageUrl.runTaskAsynchronously(CPMain.PluginInstance);
			}
			
			//cp stop <array_name>
			else if(args.length == 2 && args[0].equalsIgnoreCase("stop"))
			{
				if(CPMain.ActiveFramePlayers.containsKey(args[1]))
				{
					FramePlayer framePlayer = CPMain.ActiveFramePlayers.get(args[1]);
					framePlayer.cancel();
					CPMain.ActiveFramePlayers.remove(args[1]);
					
					CanvasPlayer cp = CPMain.CanvasPlayers.get(args[1]);
					
					if(cp != null)
					{
						cp.setIsRunning(false);
						cp.gifNames = new ArrayList<String>();
					}
					
					player.sendMessage(ChatColor.AQUA + "'" + args[1] + "'" + ChatColor.GREEN + " has been stopped.");
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "That canvas id doesn't exist or isn't running.");
				}
			}
			
			//cp running
			else if(args.length == 1 && args[0].equalsIgnoreCase("running"))
			{				
				String message = ChatColor.GOLD + "=========================\n";
				message += "Running LEDArrays:\n";
				
				for(CanvasPlayer cp : CPMain.CanvasPlayers.values())
				{
					if(cp.isRunning())
					{
						message += ChatColor.AQUA + "'" + cp.getName() + "'\n";
					}
				}
				message += ChatColor.GOLD + "=========================\n";
				
				sender.sendMessage(message);
			}
			
			//cp list [page]
			else if(args.length >= 1 && args[0].equalsIgnoreCase("list"))
			{
				ArrayList<CanvasPlayer> values = new ArrayList<CanvasPlayer>();
				
				for(CanvasPlayer cp : CPMain.CanvasPlayers.values())
				{
					values.add(cp);
				}
				
				int page = 0;
				
				if(args.length == 2)
				{
					try
					{
						page = Integer.parseInt(args[1]) - 1;
						
						if(page < 0)
						{
							page = 0;
						}
					}
					catch(Exception e){}
				}	
				
				String message = ChatColor.GOLD + "=========================\n";
				message += ChatColor.GOLD + "Canvas List - Page: " + (page + 1) + "\n";
				
				for(int i = page * 10; i <= ((page*10) + 10); i++)
				{
					if(i >= values.size())
					{
						break;
					}
					else
					{
						message +=  ChatColor.AQUA + values.get(i).getName() + "\n";
					}
				}
				
				message += ChatColor.GOLD + "=========================";
				
				sender.sendMessage(message);
			}
			
			//cp imagelist [page]
			else if(args.length >= 1 && args[0].equalsIgnoreCase("imagelist"))
			{
				ArrayList<String> imageNames = CPMain.getImageNames();
				int page = 0;
				
				if(args.length == 2)
				{
					try
					{
						page = Integer.parseInt(args[1]) - 1;
						
						if(page < 0)
						{
							page = 0;
						}
					}
					catch(Exception e){}
				}
				
				String message = ChatColor.GOLD + "=========================\n";
				message += ChatColor.GOLD + "Image List - Page: " + (page + 1) + "\n";
				
				for(int i = page*10; i < ((page*10) + 10); i++)
				{
					if(i >= imageNames.size())
					{
						break;
					}
					
					message += ChatColor.AQUA + "'" + imageNames.get(i) + "'\n";
				}
				
				message += ChatColor.GOLD + "=========================\n";
				
				sender.sendMessage(message);
			}
			
			else
			{
				sender.sendMessage(ChatColor.RED + "Invalid command or arguments.");
			}
			
			return true;
		}
		return false;
	}
}
