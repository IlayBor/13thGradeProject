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
	 
	public int evaluate(LogicStone futureStone) 
	{
		int score = 0;
		LogicBoard boardAfterMove = new LogicBoard(this.logicGame.getLogicBoard());
		boardAfterMove.placeStone(futureStone.getRow(), futureStone.getCol(), futureStone.getColor());
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		
		if(possibleGame.isWinner(futureStone.getColor())) 
			score += 9;
		else if(possibleGame.isStoneInTrio(futureStone)) 
			score += 8;
		else if(possibleGame.isBlockingTrio(futureStone)) 
			score += 7;
		else if(possibleGame.allowedMoves(futureStone).size() == 4)
			score += 6;
		else if(possibleGame.allowedMoves(futureStone).size() == 3)
			score += 5;
		else if(possibleGame.isCreatingDuo(futureStone))
			score += 4;
		else if (possibleGame.allowedMoves(futureStone).size() <= 2)
			score += possibleGame.allowedMoves(futureStone).size();
		return score;
	}
	
	public LogicStone getBestStonePlace(Color color) 
	{
		ArrayList<LogicStone> possiblePlaces = logicGame.allPossibleStonePlaces(color);
		LogicStone bestPlace = possiblePlaces.get(0);
		int maxScore = evaluate(bestPlace);
		
		for(int i = 1; i < possiblePlaces.size(); i++) 
		{
			LogicStone curPlace = possiblePlaces.get(i);
			int curScore = evaluate(curPlace);
			if(curScore > maxScore) 
			{
				bestPlace = curPlace;
				maxScore = curScore;
			}
		}
		return bestPlace;
	}
	
	public Move getBestMove(Color color) 
	{
		ArrayList<Move> possibleMoves = logicGame.allPossibleMoves(color);
		Move bestMove = possibleMoves.get(0);
		int maxScore = evaluate(new LogicStone(bestMove.getNextRow(), bestMove.getNextCol(), color));
		for(int i = 1; i < possibleMoves.size(); i++) 
		{
			Move curMove = possibleMoves.get(i);
			int curScore = evaluate(new LogicStone(curMove.getNextRow(), curMove.getNextCol(), color));
			if(curScore > maxScore) 
			{
				bestMove = curMove;
				maxScore = curScore;
			}
		}
		System.out.printf("Chose Move from %d %d to %d %d with score %d\n", 
				bestMove.getCurRow(), bestMove.getCurCol(), bestMove.getNextRow(), bestMove.getNextCol(), maxScore);
		return bestMove;
	}
	
	// improve for place phase 
	public LogicStone getBestStoneToRemove(Color enemyColor) 
	{
		Move bestEnemyMove = getBestMove(enemyColor);
		LogicStone curBestStoneToRemove = new LogicStone(bestEnemyMove.getCurRow(), bestEnemyMove.getCurCol(), enemyColor);
		return curBestStoneToRemove;
	}
	
	public void printAllBoards(Color color) 
	{
		ArrayList<Move> possibleMoves = logicGame.allPossibleMoves(color);
		for(int i = 0; i < possibleMoves.size(); i++) 
		{
			Move m = possibleMoves.get(i);
			System.out.printf("-------Move %d------\n", i);
			System.out.printf("Move Info: from %d %d to %d %d\n", m.getCurRow(), m.getCurCol(), m.getNextRow(), m.getNextCol());
		}
	}
}
