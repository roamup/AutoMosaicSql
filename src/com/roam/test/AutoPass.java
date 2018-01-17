package com.roam.test;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AutoPass {
	private JTextField yearT;
	private JTextField paramT;
	private JTextField sqlT;
	private JTextArea resultT;
	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AutoPass window = new AutoPass();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public AutoPass() {
		createContents();
	}

	private void createContents() {
		this.frame = new JFrame();
		this.frame.getContentPane().setLayout(null);
		this.frame.setTitle("roam惊天地泣鬼神之sql自动填充");
		this.frame.setName("frame");
		this.frame.setIconImage(getImage("1.png"));
		Dimension size = new Dimension(720, 540);
		this.frame.setSize(size);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setLocation((dimension.width - size.width) / 2,
				(dimension.height - size.height) / 2);
		this.frame.setDefaultCloseOperation(3);

		JLabel sqlL = new JLabel();
		sqlL.setText("sql语句");
		sqlL.setBounds(10, 9, 100, 18);
		this.frame.getContentPane().add(sqlL);

		this.sqlT = new JTextField();
		this.sqlT.setBounds(120, 7, 561, 22);
		this.frame.getContentPane().add(this.sqlT);

		JLabel paramL = new JLabel();
		paramL.setText("参数");
		paramL.setBounds(10, 37, 100, 18);
		this.frame.getContentPane().add(paramL);

		this.paramT = new JTextField();
		this.paramT.setBounds(120, 36, 561, 22);
		this.frame.getContentPane().add(this.paramT);

		JLabel yearL = new JLabel();
		yearL.setText("年份");
		yearL.setBounds(10, 67, 100, 18);
		this.frame.getContentPane().add(yearL);

		this.yearT = new JTextField();
		this.yearT.setBounds(120, 65, 227, 22);
		this.frame.getContentPane().add(this.yearT);
		yearT.setText("2018");

		JLabel resultL = new JLabel();
		resultL.setText("最后结果");
		resultL.setBounds(10, 95, 100, 18);
		this.frame.getContentPane().add(resultL);

		this.resultT = new JTextArea();
		this.resultT.setLineWrap(true);

		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.add(this.resultT);
		jScrollPane.setViewportView(this.resultT);
		jScrollPane.setBounds(120, 94, 227, 375);
		this.frame.getContentPane().add(jScrollPane);

		JButton generateB = new JButton();
		generateB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					AutoPass.this.generateBoMeta();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(AutoPass.this.frame,
							ex.getMessage() + ex.getStackTrace(), "出错", 0);
				}
			}
		});

		generateB.setText("生成");
		generateB.setBounds(566, 475, 60, 20);
		this.frame.getContentPane().add(generateB);

		JButton saveToFileB = new JButton();
		saveToFileB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					AutoPass.this.saveBoMeta();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(AutoPass.this.frame,
							ex.getMessage() + ex.getStackTrace(), "出错", 0);
				}
			}
		});
		saveToFileB.setText("保存");
		saveToFileB.setBounds(638, 475, 60, 20);
		this.frame.getContentPane().add(saveToFileB);
	}

	private void generateBoMeta() throws Exception {
		String sql = this.sqlT.getText() == null ? "" : this.sqlT.getText()
				.trim();
		String params = this.paramT.getText() == null ? "" : this.paramT
				.getText().trim();
		String currentYear = this.yearT.getText() == null ? "" : this.yearT
				.getText().trim();
		resultT.setText(autoWriteIn(sql, params, currentYear));

	}

	private void saveBoMeta() throws IOException {
        String str = resultT.getText();
		FileDialog fileDialog = new FileDialog(this.frame, "选择sql文件保存地址");
		fileDialog.setMode(1);
		fileDialog.setVisible(true);

		if ((fileDialog.getDirectory() != null)
				&& (fileDialog.getFile() != null)) {
			File file = new File(fileDialog.getDirectory(),
					fileDialog.getFile());
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(str.getBytes());
			fos.close();
		}
	}

	private Image getImage(String fileName) {
		try {
			InputStream is = getClass().getResourceAsStream(fileName);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			while (true) {
				int n = is.read(buf);
				if (n == -1)
					break;
				baos.write(buf, 0, n);
			}
			baos.close();
			return Toolkit.getDefaultToolkit().createImage(baos.toByteArray());
		} catch (Throwable e) {
		}
		return null;
	}

	public String autoWriteIn(String sql, String params, String currentYear) {
		String[] paramsList = params.trim().split(",");
		String[] sqlList = sql.split("\\?");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < paramsList.length; i++) {
			if (paramsList[i].contains(currentYear)) {
				/*sb.append(sqlList[i]).append("to_date('").append(paramsList[i])
						.append("','yyyy-MM-dd HH24:mi:ss:fff')");*/
				sb.append(sqlList[i]).append("to_timestamp('").append(paramsList[i])
				        .append("','yyyy-MM-dd HH24:mi:ss:ff9')");
			} else {
				sb.append(sqlList[i]).append("'").append(paramsList[i])
						.append("'");
			}
		}
		if (sqlList.length > paramsList.length) {
			sb.append(sqlList[sqlList.length - 1]);
		}
		return sb.toString();
	}

}
