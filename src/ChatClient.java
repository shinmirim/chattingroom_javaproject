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
		
		disconnect = new JButton("연결해제");
		send = new JButton("보내기");

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
		// 닉네임
		String nickName = JOptionPane.showInputDialog(this, "닉네임을 입력하세요", "닉네임", JOptionPane.INFORMATION_MESSAGE);
		if (nickName == null || nickName.length() == 0) {
			nickName = "guest";
		}
		
		// 서버IP
		// String serverIP = JOptionPane.showInputDialog(this,"서버IP를
		// 입력하세요","서버IP",JOptionPane.INFORMATION_MESSAGE);
		String serverIP = JOptionPane.showInputDialog(this, "서버IP를 입력하세요", "192.168.0.10");
		if (serverIP == null || serverIP.length() == 0) {
			System.out.println("서버IP가 입력되지 않았습니다");
			System.exit(0);
		}

		

		// 소켓 생성
		try {
			int port = Integer.parseInt(JOptionPane.showInputDialog(this, "서버 포트를 입력하세요", "9500"));
			if (port <= 0) {
				System.out.println("잘못된 포트가 입력되었습니다");
				System.exit(0);
			}
			socket = new Socket(serverIP, port);

			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			

			// 서버 보내기
			pw.println(nickName);
			pw.flush();

		} catch (UnknownHostException e) {
			System.out.println("서버를 찾을 수 없습니다");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("서버와 연결이 안되었습니다");
			e.printStackTrace();
			System.exit(0);
		}

		// 이벤트
		send.addActionListener(this);
		input.addActionListener(this);
		disconnect.addActionListener(this);

		// 스레드 생성
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if(e.getActionCommand().equals("연결해제")) {
			int exitConfirm = JOptionPane.showConfirmDialog(null,"채팅을 종료하시겠습니까?","Comfirm",JOptionPane.YES_NO_OPTION);
			if(exitConfirm == JOptionPane.CLOSED_OPTION) {
				JOptionPane.showMessageDialog(null,"취소하셨습니다.","Message",JOptionPane.ERROR_MESSAGE);
			}
			else if(exitConfirm == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(null,"채팅을 종료합니다.","Message",JOptionPane.CLOSED_OPTION);
				pw.println("exit");
				pw.flush();
			}
			else {
				JOptionPane.showMessageDialog(null,"취소하셨습니다.","Message",JOptionPane.CANCEL_OPTION);
			}
			
		}
		else {
			// 서버로 보내기
			String line = input.getText();
			pw.println(line);
			pw.flush();
			input.setText("");
		}
		
	}

	@Override
	public void run() {
		// 받는쪽
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