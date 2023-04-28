package Logic;

import java.awt.Color;
import Graphics.Stone;

public class LogicStone {
	protected int row, col;
	protected Color color;
	
	public LogicStone(int _row, int _col, Color _color) 
	{
		row = _row;
		col = _col;
		color = _color;
	}
	
	public LogicStone(Stone toCopy) 
	{
		row = toCopy.getRow();
		col = toCopy.getCol();
		color = toCopy.getColor();
	}
	
	public LogicStone(LogicStone toCopy) 
	{
		row = toCopy.getRow();
		col = toCopy.getCol();
		color = toCopy.getColor();
	}
	
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public String toString() {
		return String.format("[%d,%d]", row, col);
	}
}
