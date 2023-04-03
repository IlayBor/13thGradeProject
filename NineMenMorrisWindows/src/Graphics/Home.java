package Graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Text;

public class Home extends JPanel implements ActionListener{
	private Frame frame;
	private JButton localButton, AiButton;
	private ImageIcon image;
	
	public Home(Frame _frame) 
	{	
		frame = _frame;
		setLayout(null);
		
		image = new ImageIcon("images/BackgroundImage.jpeg");
		
		localButton = new JButton("Play Local");
		localButton.setBounds(400,300,200,50);
		localButton.addActionListener(this);
		add(localButton);
		
		AiButton = new JButton("Play Ai");
		AiButton.setBounds(400,400,200,50);
		AiButton.addActionListener(this);
		add(AiButton);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == localButton)
		{
			frame.MoveToGame();
		}
	}
	
	@Override
    protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g); 
    	g.drawImage(image.getImage(), 0, 0, Frame.frameX, Frame.frameY, null); // draw background image
    }
}
