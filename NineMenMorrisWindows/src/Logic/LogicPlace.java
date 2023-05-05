package Logic;

import java.awt.Color;

public class LogicPlace extends LogicStone implements Comparable{
	private int score;

	public LogicPlace(int _row, int _col, Color _color, int score) {
		super(_row, _col, _color);
		this.score = score;
	}
	
	public LogicPlace(LogicStone stone, int score) {
		super(stone.row, stone.col, stone.color);
		this.score = score;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void addScore(int score) {
		this.score += score;
	}
	
	public int compareTo(Object o) {
		if(o instanceof LogicPlace) 
		{
			LogicPlace otherStone = (LogicPlace)o;
			return Integer.compare(this.score, otherStone.score);
		}
		throw new IllegalArgumentException("Object is not a PlaceStone");
	}
}
