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
	
	private int firstColorStonesLeft = 9;
	private int secColorStonesLeft = 9;
	
	private LogicStone board[][] = new LogicStone[7][7];
	
	public LogicBoard()
	{
		initBoard();
	}
	
	public LogicBoard(LogicBoard toCopy) 
	{
		initBoard();
		for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		board[allowedRowArr[duoIndex]][allowedColArr[duoIndex]] = new LogicStone(toCopy.getBoard()[allowedRowArr[duoIndex]][allowedColArr[duoIndex]]);
		this.firstColorStonesLeft = toCopy.getFirstColorStonesLeft();
		this.secColorStonesLeft = toCopy.getSecColorStonesLeft();
	}
	
	public void initBoard() 
	{
    	for(int duoIndex = 0; duoIndex < allowedColArr.length; duoIndex++) 
    		board[allowedRowArr[duoIndex]][allowedColArr[duoIndex]] = new LogicStone(allowedRowArr[duoIndex], allowedColArr[duoIndex], null);
	}
	
	public void placeStone(int row, int col, Color color) 
	{
		if(board[row][col] != null) 
			board[row][col].setColor(color);
	}
	
	public void removeStone(int row, int col) 
	{
		placeStone(row, col, null);
	}
	
	public void moveStone(int curRow, int curCol, int nextRow, int nextCol) 
	{
		placeStone(nextRow, nextCol, board[curRow][curCol].getColor());
		removeStone(curRow, curCol);
	}
	
	public void printLogicBoard() 
	{
		System.out.println("-----------------------------------");
		for(int row = 0; row < 7; row++) 
		{
			for(int col = 0; col < 7; col++)
				System.out.printf("%2c" ,getSign(board[row][col]));
			System.out.println();
		}
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

	public int getFirstColorStonesLeft() {
		return firstColorStonesLeft;
	}

	public void setFirstColorStonesLeft(int firstColorStonesLeft) {
		this.firstColorStonesLeft = firstColorStonesLeft;
	}

	public int getSecColorStonesLeft() {
		return secColorStonesLeft;
	}

	public void setSecColorStonesLeft(int secColorStonesLeft) {
		this.secColorStonesLeft = secColorStonesLeft;
	}
	
	
}
