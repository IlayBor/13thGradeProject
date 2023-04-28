package Logic;

import java.awt.Color;
import java.util.ArrayList;

import Graphics.Board;
import Graphics.Game;
import Graphics.Stone;

public class LogicGame {	
	private LogicBoard logicBoard; 
	
	public LogicGame() 
	{
		logicBoard = new LogicBoard();
	}

	public LogicGame(LogicBoard logicBoard) 
	{
		this.logicBoard = logicBoard;
	}
	
	public boolean isMoveAllowed(Move m) 
	{	
		// Junk moves
		if(logicBoard.getBoard()[m.getCurRow()][m.getCurCol()].getColor() == Game.firstColor && logicBoard.getFirstColorStonesOnBoard() <= 3)
			return true;
		if(logicBoard.getBoard()[m.getCurRow()][m.getCurCol()].getColor() == Game.secColor && logicBoard.getSecColorStonesOnBoard() <= 3)
			return true;
		
		int allowedRowDis = 0;
		int allowedColDis = 0;
		switch(m.getCurRow()) 
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
		
		switch(m.getCurCol()) 
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
		
		if(Math.abs(m.getCurRow() - m.getNextRow()) == allowedRowDis && m.getCurCol() - m.getNextCol() == 0)
			return true;
		if(Math.abs(m.getCurCol() - m.getNextCol()) == allowedColDis && m.getCurRow() - m.getNextRow() == 0)
			return true;
		return false;
	}
	
