package che.panels;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import che.bean.ConvertedBean;
import che.https.HttpConnector;

public class MainFrame extends JFrame {

	private final MainFrame mainFrame = this;
	
//	private final LogFrame logFram = new LogFrame();
	private final TableFrame tableFrame = new TableFrame();
	
	private final JTextField urlField = new JTextField("http://rapdb.dna.affrc.go.jp/tools/converter/run", 50);
	
	private List<ConvertedBean> result = new LinkedList<ConvertedBean>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextArea textArea = new JTextArea(10,80);
	private JTextArea logArea = new JTextArea(10,80);

	HttpConnector connector = null;
	
	public MainFrame() {
		super("rapdb");
		
		connector = new HttpConnector(mainFrame);
	}
	
	private void drawPanel(){
		JPanel panel = new JPanel();  
		panel.add(urlField, BorderLayout.WEST);
		JButton queryBtn = new JButton("点击查询");
		queryBtn.addActionListener(new QueryBtnHandler());
		panel.add(queryBtn, BorderLayout.EAST);
		JButton lastResultBtn = new JButton("查看结果");
		lastResultBtn.addActionListener(new LastResultBtnhandler());
		panel.add(lastResultBtn, BorderLayout.EAST);
		
		textArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(new JScrollPane(logArea), BorderLayout.SOUTH);
		
		pack();
	}
	
	public void draw() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     //设定按关闭时的操作，这里是关闭窗口，如果不设定，就什么也不会发生
        setLocationRelativeTo(null);
        setSize(650, 500);    //设定大小，按像素来
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int)(toolkit.getScreenSize().getWidth()-this.getWidth())/2;
        int y = (int)(toolkit.getScreenSize().getHeight()-this.getHeight())/2;
        setLocation(x, y);
        
        drawPanel();
        
        setVisible(true);    //显示，如果不设置就什么都看不到
	}
	

	public void connectorCallBackk(List<ConvertedBean> result) {
		this.result = result;
		if(result != null && result.size() > 0){
			tableFrame.showTable(result);
		}
	}
	
	class QueryBtnHandler implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String inputStr = textArea.getText();
			if(inputStr == null || "".equals(inputStr) || "".equals(inputStr.trim())){
				alert("请输入查询内容.");
				return ;
			}else{
				inputStr = inputStr.replaceAll("\n", " ").replaceAll("  ", " ").trim();
				addLog("---------------------");
				
				connector.setRapdbUrl(urlField.getText());
				connector.setRapdbParam(inputStr);
				connector.start();
			}
		}
	}
	class LastResultBtnhandler implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			if(result != null && result.size() > 0){
				tableFrame.showTable(result);
			}
		}
		
	}
	
	public void addLog(String logStr){
//		logFram.addLog(logStr + "\n");
		logArea.append(logStr + "\n");
	}
	
	private void alert(String message){
		JOptionPane.showMessageDialog(null, message, "注意", JOptionPane.ERROR_MESSAGE);
	}

}
