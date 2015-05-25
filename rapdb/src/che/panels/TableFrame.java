package che.panels;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import che.bean.ConvertedBean;

public class TableFrame extends JFrame {
	
	private final TableFrame frame;
	private JTable table;

	public TableFrame() {
		super("查询结果");
		this.frame = this;
		
	}
	
	public void showTable(List<ConvertedBean> idconverts) {
		getContentPane().removeAll();
		setLocationRelativeTo(null);
        setSize(600, 500);    //设定大小，按像素来
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int)(toolkit.getScreenSize().getWidth()-this.getWidth())/2;
        int y = (int)(toolkit.getScreenSize().getHeight()-this.getHeight())/2;
        setLocation(x, y);
		
		Object columnNames[] = { "MSU (LOC_Os ID)", "RAP (Os ID)", "RAP (Os ID)", "Sequence" };
		Object[][] rowData = initializeTableDate(idconverts);
		
		table = new JTable(rowData, columnNames);

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();  
//		panel.add(urlField, BorderLayout.WEST);
		JButton queryBtn = new JButton("保存文件");
		queryBtn.addActionListener(new SaveBtnHandler());
		panel.add(queryBtn, BorderLayout.WEST);
		
		getContentPane().add(panel, BorderLayout.NORTH);
		pack();
		repaint();
		invalidate();
		setVisible(true);
	}
	
	private Object[][] initializeTableDate(List<ConvertedBean> idconverts){
		
		Object[][] result = new Object[idconverts.size()][];
		for(int i = 0; i < idconverts.size(); i ++){
			ConvertedBean rowBean = idconverts.get(i);
			Object[] row = new Object[4];
			row[0] = rowBean.getMsu();
			row[1] = rowBean.getRap();
			row[2] = rowBean.getRapVariants();
			row[3] = rowBean.getRapSequence();
			result[i] = row;
		}
		return result;
	}

	class SaveBtnHandler implements ActionListener{
		

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();  
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);  
            chooser.setMultiSelectionEnabled(false);  
            chooser.setAcceptAllFileFilterUsed(false);  
            chooser.setDialogTitle("保存数据文件");  
            
            //取得文件名输入框冰设置指定格式  
//            JTextField fileNameField = getTextField(chooser);  
//            fileNameField.setText(name);  
              
            //添加文件过滤器  
            chooser.addChoosableFileFilter(new FileFilter(){
                public boolean accept(File f) {  
                         return true;   
                }  
                public String getDescription() {  
                    return "所有文件(*.*)";  
                }  
            });  
//            chooser.addChoosableFileFilter(new FileFilter(){  
//                public boolean accept(File f) {  
//                    if (f.getName().endsWith("xls") || f.isDirectory()) {  
//                         return true;   
//                    }else{  
//                        return false;   
//                    }  
//                }  
//                public String getDescription() {  
//                    return "Excel文件(*.xls)";  
//                }  
//            });  
              
            //打开对话框  
            int result = chooser.showSaveDialog(frame);//null  
            //文件确定  
            if(result==JFileChooser.APPROVE_OPTION) {
                String outPath = chooser.getSelectedFile().getAbsolutePath(); 
                if(outPath.indexOf(".") == -1){
                	outPath += ".csv";
        		}
                if(new File(outPath).exists()){  
                	int selected = JOptionPane.showConfirmDialog(chooser, "文件已经存在,是否要覆盖该文件?", "警告", JOptionPane.YES_NO_OPTION);
                	if(selected == 1){
                		return ;
                	}
                }
                
                exportFile(outPath);
            }
		}
	}
	
	public void exportFile(String filePath){
		
		FileWriter out = null;
		try{
			TableModel model = table.getModel();
	        out = new FileWriter(filePath);
	        
	        for(int i=0; i < model.getColumnCount(); i++) {
	            out.write("\"" + model.getColumnName(i) + "\",");
	        }
	        out.write("\n");
	        for(int i=0; i< model.getRowCount(); i++) {
	            for(int j=0; j < model.getColumnCount(); j++) {
	                out.write("\"" + model.getValueAt(i,j)+"\",");
	            }
	            out.write("\n");
	        }
	        JOptionPane.showMessageDialog(frame, "导出文件成功.");
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "导出失败:" + e.getLocalizedMessage());
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
