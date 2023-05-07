package Logic;

import java.awt.Color;
import java.util.Iterator;

import Graphics.Game;
import Graphics.Stone;

public class LogicBoard {
	
	public static int allowedRowArr[] = {0,0,0,1,1,1,2,2,2,3,3,3,3,3,3,4,4,4,5,5,5,6,6,6};
	public static int allowedColArr[] = {0,3,6,1,3,5,2,3,4,0,1,2,4,5,6,2,3,4,1,3,5,0,3,6};
	
	public static char initChar = '0'; // not playable places
	public static char playableChar = 'X';  // playable but empty
	public static char firstPlayerChar = '1'; // first player
	public static char secPlayerChar = '2'; // second player
	
	private int firstColorStonesOnBoard = 9;
	private int secColorStonesOnBoard = 9;
	private int amountOfTurns = 0;
	
	private LogicStone board[][] = new LogicStone[7][7];
	
	public LogicBoard()
	{
		initBoard();
	}
	
	public LogicBoard(LogicBoard copyFrom) 
	{
		initBoard();
		copyBoard(copyFrom);
	}
	
	public void copyBoard(LogicBoard copyFrom) 
	{
		for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		board[allowedRowArr[duoIndex]][allowedColArr[duoIndex]] = new LogicStone(copyFrom.getBoard()[allowedRowArr[duoIndex]][allowedColArr[duoIndex]]);
		this.firstColorStonesOnBoard = copyFrom.firstColorStonesOnBoard;
		this.secColorStonesOnBoard = copyFrom.secColorStonesOnBoard;
	}
	
	public void initBoard() 
	{
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		board[allowedRowArr[duoIndex]][allowedColArr[duoIndex]] = new LogicStone(allowedRowArr[duoIndex], allowedColArr[duoIndex], null);
	}
	
	public void placeStone(int row, int col, Color color) 
	{
		// checks if the place exists (row,col)
		if(board[row][col] != null) 
			board[row][col].setColor(color);
	}

	public void placeStone(LogicStone stone) 
	{
		// places a stone, cannot place an empty stone
		if(stone.getColor() != null)
			placeStone(stone.getRow(), stone.getCol(), stone.getColor());
	}
	
	public void removeStone(int row, int col) 
	{
		placeStone(row, col, null);
	}
	
	public void removeStone(LogicStone stone) 
	{
		removeStone(stone.getRow(), stone.getCol());
	}
	
	public void moveStone(Move m) 
	{
		placeStone(m.getNextRow(), m.getNextCol(), board[m.getCurRow()][m.getCurCol()].getColor());
		removeStone(m.getCurRow(), m.getCurCol());
	}	
	
	public void reverseMove(Move m) 
	{
		placeStone(m.getCurRow(), m.getCurCol() ,board[m.getNextRow()][m.getNextCol()].getColor());
		removeStone(m.getNextRow(), m.getNextCol());
	}
	
	public void increaseStonesOnBoard(Color color) 
	{
		if(color == Game.firstColor)
			firstColorStonesOnBoard++;
		else 
			secColorStonesOnBoard++;
	}
	
	public void decreaseStonesOnBoard(Color color) 
	{
		if(color == Game.firstColor)
			firstColorStonesOnBoard--;
		else 
			secColorStonesOnBoard--;
	}
	
	public void increaseAmountOfTurns() 
	{
		amountOfTurns++;
	}
	
	public int getAmountOfTurns() {
		return amountOfTurns;
	}
	
	public void printLogicBoard() 
	{
		for(int row = 0; row < 7; row++) 
		{
			for(int col = 0; col < 7; col++)
				System.out.printf("%2c" ,getSign(board[row][col]));
			System.out.println();
		}
		System.out.println("-----------------------------------");
	}
	
	public char getSign(LogicStone logicStone) 
	{
		char sign = initChar;
		if(logicStone != null) 
		{
			if(logicStone.getColor() == Game.firstColor)
				sign = firstPlayerChar;
			else if(logicStone.getColor() == Game.secColor)
				sign = secPlayerChar;
			else
				sign = playableChar;
		}
		return sign;
	}

	public LogicStone[][] getBoard() {
		return board;
	}

	public int getFirstColorStonesOnBoard() {
		return firstColorStonesOnBoard;
	}

	public int getSecColorStonesOnBoard() {
		return secColorStonesOnBoard;
	}
}
