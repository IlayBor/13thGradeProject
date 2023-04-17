package Logic;

import java.awt.Color;
import Graphics.Stone;

public class LogicStone {
	private int row, col;
	private Color color;
	private boolean initialized = false;
	
	public LogicStone(int _row, int _col, Color _color) 
	{
		row = _row;
		col = _col;
		color = _color;
		initialized = true;
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
	
	public boolean initialized() 
	{
		return initialized;
	}
	
	public boolean isEmpty() 
	{
		return color == null ? true : false;
	}
}
