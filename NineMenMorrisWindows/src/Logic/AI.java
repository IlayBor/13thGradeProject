package Logic;

import java.awt.Color;
import java.util.ArrayList;

import Graphics.Game;

public class AI {
	private LogicGame logicGameCopy;

	public AI(LogicGame logicGame) {
		this.logicGameCopy = logicGame;
	}
	
	public int evaluate(Color color) {
	    int playerPieces = color == Game.firstColor ? logicGameCopy.getFirstColorStonesLeft() : logicGameCopy.getSecColorStonesLeft();
	    int opponentPieces = color == Game.firstColor ? logicGameCopy.getSecColorStonesLeft() : logicGameCopy.getFirstColorStonesLeft();
	    int playerMoves = logicGameCopy.allPossibleMoves(color).size();
	    int opponentMoves = logicGameCopy.allPossibleMoves(color == Game.firstColor ? Game.secColor : Game.firstColor).size();
	    int playerMills = logicGameCopy.countTrios(color);
	    int opponentMills = logicGameCopy.countTrios(color == Game.firstColor ? Game.secColor : Game.firstColor);
	    
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
		ArrayList<LogicStone> possibleMoves = logicGameCopy.allPossibleMoves(color);
		LogicStone maxStone = possibleMoves.get(0);
		
		int maxScore;
		
		
	}
}
