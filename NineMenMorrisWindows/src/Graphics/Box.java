package Graphics;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Box extends JPanel{
	
	public static int boxXSize = 50; // statics
	public static int boxYSize = 450;
	public static int stoneDrawSize = 40;
	public static int drawYGap = 41;
	
	private Game game;
	private Color boxStoneColor; // variables
	
	public Box(Color color, Game _game) 
	{
		game = _game;
		boxStoneColor = color;
		setOpaque(false);
	}
	protected void paintComponent(Graphics g) 
	{
		int currentY = 0;
		int stonesLeftToDraw = (boxStoneColor == Game.firstColor ? game.getFirstColorStonesLeft() : game.getSecColorStonesLeft());
		
		for(int i = 1; i <= stonesLeftToDraw; i++) 
		{
			g.setColor(boxStoneColor);
			
			g.fillOval(0, currentY, stoneDrawSize, stoneDrawSize);
			
			g.setColor(g.getColor().darker());
	    	g.drawOval(0, currentY, stoneDrawSize, stoneDrawSize);
			
	    	currentY+=drawYGap;
		}
	}
}


