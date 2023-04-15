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

public class Board extends JPanel{
	
	public static int allowedRowArr[] = {0,0,0,1,1,1,2,2,2,3,3,3,3,3,3,4,4,4,5,5,5,6,6,6}; // allowed row places
	public static int allowedColArr[] = {0,3,6,1,3,5,2,3,4,0,1,2,4,5,6,2,3,4,1,3,5,0,3,6}; // allowed col places
	
	public static ImageIcon image; // board background & size
	public static int boardX = 650;
	public static int boardY = 650;
	
	public Game game;
	public LogicBoard logicBoard = new LogicBoard();
	public LogicGame logicGame = new LogicGame(logicBoard, this);
	public Stone stoneArr[][] = new Stone[7][7];
	public Stone lastClickedStone = null;
	public boolean shouldRemoveStone = false;
	
    public Board(Game _game) {
    	game = _game;
    	
    	setLayout(new GridLayout(7,7,5,5));
    	
    	setBounds(0, 0, boardX, boardY);
        image = new ImageIcon("images/GameBoard.png");
        
        setupStoneMatrix();
        
    }
    
    void setupStoneMatrix() 
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
    	{
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].activate();
    		logicBoard.setValue(allowedRowArr[duoIndex], allowedColArr[duoIndex], null);
    	}
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
		
		logicBoard.setValue(stone.getRow(), stone.getCol(), game.getCurrentPlayerColor());
		
		checkAndMarkForTrio(stone);
		
		if(game.getFirstColorStonesLeft() == 0 && game.getSecColorStonesLeft() == 0) // end stone placing phase
			game.setPlacingPhase(false);
		
		if(!shouldRemoveStone)
			game.changeTurn();
		
		repaintAllPanels();
		
		//logicGame.LBoard.printLBoard();
		//logicGame.printAllMoves(logicGame.allPossibleStonePlaces(game.currentPlayerColor));
	}
    
    public void stoneClicked(Stone stone)
    {	
    	if(shouldRemoveStone) 
    	{
    		if(isAllowedToBeRemoved(stone))
    		{
    			if(stone.getStoneColor() == Game.firstColor)
    				logicGame.setFirstColorStonesLeft(logicGame.getFirstColorStonesLeft() - 1);
    			else
    				logicGame.setSecColorStonesLeft(logicGame.getSecColorStonesLeft() - 1);
    			
    			stone.removeStone();
    			logicBoard.setValue(stone.getRow(), stone.getCol(), null);
    			 
    			checkForWinner();
    			
    			unmarkTrio();
    			shouldRemoveStone = false;
    			game.changeTurn();
    		}
    	}
    	else if(!game.getPlacingPhase()) // moving phase
    	{
    		unmarkAllowedMoves();
    		if(stone.getStoneColor() != null && stone.getStoneColor() == game.getCurrentPlayerColor()) // "copy" stone
        	{
        		lastClickedStone = stone;
        		markAllowedMoves(stone);
        	}
        	else if(stone.getStoneColor() == null && lastClickedStone != null && logicGame.isMoveAllowed(lastClickedStone, stone))// "paste" stone
        	{	
    			stone.drawStone(lastClickedStone.getStoneColor());
    			logicBoard.setValue(stone.getRow(), stone.getCol(), stone.getStoneColor());
    			
        		lastClickedStone.removeStone();
        		logicBoard.setValue(lastClickedStone.getRow(), lastClickedStone.getCol(), null);
        		
        		lastClickedStone = null;
        		
        		checkAndMarkForTrio(stone);
        		
        		checkForWinner();
        		
        		if(!shouldRemoveStone)
        			game.changeTurn();	
        	}
    	}
    	
    	repaintAllPanels();
    	//logicGame.LBoard.printLBoard();
		//logicGame.printAllMoves(logicGame.allPossibleMoves(game.currentPlayerColor));
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
    	return stone.getStoneColor() != null && stone.getStoneColor() != game.getCurrentPlayerColor() && !logicGame.isStoneInTrio(stone);
    }
    
    public boolean canRemoveAnyStone(Color color)
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		if(stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].getStoneColor() == color && !logicGame.isStoneInTrio(stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]])) // right color and can remove
    			return true;
    	return false;
    }
    
    public void checkAndMarkForTrio(Stone stone) 
    {
    	ArrayList<LogicStone> logicStoneArr = logicGame.checkCol(stone);
    	if(canRemoveAnyStone(stone.getStoneColor() == game.firstColor ? game.secColor : game.firstColor)) // will mark and allow to remove only if you CAN remove 
    	{
    		if(logicStoneArr != null) // checking cols first
        	{
        		markTrio(logicStoneArr);
        		shouldRemoveStone = true;
        	}
        	else
        	{
        		logicStoneArr = logicGame.checkRow(stone);
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
			stoneArr[logicStone.row][logicStone.col].setInTrio(true);
			stoneArr[logicStone.row][logicStone.col].repaint();
		}
    }
    
    public void unmarkTrio() 
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].setInTrio(false);
    }
    
    public void markAllowedMoves(Stone stone) 
    {
    	ArrayList<LogicStone> allowedMovesArr = logicGame.allowedMoves(stone);
    	
    	for(int i = 0; i < allowedMovesArr.size(); i++) 
		{
			LogicStone logicStone = allowedMovesArr.get(i);
			stoneArr[logicStone.row][logicStone.col].setAllowed(true);
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
    
    
}