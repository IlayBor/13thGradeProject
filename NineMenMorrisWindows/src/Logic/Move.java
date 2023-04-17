package Logic;

import java.awt.Color;

public class Move {
	private int curRow, curCol;
	private int nextRow, nextCol;
	private Color color;
	
	public Move(int curRow, int curCol, int nextRow, int nextCol, Color color) {
		super();
		this.curRow = curRow;
		this.curCol = curCol;
		this.nextRow = nextRow;
		this.nextCol = nextCol;
		this.color = color;
	}

	public int getCurRow() {
		return curRow;
	}

	public int getCurCol() {
		return curCol;
	}

	public int getNextRow() {
		return nextRow;
	}

	public int getNextCol() {
		return nextCol;
	}

	public Color getColor() {
		return color;
	}
}


