package Logic;

import java.awt.Color;
import java.util.ArrayList;

import Graphics.Board;
import Graphics.Game;
import Graphics.Stone;

public class LogicGame {
	public static int aiLevel = 0; // 0 - None, 1 - Easy, 2 - Medium, 3 - Hard
	
	private LogicBoard logicBoard; 
	
	private int firstColorStonesLeft = 9;
	private int secColorStonesLeft = 9;
	
	public LogicGame() 
	{
		logicBoard = new LogicBoard();
	}
	
	public boolean isMoveAllowed(LogicStone lastStone, LogicStone nextStone) 
	{	
		if(lastStone.getColor() == Game.firstColor && firstColorStonesLeft <= 3) // JUNK MOVES
			return true;
		if(lastStone.getColor() == Game.secColor && secColorStonesLeft <= 3)
			return true;
		
		int allowedRowDis = 0;
		int allowedColDis = 0;
		switch(lastStone.getRow()) 
		{
		case 0,6:
			allowedColDis = 3;
			break;
		case 1,5:
			allowedColDis = 2;
			break;
		case 2,3,4:
			allowedColDis = 1;
			break;
		}
		
		switch(lastStone.getCol()) 
		{
		case 0,6:
			allowedRowDis = 3;
			break;
		case 1,5:
			allowedRowDis = 2;
			break;
		case 2,3,4:
			allowedRowDis = 1;
			break;
		}
		
		if(Math.abs(lastStone.getRow() - nextStone.getRow()) == allowedRowDis && lastStone.getCol() - nextStone.getCol() == 0)
			return true;
		if(Math.abs(lastStone.getCol() - nextStone.getCol()) == allowedColDis && lastStone.getRow() - nextStone.getRow() == 0)
			return true;
		return false;
	}
	
	public ArrayList<LogicStone> allowedMoves(LogicStone stone)
	{
		ArrayList<LogicStone> allowedMovesArr = new ArrayList<LogicStone>();
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
		{
			if(isMoveAllowed(stone, logicBoard.getBoard()[LogicBoard.allowedRowArr[duoIndex]][LogicBoard.allowedColArr[duoIndex]]) 
					&& logicBoard.getBoard()[LogicBoard.allowedRowArr[duoIndex]][LogicBoard.allowedColArr[duoIndex]] != null) 
			{
				allowedMovesArr.add(new LogicStone(LogicBoard.allowedRowArr[duoIndex], LogicBoard.allowedColArr[duoIndex], stone.getColor()));
			}
		}
		return allowedMovesArr;
	}
	
	public ArrayList<LogicStone> checkRow(LogicStone stone) 
	{
		int rowToCheck = stone.getRow();
		
		int startCol = (rowToCheck == 3) ? stone.getCol() < 3 ? 0 : 4 : 0;
		int endCol = (rowToCheck == 3) ? stone.getCol() < 3 ? 3 : 7: 7;
		ArrayList<LogicStone> logicStoneArr = new ArrayList<LogicStone>();
		
		for(int curCol = startCol; curCol < endCol; curCol++) 
		{
			if(logicBoard.getBoard()[rowToCheck][curCol] != null) // if exists
				if(logicBoard.getBoard()[rowToCheck][curCol].getColor() == stone.getColor())
					logicStoneArr.add(logicBoard.getBoard()[rowToCheck][curCol]);
		}
		return logicStoneArr.size() == 3 ? logicStoneArr : null;
	}
	
	public ArrayList<LogicStone> checkCol(LogicStone stone) 
	{
		int colToCheck = stone.getCol();
		
		int startRow = (colToCheck == 3) ? stone.getRow() < 3 ? 0 : 4 : 0;
		int endRow = (colToCheck == 3) ? stone.getRow() < 3 ? 3 : 7: 7;
		ArrayList<LogicStone> logicStoneArr = new ArrayList<LogicStone>();
		
		for(int curRow = startRow; curRow < endRow; curRow++) 
		{
			if(logicBoard.getBoard()[curRow][colToCheck] != null) // if exists
				if(logicBoard.getBoard()[curRow][colToCheck].getColor() == stone.getColor())
					logicStoneArr.add(logicBoard.getBoard()[curRow][colToCheck]);
		}
		return logicStoneArr.size() == 3 ? logicStoneArr : null;
	}
	
	public boolean isStoneInTrio(LogicStone stone) 
	{
		return checkCol(stone) != null || checkRow(stone) != null;
	}
	
	public int countTrios(Color curColor) 
	{
		ArrayList<Integer> rowsToSkip = new ArrayList<Integer>();
		ArrayList<Integer> colsToSkip = new ArrayList<Integer>();
		
		int counter = 0;
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
    	{
			if(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]].getColor() == curColor) 
			{
				if(!rowsToSkip.contains(Board.allowedRowArr[duoIndex]) && checkRow(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]]) != null) 
				{
					counter++;
					rowsToSkip.add(Board.allowedRowArr[duoIndex]);
				}
				if(!colsToSkip.contains(Board.allowedColArr[duoIndex]) && checkCol(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]]) != null) 
				{
					counter++;
					colsToSkip.add(Board.allowedColArr[duoIndex]);
				}
			}
    	}
		
		return counter;
	}
	
	public ArrayList<LogicStone> allPossibleMoves(Color curColor) 
	{
		ArrayList<LogicStone> possibleMoves = new ArrayList<LogicStone>();
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
    	{
			if(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]].getColor() == curColor) 
				possibleMoves.addAll(allowedMoves(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]]));
    	}
		return possibleMoves;
	}
	
	public ArrayList<LogicStone> allPossibleStonePlaces()
	{
		ArrayList<LogicStone> possibleStonePlaces = new ArrayList<LogicStone>();
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
    	{
			if(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]].getColor() == null) 
				possibleStonePlaces.add(new LogicStone(Board.allowedRowArr[duoIndex], Board.allowedColArr[duoIndex], null));
    	}
		
		return possibleStonePlaces;
	}
	
	public boolean isWinner(Color _color) 
	{
		Color loserColor = _color == Game.firstColor ? Game.secColor : Game.firstColor;
		printAllMoves(allPossibleMoves(loserColor));
		if(_color == Game.firstColor && secColorStonesLeft < 3)
			return true;
		if(_color == Game.secColor && firstColorStonesLeft < 3)
			return true;
		if(allPossibleMoves(loserColor).size() == 0)
			return true;
		return false;
	}
	
	public void printAllMoves(ArrayList<LogicStone> possibleMoves) 
	{
		System.out.println("-------------------------");
		for(int i = 0; i < possibleMoves.size(); i++) 
		{
			LogicStone logicStone = possibleMoves.get(i);
			System.out.println("Row:" + logicStone.getRow() + " Col:" + logicStone.getCol());
		}
		System.out.print("size: ");
		System.out.println(possibleMoves.size());
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

	public LogicBoard getLogicBoard() {
		return logicBoard;
	}	
}
