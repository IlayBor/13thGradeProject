package Graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Stone extends JPanel{
	
	private static int stoneSize = 50; // static variables
	
	public int row; // variables
	public int col;
	public boolean isPanelActive = false;
	public boolean isAllowed = false;
	public boolean isInTrio = false;
	private int stoneCenterX = 42;
	private int stoneCenterY = 42;
	public Color stoneColor = null;
	public Color glowColor = Game.defaultIndicator;
	
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
		if(stoneColor != null) // if there is a stone
		{
			g.setColor(isInTrio ? Game.trioColor : stoneColor); // drawing the placed stone
	    	g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
	    	
	    	g.setColor(g.getColor().darker()); // normal border
	    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
	    	
	    	if(!isInTrio) // not in trio, add glow.
	    	{
	    		if(board.lastClickedStone == this) // last clicked glow
		    	{
			    	g.setColor(Game.specificGlowIndicatorColor); 
			    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		    	}
	    		if(board.game.isPlacingPhase) // placing phase
	    		{
	    			if(board.shouldRemoveStone) // remove phase
	    			{
	    				if(board.game.currentPlayerColor == stoneColor) // same color glow
	    		    	{
	    		    		g.setColor(Game.globalGlowIndicatorColor);
	    			    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
	    		    	}
	    				else // remove glow
	    				{
	    					g.setColor(Game.removeGlowIndicatorColor);
	    			    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
	    				}
	    			}
	    		}
	    		else // moving phase
	    		{
	    			if(board.shouldRemoveStone) // remove stage
	    			{
	    				if(board.game.currentPlayerColor == stoneColor) // same color glow
	    		    	{
	    		    		g.setColor(Game.globalGlowIndicatorColor);
	    			    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
	    		    	}
	    				else // remove glow
	    				{
	    					g.setColor(Game.removeGlowIndicatorColor);
	    			    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
	    				}
	    			}
	    			else
	    			{
	    				if(board.game.currentPlayerColor == stoneColor) // same color glow
				    	{
				    		g.setColor(Game.globalGlowIndicatorColor);
					    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
				    	}
	    			}
	    		}
	    	}
		}
		else if(isAllowed) // no stone and move is allowed
		{
			g.setColor(Game.allowedColor); // mark place
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(g.getColor().darker()); // normal border
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
			if(board.game.isPlacingPhase && stoneColor == null && !board.shouldRemoveStone) // stone-placing phase, and there is no stone on the panel.
			{
				drawStone(board.game.currentPlayerColor);
				board.stonePlaced(Stone.this);
			}
			else // stone-moving phase or clicked stone clicked on placing phase
			{
				board.stoneClicked(Stone.this);
			}
		}
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