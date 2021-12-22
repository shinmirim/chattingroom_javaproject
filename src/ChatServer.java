import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



class ChatServer {
	private ServerSocket ss;
	private ArrayList<ChatHandler> list;

	public ChatServer(){
		try{
			ss = new ServerSocket(9500);
			System.out.println("�����غ�Ϸ�");

			list = new ArrayList<ChatHandler>();

			while(true){
				Socket socket = ss.accept();
				ChatHandler handler = new ChatHandler(socket, list);//������ ����
				handler.start();//������ ����

				list.add(handler);
			}//while
		}catch(IOException io){
			io.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ChatServer();
	}
}