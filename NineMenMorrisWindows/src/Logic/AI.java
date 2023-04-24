package Logic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import Graphics.Board;
import Graphics.Board.Phase;
import Graphics.Board.Status;
import Graphics.Game;
import Graphics.Stone;
import java.util.Timer;
import java.util.TimerTask;

public class AI {
	// statics
	public static int aiLevel = 0; // 0 - None, 1 - Easy, 2 - Medium, 3 - Hard
	public static Color aiColor = Game.secColor;
	private static int turnDelay = 800;
	
	private Board graphicBoard;
	private LogicGame logicGame;
	
	public AI(Board graphicBoard) {
		this.graphicBoard = graphicBoard;
		this.logicGame = graphicBoard.getLogicGame();
	}
	 
	public void AiTurn() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() 
            {
            	AiPlay();
            }
        }, turnDelay);
    }
	
	private void AiPlay() 
	{	
		// Placing Phase
		if(graphicBoard.getGamePhase() == Phase.place) 
		{
			LogicStone AiStone = getBestStonePlace(aiColor);
			System.out.println(String.format("Ai Placed: %s. Score: %d", AiStone, evaluate(AiStone, aiColor)));
			graphicBoard.placeStone(graphicBoard.getStoneArr()[AiStone.getRow()][AiStone.getCol()]);
		}
		// Moving Phase
		else if(graphicBoard.getGamePhase() == Phase.move) 
		{
			Move bestMove = getBestMove(aiColor);
			Stone curStone = graphicBoard.getStoneArr()[bestMove.getCurRow()][bestMove.getCurCol()];
			Stone nextStone = graphicBoard.getStoneArr()[bestMove.getNextRow()][bestMove.getNextCol()];
			System.out.println(String.format("Ai Moved: %s. Score: %d", bestMove, bestMove.getScore()));
			graphicBoard.moveStone(curStone, Status.copy);
			graphicBoard.moveStone(nextStone, Status.paste);
			
		}
		// Removing Phase
		else 
		{
			Color enemyColor = aiColor == Game.firstColor ? Game.secColor : Game.firstColor;
			LogicStone logicStoneToRemove = getBestStoneToRemove(enemyColor, graphicBoard.getPrevPhase());
			Stone stoneToRemove = graphicBoard.getStoneArr()[logicStoneToRemove.getRow()][logicStoneToRemove.getCol()];
			System.out.println(String.format("Ai Removed: %s. Score: %d", logicStoneToRemove, evaluate(logicStoneToRemove, aiColor == Game.firstColor ? Game.secColor : Game.firstColor)));
			graphicBoard.removeStone(stoneToRemove);
		}
	}
	
	// Evaluates stone place 
	public int evaluate(LogicStone futureStone, Color futureColor) 
	{
		int score = 0;
		LogicBoard boardAfterMove = new LogicBoard(this.logicGame.getLogicBoard());
		futureStone.setColor(futureColor);
		boardAfterMove.placeStone(futureStone);
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		
		if(possibleGame.isWinner(futureStone.getColor())) 
			score += 60;
		else if(possibleGame.isStoneInTrio(futureStone))
			score += 10;
		else if(possibleGame.isBlockingTrio(futureStone)) 
			score += 8;
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
		ArrayList<LogicStone> possiblePlaces = logicGame.getAllStonesOnBoard(null);
		LogicStone bestPlace = null;
		int maxScore = -1;
		
		for(int i = 0; i < possiblePlaces.size(); i++) 
		{
			LogicStone curPlace = possiblePlaces.get(i);
			int curScore = evaluate(curPlace, color);
			
			if(curScore > maxScore) 
			{
				bestPlace = curPlace;
				maxScore = curScore;
			}
		}
		return bestPlace;
	}
	
	// Evaluates move
	public int evaluate(LogicBoard currentBoard, Move move, Color color) 
	{
		int score = 0;
		LogicStone futureStone = new LogicStone(move.getNextRow(), move.getNextCol(), color);
		LogicBoard boardAfterMove = new LogicBoard(currentBoard);
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		boardAfterMove.moveStone(move);
		
		if(possibleGame.isWinner(futureStone.getColor())) 
			score += 60;
		else if(possibleGame.isStoneInTrio(futureStone)) 
			score += 10;
		else if(possibleGame.isBlockingTrio(futureStone)) 
			score += 8;
		else if(possibleGame.allowedMoves(futureStone).size() == 4)
			score += 6;
		else if(possibleGame.allowedMoves(futureStone).size() == 3)
			score += 5;
		else if(possibleGame.isCreatingDuo(futureStone))
			score += 4;
		else if (possibleGame.allowedMoves(futureStone).size() <= 2)
			score += possibleGame.allowedMoves(futureStone).size();
		move.setScore(score);
		
		return score;
	}
	
	public Move getBestMove(Color color) 
	{
		return getMoveDesc(color, 1);
	}
	
	
	private Move getMoveDesc(Color color, int rate) 
	{
		ArrayList<Move> possibleMoves = logicGame.allPossibleMoves(color);
		for(Move m : possibleMoves) 
			evaluate(logicGame.getLogicBoard(), m, color);
		
		Collections.sort(possibleMoves, Collections.reverseOrder());
		return possibleMoves.get(rate - 1);
	}
	
	public LogicStone getBestStoneToRemove(Color enemyColor, Phase gamePhase) 
	{
		// Remove enemy's best stone
		if(gamePhase == Phase.place) 
			return bestStoneToRemovePlacePhase(enemyColor);
		// Remove enemy's best allowed move.
		else 
			return bestStoneToRemoveMovePhase(enemyColor);
	}
	
	private LogicStone bestStoneToRemovePlacePhase(Color enemyColor) 
	{
		ArrayList<LogicStone> stones = logicGame.getAllStonesOnBoard(enemyColor);
		LogicStone bestStone = null;
		int maxScore = -1;
		
		for(int i = 0; i < stones.size(); i++) 
		{
			LogicStone curStone = stones.get(i);
			// If stone not in a mill, allow to remove.
			if(!logicGame.isStoneInTrio(curStone)) 
			{
				int curScore = evaluate(curStone, enemyColor);
				if(curScore > maxScore) 
				{
					bestStone = curStone;
					maxScore = curScore;
				}
			}
		}
		return bestStone;
	}
	
	private LogicStone bestStoneToRemoveMovePhase(Color enemyColor) 
	{
		int amountOfMoves = logicGame.allPossibleMoves(enemyColor).size();
		for(int rate = 1; rate <= amountOfMoves; rate++) 
		{
			Move bestEnemyMove = getMoveDesc(enemyColor, rate);
			LogicStone curBestStoneToRemove = new LogicStone(bestEnemyMove.getCurRow(), bestEnemyMove.getCurCol(), enemyColor);
			// If stone is allowed to be removed
			if(!logicGame.isStoneInTrio(curBestStoneToRemove))
				return curBestStoneToRemove;
		}
		// If reached here, cannot remove any future move, instead remove a stuck stone.
		return bestStoneToRemovePlacePhase(enemyColor);
	} 
}
