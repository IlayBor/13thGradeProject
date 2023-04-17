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
	
	public int evaluateMove(LogicStone move) 
	{
		int score = 0;
		LogicBoard boardAfterMove = new LogicBoard(this.logicGame.getLogicBoard());
		//boardAfterMove.moveStone(move.ge);
		LogicGame possibleGame = new LogicGame(boardAfterMove);
		
		
		return score;
	}
}
