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
	 
	public int evaluateStonePlace(LogicStone futureStone) 
	{
		int score = 0;
		LogicBoard boardAfterMove = new LogicBoard(this.logicGame.getLogicBoard());
		boardAfterMove.placeStone(futureStone.getRow(), futureStone.getCol(), futureStone.getColor());
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		
		if(possibleGame.isStoneInTrio(futureStone)) 
			score += 8;
		else if(possibleGame.isBlockingTrio(futureStone)) 
			score += 7;
		else if(possibleGame.allowedMoves(futureStone).size() == 4)
			score += 6;
		else if(possibleGame.allowedMoves(futureStone).size() == 3)
			score += 5;
		else if(possibleGame.isCreatingDuo(futureStone))
			score += 4;
		else
			score += possibleGame.allowedMoves(futureStone).size();
		
		return score;
	}
	
	public LogicStone getBestStonePlace(Color color) 
	{
		ArrayList<LogicStone> possiblePlaces = logicGame.allPossibleStonePlaces(color);
		System.out.println(possiblePlaces.size());
		LogicStone bestPlace = possiblePlaces.get(0);
		int maxScore = evaluateStonePlace(bestPlace);
		
		for(int i = 1; i < possiblePlaces.size(); i++) 
		{
			LogicStone curPlace = possiblePlaces.get(i);
			int curScore = evaluateStonePlace(curPlace);
			if(curScore > maxScore) 
			{
				bestPlace = curPlace;
				maxScore = curScore;
			}
		}
		System.out.printf("best place : %d %d with score %d\n", bestPlace.getRow(), bestPlace.getCol(), maxScore);
		return bestPlace;
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
	
	public int evaluateStoneRemoval(LogicStone stone) 
	{
		int score = 0;
		
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
