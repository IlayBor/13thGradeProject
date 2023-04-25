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
	private static int DEPTH = 3;
	
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
			Move bestMove = getBestMove(this.logicGame.getLogicBoard(),aiColor, DEPTH);
			System.out.println(m3 + " with score " + m3.getScore());
			System.out.println(m2 + " with score " + m2.getScore());
			System.out.println(m1 + " with score " + m1.getScore());
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
	public int evaluate(LogicBoard possibleBoard, Move move, Color color) 
	{
		int score = 0;
		LogicStone futureStone = new LogicStone(move.getNextRow(), move.getNextCol(), color);
		LogicBoard boardAfterMove = new LogicBoard(possibleBoard);
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		boardAfterMove.moveStone(move);
		
		if(possibleGame.isWinner(futureStone.getColor())) 
			score += 60;
		else if(possibleGame.isStoneInTrio(futureStone)) 
			score += 10;
		else if(possibleGame.isCreatingDuo(futureStone))
			score += 6;
		else if (possibleGame.allowedMoves(futureStone).size() <= 4)
			score += possibleGame.allowedMoves(futureStone).size();
		move.setScore(score);
		
		return score;
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
			Move bestEnemyMove = getBestMove(this.logicGame.getLogicBoard(),enemyColor, 1);
			LogicStone curBestStoneToRemove = new LogicStone(bestEnemyMove.getCurRow(), bestEnemyMove.getCurCol(), enemyColor);
			// If stone is allowed to be removed
			if(!logicGame.isStoneInTrio(curBestStoneToRemove))
				return curBestStoneToRemove;
		}
		// If reached here, cannot remove any future move, instead remove a stuck stone.
		return bestStoneToRemovePlacePhase(enemyColor);
	}
	
	Move m1 = new Move();
	Move m2 = new Move();
	Move m3 = new Move();
	public Move getBestMove(LogicBoard board, Color color, int depth) 
	{
		ArrayList<Move> bestMoves = getMoveSorted(board, color);
		if(depth > 1) 
		{
			int bestScoreIndex = 0;
			for(int i = 0; i < MOVES_TO_CHECK && i < bestMoves.size(); i++) 
			{
				LogicBoard testBoard = new LogicBoard(board);
				testBoard.moveStone(bestMoves.get(i));
				bestMoves.get(i).addScore(
						getBestMove(testBoard, color == Game.firstColor ? Game.secColor : Game.firstColor, depth-1).getScore() * -1);
				
				if(bestMoves.get(i).getScore() > bestMoves.get(bestScoreIndex).getScore())
					bestScoreIndex = i;
			}
			if(depth==2 && m2.getScore() < bestMoves.get(bestScoreIndex).getScore())
				m2 = bestMoves.get(bestScoreIndex);
			if(depth==3 && m3.getScore() < bestMoves.get(bestScoreIndex).getScore())
				m3 = bestMoves.get(bestScoreIndex);
			return bestMoves.get(bestScoreIndex);
		}
		if(depth==1 && m1.getScore() < bestMoves.get(0).getScore())
			m1 = bestMoves.get(0);
		return bestMoves.get(0);
	}
}
