package Graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import Graphics.Board.Phase;
import Graphics.Board.Status;
import Logic.AI;
import Logic.LogicStone;
import Logic.Move;

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
		board.unmarkMill();
		
		playerTurn = playerTurn == 0 ? 1 : 0;
		currentPlayerColor = playerTurn == 0 ? firstColor : secColor;
		
		if(Ai.aiLevel > 0 && currentPlayerColor == secColor) 
		{
			AiTurn(secColor);
		}
	}
	
	public void AiTurn(Color AiColor) 
	{	
		// AI Timer
		if(board.getGamePhase() == Phase.place) 
		{
			LogicStone AiStone = Ai.getBestStonePlace(AiColor);
			board.placeStone(board.getStoneArr()[AiStone.getRow()][AiStone.getCol()]);
		}
		else if(board.getGamePhase() == Phase.move) 
		{
			Move bestMove = Ai.getBestMove(AiColor);
			Stone curStone = board.getStoneArr()[bestMove.getCurRow()][bestMove.getCurCol()];
			Stone nextStone = board.getStoneArr()[bestMove.getNextRow()][bestMove.getNextCol()];
			board.moveStone(curStone, Status.copy);
			board.moveStone(nextStone, Status.paste);
		}
		else 
		{
			Color enemyColor = AiColor == Game.firstColor ? Game.secColor : Game.firstColor;
			LogicStone logicStoneToRemove = Ai.getBestStoneToRemove(enemyColor);
			Stone stoneToRemove = board.getStoneArr()[logicStoneToRemove.getRow()][logicStoneToRemove.getCol()];
			board.removeStone(stoneToRemove);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == resetButton)
			frame.MoveToGame();
		
		if(e.getSource() == homeButton) 
			frame.MoveToHome();
	}	
	
	public void moveToHome() {
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
	
	public Color getCurrentPlayerColor() {
		return currentPlayerColor;
	}
}
