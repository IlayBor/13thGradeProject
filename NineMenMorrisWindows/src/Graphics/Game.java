package Graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Game extends JPanel implements ActionListener{
	public static Color firstColor = new Color(255,255,255,255); // game static colors
	public static Color secColor = new Color(0,0,0,255); 
	
	public static Color defaultIndicator = Color.black; 
	public static Color globalGlowIndicatorColor = Color.cyan; 
	public static Color removeGlowIndicatorColor = Color.red; 
	public static Color specificGlowIndicatorColor = Color.red; 
	
	public static Color allowedColor = Color.MAGENTA; 
	public static Color trioColor = Color.green; 
	//private static Color moveIndicatorColor = new Color(0,247,247,200);
	public static Color firstColorHover = new Color(255,255,255,180);
	public static Color secColorHove = new Color(0,0,0,160);
	
	private Frame frame; // game visual variables
	private Board board;
	private Box firstColorBox;
	private Box secColorBox;
	private JButton homeButton;
	private JButton resetButton;
	
	private int playerTurn = 0; // game semi - logic variables
	private Color currentPlayerColor = firstColor;
	private boolean placingPhase = true;
	private int firstColorStonesLeft = 9;
	private int secColorStonesLeft = 9;
	
	public Game(Frame _frame) 
	{
		frame = _frame;
		setLayout(null);
		
		board = new Board(this);
		board.setBounds(Frame.frameX/2-Board.boardX/2 , 0, Board.boardX,Board.boardY );
		add(board);
		
		firstColorBox = new Box(firstColor, this);
		firstColorBox.setBounds(Frame.frameX/2+Board.boardX/2 + 40,100,Box.boxXSize, Box.boxYSize);
		add(firstColorBox);
		
		secColorBox = new Box(secColor, this);
		secColorBox.setBounds(Frame.frameX/2-Board.boardX/2 - 80,100,Box.boxXSize, Box.boxYSize);
		add(secColorBox);
		
		homeButton = new JButton("Back");
		homeButton.setBounds(0,0,100,50);
		homeButton.addActionListener(this);
		add(homeButton);
		
		resetButton = new JButton("Reset");
		resetButton.setBounds(886,0,100,50);
		resetButton.addActionListener(this);
		add(resetButton);
	}
	
	public void changeTurn() 
	{
		playerTurn = playerTurn == 0 ? 1 : 0;
		currentPlayerColor = playerTurn == 0 ? firstColor : secColor;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == resetButton)
			frame.MoveToGame();
		
		if(e.getSource() == homeButton) 
			frame.MoveToHome();
	}	
	
	public Box getFirstColorBox() {
		return firstColorBox;
	}

	public void setFirstColorBox(Box firstColorBox) {
		this.firstColorBox = firstColorBox;
	}

	public Box getSecColorBox() {
		return secColorBox;
	}

	public void setSecColorBox(Box secColorBox) {
		this.secColorBox = secColorBox;
	}

	public int getPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
	}

	public boolean getPlacingPhase() {
		return placingPhase;
	}

	public void setPlacingPhase(boolean isPlacingPhase) {
		this.placingPhase = isPlacingPhase;
	}

	public int getFirstColorStonesLeft() {
		return firstColorStonesLeft;
	}

	public void setFirstColorStonesLeft(int firstColorStonesLeft) {
		this.firstColorStonesLeft = firstColorStonesLeft;
	}

	public int getSecColorStonesLeft() {
		return secColorStonesLeft;
	}

	public void setSecColorStonesLeft(int secColorStonesLeft) {
		this.secColorStonesLeft = secColorStonesLeft;
	}

	public Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}
	
	public Color getCurrentPlayerColor() {
		return currentPlayerColor;
	}

	public void setCurrentPlayerColor(Color currentPlayerColor) {
		this.currentPlayerColor = currentPlayerColor;
	}

}
