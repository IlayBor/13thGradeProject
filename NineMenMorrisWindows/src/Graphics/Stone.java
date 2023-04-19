package Graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import Graphics.Board.Phase;
import Graphics.Board.Status;
import Logic.AI;

public class Stone extends JPanel{
	
	private static int stoneSize = 50; // static variables
	
	private int row; // variables
	private int col;
	
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
		
		// There is nothing to draw
		if(!inTrio && !allowed && stoneColor == null)
			return;
		
		Color currentColor = board.getGame().getCurrentPlayerColor();
		Color opponentColor = currentColor == Game.firstColor ? Game.secColor : Game.firstColor;
		
		if(inTrio) 
		{
			g.setColor(Game.trioColor);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(g.getColor().darker());
			g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		else if(allowed) 
		{
			g.setColor(Game.allowedColor);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(g.getColor().darker());
	    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		else if(board.getGamePhase() == Phase.remove) 
		{
			g.setColor(stoneColor);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(stoneColor == opponentColor ? Game.removeGlowIndicatorColor : g.getColor().darker());
			g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		else if(board.getGamePhase() == Phase.move)
		{
			g.setColor(stoneColor);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(stoneColor == currentColor ? Game.globalGlowIndicatorColor : g.getColor().darker());
	    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		else
		{
			g.setColor(stoneColor);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(g.getColor().darker());
	    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
	}
	
	public void addMouseListener() 
	{
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
			// Stone placing phase
			if(board.getGamePhase() == Phase.place) 
				board.placeStone(Stone.this);
			// Stone Moving Phase
			else if(board.getGamePhase() == Phase.move)
				board.moveStone(Stone.this, stoneColor != null ? Status.copy : Status.paste);
			// Stone Remove Phase
			else
				board.removeStone(Stone.this);
		}
	}
	
	public Color getColor() {
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