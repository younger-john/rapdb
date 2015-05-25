package che;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.swing.UIManager;

import che.panels.MainFrame;

public class Main {

	public static void main(String[] args) {
//		try {
//			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
//		} catch (Exception e) {
//			// TODO exception
//		}
		
		try {	 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
		    e1.printStackTrace();
		}
		
		MainFrame mainframe = new MainFrame();
		mainframe.draw();
		
		String ss = "ssss";
		InputStream input = new ByteArrayInputStream(ss.getBytes());
		
//		List<ConvertedBean> idconverts = new LinkedList<ConvertedBean>();
//		TableFrame table = new TableFrame();
//		table.showTable(idconverts);
	}

	
	public void drawPanel(){
		
	}
}
