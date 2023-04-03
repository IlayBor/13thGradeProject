package Logic;

import java.awt.Color;
import java.util.ArrayList;

import Graphics.Board;
import Graphics.Game;
import Graphics.Stone;

public class LogicGame {
	
	// way to rate - move that will create - 3 is the best, 2 is second best, 1 is the worst. and MOBILITY!
	
	public Board GBoard;
	public LogicBoard LBoard; 
	public int firstColorStonesLeft = 9;
	public int secColorStonesLeft = 9;
	
	public LogicGame(LogicBoard _LBoard, Board _GBoard) 
	{
		LBoard = _LBoard;
		GBoard = _GBoard;
	}
	
	public boolean isWinner(Color _color) 
	{
		if(_color == Game.firstColor && secColorStonesLeft < 3)
			return true;
		if(_color == Game.secColor && firstColorStonesLeft < 3)
			return true;
		return false;
	}
	
	public boolean isMoveAllowed(Stone lastStone, Stone nextStone) 
	{	
		if(lastStone.stoneColor == Game.firstColor && firstColorStonesLeft <= 3) // JUNK MOVES
			return true;
		if(lastStone.stoneColor == Game.secColor && secColorStonesLeft <= 3)
			return true;
		
		int allowedRowDis = 0;
		int allowedColDis = 0;
		switch(lastStone.row) 
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
		
		switch(lastStone.col) 
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
		
		if(Math.abs(lastStone.row - nextStone.row) == allowedRowDis && lastStone.col - nextStone.col == 0)
			return true;
		if(Math.abs(lastStone.col - nextStone.col) == allowedColDis && lastStone.row - nextStone.row == 0)
			return true;
		return false;
	}
	
	public ArrayList<LogicStone> allowedMoves(Stone stone)
	{
		ArrayList<LogicStone> allowedMovesArr = new ArrayList<LogicStone>();
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
		{
			if(isMoveAllowed(stone, GBoard.stoneArr[GBoard.allowedRowArr[duoIndex]][GBoard.allowedColArr[duoIndex]])) 
			{
				allowedMovesArr.add(new LogicStone(GBoard.allowedRowArr[duoIndex], GBoard.allowedColArr[duoIndex]));
			}
		}
		
		return allowedMovesArr;
	}
	
	public ArrayList<LogicStone> checkRow(Stone stone) 
	{
		char charInLogic = stone.stoneColor == Game.firstColor ? LogicBoard.firstPlayerChar : LogicBoard.secPlayerChar;
		int rowToCheck = stone.row;
		
		int startCol = (rowToCheck == 3) ? stone.col < 3 ? 0 : 4 : 0;
		int endCol = (rowToCheck == 3) ? stone.col < 3 ? 3 : 7: 7;
		ArrayList<LogicStone> logicStoneArr = new ArrayList<LogicStone>();
		
		for(int curCol = startCol; curCol < endCol; curCol++) 
		{
			if(LBoard.LBoard[rowToCheck][curCol] == charInLogic)
				logicStoneArr.add(new LogicStone(rowToCheck, curCol));
		}
		return logicStoneArr.size() == 3 ? logicStoneArr : null;
	}
	
	public ArrayList<LogicStone> checkCol(Stone stone) 
	{
		char charInLogic = stone.stoneColor == Game.firstColor ? LogicBoard.firstPlayerChar : LogicBoard.secPlayerChar;
		int colToCheck = stone.col;
		
		int startRow = (colToCheck == 3) ? stone.row < 3 ? 0 : 4 : 0;
		int endRow = (colToCheck == 3) ? stone.row < 3 ? 3 : 7: 7;
		
		ArrayList<LogicStone> logicStoneArr = new ArrayList<LogicStone>();
		
		for(int curRow = startRow; curRow < endRow; curRow++) 
		{
			
			if(LBoard.LBoard[curRow][colToCheck] == charInLogic)
				logicStoneArr.add(new LogicStone(curRow, colToCheck));
		}
		return logicStoneArr.size() == 3 ? logicStoneArr : null;
	}
	
	public boolean isStoneInTrio(Stone stone) 
	{
		return checkCol(stone) != null || checkRow(stone) != null;
	}
	
	public ArrayList<LogicStone> allPossibleMoves(Color curColor) 
	{
		char charInLogic = curColor == Game.firstColor ? LogicBoard.firstPlayerChar : LogicBoard.secPlayerChar;
		ArrayList<LogicStone> possibleMoves = new ArrayList<LogicStone>();
		
		for(int curRow = 0; curRow < LBoard.LBoard.length; curRow++) 
		{
			for(int curCol = 0; curCol < LBoard.LBoard.length; curCol++) 
			{
				if(LBoard.LBoard[curRow][curCol] == charInLogic) 
				{
					possibleMoves.addAll(allowedMoves(GBoard.stoneArr[curRow][curCol]));
				}
			}
		}
		
		return possibleMoves;
	}
	
	public ArrayList<LogicStone> allPossibleStonePlaces(Color curColor)
	{
		char charInLogic = curColor == Game.firstColor ? LogicBoard.firstPlayerChar : LogicBoard.secPlayerChar;
		ArrayList<LogicStone> possibleStonePlaces = new ArrayList<LogicStone>();
		
		for(int curRow = 0; curRow < LBoard.LBoard.length; curRow++) 
		{
			for(int curCol = 0; curCol < LBoard.LBoard.length; curCol++) 
			{
				if(LBoard.LBoard[curRow][curCol] == LogicBoard.playableChar) 
					possibleStonePlaces.add(new LogicStone(curRow, curCol));
			}
		}
		return possibleStonePlaces;
	}
	
	public void printAllMoves(ArrayList<LogicStone> possibleMoves) 
	{
		System.out.println("-------------------------");
		for(int i = 0; i < possibleMoves.size(); i++) 
		{
			LogicStone logicStone = possibleMoves.get(i);
			System.out.println("Row:" + logicStone.row + " Col:" + logicStone.col);
		}
		System.out.println("-------------------------");
	}
}
