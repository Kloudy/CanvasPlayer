package com.antarescraft.canvasplayer.main;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class CanvasRenderer extends MapRenderer
{
	private AnimatedGif gif;
	private int mapIndex;
	//private boolean alreadyRenderered;
	
	public CanvasRenderer(AnimatedGif gif, int mapIndex)
	{
		this.gif = gif;
		this.mapIndex = mapIndex;
		//alreadyRenderered = false;
	}

	@Override
	public void render(MapView mapView, MapCanvas mapCanvas, Player player) 
	{
		//if(!alreadyRenderered)
		//{
			//if(CPMain.GifImages.containsKey(gif.getName()))
			//{
			//	mapCanvas.drawImage(0, 0, CPMain.GifImages.get(gif.getName()).get(mapIndex));
			//}
			//else
			//{
				mapCanvas.drawImage(0, 0, gif.getFrames().get(gif.getCurrentMapId()));
			//}
			
			//alreadyRenderered = true;
		//}
	}
}
