package com.xjy.test;
import java.io.CharArrayReader;
/**
 * ��ͳ����ʽsocket��̣������
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TcpServerTester {
	ServerSocket ss = null; //������׽���
	boolean started = true; 
	List<Client> clients = new ArrayList<Client>();//�洢���пͻ���

	// ������
	public static void main(String[] args) {
		new TcpServerTester().start();//�����������߳�
	}

	public void start() {
		try {
			try {
				ss = new ServerSocket(8886);
			} catch (BindException e) {
				System.out.println("�˿��Ѿ���ռ�ã����ȹر�������������");
				System.exit(0);
			}
			//����ѭ������
			while (started) {
				Client client = new Client(ss.accept());//accept�������ؿͻ��˵�Socket��Ȼ���ʼ���ͻ�����
				clients.add(client);
				new Thread(client).start();
			}
		} catch (IOException e) {
			started = false;
			e.printStackTrace();
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//һ���ͻ��˶�Ӧһ���ͻ��˴����߳�
	class Client implements Runnable {
		Socket sc = null;//���ڽ��ܿͻ����׽���
		DataInputStream dis = null;
		//InputStreamReader isr = null;
		DataOutputStream dos = null;
		boolean bConnect = false;

		Client(Socket s) {
			this.sc = s;//��ʼ������˳��еĿͻ����׽���
			try {
				dis = new DataInputStream(sc.getInputStream());//��ʼ��������
				//isr = new InputStreamReader(sc.getInputStream());
				dos = new DataOutputStream(sc.getOutputStream());//��ʼ�������
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("A client connected!");
		}
		
		//���ͷ���
		public void send(String str) {
			try {
				//dos.writeUTF(str);//��������ɷ���˵Ŀͻ��˴����̷߳��͸���Ӧ�Ŀͻ���
				dos.write(str.getBytes("ASCII"));//�����ַ�����ASCII����ɵ��ֽ���
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			bConnect = true;

			while (bConnect) {
				try {
					String str = null;
					byte[] bs = new byte[1024];
					int len ;
					if((len=dis.read(bs))>0) {
						str = new String(bs,0,len,"ASCII");
					}
					System.out.println(str);
					//�Ѷ�������Ϣ���͸����еĿͻ���
					for (int i = 0; i < clients.size(); i++) {
						clients.get(i).send(str);
					}

				} catch (Exception e) {
					System.out.println("lost a connect with Client!");
					clients.remove(this);
					bConnect = false;
				}
			}
			try {
				if (dis != null)
					dis.close();
				if (dos != null)
					dos.close();
				if (sc != null)
					sc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
