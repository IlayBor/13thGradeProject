package Graphics;
import javax.swing.JFrame;

public class Frame extends JFrame{
	public static int frameX = 1000; // frame size
	public static int frameY = 700;
	private Game game;
	private Home home;
	private Settings settings;
	
	public Frame() 
	{
		setTitle("Nine Men's Morris");
		
		home = new Home(this);
		add(home);
		
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
		getContentPane().removeAll();
		
		home = new Home(this);
		add(home);
		revalidate();
		repaint();
	}
	
	public void MoveToSettings() 
	{
		getContentPane().removeAll();
		
		settings = new Settings(this);
		add(settings);
		revalidate();
		repaint();
	}
	
	public Game getGame() 
	{
		return game;
	}
}
