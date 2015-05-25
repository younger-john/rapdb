package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest{
	
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(80);
		while(true){
			Socket socket = server.accept();
			InputStream input = socket.getInputStream();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//			String str = null;
//			while ((str = reader.readLine()) != null){
//				System.out.println(str);
//			}
			
			byte[] bytes = new byte[1024];
			int n = 0;
			n = input.read(bytes);
			System.out.println(n);
//			while((n = input.read(bytes)) > 0){
				System.out.println(new String(bytes));
//			}
			
			
			OutputStream output = socket.getOutputStream();
			output.write("Hello, World.".getBytes());
			output.flush();
			input.close();
			output.close();
			socket.close();
			System.out.println("-----------------------------");
		}
	}
	
}