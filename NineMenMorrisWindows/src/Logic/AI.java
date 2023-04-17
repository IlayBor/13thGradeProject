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
	
	public int evaluateGame(LogicGame futureLogicGame, Color color) {
	    int playerPieces = color == Game.firstColor ? futureLogicGame.getFirstColorStonesLeft() : futureLogicGame.getSecColorStonesLeft();
	    int opponentPieces = color == Game.firstColor ? futureLogicGame.getSecColorStonesLeft() : futureLogicGame.getFirstColorStonesLeft();
	    int playerMoves = futureLogicGame.allPossibleMoves(color).size();
	    int opponentMoves = futureLogicGame.allPossibleMoves(color == Game.firstColor ? Game.secColor : Game.firstColor).size();
	    int playerMills = futureLogicGame.countTrios(color);
	    int opponentMills = futureLogicGame.countTrios(color == Game.firstColor ? Game.secColor : Game.firstColor);
	    
	    int score = 0;
	    
	    // Favor having more pieces on the board
	    score += (playerPieces - opponentPieces) * 10;
	    
	    // Favor having more legal moves
	    score += (playerMoves - opponentMoves) * 5;
	    
	    // Favor having more mills
	    score += (playerMills - opponentMills) * 20;
	    
	    return score;
	}
	
	public void getBestMove(Color color) 
	{
		LogicBoard prevBoard = new LogicBoard(logicGame.getLogicBoard());
		ArrayList<LogicStone> possibleMoves = logicGame.allPossibleMoves(color);
		
		LogicStone bestMove = possibleMoves.get(0);
		logicGame.getLogicBoard().setStoneColor(bestMove.getRow(), bestMove.getCol(), color);
		int maxScore = evaluateGame(color);
		
		for(int i = 1; i < possibleMoves.size(); i++) 
		{
			logicGame.setLogicBoard(prevBoard);
			
			LogicStone curMove = possibleMoves.get(i);
			logicGame.getLogicBoard().setStoneColor(curMove.getRow(), curMove.getCol(), color);
			int curScore = evaluateGame(color);
			
			if(curScore > maxScore)
				bestMove = curMove;
		}
		
		logicGame.setLogicBoard(prevBoard);
		
		System.out.printf("Best move is to row:%d col:%d \n", bestMove.getRow(), bestMove.getCol());
	}
}
