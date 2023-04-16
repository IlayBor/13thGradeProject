package Graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Stone extends JPanel{
	
	private static int stoneSize = 50; // static variables
	
	private int row; // variables
	private int col;
	
	private boolean isPanelActive = false;
	private boolean allowed = false;
	private boolean inTrio = false;
	private int stoneCenterX = 42;
	private int stoneCenterY = 42;
	
	private Color stoneColor = null;
	private Color glowColor = Game.defaultIndicator;
	
	private Board board; // the board
	
	public Stone(int _row, int _col, Board _board) 
	{
		row = _row;
		col = _col;
		board = _board;
		
		setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(stoneColor != null) // If there is a stone
		{
			g.setColor(inTrio ? Game.trioColor : stoneColor); // Draw Stone
	    	g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
	    	
	    	g.setColor(g.getColor().darker()); // Normal border
	    	
	    	if(!inTrio) // Add glow
	    	{
	    		if(board.getLastClickedStone() == this) // Last clicked (Focused) glow
			    	g.setColor(Game.specificGlowIndicatorColor); 
	    		
	    		if(board.getGame().getPlacingPhase()) // Placing phase
	    		{
	    			if(board.getIsShouldRemoveStone())
	    				if(board.getGame().getCurrentPlayerColor() != stoneColor) // Enemy (to remove) color glow
	    					g.setColor(Game.removeGlowIndicatorColor);
	    		}
	    		else // Moving phase
	    		{
	    			if(board.getIsShouldRemoveStone())
	    			{
	    				if(board.getGame().getCurrentPlayerColor() != stoneColor) // Enemy (to remove) color glow
	    					g.setColor(Game.removeGlowIndicatorColor);
	    			}
	    			else
	    				if(board.getGame().getCurrentPlayerColor() == stoneColor) // Same color glow (possible stones) 
	    					g.setColor(Game.globalGlowIndicatorColor);
	    		}
	    	}
	    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		else if(allowed) // Possible allowed next move
		{
			g.setColor(Game.allowedColor); // Mark place
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(g.getColor().darker()); // Normal border
	    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
	}
	
	public void activate() 
	{
		isPanelActive = true;
		setCenterValues();
		
		addMouseListener(new mouseHandler());
		addMouseMotionListener(new mouseHandler());
		
		repaint();
	}
	
	public void removeStone() 
	{
		glowColor = Game.defaultIndicator;
		stoneColor = null;
		repaint();
	}
	
	public void drawStone(Color _color) 
	{
		stoneColor = _color;
		repaint();
	}
	
	public class mouseHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent e) 
		{
			if(board.getGame().getPlacingPhase() && stoneColor == null && !board.getIsShouldRemoveStone()) // stone-placing phase, and there is no stone on the panel.
			{
				drawStone(board.getGame().getCurrentPlayerColor());
				board.stonePlaced(Stone.this);
			}
			else // stone-moving phase or clicked stone clicked on placing phase
			{
				board.stoneClicked(Stone.this);
			}
		}
	}
	
	public Color getStoneColor() {
		return stoneColor;
	}

	public void setInTrio(boolean isInTrio) {
		this.inTrio = isInTrio;
	}

	public void setAllowed(boolean _Allowed) {
		this.allowed = _Allowed;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public void setCenterValues() 
	{
		switch(row) 
		{
			case 0:
			{
				stoneCenterY-=10;
				break;
			}
			case 1:
			{
				stoneCenterY-=8;
				break;
			}
			case 3:
			{
				stoneCenterY+=4;
				break;
			}
			case 4:
			{
				stoneCenterY+=10;
				break;
			}
			case 5:
			{
				stoneCenterY+=16;
				break;
			}
			case 6:
			{
				stoneCenterY+=20;
				break;
			}
		}
		switch(col) 
		{
			case 0:
			{
				stoneCenterX-=12;
				break;
			}
			case 1:
			{
				stoneCenterX-=10;
				break;
			}
			case 3:
			{
				stoneCenterX+=2;
				break;
			}
			case 4:
			{
				stoneCenterX+=6;
				break;
			}
			case 5:
			{
				stoneCenterX+=14;
				break;
			}
			case 6:
			{
				stoneCenterX+=20;
				break;
			}
		}
	}
}