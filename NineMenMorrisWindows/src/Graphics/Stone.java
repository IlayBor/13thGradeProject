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
	
	private int stoneCenterX = 42; // draw coords
	private int stoneCenterY = 42;
	
	private Board board;
	
	private int row; // variables
	private int col;
	private boolean isAllowedMove = false;
	private boolean isInMill = false;
	private Color color = null;
	
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
		if(!isAllowedMove && !isInMill && color == null)
			return;
		
		Color currentColor = board.getGame().getCurrentPlayerColor();
		Color opponentColor = currentColor == Game.firstColor ? Game.secColor : Game.firstColor;
		
		// Is in mill
		if(isInMill) 
		{
			g.setColor(Game.trioColor);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(g.getColor().darker());
			g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		// In allowed move
		else if(isAllowedMove) 
		{
			g.setColor(Game.allowedColor);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(g.getColor().darker());
	    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		// Remove game phase
		else if(board.getGamePhase() == Phase.remove) 
		{
			g.setColor(color);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(color == opponentColor ? Game.removeGlowIndicatorColor : g.getColor().darker());
			g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		// Move game phase
		else if(board.getGamePhase() == Phase.move)
		{
			g.setColor(color);
			g.fillOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
			
			g.setColor(color == currentColor ? Game.globalGlowIndicatorColor : g.getColor().darker());
	    	g.drawOval(stoneCenterX - stoneSize/2, stoneCenterY - stoneSize/2, stoneSize, stoneSize);
		}
		// Place game phase
		else
		{
			g.setColor(color);
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
		color = null;
		repaint();
	}
	
	public void drawStone(Color _color) 
	{
		color = _color;
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
				board.moveStone(Stone.this, color != null ? Status.copy : Status.paste);
			// Stone Remove Phase
			else
				board.removeStone(Stone.this);
		}
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setAllowedMove(boolean isAllowedMove) {
		this.isAllowedMove = isAllowedMove;
	}

	public void setInMill(boolean isInMill) {
		this.isInMill = isInMill;
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