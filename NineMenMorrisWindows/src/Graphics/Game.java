package Graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import Logic.AI;
import Logic.LogicStone;

public class Game extends JPanel implements ActionListener{
	public static Color firstColor = new Color(255,255,255,255); // game static colors
	public static Color secColor = new Color(0,0,0,255); 
	
	public static Color defaultIndicator = Color.black; 
	public static Color globalGlowIndicatorColor = Color.cyan; 
	public static Color removeGlowIndicatorColor = Color.red; 
	public static Color specificGlowIndicatorColor = Color.red; 
	
	public static Color allowedColor = Color.MAGENTA; 
	public static Color trioColor = Color.green; 
	
	private Frame frame; // game visual variables
	private Board board;
	private Box firstColorBox;
	private Box secColorBox;
	private JButton homeButton;
	private JButton resetButton;
	private ImageIcon image;
	
	private AI Ai; // game logic variables 
	private int playerTurn = 0; 
	private Color currentPlayerColor = firstColor;
	private boolean placingPhase = true;
	private int firstColorStonesLeft = 9;
	private int secColorStonesLeft = 9;
	
	public Game(Frame _frame) 
	{
		frame = _frame;
		setLayout(null);
		
		image = new ImageIcon("images/WoodenBackground.jpg");
		
		board = new Board(this);
		board.setBounds(Frame.frameX/2-Board.boardX/2 , 0, Board.boardX,Board.boardY );
		add(board);
		Ai = new AI(board.getLogicGame());
		
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
		
		if(Ai.aiLevel > 0 && currentPlayerColor == secColor) 
		{
			LogicStone AiStone = Ai.getBestStonePlace(getCurrentPlayerColor());
			board.getStoneArr()[AiStone.getRow()][AiStone.getCol()].drawStone(secColor);
			board.stonePlaced(board.getStoneArr()[AiStone.getRow()][AiStone.getCol()]);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == resetButton)
			frame.MoveToGame();
		
		if(e.getSource() == homeButton) 
			frame.MoveToHome();
	}	
	
	protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g); 
    	g.drawImage(image.getImage(), 0, 0, Frame.frameX, Frame.frameY, null); // draw background image
    }

	public Box getFirstColorBox() {
		return firstColorBox;
	}

	public Box getSecColorBox() {
		return secColorBox;
	}

	public int getPlayerTurn() {
		return playerTurn;
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
	
	public Color getCurrentPlayerColor() {
		return currentPlayerColor;
	}
}
