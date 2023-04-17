package Logic;

public class Move {
	private int curRow, curCol;
	private int nextRow, nextCol;
	
	public Move(int curRow, int curCol, int nextRow, int nextCol) {
		this.curRow = curRow;
		this.curCol = curCol;
		this.nextRow = nextRow;
		this.nextCol = nextCol;
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
}


