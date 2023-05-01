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
	public static int turnDelay = 800;
	//private static int MOVES_TO_CHECK = 5; 
	
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
			ArrayList<PlaceStone> lst = getMoveTrioPlace(this.logicGame.getLogicBoard(),aiColor, aiDepth);
			for(int i = 0; i < lst.size(); i++)
				System.out.println(lst.get(i) + " score: " + lst.get(i).getScore());
			PlaceStone AiStone = lst.get(0);
			System.out.println(String.format("Ai Placed: %s. With score : %d", AiStone, AiStone.getScore()));
			graphicBoard.placeStone(graphicBoard.getStoneArr()[AiStone.getRow()][AiStone.getCol()]);
		}
		// Moving Phase
		else if(graphicBoard.getGamePhase() == Phase.move) 
		{
			ArrayList<Move> lst = getMoveTrio(this.logicGame.getLogicBoard(),aiColor, aiDepth);
			for(int i = 0; i < lst.size(); i++)
				System.out.println(lst.get(i));
			Move bestMove = getBestMove(this.logicGame.getLogicBoard(),aiColor, aiDepth);
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
			score += 100;
		else 
			score += possibleGame.allPossibleMoves(futureColor).size();
		
		return score;
	}
	
	// Returns an arrayList with all the possible places to place a stone, sorted from best to worst.
	public ArrayList<PlaceStone> getPlacesSorted(LogicBoard board, Color color) 
	{
		LogicGame possibleGame = new LogicGame(board);
		ArrayList<LogicStone> possiblePlaces = possibleGame.getAllStonesOnBoard(null);
		ArrayList<PlaceStone> placesSorted = new ArrayList<PlaceStone>();
		
		for(int i = 0; i < possiblePlaces.size(); i++) 
		{
			PlaceStone curPlaceStone = new PlaceStone(possiblePlaces.get(i), evaluate(board, possiblePlaces.get(i), color));
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
		LogicGame possibleGame = new LogicGame(board);
		ArrayList<LogicStone> stones = possibleGame.getAllStonesOnBoard(enemyColor);
		LogicStone bestStone = null;
		int minScore = 1001;
		
		for(int i = 0; i < stones.size(); i++) 
		{
			LogicStone curStone = stones.get(i);
			if(!possibleGame.isStoneInTrio(curStone)) 
			{
				LogicBoard testBoard = new LogicBoard(board);
				testBoard.removeStone(curStone);
				int curScore = getBestStonePlace(testBoard, enemyColor, 1).getScore();
				
				if(curScore < minScore) 
				{
					minScore = curScore;
					bestStone = curStone;
				}
			}
		}
		return bestStone;
	}
	
	private LogicStone bestStoneToRemoveMovePhase(LogicBoard board, Color enemyColor) 
	{
		LogicGame game = new LogicGame(board);
		Move bestEnemyMove = getBestMove(game.getLogicBoard(),enemyColor, 1);
		LogicStone curBestStoneToRemove = new LogicStone(bestEnemyMove.getCurRow(), bestEnemyMove.getCurCol(), enemyColor);
		
		// If stone is allowed to be removed
		if(!game.isStoneInTrio(curBestStoneToRemove))
			return curBestStoneToRemove;
		// If reached here cannot remove the best move, remove
		return bestStoneToRemovePlacePhase(board, enemyColor);
	}
	
	public PlaceStone getBestStonePlace(LogicBoard board, Color color, int depth) 
	{
		ArrayList<PlaceStone> allPlacesSorted = getPlacesSorted(board, color);
		if(depth > 1  && board.getAmountOfTurns() < 17) 
		{
			int bestPlaceIndex = 0; 
			for(int i = 0; i < allPlacesSorted.size(); i++) 
			{
				LogicBoard testBoard = new LogicBoard(board);
				LogicGame testGame = new LogicGame(testBoard);
				testBoard.placeStone(allPlacesSorted.get(i));
				
				Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
				// If there is a trio, and can remove, then remove the best stone and update the board.
				if(testGame.isStoneInTrio(allPlacesSorted.get(i)) && testGame.canRemoveAnyStone(opponentColor)) 
				{
					testBoard.decreaseStonesOnBoard(opponentColor);
					testBoard.removeStone(getBestStoneToRemove(testBoard, opponentColor, Phase.place));
				}
				
				allPlacesSorted.get(i).addScore((int)
						(getBestStonePlace(testBoard, opponentColor, depth-1).getScore() * -1));
				
				// Get the best stone place
				if(allPlacesSorted.get(i).getScore() > allPlacesSorted.get(bestPlaceIndex).getScore())
					bestPlaceIndex = i;
			}
			return allPlacesSorted.get(bestPlaceIndex);
		}
		return allPlacesSorted.get(0);
	}
	
	public int sumArray2(ArrayList<PlaceStone> lst) {
		int sum = 0;
		for(int i = 0; i < lst.size(); i++) 
		{
			if(i%2 == 0)
				sum += lst.get(i).getScore() * Math.pow(0.8, i);
			else
				sum -= lst.get(i).getScore() * Math.pow(0.8, i);
		}
		return sum;
	}
	
	public ArrayList<PlaceStone> getMoveTrioPlace(LogicBoard board, Color color, int depth) 
	{
		ArrayList<PlaceStone> bestPlaces = getPlacesSorted(board, color);
		ArrayList<ArrayList<PlaceStone>> allLists = new ArrayList<ArrayList<PlaceStone>>();
		int maxIndex = 0;
		if(depth > 1  && board.getAmountOfTurns() < 17) 
		{
			for(int i = 0; i < bestPlaces.size(); i++) 
			{
				LogicBoard testBoard = new LogicBoard(board);
				LogicGame testGame = new LogicGame(testBoard);
				LogicStone futureStone = new LogicStone(bestPlaces.get(i));
				testBoard.placeStone(futureStone);
				
				Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
				if(testGame.isStoneInTrio(futureStone) && testGame.canRemoveAnyStone(opponentColor))
				{
					testBoard.decreaseStonesOnBoard(opponentColor);
					testBoard.removeStone(getBestStoneToRemove(testBoard, opponentColor, Phase.place));
				}
				
				allLists.add(getMoveTrioPlace(testBoard, opponentColor, depth-1));
				allLists.get(i).add(0, bestPlaces.get(i));
				
				if(sumArray2(allLists.get(i)) > sumArray2(allLists.get(maxIndex)))
					maxIndex = i;
			}
			return allLists.get(maxIndex);
		}
		
		ArrayList<PlaceStone> returnOne = new ArrayList<PlaceStone>();
		returnOne.add(bestPlaces.get(0));
		return returnOne;
	}
	
	public Move getBestMove(LogicBoard board, Color color, int depth) 
	{
		ArrayList<Move> allMovesSorted = getMovesSorted(board, color);
		
		// If there is no moves, then its a loss, return loss value.
		if(allMovesSorted.size() == 0)
			return new Move(-1,-1,-1,-1,-1000);
		
		if(depth > 1) 
		{
			int bestMoveIndex = 0; 
			for(int i = 0; i < allMovesSorted.size(); i++) 
			{
				LogicBoard testBoard = new LogicBoard(board);
				LogicGame testGame = new LogicGame(testBoard);
				LogicStone futureStone = new LogicStone(allMovesSorted.get(i).getNextRow(), allMovesSorted.get(i).getNextCol(), color);
				testBoard.moveStone(allMovesSorted.get(i));
				
				Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
				// If there is a trio, and can remove, then remove the best stone and update the board.
				if(testGame.isStoneInTrio(futureStone) && testGame.canRemoveAnyStone(opponentColor)) 
				{
					testBoard.decreaseStonesOnBoard(opponentColor);
					testBoard.removeStone(getBestStoneToRemove(testBoard, opponentColor, Phase.move));
				}
				
				// Reduce 20% the further the move.
				allMovesSorted.get(i).addScore((int)
						(getBestMove(testBoard, opponentColor, depth-1).getScore() * -0.8));
				
				// Get the best move
				if(allMovesSorted.get(i).getScore() > allMovesSorted.get(bestMoveIndex).getScore())
					bestMoveIndex = i;
			}
			return allMovesSorted.get(bestMoveIndex);
		}
		
		return allMovesSorted.get(0);
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
		ArrayList<Move> bestMoves = getMovesSorted(board, color);
		ArrayList<ArrayList<Move>> allLists = new ArrayList<ArrayList<Move>>();
		int maxIndex = 0;
		
		if(bestMoves.size() == 0) 
		{
			ArrayList<Move> returnOne = new ArrayList<Move>();
			returnOne.add(new Move(-1,-1,-1,-1,-1000));
			return returnOne;
		}
		
		if(depth > 1) 
		{
			for(int i = 0; i < bestMoves.size(); i++) 
			{
				LogicBoard testBoard = new LogicBoard(board);
				LogicGame testGame = new LogicGame(testBoard);
				LogicStone futureStone = new LogicStone(bestMoves.get(i).getNextRow(), bestMoves.get(i).getNextCol(), color);
				testBoard.moveStone(bestMoves.get(i));
				
				Color opponentColor = color == Game.firstColor ? Game.secColor : Game.firstColor;
				if(testGame.isStoneInTrio(futureStone) && testGame.canRemoveAnyStone(opponentColor))
				{
					testBoard.decreaseStonesOnBoard(opponentColor);
					testBoard.removeStone(getBestStoneToRemove(testBoard, opponentColor, Phase.move));
				}
				
				allLists.add(getMoveTrio(testBoard, opponentColor, depth-1));
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
