package Graphics;
import javax.swing.JFrame;

public class Frame extends JFrame{
	public static int frameX = 1000; // frame size
	public static int frameY = 700;
	public Game game;
	public Home home;
	
	public Frame() 
	{
		setTitle("Nine Men's Morris");
		
		game = new Game(this);
		add(game);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(frameX, frameY);
		setVisible(true);
	}
	
	public void MoveToGame() 
	{
		getContentPane().removeAll();
		
		game = new Game(this);
		add(game);
		
		revalidate();
		repaint();
	}
	
	public void MoveToHome() 
	{
		//game.ResetGame();
		
		getContentPane().removeAll();
		
		home = new Home(this);
		add(home);
		revalidate();
		repaint();
	}
}
