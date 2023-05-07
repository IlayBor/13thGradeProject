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
	// Statics
	public static int aiDepth = 0;
	public static Color aiColor = Game.secColor;
	public static int turnDelay = 500;
	
	private Board graphicBoard;
	private LogicGame logicGame;
	private LogicBoard logicBoard;
	
	public AI(Board graphicBoard) {
		this.graphicBoard = graphicBoard;
		this.logicGame = graphicBoard.getLogicGame();
		this.logicBoard = logicGame.getLogicBoard();
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
			LogicPlace AiStone = getBestStonePlace(aiColor, aiDepth);
			graphicBoard.placeStone(graphicBoard.getStoneArr()[AiStone.getRow()][AiStone.getCol()]);
			System.out.println(String.format("Ai Placed: %s. With score : %d", AiStone, AiStone.getScore()));
		}
		// Moving Phase
		else if(graphicBoard.getGamePhase() == Phase.move) 
		{
			Move bestMove = getBestMove(aiColor, aiDepth);
			Stone curStone = graphicBoard.getStoneArr()[bestMove.getCurRow()][bestMove.getCurCol()];
			Stone nextStone = graphicBoard.getStoneArr()[bestMove.getNextRow()][bestMove.getNextCol()];
			graphicBoard.moveStone(curStone, Status.copy);
			graphicBoard.moveStone(nextStone, Status.paste);
			System.out.println(String.format("Ai Moved: %s. Score: %d", bestMove, bestMove.getScore()));
		}
		// Removing Phase
		else 
		{
			Color enemyColor = aiColor == Game.firstColor ? Game.secColor : Game.firstColor;
			LogicStone logicStoneToRemove = getBestStoneToRemove(enemyColor, graphicBoard.getPrevPhase());
			Stone graphicStoneToRemove = graphicBoard.getStoneArr()[logicStoneToRemove.getRow()][logicStoneToRemove.getCol()];
			graphicBoard.removeStone(graphicStoneToRemove);
			System.out.println(String.format("Ai Removed: %s", logicStoneToRemove));
		}
	}
	
	// Evaluates stone place, returns the score
	public int evaluate(LogicStone futureStone, Color futureColor) 
	{
		int score = 0;
		futureStone.setColor(futureColor);
		logicBoard.placeStone(futureStone);
		
		if(logicGame.isWinner(futureStone.getColor())) 
			score = 1000;
		else if(logicGame.isStoneInTrio(futureStone))
			score = 100;
		else 
			score = logicGame.allPossibleMoves(futureColor).size();
		
		logicBoard.removeStone(futureStone);
		return score;
	}
	
	// Returns an arrayList with all the possible places to place a stone, sorted from best to worst.
	public ArrayList<LogicPlace> getPlacesSorted(Color color) 
	{
		ArrayList<LogicStone> possiblePlaces = logicGame.getAllStonesOnBoard(null);
		ArrayList<LogicPlace> placesSorted = new ArrayList<LogicPlace>();
		
		for(int i = 0; i < possiblePlaces.size(); i++) 
		{
			LogicPlace curPlaceStone = new LogicPlace(possiblePlaces.get(i), evaluate(possiblePlaces.get(i), color));
			placesSorted.add(curPlaceStone);
		}
		
		Collections.sort(placesSorted, Collections.reverseOrder());
		return placesSorted;
	}
	
	// Evaluates move, sets the move score.
	public void evaluate(Move move, Color color, int depth) 
	{
		int score = 0;
		LogicStone futureStone = new LogicStone(move.getNextRow(), move.getNextCol(), color);
		logicBoard.moveStone(move);
		
		if(logicGame.isWinner(futureStone.getColor())) 
			score += 1000;
		else if(logicGame.isStoneInTrio(futureStone)) 			
				score += 100;
		score = (int)(score * Math.pow(1.1, depth));
		
		logicBoard.reverseMove(move);
		move.setScore(score);
	}
	
	// Returns an arrayList with all the possible moves, sorted from best to worst.
	public ArrayList<Move> getMovesSorted(Color color, int depth) 
	{
		ArrayList<Move> possibleMoves = logicGame.allPossibleMoves(color);
		for(Move move : possibleMoves) 
			evaluate(move, color, depth);
		
		Collections.sort(possibleMoves, Collections.reverseOrder());
		return possibleMoves;
	}
	
	// Returns the best stone to remove.
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
		int minScore = Integer.MAX_VALUE;
		
		for(int i = 0; i < stones.size(); i++) 
		{
			LogicStone curStone = stones.get(i);
			if(!logicGame.isStoneInTrio(curStone)) 
			{
				logicBoard.removeStone(curStone);
				int curScore = getBestStonePlace(enemyColor, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, 0).getScore();
				logicBoard.placeStone(curStone);
				
				if(curScore < minScore) 
				{
					minScore = curScore;
					bestStone = curStone;
				}
			}
		}
		return bestStone;
	}
	
	private LogicStone bestStoneToRemoveMovePhase(Color enemyColor) 
	{
		Move bestEnemyMove = getBestMove(enemyColor, 1);
		LogicStone curBestStoneToRemove = new LogicStone(bestEnemyMove.getCurRow(), bestEnemyMove.getCurCol(), enemyColor);
		
		// If stone is allowed to be removed
		if(!logicGame.isStoneInTrio(curBestStoneToRemove))
			return curBestStoneToRemove;
		// If reached here cannot remove the best move, remove
		return bestStoneToRemovePlacePhase(enemyColor);
	}
	
	public LogicPlace getBestStonePlace(Color color, int depth) 
	{
		return getBestStonePlace(color, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
	}
	
	public LogicPlace getBestStonePlace(Color color, int depth, int alpha, int beta, int score) 
	{
		ArrayList<LogicPlace> allPlacesSorted = getPlacesSorted(color);
		if(depth > 1  && logicBoard.getAmountOfTurns() < 17) 
		{
			boolean flag = true;
			int bestPlaceIndex = 0; 
			LogicStone removedStone; 
			
			for(int i = 0; i < allPlacesSorted.size() && flag; i++) 
			{
				removedStone = null;
				logicBoard.placeStone(allPlacesSorted.get(i));
				
				Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
				// If there is a trio, and can remove, then remove the best stone and update the board.
				if(logicGame.isStoneInTrio(allPlacesSorted.get(i)) && logicGame.canRemoveAnyStone(opponentColor)) 
				{
					logicBoard.decreaseStonesOnBoard(opponentColor);
					removedStone = getBestStoneToRemove(opponentColor, Phase.place);
					logicBoard.removeStone(removedStone);
				}
				
				allPlacesSorted.get(i).addScore(score);
				
				allPlacesSorted.get(i).addScore((int)
						(getBestStonePlace(opponentColor, depth-1, alpha, beta, 
								allPlacesSorted.get(i).getScore() * -1).getScore() * -1));
				
				// Get the best stone place
				if(allPlacesSorted.get(i).getScore() > allPlacesSorted.get(bestPlaceIndex).getScore())
					bestPlaceIndex = i;
				
				if(removedStone != null) {
					logicBoard.placeStone(removedStone);
					logicBoard.increaseStonesOnBoard(opponentColor);
				}
				
				logicBoard.removeStone(allPlacesSorted.get(i));
				
				if(color == aiColor) 
	                alpha = Math.max(alpha, allPlacesSorted.get(i).getScore());
	            else
	                beta = Math.min(beta, allPlacesSorted.get(i).getScore() * -1);
				
	            if (beta <= alpha)
	                flag = false;
			}
			return allPlacesSorted.get(bestPlaceIndex);
		}
		return allPlacesSorted.get(0);
	}
	
	public Move getBestMove(Color color, int depth) 
	{
		return getBestMove(color, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
	}
	
	public Move getBestMove(Color color, int depth, int alpha, int beta, int score) 
	{
		ArrayList<Move> allMovesSorted = getMovesSorted(color, depth);
		
		// If there is no moves, then its a loss, return loss value.
		if(allMovesSorted.size() == 0)
			return new Move(-1,-1,-1,-1,-1000);
		
		if(depth > 1) 
		{
			int bestMoveIndex = 0; 
			boolean flag = true;
			LogicStone removedStone;
			
			for(int i = 0; i < allMovesSorted.size() && flag; i++) 
			{
				removedStone = null;
				LogicStone futureStone = new LogicStone(allMovesSorted.get(i).getNextRow(), allMovesSorted.get(i).getNextCol(), color);
				logicBoard.moveStone(allMovesSorted.get(i));
				
				Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
				// If there is a trio, and can remove, then remove the best stone and update the board.
				if(logicGame.isStoneInTrio(futureStone) && logicGame.canRemoveAnyStone(opponentColor)) 
				{
					logicBoard.decreaseStonesOnBoard(opponentColor);
					removedStone = getBestStoneToRemove(opponentColor, Phase.move);
					logicBoard.removeStone(removedStone);
				}
				
				allMovesSorted.get(i).addScore(score);
				
				allMovesSorted.get(i).addScore(
						getBestMove(opponentColor, depth-1, alpha, beta, 
								allMovesSorted.get(i).getScore() * -1).getScore() *-1);
				
				// Get the best move
				if(allMovesSorted.get(i).getScore() > allMovesSorted.get(bestMoveIndex).getScore())
					bestMoveIndex = i;
				
				if(removedStone != null) {
					logicBoard.placeStone(removedStone);
					logicBoard.increaseStonesOnBoard(opponentColor);
				}
				logicBoard.reverseMove(allMovesSorted.get(i));
				
				if(color == aiColor) 
	                alpha = Math.max(alpha, allMovesSorted.get(i).getScore());
	            else
	                beta = Math.min(beta, allMovesSorted.get(i).getScore() * -1);
				
	            if (beta <= alpha)
	                flag = false;
			}
			return allMovesSorted.get(bestMoveIndex);
		}
		
		return allMovesSorted.get(0);
	}

}