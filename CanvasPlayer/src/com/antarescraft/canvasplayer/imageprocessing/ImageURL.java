package com.antarescraft.canvasplayer.imageprocessing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import com.antarescraft.canvasplayer.main.AnimatedGif;
import com.antarescraft.canvasplayer.main.CPMain;
import com.antarescraft.canvasplayer.main.CanvasRenderer;

public class ImageURL extends BukkitRunnable
{
	private String webURL;
	private Player player;
	private String gifName;
	
	public ImageURL(Player player, String webURL, String gifName)
	{
		this.webURL = webURL;
		this.player = player;
		this.gifName = gifName;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run()
	{
		URL url = null;
		
		try 
		{
			url = new URL(webURL);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(20000);
			InputStream is = connection.getInputStream();
			
			GifDecoder decoder = new GifDecoder();
			int code = decoder.read(is);
			
			int width = 128;
			int height = 128;
			
			if(code != 2)
			{					
				AnimatedGif gif = new AnimatedGif(gifName, width, height);
				//process each frame
				
				int frameCount = decoder.getFrameCount();

				for(int i = 0; i < frameCount; i++)
				{
					BufferedImage frame = decoder.getFrame(i);
					
					//resize the image
					if(frame.getHeight() != 128 || frame.getWidth() != 128)
					{	
						int type = frame.getType() == 0? BufferedImage.TYPE_INT_ARGB : frame.getType();
						frame = GifDecoder.resizeImageWithHint(frame, type, 128, 128);
					}
					
					int[] imageBytes = new int[width * height];
					frame.getRGB(0, 0, width, height, imageBytes, 0, width);
					
					gif.imageBytes.add(imageBytes);
					
					MapView[] mapViews = new MapView[gif.getFrameCount()];

					MapView map = Bukkit.createMap(player.getWorld());
					mapViews[i] = map;
					
					map.getRenderers().clear();
					CanvasRenderer cr = new CanvasRenderer(gif, i);
					//cr.initialize(map);
					
					map.addRenderer(cr);
					
					gif.mapIds.add(map.getId());
				}

				//persist the gif data	
				File animGif = new File("plugins/CanvasPlayer/images/" + gifName + ".dat");
				
				if(!animGif.exists())
				{
					animGif.createNewFile();
				}
				
				FileOutputStream fileOut = new FileOutputStream("plugins/CanvasPlayer/images/" + gifName + ".dat");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				
				out.writeObject(gif);
				out.close();
				
				String message = ChatColor.GOLD + "Image ";
				message += ChatColor.AQUA + "'" + gifName + "'";
				message += ChatColor.GOLD + " was successfully processed!";
				
				player.sendMessage(message);

				CPMain.Gifs.put(gifName, gif);
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Url not reachable or gif type not supported. Try a different url.");
			}
			
			is.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Invalid URL, filetype, or config values. Try a different image.\n");
		}
	}
}