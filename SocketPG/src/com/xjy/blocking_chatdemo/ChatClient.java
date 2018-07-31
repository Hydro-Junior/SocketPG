package com.xjy.blocking_chatdemo;
/**
 * ��ͳ����ʽSocket�Ŀͻ��ˣ�ÿ���ͻ�����һ��textfield���Է�����Ϣ,һ��textArea������ʾ���пͻ�������������͵���Ϣ��
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame {
	private String name = new String();//ģ���û�����
	private static final long serialVersionUID = 1L;
	private boolean connectState = true;//һ��ʼδ������
	private boolean bConnected = true;//��ʾ��������״̬
	private DataOutputStream dos = null;//�����д����Ϣ
	private DataInputStream dis = null;//��������ȡ��Ϣ
	private JTextField tf = new JTextField();//���ִ���
	private JTextArea ta = new JTextArea();//��ʾ����
	private Socket sc;//�ͻ����׽���

	public static void main(String[] args) {
		ChatClient cc = new ChatClient();
		cc.init();
	}
     //��ȡ��Ϣ��Run����
	class InfoGetter implements Runnable{
		@Override
		public void run() {
			while(bConnected){
				try {
					String info = dis.readUTF();
					System.out.println(info);
					ta.append(info+"\n");
				} catch (Exception e) {
					System.out.println("disconnected!");
					bConnected = false;
					disconnect();
				}
			}
			
		}
		
	}
	//��ʼ������
	public void init() {
		Random rd = new Random();
		this.name = "����" + String.format("%010d",Math.abs(rd.nextInt()));
		this.setTitle("Chating Room" + "      (���Ĵ��ţ�" + name + ")");
		this.setBounds(400, 300, 600, 400);
		this.add(ta, BorderLayout.CENTER);
		this.add(tf, BorderLayout.SOUTH);
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}

		});
		setVisible(true);
		registerListener();
		connect();
		//������ȡ��Ϣ���߳�
		new Thread(new InfoGetter()).start();
	}

	public void connect() {
		while (connectState) {
			try {
				sc = new Socket("127.0.0.1", 8886);
				dos = new DataOutputStream(sc.getOutputStream());//��ʼ�������
				dis = new DataInputStream(sc.getInputStream());//��ʼ��������
				System.out.println("success to connect the server��");
				connectState = false;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (ConnectException e) {//���������쳣������������
				System.out.println("trying to connect...");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//�Ͽ����Ӵ���
	public void disconnect() {
		try {
			if (dos != null)
				dos.close();
			if (sc != null)
				sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//Ϊtextfieldע��س���ʾ�¼������field�����������
	public void registerListener() {

		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ss = tf.getText().trim();
				tf.setText("");
				try {
					dos.writeUTF(name+": "+ss);
					dos.flush();
					// dos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
