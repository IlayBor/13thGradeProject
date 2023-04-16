package Logic;

import java.awt.Color;
import java.util.ArrayList;

import Graphics.Board;
import Graphics.Game;
import Graphics.Stone;

public class LogicGame {
	
	// way to rate - move that will create - 3 is the best, 2 is second best, 1 is the worst. and MOBILITY!
	public static int aiLevel = 0; // 0 - None, 1 - Easy, 2 - Medium, 3 - Hard
	
	private Board GraphicsBoard;
	private LogicBoard logicBoard; 
	
	private int firstColorStonesLeft = 9;
	private int secColorStonesLeft = 9;
	
	public LogicGame(LogicBoard _LBoard, Board _GraphicsBoard) 
	{
		logicBoard = _LBoard;
		GraphicsBoard = _GraphicsBoard;
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
	
	public boolean isMoveAllowed(Stone lastStone, Stone nextStone) 
	{	
		if(lastStone.getStoneColor() == Game.firstColor && firstColorStonesLeft <= 3) // JUNK MOVES
			return true;
		if(lastStone.getStoneColor() == Game.secColor && secColorStonesLeft <= 3)
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
	
	public ArrayList<LogicStone> allowedMoves(Stone stone)
	{
		ArrayList<LogicStone> allowedMovesArr = new ArrayList<LogicStone>();
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
		{
			if(isMoveAllowed(stone, GraphicsBoard.stoneArr[GraphicsBoard.allowedRowArr[duoIndex]][GraphicsBoard.allowedColArr[duoIndex]]) 
					&& logicBoard.LBoard[GraphicsBoard.allowedRowArr[duoIndex]][GraphicsBoard.allowedColArr[duoIndex]] == LogicBoard.playableChar) 
			{
				allowedMovesArr.add(new LogicStone(GraphicsBoard.allowedRowArr[duoIndex], GraphicsBoard.allowedColArr[duoIndex]));
			}
		}
		return allowedMovesArr;
	}
	
	public ArrayList<LogicStone> checkRow(Stone stone) 
	{
		char charInLogic = stone.getStoneColor() == Game.firstColor ? LogicBoard.firstPlayerChar : LogicBoard.secPlayerChar;
		int rowToCheck = stone.getRow();
		
		int startCol = (rowToCheck == 3) ? stone.getCol() < 3 ? 0 : 4 : 0;
		int endCol = (rowToCheck == 3) ? stone.getCol() < 3 ? 3 : 7: 7;
		ArrayList<LogicStone> logicStoneArr = new ArrayList<LogicStone>();
		
		for(int curCol = startCol; curCol < endCol; curCol++) 
		{
			if(logicBoard.LBoard[rowToCheck][curCol] == charInLogic)
				logicStoneArr.add(new LogicStone(rowToCheck, curCol));
		}
		return logicStoneArr.size() == 3 ? logicStoneArr : null;
	}
	
	public ArrayList<LogicStone> checkCol(Stone stone) 
	{
		char charInLogic = stone.getStoneColor() == Game.firstColor ? LogicBoard.firstPlayerChar : LogicBoard.secPlayerChar;
		int colToCheck = stone.getCol();
		
		int startRow = (colToCheck == 3) ? stone.getRow() < 3 ? 0 : 4 : 0;
		int endRow = (colToCheck == 3) ? stone.getRow() < 3 ? 3 : 7: 7;
		ArrayList<LogicStone> logicStoneArr = new ArrayList<LogicStone>();
		
		for(int curRow = startRow; curRow < endRow; curRow++) 
		{
			
			if(logicBoard.LBoard[curRow][colToCheck] == charInLogic)
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
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
    	{
			if(logicBoard.LBoard[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]] == charInLogic) 
				possibleMoves.addAll(allowedMoves(GraphicsBoard.stoneArr[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]]));
    	}
		return possibleMoves;
	}
	
	public ArrayList<LogicStone> allPossibleStonePlaces()
	{
		ArrayList<LogicStone> possibleStonePlaces = new ArrayList<LogicStone>();
		
		for(int duoIndex = 0; duoIndex < Board.allowedColArr.length; duoIndex++) 
    	{
			if(logicBoard.LBoard[Board.allowedRowArr[duoIndex]][Board.allowedColArr[duoIndex]] == LogicBoard.playableChar) 
				possibleStonePlaces.add(new LogicStone(Board.allowedRowArr[duoIndex], Board.allowedColArr[duoIndex]));
    	}
		
		return possibleStonePlaces;
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
