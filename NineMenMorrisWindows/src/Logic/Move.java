package Logic;

public class Move implements Comparable{
	private int curRow, curCol;
	private int nextRow, nextCol;
	private int score;
	
	public Move(int curRow, int curCol, int nextRow, int nextCol) {
		this.curRow = curRow;
		this.curCol = curCol;
		this.nextRow = nextRow;
		this.nextCol = nextCol;
		this.score = 0;
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
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
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
	
	@Override
	public String toString() {
		return String.format("From [%d,%d] To [%d, %d]", curRow, curCol, nextRow, nextCol);
	}
	
	@Override
	public int compareTo(Object o) {
		if(o instanceof Move) 
		{
			Move otherMove = (Move)o;
			return Integer.compare(this.score, otherMove.score);
		}
		throw new IllegalArgumentException("Object is not a Move");
	}	
}


