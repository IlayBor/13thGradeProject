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
		if(game.playerTurn == 0) 
		{
			game.firstColorStonesLeft--;
			game.firstColorBox.repaint();
		}
		else 
		{
			game.secColorStonesLeft--;
			game.secColorBox.repaint();
		}
		
		logicBoard.setValue(stone.row, stone.col, game.currentPlayerColor);
		
		checkAndMarkForTrio(stone);
		
		if(game.firstColorStonesLeft == 0 && game.secColorStonesLeft == 0) // end stone placing phase
			game.isPlacingPhase = false;
		
		if(!shouldRemoveStone)
			game.changeTurn();
		
		repaintAllPanels();
		
		logicGame.LBoard.printLBoard();
		logicGame.printAllMoves(logicGame.allPossibleStonePlaces(game.currentPlayerColor));
	}
    
    public void stoneClicked(Stone stone)
    {	
    	if(shouldRemoveStone) 
    	{
    		if(isAllowedToBeRemoved(stone)) // removing a right colored stone
    		{
    			if(stone.stoneColor == Game.firstColor)
    				logicGame.firstColorStonesLeft--;
    			else
    				logicGame.secColorStonesLeft--;
    			
    			stone.removeStone();
    			logicBoard.setValue(stone.row, stone.col, null);
    			
    			if(logicGame.isWinner(game.currentPlayerColor))
    			{
    				javax.swing.JOptionPane.showMessageDialog(null, "Player " + (game.playerTurn+1) + " Won!");
    				game.frame.MoveToHome();
    			}    			
    			unmarkTrio();
    			shouldRemoveStone = false;
    			game.changeTurn();
    		}
    	}
    	else if(!game.isPlacingPhase) // not placing phase
    	{
    		unmarkAllowedMoves();
    		if(stone.stoneColor != null && stone.stoneColor == game.currentPlayerColor) // panel with right colored stone clicked
        	{
        		lastClickedStone = stone;
        		markAllowedMoves(stone);
        	}
        	else if(stone.stoneColor == null && lastClickedStone != null && logicGame.isMoveAllowed(lastClickedStone, stone))// panel without stone clicked, and there is stone picked, and move is allowed.
        	{	
    			stone.drawStone(lastClickedStone.stoneColor);
    			logicBoard.setValue(stone.row, stone.col, stone.stoneColor);
    			
        		lastClickedStone.removeStone();
        		logicBoard.setValue(lastClickedStone.row, lastClickedStone.col, null);
        		
        		lastClickedStone = null;
        		
        		checkAndMarkForTrio(stone);
        		
        		if(!shouldRemoveStone)
        			game.changeTurn();	
        	}
    	}
    	repaintAllPanels();
    	logicGame.LBoard.printLBoard();
		logicGame.printAllMoves(logicGame.allPossibleMoves(game.currentPlayerColor));
    }
    
    public boolean isAllowedToBeRemoved(Stone stone) 
    {
    	return stone.stoneColor != null && stone.stoneColor != game.currentPlayerColor && !logicGame.isStoneInTrio(stone);
    }
    
    public boolean canRemoveAnyStone(Color color)
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		if(stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].stoneColor == color && !logicGame.isStoneInTrio(stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]])) // right color and can remove
    			return true;
    	return false;
    }
    
    public void checkAndMarkForTrio(Stone stone) 
    {
    	ArrayList<LogicStone> logicStoneArr = logicGame.checkCol(stone);
    	if(canRemoveAnyStone(stone.stoneColor == game.firstColor ? game.secColor : game.firstColor)) // will mark and allow to remove only if you CAN remove 
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
			stoneArr[logicStone.row][logicStone.col].isInTrio = true;
			stoneArr[logicStone.row][logicStone.col].repaint();
		}
    }
    
    public void unmarkTrio() 
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].isInTrio = false;
    }
    
    public void markAllowedMoves(Stone stone) 
    {
    	ArrayList<LogicStone> allowedMovesArr = logicGame.allowedMoves(stone);
    	
    	for(int i = 0; i < allowedMovesArr.size(); i++) 
		{
			LogicStone logicStone = allowedMovesArr.get(i);
			stoneArr[logicStone.row][logicStone.col].isAllowed = true;
		}
    }
    
    public void unmarkAllowedMoves() 
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].isAllowed = false;
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