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
	private static int MOVES_TO_CHECK = 5; 
	
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
			LogicPlace AiStone = getBestStonePlace(aiColor, aiDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			System.out.println(String.format("Ai Placed: %s. With score : %d", AiStone, AiStone.getScore()));
			graphicBoard.placeStone(graphicBoard.getStoneArr()[AiStone.getRow()][AiStone.getCol()]);
		}
		// Moving Phase
		else if(graphicBoard.getGamePhase() == Phase.move) 
		{
			Move bestMove = getBestMove(aiColor, aiDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
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
			System.out.println(String.format("Ai Removed: %s", logicStoneToRemove));
			graphicBoard.removeStone(stoneToRemove);
		}
	}
	
	// Evaluates stone place, returns the score
	public int evaluate(LogicBoard possibleBoard, LogicStone futureStone, Color futureColor) 
	{
		int score = 0;
		LogicBoard boardAfterMove = new LogicBoard(possibleBoard);
		futureStone.setColor(futureColor);
		boardAfterMove.placeStone(futureStone);
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		
		if(possibleGame.isWinner(futureStone.getColor())) 
			score += 1000;
		else if(possibleGame.isStoneInTrio(futureStone))
			score += 200;
		else 
			score += possibleGame.allPossibleMoves(futureColor).size();
		
		return score;
	}
	
	// Returns an arrayList with all the possible places to place a stone, sorted from best to worst.
	public ArrayList<LogicPlace> getPlacesSorted(LogicBoard board, Color color) 
	{
		LogicGame possibleGame = new LogicGame(board);
		ArrayList<LogicStone> possiblePlaces = possibleGame.getAllStonesOnBoard(null);
		ArrayList<LogicPlace> placesSorted = new ArrayList<LogicPlace>();
		
		for(int i = 0; i < possiblePlaces.size(); i++) 
		{
			LogicPlace curPlaceStone = new LogicPlace(possiblePlaces.get(i), evaluate(board, possiblePlaces.get(i), color));
			placesSorted.add(curPlaceStone);
		}
		
		Collections.sort(placesSorted, Collections.reverseOrder());
		return placesSorted;
	}
	
	// Evaluates move, sets the move score.
	public void evaluate(LogicBoard possibleBoard, Move move, Color color) 
	{
		int score = 0;
		LogicStone futureStone = new LogicStone(move.getNextRow(), move.getNextCol(), color);
		LogicBoard boardAfterMove = new LogicBoard(possibleBoard);
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		boardAfterMove.moveStone(move);
		
		if(possibleGame.isWinner(futureStone.getColor())) 
			score += 1000;
		else {
			if(possibleGame.isStoneInTrio(futureStone)) 			
				score += 100;
 		}
		move.setScore(score);
	}
	
	// Returns an arrayList with all the possible moves, sorted from best to worst.
	public ArrayList<Move> getMovesSorted(LogicBoard board, Color color) 
	{
		LogicGame possibleGame = new LogicGame(board);
		ArrayList<Move> possibleMoves = possibleGame.allPossibleMoves(color);
		for(Move m : possibleMoves) 
			evaluate(board, m, color);
		
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
		int minScore = 1001;
		
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
		Move bestEnemyMove = getBestMove(enemyColor, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
		LogicStone curBestStoneToRemove = new LogicStone(bestEnemyMove.getCurRow(), bestEnemyMove.getCurCol(), enemyColor);
		
		// If stone is allowed to be removed
		if(!logicGame.isStoneInTrio(curBestStoneToRemove))
			return curBestStoneToRemove;
		// If reached here cannot remove the best move, remove
		return bestStoneToRemovePlacePhase(enemyColor);
	}
	
	public LogicPlace getBestStonePlace(Color color, int depth, int alpha, int beta, int score) 
	{
		ArrayList<LogicPlace> allPlacesSorted = getPlacesSorted(logicBoard, color);
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
	
	public Move getBestMove(Color color, int depth, int alpha, int beta, int score) 
	{
		ArrayList<Move> allMovesSorted = getMovesSorted(logicBoard, color);
		
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
				
				// Reduce 20% the further the move.
				allMovesSorted.get(i).addScore((int)
						(getBestMove(opponentColor, depth-1, alpha, beta, 
								(int)(allMovesSorted.get(i).getScore() * -1.25)).getScore() * -0.8));
				
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
	
	public ArrayList<LogicPlace> getAllPossiblePlaces(Color color) 
	{
		ArrayList<LogicStone> allPlaces = logicGame.getAllStonesOnBoard(null);
		ArrayList<LogicPlace> allPlacesWithColor = new ArrayList<LogicPlace>();
		for(LogicStone l : allPlaces)
			allPlacesWithColor.add(new LogicPlace(l.getRow(), l.getCol(), color, 0));
		return allPlacesWithColor;
	}
	
	public int evaluateBoard() 
	{
		int whiteStones = logicBoard.getFirstColorStonesOnBoard() * 30;
		int whiteMills = logicGame.countMills(Game.firstColor) * 10;
		int whiteMoves = logicGame.allPossibleMoves(Game.firstColor).size() * 1;
		
		int blackStones = logicBoard.getSecColorStonesOnBoard() * 30;
		int blackMills = logicGame.countMills(Game.secColor) * 10;
		int blackMoves = logicGame.allPossibleMoves(Game.secColor).size() * 1;
		
		int whiteScore, blackScore;
		whiteScore = whiteStones + whiteMills + whiteMoves;
		blackScore = blackStones + blackMills + blackMoves;
		
		return whiteScore - blackScore;
	}
	
	public LogicPlace miniMaxPlace(int depth, Color color, int alpha, int beta) 
	{
		if(depth == 0 || logicBoard.getAmountOfTurns() > 17 || logicGame.isWinner(color))
			return new LogicPlace(-1, -1, null, evaluateBoard());
		
		LogicStone removedStone;
		Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
		ArrayList<LogicPlace> allPlaces = getAllPossiblePlaces(color);
		int maxIndex = 0;
		boolean flag = true;
		
		for(int i = 0; i < allPlaces.size() && flag; i++) 
		{
			removedStone = null;
			logicBoard.placeStone(allPlaces.get(i));
			
			if(logicGame.isStoneInTrio(allPlaces.get(i)) && logicGame.canRemoveAnyStone(opponentColor)) 
			{
				removedStone = getBestStoneToRemove(opponentColor, Phase.place);
				logicBoard.removeStone(removedStone);
				logicBoard.decreaseStonesOnBoard(opponentColor);
			}
			
			allPlaces.get(i).setScore(miniMaxPlace(depth-1, opponentColor, alpha, beta).getScore() * -1);
			
			if(allPlaces.get(i).getScore() > allPlaces.get(maxIndex).getScore())
				maxIndex = i;
			
			logicBoard.removeStone(allPlaces.get(i));
			
			if(removedStone != null) {
				logicBoard.placeStone(removedStone);
				logicBoard.increaseStonesOnBoard(opponentColor);
			}
			
			if(color == aiColor) 
				alpha = Math.max(alpha, allPlaces.get(i).getScore());
			else
				beta = Math.min(beta, allPlaces.get(i).getScore() * -1);
			if (beta <= alpha)
                flag = false;
		}
		return allPlaces.get(maxIndex);
	}
	
	public Move miniMaxMove(int depth, Color color, int alpha, int beta) 
	{
		if(depth == 0 || logicGame.isWinner(color))
			return new Move(-1, -1, -1, -1, evaluateBoard());
		
		LogicStone removedStone;
		Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
		ArrayList<Move> allMoves = logicGame.allPossibleMoves(color);
		int maxIndex = 0, maxValue = Integer.MIN_VALUE;
		boolean flag = true;
		
		for(int i = 0; i < allMoves.size() && flag; i++) 
		{
			boolean flag2 = false;
			removedStone = null;
			LogicStone futureStone = new LogicStone(allMoves.get(i).getNextRow(), allMoves.get(i).getNextCol(), color);
			logicBoard.moveStone(allMoves.get(i));
			
			if(logicGame.isStoneInTrio(futureStone) && logicGame.canRemoveAnyStone(opponentColor)) 
			{
				removedStone = getBestStoneToRemove(opponentColor, Phase.move);
				logicBoard.removeStone(removedStone);
				logicBoard.decreaseStonesOnBoard(opponentColor);
				flag2 = true;
			}
			
			allMoves.get(i).setScore(miniMaxMove(depth-1, opponentColor, alpha, beta).getScore() * -1);
			
			if(flag2  && depth > 10) 
			{
				System.out.println(depth);
				System.out.println(allMoves.get(i));
				System.out.printf("allmoves.get(i) %d\n", allMoves.get(i).getScore());
				
				
				System.out.printf("eval board %d\n", evaluateBoard());
				System.out.println("--------------------------------");
			}
			
			int curValue = evaluateBoard() * (color == aiColor ? -1 : 1);
			if(allMoves.get(i).getScore() > allMoves.get(maxIndex).getScore()) 
			{
				maxIndex = i;
				maxValue = curValue;
			}
			else if(allMoves.get(i).getScore() == allMoves.get(maxIndex).getScore() && curValue > maxValue) 
			{
				maxIndex = i;
				maxValue = curValue;
			}	
			
			logicBoard.reverseMove(allMoves.get(i));
			
			if(removedStone != null) {
				logicBoard.placeStone(removedStone);
				logicBoard.increaseStonesOnBoard(opponentColor);
			}
			
			/*
			if(color == aiColor) 
				alpha = Math.max(alpha, allMoves.get(i).getScore());
			else
				beta = Math.min(beta, allMoves.get(i).getScore() * -1);
			if (beta <= alpha)
                flag = false;
            */
            
		}
		if(allMoves.size() == 0)
				logicBoard.printLogicBoard();
		
		return allMoves.get(maxIndex);
	}
	
	
	
}