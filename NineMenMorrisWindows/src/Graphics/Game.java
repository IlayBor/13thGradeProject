package Graphics;
import java.awt.Color;
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
	
	public Frame frame; // game visual variables
	public Board board;
	public Box firstColorBox;
	public Box secColorBox;
	public JButton homeButton;
	public JButton resetButton;
	
	public int playerTurn = 0; // game semi - logic variables
	public Color currentPlayerColor = firstColor;
	public boolean isPlacingPhase = true;
	public int firstColorStonesLeft = 9;
	public int secColorStonesLeft = 9;
	
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
		resetButton.setBounds(900,0,100,50);
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
	
}