	public ArrayList<Move> allowedMoves(LogicStone stone)
	{
		ArrayList<Move> allowedMovesArr = new ArrayList<Move>();
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
		{
			if(logicBoard.getBoard()[LogicBoard.allowedRowArr[duoIndex]][LogicBoard.allowedColArr[duoIndex]].getColor() == null) 
			{
				Move m = new Move(stone.getRow(), stone.getCol(), LogicBoard.allowedRowArr[duoIndex], LogicBoard.allowedColArr[duoIndex]);
				if(isMoveAllowed(m))
					allowedMovesArr.add(m);
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
	
	public boolean canRemoveAnyStone(Color color) 
	{
		for(int duoIndex = 0; duoIndex < LogicBoard.allowedColArr.length; duoIndex++) 
		{
			LogicStone curStone = logicBoard.getBoard()[LogicBoard.allowedRowArr[duoIndex]][LogicBoard.allowedColArr[duoIndex]];
			if(curStone.getColor() == color && !isStoneInTrio(curStone)) 
				return true;
		}
    	return false;
	}
	
	public boolean isBlockingRow(LogicStone futureStone) 
	{
		Color opponentColor = futureStone.getColor() == Game.firstColor ? Game.secColor : Game.firstColor; 
		
		int counter = 0;
		int rowToCheck = futureStone.getRow();
		int startCol = (rowToCheck == 3) ? futureStone.getCol() < 3 ? 0 : 4 : 0;
		int endCol = (rowToCheck == 3) ? futureStone.getCol() < 3 ? 3 : 7: 7;
		
		for(int curCol = startCol; curCol < endCol; curCol++) 
		{
			if(logicBoard.getBoard()[rowToCheck][curCol] != null) // if exists
				if(logicBoard.getBoard()[rowToCheck][curCol].getColor() == opponentColor)
					counter++;
		}
		return (counter == 2);
	}
	
	public boolean isBlockingCol(LogicStone futureStone) 
	{
		Color opponentColor = futureStone.getColor() == Game.firstColor ? Game.secColor : Game.firstColor; 
		
		int counter = 0;
		
		int colToCheck = futureStone.getCol();
		int startRow = (colToCheck == 3) ? futureStone.getRow() < 3 ? 0 : 4 : 0;
		int endRow = (colToCheck == 3) ? futureStone.getRow() < 3 ? 3 : 7: 7;
		
		for(int curRow = startRow; curRow < endRow; curRow++) 
		{
			if(logicBoard.getBoard()[curRow][colToCheck] != null) // if exists
				if(logicBoard.getBoard()[curRow][colToCheck].getColor() == opponentColor)
					counter++;
		}
		return (counter == 2);
	}
	
	public boolean isBlockingTrio(LogicStone futureStone) 
	{
		return isBlockingRow(futureStone) || isBlockingCol(futureStone);
	}
	
	public boolean isDuoRow(LogicStone futureStone) 
	{
		int stonesInRow = 0;
		Color color = futureStone.getColor();
		Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
		
		int rowToCheck = futureStone.getRow();
		int startCol = (rowToCheck == 3) ? futureStone.getCol() < 3 ? 0 : 4 : 0;
		int endCol = (rowToCheck == 3) ? futureStone.getCol() < 3 ? 3 : 7: 7;
		
		for(int curCol = startCol; curCol < endCol; curCol++) 
		{
			// if exists
			if(logicBoard.getBoard()[rowToCheck][curCol] != null)
				// if have 2 right colored stones
				if(logicBoard.getBoard()[rowToCheck][curCol].getColor() == color)
					stonesInRow++;
				// if have a stone in the wrong color
				else if(logicBoard.getBoard()[rowToCheck][curCol].getColor() == opponentColor)
					return false;
		}
		return (stonesInRow == 2);
	} 
	
	public boolean isDuoCol(LogicStone futureStone) 
	{
		int stonesInCol = 0;
		Color color = futureStone.getColor();
		Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
		
		int colToCheck = futureStone.getCol();
		int startRow = (colToCheck == 3) ? futureStone.getRow() < 3 ? 0 : 4 : 0;
		int endRow = (colToCheck == 3) ? futureStone.getRow() < 3 ? 3 : 7: 7;
		
		for(int curRow = startRow; curRow < endRow; curRow++) 
		{
			// if exists
			if(logicBoard.getBoard()[curRow][colToCheck] != null)
				// if have 2 right colored stones
				if(logicBoard.getBoard()[curRow][colToCheck].getColor() == color)
					stonesInCol++;
				// if have a stone in the wrong color
				else if(logicBoard.getBoard()[curRow][colToCheck].getColor() == opponentColor)
					return false;
		}
		return (stonesInCol == 2);
	}
	
	public boolean isCreatingDuo(LogicStone futureStone) 
	{
		return isDuoRow(futureStone) || isDuoCol(futureStone);
	}
	
	public ArrayList<Move> allPossibleMoves(Color curColor) 
	{
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
    	{
			if(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]].getColor() == curColor) 
				possibleMoves.addAll(allowedMoves(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]]));
    	}
		return possibleMoves;
	}
	
	public ArrayList<LogicStone> getAllStonesOnBoard(Color color)
	{
		ArrayList<LogicStone> stones = new ArrayList<LogicStone>();
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
    	{
			if(logicBoard.getBoard()[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]].getColor() == color)
				stones.add(new LogicStone(Board.allowedRowArr[duoIndex], Board.allowedColArr[duoIndex], color));
    	}
		return stones;
	}
	
	public boolean isWinner(Color _color) 
	{
		Color loserColor = _color == Game.firstColor ? Game.secColor : Game.firstColor;
		if(_color == Game.firstColor && logicBoard.getSecColorStonesOnBoard() < 3)
			return true;
		if(_color == Game.secColor && logicBoard.getFirstColorStonesOnBoard() < 3)
			return true;
		if(allPossibleMoves(loserColor).size() == 0)
			return true;
		return false;
	}
	
	public void printAllMoves(ArrayList<Move> possibleMoves) 
	{
		System.out.println("-------------------------");
		for(int i = 0; i < possibleMoves.size(); i++) 
		{
			Move move = possibleMoves.get(i);
			System.out.printf("From: %d %d To %d %d \n", move.getCurRow(), move.getCurCol(), move.getNextRow(), move.getNextCol());
		}
		System.out.print("size: ");
		System.out.println(possibleMoves.size());
	}

	public LogicBoard getLogicBoard() {
		return logicBoard;
	}

	public void setLogicBoard(LogicBoard logicBoard) {
		this.logicBoard = logicBoard;
	}
}
