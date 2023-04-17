package Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Logic.LogicBoard;
import Logic.LogicGame;
import Logic.LogicStone;
import Logic.Move;
import Logic.AI;

public class Board extends JPanel{
	
	public static int allowedRowArr[] = {0,0,0,1,1,1,2,2,2,3,3,3,3,3,3,4,4,4,5,5,5,6,6,6};
	public static int allowedColArr[] = {0,3,6,1,3,5,2,3,4,0,1,2,4,5,6,2,3,4,1,3,5,0,3,6};
	
	public static ImageIcon image;
	public static int boardX = 650;
	public static int boardY = 650;
	
	private Game game;
	private LogicGame logicGame = new LogicGame();
	private AI Ai = new AI(logicGame);
	private Stone stoneArr[][] = new Stone[7][7];
	private Stone lastClickedStone = null;
	private boolean shouldRemoveStone = false;
	
    public Board(Game _game) {
    	game = _game;
    	
    	setLayout(new GridLayout(7,7,5,5));
    	
    	setBounds(0, 0, boardX, boardY);
        image = new ImageIcon("images/GameBoard.png");
        
        setupStoneMatrix();
        
    }
    
    public void setupStoneMatrix() 
    {
    	for(int i = 0; i < 7; i++) 
    	{
    		for(int j = 0; j < 7; j++) 
    		{
    			stoneArr[i][j] = new Stone(i,j,this); 
    			add(stoneArr[i][j]);
    		}
    	}
    	
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].activate();
    }
    
    public void stonePlaced(Stone stone) // stone placing phase
	{	
		if(game.getPlayerTurn() == 0) 
		{
			game.setFirstColorStonesLeft(game.getFirstColorStonesLeft()-1);
			game.getFirstColorBox().repaint();
		}
		else 
		{
			game.setSecColorStonesLeft(game.getSecColorStonesLeft()-1);
			game.getSecColorBox().repaint();
		}
		
		logicGame.getLogicBoard().placeStone(stone.getRow(), stone.getCol(), game.getCurrentPlayerColor());

		checkAndMarkForTrio(stone);
		
		if(game.getFirstColorStonesLeft() == 0 && game.getSecColorStonesLeft() == 0) // end stone placing phase
			game.setPlacingPhase(false);
		
		if(!shouldRemoveStone)
			game.changeTurn();
		
		repaintAllPanels();
		logicGame.getLogicBoard().printLogicBoard();
	}
    
    public void stoneClicked(Stone stone) // stone Moving / Removing Phase.
    {	
    	if(shouldRemoveStone) 
    	{
    		if(isAllowedToBeRemoved(stone))
    		{
    			if(stone.getColor() == Game.firstColor)
    				logicGame.getLogicBoard().setFirstColorStonesLeft(logicGame.getLogicBoard().getFirstColorStonesLeft() - 1);
    			else
    				logicGame.getLogicBoard().setSecColorStonesLeft(logicGame.getLogicBoard().getSecColorStonesLeft() - 1);
    			
    			stone.removeStone();
    			logicGame.getLogicBoard().removeStone(stone.getRow(), stone.getCol());
    			 
    			checkForWinner();
    			
    			unmarkTrio();
    			shouldRemoveStone = false;
    			game.changeTurn();
    		}
    	}
    	
    	// moving phase
    	else if(!game.getPlacingPhase()) 
    	{	
    		unmarkAllowedMoves();
    		
    		// "copy" stone
    		if(stone.getColor() != null && stone.getColor() == game.getCurrentPlayerColor())
        	{
        		lastClickedStone = stone;
        		markAllowedMoves(stone);
        	}
    		// "paste" stone
        	else if(stone.getColor() == null && lastClickedStone != null && logicGame.isMoveAllowed(new Move(lastClickedStone.getRow(), lastClickedStone.getCol(), stone.getRow(), stone.getCol(), stone.getColor())))
        	{	
    			stone.drawStone(lastClickedStone.getColor());
    			lastClickedStone.removeStone();
    			logicGame.getLogicBoard().moveStone(lastClickedStone.getRow(), lastClickedStone.getCol(), stone.getRow(), stone.getCol());
        		
        		lastClickedStone = null;
        		
        		checkAndMarkForTrio(stone);
        		
        		checkForWinner();
        		
        		if(!shouldRemoveStone)
        			game.changeTurn();	
        	}
    	}
    	
    	repaintAllPanels();
    	logicGame.getLogicBoard().printLogicBoard();
    }
    
    public void checkForWinner() 
    {
    	if(logicGame.isWinner(game.getCurrentPlayerColor()))
		{
    		String color = game.getCurrentPlayerColor() == Game.firstColor ? "White" : "Black";
			javax.swing.JOptionPane.showMessageDialog(null, color + " Won!");
			game.getFrame().MoveToHome();
		}
    }
    
    public boolean isAllowedToBeRemoved(Stone stone) 
    {
    	return stone.getColor() != null && stone.getColor() != game.getCurrentPlayerColor() && !logicGame.isStoneInTrio(new LogicStone(stone));
    }
    
    public boolean canRemoveAnyStone(Color color)
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		if(stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].getColor() == color && !logicGame.isStoneInTrio(new LogicStone(stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]]))) // right color and can remove
    			return true;
    	return false;
    }
    
    public void checkAndMarkForTrio(Stone stone) 
    {
    	ArrayList<LogicStone> logicStoneArr = logicGame.checkCol(new LogicStone(stone));
    	if(canRemoveAnyStone(stone.getColor() == game.firstColor ? game.secColor : game.firstColor)) // will mark and allow to remove only if you CAN remove 
    	{
    		if(logicStoneArr != null) // checking cols first
        	{
        		markTrio(logicStoneArr);
        		shouldRemoveStone = true;
        	}
        	else
        	{
        		logicStoneArr = logicGame.checkRow(new LogicStone(stone));
        		if(logicStoneArr != null) // checking rows
        		{
        			markTrio(logicStoneArr);
        			shouldRemoveStone = true;
        		}
        	}
    	}
    }
    
    public void markTrio(ArrayList<LogicStone> logicStoneArr) 
    {
    	for(int i = 0; i < logicStoneArr.size(); i++) 
		{
			LogicStone logicStone = logicStoneArr.get(i);
			stoneArr[logicStone.getRow()][logicStone.getCol()].setInTrio(true);
			stoneArr[logicStone.getRow()][logicStone.getCol()].repaint();
		}
    }
    
    public void unmarkTrio() 
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].setInTrio(false);
    }
    
    public void markAllowedMoves(Stone stone) 
    {
    	ArrayList<Move> allowedMovesArr = logicGame.allowedMoves(new LogicStone(stone));
    	
    	for(int i = 0; i < allowedMovesArr.size(); i++) 
		{
			Move m = allowedMovesArr.get(i);
			stoneArr[m.getNextRow()][m.getNextCol()].setAllowed(true);
		}
    }
    
    public void unmarkAllowedMoves() 
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].setAllowed(false);
    }
    
    public void repaintAllPanels() 
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g); 
    	g.drawImage(image.getImage(), 0, 0, boardX, boardY, null); // draw background
    }

	public Game getGame() {
		return game;
	}

	public Stone[][] getStoneArr() {
		return stoneArr;
	}

	public Stone getLastClickedStone() {
		return lastClickedStone;
	}

	public boolean getIsShouldRemoveStone() {
		return shouldRemoveStone;
	}
    
    
    
}