import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;

public class SocketClient{
	InetAddress host;
	Socket socket = null;
	//DataInputStream  input   = null;  //original
	//DataOutputStream output = null;   //original
	DataInputStream  input   = null;  //testing
	DataOutputStream output = null;   //testing
	PrintStream out     = null;
	String IP_Addr;
	int Port;
	boolean firstflag = false;
	public SocketClient(String IP_Addr, int Port){
		this.IP_Addr = IP_Addr;
		this.Port = Port;
	}

	public boolean connectToDevice() {
		int timeout = 6000;
		try
		{
			if(socket != null) {
				if(socket.isConnected()) {
					return true;
				}
			}
			InetSocketAddress ISA =  new InetSocketAddress(IP_Addr, Port);
			socket = new Socket();
			socket.connect(ISA, timeout);
			System.out.println("Connected");
			// takes input from terminal

			//input = new DataInputStream(socket.getInputStream());  //original
			input = new DataInputStream(socket.getInputStream());  //testing
			// sends output to the socket
			output = new DataOutputStream(socket.getOutputStream()); //testing
			//out   = new PrintStream(socket.getOutputStream());       //original
			return true;
		}
		catch(UnknownHostException u)
		{
			System.out.println(u);
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
		return false;

	}

	public int sendPacket(String packetData){
		try {
			System.out.println("Sending packetData...");
			System.out.println(packetData);
			output.writeUTF(packetData);   //testing
			System.out.println("Packet sent.");
			//out.flush();   //original
			output.flush();  //testing
			return 0;

		}catch(Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Sending Error: " + e);
			e.printStackTrace();
			while(socket.isClosed()) {
				System.out.println("socket is not connected... reconnecting..");
				connectToDevice();
			}
			System.out.println("resending packet.");
			// sendPacket(packetData);
		}
		return 0;
	}

	public String receivePacket(){
		String instruction = null;
		try {
			do {
				long timestart = System.currentTimeMillis();
				while(input.available() == 0) {
					Thread.sleep(10);
				}
				//instruction = input.readLine(); //original
				byte[] ary = new byte[1024];
				input.read(ary);
				instruction = new String(ary, "UTF-8").replaceAll("\u0000.*", "");
			}while(instruction == null ||instruction.equalsIgnoreCase(""));			
		}


		catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Receiving Error: " + e);
			e.printStackTrace();
			connectToDevice();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return instruction;
	}

	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}