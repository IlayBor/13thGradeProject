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
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.w3c.dom.Text;

import Logic.LogicGame;

public class Rules extends JPanel implements ActionListener{
	private Frame frame;
	private JButton backButton;
	private ImageIcon image;
	private JTextArea area;
	
	public Rules(Frame _frame) 
	{	
		frame = _frame;
		setLayout(null);
		image = new ImageIcon("images/Rules.png");
		
		backButton = new JButton("Back");
		backButton.setBounds(400,550,100,50);
		backButton.addActionListener(this);
		backButton.setSize(150,50);
		add(backButton);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == backButton)
		{
			frame.MoveToHome();
		}
	}
	
	@Override
    protected void paintComponent(Graphics g) 
    {
    	super.paintComponent(g); 
    	g.drawImage(image.getImage(), 0, 0, Frame.frameX, Frame.frameY, null); // draw background image
    }
}
