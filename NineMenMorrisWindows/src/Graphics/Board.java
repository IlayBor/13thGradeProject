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
	
	private Move currentMove = new Move();
	private Stone stoneArr[][] = new Stone[7][7];
	private Phase gamePhase = Phase.place;
	private Phase prevPhase;
	
	public enum Status
	{
		copy,
		paste;
	}
	
	public enum Phase
	{
		place,
		move,
		remove;
	}
	
    public Board(Game _game) {
    	game = _game;
    	
    	setLayout(new GridLayout(7,7,5,5));
    	
    	setBounds(0, 0, boardX, boardY);
        image = new ImageIcon("images/GameBoard.png");
        
        initBoard();
    }
    
    public void initBoard() 
    {
    	for(int i = 0; i < stoneArr.length; i++) 
    	{
    		for(int j = 0; j < stoneArr.length; j++) 
    		{
    			stoneArr[i][j] = new Stone(i,j,this); 
    			add(stoneArr[i][j]);
    		}
    	}
    	
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].addMouseListener();
    }
    
    // stone placing phase
    public void placeStone(Stone stone)
	{	
    	// If there is already a stone
    	if(stoneArr[stone.getRow()][stone.getCol()].getColor() != null)
    		return;
    	
    	// Remove one stone from the player
    	if(game.getCurrentPlayerColor() == Game.firstColor)
    		game.setFirstColorStonesLeft(game.getFirstColorStonesLeft()-1);
    	else
    		game.setSecColorStonesLeft(game.getSecColorStonesLeft()-1);
    	
    	updateBox();
    	
    	stone.drawStone(game.getCurrentPlayerColor());
		logicGame.getLogicBoard().placeStone(stone.getRow(), stone.getCol(), game.getCurrentPlayerColor());
		
		logicGame.getLogicBoard().increaseAmountOfTurns();
		
		// Change game phase
		if(game.getFirstColorStonesLeft() == 0 && game.getSecColorStonesLeft() == 0) 
		{
			gamePhase = Phase.move;
			repaintAllPanels();
			checkForWinner();
		}
		
		// If mill exists, dont change turn and return.
		if(checkForMill(stone)) 
			return;
		
		game.changeTurn();
	}
    
    public void moveStone(Stone stone, Status status) 
    {
    	// Copy stone
    	if(status == Status.copy) 
    	{
    		// Wrong color clicked, return.
    		if(stone.getColor() != game.getCurrentPlayerColor())
    			return;
    		
    		unmarkAllowedMoves();
    		currentMove.setCurrent(stone.getRow(), stone.getCol());
    		markAllowedMoves(stone);
    	}
    	// Paste stone
    	else 
    	{
    		// Dont have current stone, return.
    		if(!currentMove.isCurExist())
    			return;
    		
    		currentMove.setNext(stone.getRow(), stone.getCol());
    		
    		// Move is not allowed, return.
    		if(!logicGame.isMoveAllowed(currentMove))
    			return;
    		
			stoneArr[currentMove.getCurRow()][currentMove.getCurCol()].removeStone();
	    	stoneArr[currentMove.getNextRow()][currentMove.getNextCol()].drawStone(game.getCurrentPlayerColor());
	    	logicGame.getLogicBoard().moveStone(currentMove);
    		currentMove.resetMove();
    		
    		unmarkAllowedMoves();
    		
    		// If mill exists, dont change turn and return.
    		if(checkForMill(stone)) 
    			return;
    		
    		checkForWinner();
    		game.changeTurn();
    	}
    }
    
    public void removeStone(Stone stone) 
    {
    	Color oppenentColor = game.getCurrentPlayerColor() == game.firstColor ? game.secColor : game.firstColor;
    	
    	// if clicks on invalid stone, return.
    	if(stone.getColor() != oppenentColor)
    		return;
    	
    	// if stone is part of a mill, return.
    	if(logicGame.isStoneInTrio(new LogicStone(stone)))
    		return;
    	
    	stone.removeStone();
    	logicGame.getLogicBoard().removeStone(stone.getRow(), stone.getCol());
    	logicGame.getLogicBoard().decreaseStonesOnBoard(oppenentColor);
    	
    	gamePhase = prevPhase;
    	
    	checkForWinner();
    	
    	game.changeTurn();
    	repaintAllPanels();
    }
    
    public void checkForWinner() 
    {
    	if(logicGame.isWinner(game.getCurrentPlayerColor()))
		{
    		String color = game.getCurrentPlayerColor() == Game.firstColor ? "White" : "Black";
			javax.swing.JOptionPane.showMessageDialog(null, color + " Won!");
			game.moveToHome();
		}
    }
    
    public void updateBox() 
    {
    	if(game.getCurrentPlayerColor() == Game.firstColor) 
			game.getFirstColorBox().repaint();
		else 
			game.getSecColorBox().repaint();
    }
    
    public boolean checkForMill(Stone stone)
    {	
    	ArrayList<LogicStone> rowArr = logicGame.checkRow(new LogicStone(stone));
    	ArrayList<LogicStone> colArr = logicGame.checkCol(new LogicStone(stone));
    	Color oppenentColor = stone.getColor() == game.firstColor ? game.secColor : game.firstColor;
    	prevPhase = gamePhase;
    	
    	// In case there is nothing possible to remove, return.
    	if(!logicGame.canRemoveAnyStone(oppenentColor))
    		return false;
    	// checks if there is a mill in a row
    	if(rowArr != null) 
    	{
    		gamePhase = Phase.remove;
    		markMill(rowArr);
    		repaintAllPanels();
    		
    		if(AI.aiDepth > 0 && stone.getColor() == game.secColor) 
    			game.getAi().AiTurn();
    		return true;
    	}
    	// checks if there is a mill in a col
    	if(colArr != null) 
    	{
    		gamePhase = Phase.remove;
    		markMill(colArr);
    		repaintAllPanels();
    		
    		if(AI.aiDepth > 0 && stone.getColor() == game.secColor) 
    			game.getAi().AiTurn();
    		return true;
    	}
    	
    	return false;
    }
    
    public void markMill(ArrayList<LogicStone> logicStoneArr) 
    {
    	for(int i = 0; i < logicStoneArr.size(); i++) 
		{
			LogicStone logicStone = logicStoneArr.get(i);
			stoneArr[logicStone.getRow()][logicStone.getCol()].setInMill(true);
			stoneArr[logicStone.getRow()][logicStone.getCol()].repaint();
		}
    }
    
    public void unmarkMill() 
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].setInMill(false);
    }
    
    public void markAllowedMoves(Stone stone) 
    {
    	ArrayList<Move> allowedMovesArr = logicGame.allowedMoves(new LogicStone(stone));
    	
    	for(int i = 0; i < allowedMovesArr.size(); i++) 
		{
			Move m = allowedMovesArr.get(i);
			stoneArr[m.getNextRow()][m.getNextCol()].setAllowedMove(true);
			stoneArr[m.getNextRow()][m.getNextCol()].repaint();
		}
    }
    
    public void unmarkAllowedMoves() 
    {
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    	{
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].setAllowedMove(false);
    		stoneArr[allowedRowArr[duoIndex]][allowedColArr[duoIndex]].repaint();
    	}
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

	public Phase getGamePhase() {
		return gamePhase;
	}

	public Phase getPrevPhase() {
		return prevPhase;
	}

	public LogicGame getLogicGame() {
		return logicGame;
	}
}