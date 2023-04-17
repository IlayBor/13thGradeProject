package Logic;

import java.awt.Color;
import java.util.Iterator;

import Graphics.Game;

public class LogicBoard {
	
	public static char initChar = '0'; // not playable places
	public static char playableChar = 'X';  // playable but empty
	public static char firstPlayerChar = '1'; // first player
	public static char secPlayerChar = '2'; // second player
	public static char midChar = 'M'; // the middle of the board
	
	public char LBoard[][] = new char[7][7];
	
	public LogicBoard()
	{
		initBoard(initChar);
		LBoard[3][3] = midChar;
	}
	
	public void setValue(int _row, int _col, Color _color) 
	{
		if(_color == null)
			LBoard[_row][_col] = playableChar;
		else
			LBoard[_row][_col] = _color == Game.firstColor ? firstPlayerChar : secPlayerChar;
	}
	
	public void printLBoard() 
	{
		System.out.println("-----------------------------------");
		for(int row = 0; row < 7; row++) 
		{
			for(int col = 0; col < 7; col++)
				System.out.printf("%2c" ,LBoard[row][col]);
			System.out.println();
		}
	}
	
	public void initBoard(char value) 
	{
		for(int row = 0; row < LBoard.length; row++) 
			for(int col = 0; col < LBoard[0].length; col++) 
				LBoard[row][col] = value;
	}
	
	
	
}
