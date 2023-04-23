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
import javax.swing.SwingConstants;
import javax.swing.JComboBox;

import org.w3c.dom.Text;

import Logic.AI;
import Logic.LogicGame;

public class Settings extends JPanel implements ActionListener{
	private Frame frame;
	private JButton backButton;
	private String[] options = {"Human", "Easy", "Medium", "Hard"}; 
	private JComboBox comboBox; 
	private ImageIcon image;
	private JLabel label;
	
	public Settings(Frame _frame) 
	{	
		frame = _frame;
		setLayout(null);
		image = new ImageIcon("images/BackgroundImage.jpeg");
		
		label = new JLabel("Difficulty:", SwingConstants.CENTER);
		label.setFont(new Font("Ariel", Font.PLAIN, 28));
		label.setBounds(320,330,300,50);
		label.setSize(150,50);
		label.setOpaque(true);
		add(label);
		
		comboBox = new JComboBox(options);
		comboBox.setBounds(480,330,300,50);
		comboBox.addActionListener(this);
		comboBox.setSize(150,50);
		add(comboBox);
		
		backButton = new JButton("Back");
		backButton.setBounds(400,400,100,50);
		backButton.addActionListener(this);
		backButton.setSize(150,50);
		add(backButton);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == comboBox) 
		{
			AI.aiLevel = comboBox.getSelectedIndex();
		}
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
