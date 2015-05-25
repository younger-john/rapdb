package che.panels;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@Deprecated
public class LogFrame extends JFrame {

	public LogFrame() {
		super("log");
	}
	
	public void showFrame(){
		this.dispose();
        setLocationRelativeTo(null);
        setSize(600, 500);    //设定大小，按像素来
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int)(toolkit.getScreenSize().getWidth()-this.getWidth())/2;
        int y = (int)(toolkit.getScreenSize().getHeight()-this.getHeight())/2;
        setLocation(x, y);
        
        drawPanel();
        
        setVisible(true);    //显示，如果不设置就什么都看不到
        repaint();
	}
	
	private JTextArea textArea = new JTextArea(20,80);
	
	private void drawPanel(){
		JPanel panel = new JPanel();  
		
		textArea = new JTextArea(20,80);
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		pack();
	}
	
	public void addLog(String logStr){
		this.repaint();
		textArea.append(logStr);
	}
	
}
