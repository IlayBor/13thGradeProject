package Logic;

import java.awt.Color;
import java.util.ArrayList;

import Graphics.Game;

public class AI {
	public static int aiLevel = 0; // 0 - None, 1 - Easy, 2 - Medium, 3 - Hard
	private LogicGame logicGame;

	public AI(LogicGame logicGame) {
		this.logicGame = logicGame;
	}
	
	public int evaluateMove(Move m) 
	{
		int score = 0;
		LogicBoard boardAfterMove = new LogicBoard(this.logicGame.getLogicBoard());
		boardAfterMove.moveStone(m);
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		
		boardAfterMove.printLogicBoard();
		
		return score;
	}
	
	public void printAllBoards(Color c) 
	{
		ArrayList<Move> possibleMoves = logicGame.allPossibleMoves(c);
		for(int i = 0; i < possibleMoves.size(); i++) 
		{
			Move m = possibleMoves.get(i);
			System.out.printf("-------Move %d------\n", i);
			System.out.printf("Move Info: from %d %d to %d %d\n", m.getCurRow(), m.getCurCol(), m.getNextRow(), m.getNextCol());
			evaluateMove(m);
		}
	}
}
