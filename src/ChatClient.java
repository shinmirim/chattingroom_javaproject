import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

class ChatClient extends JFrame implements ActionListener, Runnable {
	private JTextField input;
	private JTextArea output;
	private JButton send;
	private JButton disconnect;
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;

	public ChatClient() {
		input = new JTextField(15);

		output = new JTextArea();
		JScrollPane scroll = new JScrollPane(output);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		output.setEditable(false);
		
		disconnect = new JButton("��������");
		send = new JButton("������");

		JPanel p = new JPanel(new BorderLayout());
		p.add("Center", input);
		p.add("East", send);
		p.add("West", disconnect);

		Container c = this.getContentPane();
		c.add("Center", scroll);
		c.add("South", p);

		setBounds(100, 80, 300, 300);
		setVisible(true);
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				pw.println("exit");
				pw.flush();
			}
		});
	}

	public void service() {
		// �г���
		String nickName = JOptionPane.showInputDialog(this, "�г����� �Է��ϼ���", "�г���", JOptionPane.INFORMATION_MESSAGE);
		if (nickName == null || nickName.length() == 0) {
			nickName = "guest";
		}
		
		// ����IP
		// String serverIP = JOptionPane.showInputDialog(this,"����IP��
		// �Է��ϼ���","����IP",JOptionPane.INFORMATION_MESSAGE);
		String serverIP = JOptionPane.showInputDialog(this, "����IP�� �Է��ϼ���", "192.168.0.10");
		if (serverIP == null || serverIP.length() == 0) {
			System.out.println("����IP�� �Էµ��� �ʾҽ��ϴ�");
			System.exit(0);
		}

		

		// ���� ����
		try {
			int port = Integer.parseInt(JOptionPane.showInputDialog(this, "���� ��Ʈ�� �Է��ϼ���", "9500"));
			if (port <= 0) {
				System.out.println("�߸��� ��Ʈ�� �ԷµǾ����ϴ�");
				System.exit(0);
			}
			socket = new Socket(serverIP, port);

			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			

			// ���� ������
			pw.println(nickName);
			pw.flush();

		} catch (UnknownHostException e) {
			System.out.println("������ ã�� �� �����ϴ�");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("������ ������ �ȵǾ����ϴ�");
			e.printStackTrace();
			System.exit(0);
		}

		// �̺�Ʈ
		send.addActionListener(this);
		input.addActionListener(this);
		disconnect.addActionListener(this);

		// ������ ����
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if(e.getActionCommand().equals("��������")) {
			int exitConfirm = JOptionPane.showConfirmDialog(null,"ä���� �����Ͻðڽ��ϱ�?","Comfirm",JOptionPane.YES_NO_OPTION);
			if(exitConfirm == JOptionPane.CLOSED_OPTION) {
				JOptionPane.showMessageDialog(null,"����ϼ̽��ϴ�.","Message",JOptionPane.ERROR_MESSAGE);
			}
			else if(exitConfirm == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(null,"ä���� �����մϴ�.","Message",JOptionPane.CLOSED_OPTION);
				pw.println("exit");
				pw.flush();
			}
			else {
				JOptionPane.showMessageDialog(null,"����ϼ̽��ϴ�.","Message",JOptionPane.CANCEL_OPTION);
			}
			
		}
		else {
			// ������ ������
			String line = input.getText();
			pw.println(line);
			pw.flush();
			input.setText("");
		}
		
	}

	@Override
	public void run() {
		// �޴���
		String line = null;

		while (true) {
			try {
				line = br.readLine();
				if (line == null || line.toLowerCase().equals("exit")) {
					br.close();
					pw.close();
					socket.close();

					System.exit(0);
				}
			} catch (IOException io) {
				io.printStackTrace();
			}

			output.append(line + "\n");

			int pos = output.getText().length();
			output.setCaretPosition(pos);
		} // while
	}

	public static void main(String[] args) {
		new ChatClient().service();
	}
}