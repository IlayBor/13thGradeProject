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
	
	public Move() {
		resetMove();
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
	
	public void setCurrent(int row, int col) 
	{
		this.curRow = row;
		this.curCol = col;
	}
	
	public void setNext(int row, int col) 
	{
		this.nextRow = row;
		this.nextCol = col;
	}
	
	public boolean isCurExist() 
	{
		return (this.curRow != -1 && this.curRow != -1);
	}
	
	public void resetMove() 
	{
		this.curRow = -1;
		this.curCol = -1;
		this.nextRow = -1;
		this.nextCol = -1;
	}
}


