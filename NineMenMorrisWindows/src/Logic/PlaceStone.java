package Logic;

import java.awt.Color;

public class PlaceStone extends LogicStone implements Comparable{
	private int score;

	public PlaceStone(int _row, int _col, Color _color, int score) {
		super(_row, _col, _color);
		this.score = score;
	}
	
	public PlaceStone(LogicStone stone, int score) {
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
		if(o instanceof PlaceStone) 
		{
			PlaceStone otherStone = (PlaceStone)o;
			return Integer.compare(this.score, otherStone.score);
		}
		throw new IllegalArgumentException("Object is not a PlaceStone");
	}
}
