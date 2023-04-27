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
	private static int MOVES_TO_CHECK = 3;
	private static int DEPTH = 5;
	
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
			ArrayList<Move> lst = getMoveTrio(this.logicGame.getLogicBoard(),aiColor, DEPTH);
			for(int i = 0; i < lst.size(); i++)
				System.out.println(lst.get(i));
			Move bestMove = getBestMove(this.logicGame.getLogicBoard(),aiColor, DEPTH);
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
			LogicStone logicStoneToRemove = getBestStoneToRemove(this.logicGame.getLogicBoard(),enemyColor, graphicBoard.getPrevPhase());
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
	public void evaluate(LogicBoard possibleBoard, Move move, Color color) 
	{
		int score = 0;
		LogicStone futureStone = new LogicStone(move.getNextRow(), move.getNextCol(), color);
		LogicBoard boardAfterMove = new LogicBoard(possibleBoard);
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		boardAfterMove.moveStone(move);
		
		if(possibleGame.isWinner(futureStone.getColor())) 
			score += 100;
		else {
			if(possibleGame.isStoneInTrio(futureStone)) 			
				score += 80;
			score += possibleGame.allowedMoves(futureStone).size() * 5;
 		}
		move.setScore(score);
	}
	
	private ArrayList<Move> getMoveSorted(LogicBoard board, Color color) 
	{
		LogicGame possibleGame = new LogicGame(board);
		ArrayList<Move> possibleMoves = possibleGame.allPossibleMoves(color);
		for(Move m : possibleMoves) 
			evaluate(board, m, color);
		
		Collections.sort(possibleMoves, Collections.reverseOrder());
		return possibleMoves;
	}
	
	public LogicStone getBestStoneToRemove(LogicBoard board, Color enemyColor, Phase gamePhase) 
	{
		// Remove enemy's best stone
		if(gamePhase == Phase.place) 
			return bestStoneToRemovePlacePhase(board, enemyColor);
		// Remove enemy's best allowed move.
		else 
			return bestStoneToRemoveMovePhase(board, enemyColor);
	}
	
	private LogicStone bestStoneToRemovePlacePhase(LogicBoard board, Color enemyColor) 
	{
		LogicGame game = new LogicGame(board);
		ArrayList<LogicStone> stones = game.getAllStonesOnBoard(enemyColor);
		LogicStone bestStone = null;
		int maxScore = -1;
		
		for(int i = 0; i < stones.size(); i++) 
		{
			LogicStone curStone = stones.get(i);
			// If stone not in a mill, allow to remove.
			if(!game.isStoneInTrio(curStone)) 
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
	
	private LogicStone bestStoneToRemoveMovePhase(LogicBoard board, Color enemyColor) 
	{
		LogicGame game = new LogicGame(board);
		int amountOfMoves = game.allPossibleMoves(enemyColor).size();
		for(int rate = 1; rate <= amountOfMoves; rate++) 
		{
			Move bestEnemyMove = getBestMove(game.getLogicBoard(),enemyColor, 1);
			LogicStone curBestStoneToRemove = new LogicStone(bestEnemyMove.getCurRow(), bestEnemyMove.getCurCol(), enemyColor);
			// If stone is allowed to be removed
			if(!game.isStoneInTrio(curBestStoneToRemove))
				return curBestStoneToRemove;
		}
		// If reached here, cannot remove any future move, instead remove a stuck stone.
		return bestStoneToRemovePlacePhase(board, enemyColor);
	}
	
	public Move getBestMove(LogicBoard board, Color color, int depth) 
	{
		ArrayList<Move> bestMoves = getMoveSorted(board, color);
		ArrayList<Integer> scoreList = new ArrayList<Integer>();
		for (Move m : bestMoves) {
			scoreList.add(m.getScore());
		}
		if(depth > 1) 
		{
			int bestScoreIndex = 0;
			for(int i = 0; i < MOVES_TO_CHECK && i < bestMoves.size(); i++) 
			{
				LogicBoard testBoard = new LogicBoard(board);
				LogicGame testGame = new LogicGame(testBoard);
				LogicStone futureStone = new LogicStone(bestMoves.get(i).getNextRow(), bestMoves.get(i).getNextCol(), color);
				testBoard.moveStone(bestMoves.get(i));
				
				if(testGame.isStoneInTrio(futureStone))
					testBoard.removeStone(getBestStoneToRemove(testBoard, color, Phase.move));
					
				// reduce stone
				bestMoves.get(i).addScore(
						getBestMove(testBoard, color == Game.firstColor ? Game.secColor : Game.firstColor, depth-1).getScore() * -1);
				
				if(bestMoves.get(i).getScore() > bestMoves.get(bestScoreIndex).getScore())
					bestScoreIndex = i;
				else if(bestMoves.get(i).getScore() == bestMoves.get(bestScoreIndex).getScore())
					if(scoreList.get(i) > scoreList.get(bestScoreIndex))
						bestScoreIndex = i;
			}
			return bestMoves.get(bestScoreIndex);
		}
		return bestMoves.get(0);
	}
	
	public int sumArray(ArrayList<Move> lst) {
		int sum = 0;
		for(int i = 0; i < lst.size(); i++) 
		{
			if(i%2 == 0)
				sum += lst.get(i).getScore();
			else
				sum -= lst.get(i).getScore();
		}
		return sum;
	}
	
	public ArrayList<Move> getMoveTrio(LogicBoard board, Color color, int depth) 
	{
		ArrayList<Move> bestMoves = getMoveSorted(board, color);
		ArrayList<ArrayList<Move>> allLists = new ArrayList<ArrayList<Move>>();
		int maxIndex = 0;
		
		if(depth > 1) 
		{
			for(int i = 0; i < bestMoves.size(); i++) 
			{
				LogicBoard testBoard = new LogicBoard(board);
				LogicGame testGame = new LogicGame(testBoard);
				LogicStone futureStone = new LogicStone(bestMoves.get(i).getNextRow(), bestMoves.get(i).getNextCol(), color);
				testBoard.moveStone(bestMoves.get(i));
				
				if(testGame.isStoneInTrio(futureStone))
					testBoard.removeStone(getBestStoneToRemove(testBoard, color, Phase.move));
				
				allLists.add(getMoveTrio(testBoard, color == Game.firstColor ? Game.secColor : Game.firstColor, depth-1));
				allLists.get(i).add(0, bestMoves.get(i));
				if(sumArray(allLists.get(i)) > sumArray(allLists.get(maxIndex)))
					maxIndex = i;
				else if(sumArray(allLists.get(i)) == sumArray(allLists.get(maxIndex)))
					if(bestMoves.get(i).getScore() > bestMoves.get(maxIndex).getScore())
						maxIndex = i;
			}
			return allLists.get(maxIndex);
		}
		ArrayList<Move> returnOne = new ArrayList<Move>();
		returnOne.add(bestMoves.get(0));
		return returnOne;
	}
}
