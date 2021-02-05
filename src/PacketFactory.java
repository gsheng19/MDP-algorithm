import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.swing.text.Position;


public class PacketFactory implements Runnable{
	
	static SocketClient sc = null;

	int waypoint_x = 0, waypoint_y = 0;
	//multi-threading this
	//have a reference to a queue of strings or commands or something.
	//will add commands that maps to do something on the robot to this.
	//will have also a send queue where we can send data out.
	Queue<Packet> buffer;
	//this might cause a problem with the buffer where we have old or repeated(handled by tcp) packets receiving. fuck.
	//String ip = "192.168.19.19";
	String ip = "localhost";

	String PreviousPacket = null;

	public void resetPreviousPacket() {
		PreviousPacket = null;
	}
	public void setPreviousPacket(String Prev) {
		PreviousPacket = Prev;
	}

	int port = 12345;
	final static int FORWARDi = 1;
	final static int TURNRIGHTi = 2;
	final static int TURNLEFTi = 3;
	final static int REVERSEi = 4;	
	public static final int CALIBRATEi = 5;
	boolean explorationflag = false;
	final static byte UPDATESENSOR = 0x1;
	public PacketFactory() {
		
	}
	
	public PacketFactory(Queue<Packet> buffer2) {
		sc = new SocketClient(ip, port);
		System.out.println("connecting to device...");
		sc.connectToDevice();
		this.buffer = buffer2;
	}

	public void reconnectToDevice() {
		sc.closeConnection();
		sc.connectToDevice();
	}

	@Override
	public void run() {
		//this cause me the problem of not having the 
		//fucking packetFactory to send. thus i need a sending buffer too...
		while(true) {
			listen();
		}
		
	}

	public void listen() {
		boolean flag = true;
		String data = null;
		while(data==null) {
			data = sc.receivePacket(explorationflag, PreviousPacket);
		}
		
			System.out.println("receiving Data : " + data);
			String removeDataString = data.replace("P:", "");
			if(explorationflag == false)
				processPacket(removeDataString);
			else
				recvSensorOrStop(removeDataString);
				
		

	}


	public void processPacket(String packetString) {
		String[] splitPacket = packetString.split(Packet.Splitter);
		if (splitPacket[0].equalsIgnoreCase(Packet.Cmd))
		{
			if(splitPacket[1].equalsIgnoreCase(Packet.StartExplorationType)) {
				//start exploration
				//when exploration end and reach the start position
				//send ok:exploration to android
				System.out.println("starting exploration...");
				buffer.add(new Packet(Packet.StartExploration));
				System.out.print("*******************************************Exploration ended*********************************************\n");
				sc.sendPacket(Packet.StartExplorationTypeOk + "$");
				sc.sendPacket(Packet.StartExplorationTypeOkARD + "$");

				explorationflag = true;
				


			}else if(splitPacket[1].equalsIgnoreCase(Packet.StartFastestPathType)) {
				//start fastest path. just send the whole stack of instruction at once
				//need to get the x,y value of the waypoint
				//send to android that it is ready to move.
				System.out.println("*****************************************recieved packet for fastest path**************************************\n");

				buffer.add(new Packet(Packet.StartFastestPath));

			}
			else if(splitPacket[1].equalsIgnoreCase(Packet.Stop)) {
				//stop moving robot FUCK i need multi thread this.
				//i need a stopping flag god damn it
				//interrupt exploration
				buffer.add(new Packet(Packet.StopInstruction));
				sc.sendPacket(Packet.StopOk);
			}
			else if(splitPacket[1].equalsIgnoreCase(Packet.Reset)) {
				//carry robot back to starting point.
				//reset map
				buffer.add(new Packet(Packet.ResetInstruction));
				sc.sendPacket(Packet.ResetOK);
				System.out.println("sending ok reset");
			}
			else if(splitPacket[1].equals(Packet.GETMAP)) {
				buffer.add(new Packet(Packet.GETMAPi));
			}

		}else if(splitPacket[0].equals(Packet.Set)) {
			if(splitPacket[1].equalsIgnoreCase(Packet.SetRobotPos)) {
				//remove bracket, split by comma , set first as x second as y
				//set robot direction and x and y
//				this is a problem now. does not handle the robot Position
//				String directionTemp = splitPacket[3];//set direction for robot
//				
//				String[] waypointcoord  = splitPacket[2].replace("[", "").split(",");
//				int x = Integer.parseInt(waypointcoord[0]);
//				int y = Integer.parseInt(waypointcoord[1]);
//				System.out.println("setting robot position at :" + x + ", " + y + "with robot facing " + directionTemp);
//				buffer.add(new Packet(Packet.setRobotPosition, x, y, direction));
				sc.sendPacket(Packet.SetRobotPosOk);
			}
			else if(splitPacket[1].equalsIgnoreCase(Packet.SetWayPoint)) {
				//remove bracket, split by comma , set first as x second as y
				String[] waypointcoord  = splitPacket[2].replace("[", "").replace("]", "").split(",");
				int x = Integer.parseInt(waypointcoord[0]);
				int y = Integer.parseInt(waypointcoord[1]);
				//set robot position waypoint for the fastest path. after setting this, we shall send all instruction to raspberry and not do anything.
				//allow faster execution when android sends the command to start fastest path.
				//must edit set robot position.
				buffer.add(new Packet(Packet.SetWayPointi, x, y));
			}
		}
		else {
			System.out.println("String received is invalid...");
		}

	}

	public void recvSensorOrStop(String packetString) {
		System.out.println("*************************************recvSensorOrStop called*********************************************\n");
		String[] commandSplit = packetString.split(Packet.Splitter);
 		if(commandSplit[0].equalsIgnoreCase(Packet.Map)) {
 			if(commandSplit[1].equalsIgnoreCase("sensor")) {
 				int[] data = new int[6];
 				String[] sensorData = commandSplit[2].replace(" ","").replace("[", "").replace("]", "").split(",");
 				for(int i = 0; i < sensorData.length; i++) {
 					data[i] = Integer.parseInt(sensorData[i]);
 				}
 				buffer.add(new Packet(Packet.setObstacle, data));

 			}			 				
 			}
		else if(commandSplit[1].equalsIgnoreCase(Packet.Stop)) {
			//stop moving robot FUCK i need multi thread this.
			//i need a stopping flag god damn it
			//interrupt exploration
			sc.sendPacket(Packet.StopOk);
			explorationflag = false;
			buffer.add(new Packet(Packet.StopInstruction));
		}
		else if(commandSplit[1].equalsIgnoreCase(Packet.Reset)) {
			//needs to multithread this too
			//stop whatever is going on
			//carry robot back to starting point.
			//reset map
			sc.sendPacket(Packet.ResetOK);
			System.out.println("sending ok reset");
			explorationflag = false;
			buffer.add(new Packet(Packet.ResetInstruction));

		}else {
			System.out.println("Data received and ignored.");
		}
	 
	}

	public int recvSensor(String commandRecv) {

		return 0;


	}
	
	
	public boolean getFlag() {
		return explorationflag;
	}

	public void setFlag(boolean flag) {
		this.explorationflag = flag;
	}

	public Packet getLatestPacket() {
		if(buffer.isEmpty())
		return null;
		return buffer.remove();
	}
	public void sideTurnCalibrate() {
		//we need a return packet after calibration?
		sc.sendPacket(Packet.SIDETURNCALIBRATE);
		setPreviousPacket(Packet.SIDETURNCALIBRATE);
	}

	
	public void sideCalibrate() {
		//we need a return packet after calibration?
		sc.sendPacket(Packet.SIDECALIBRATE);
		setPreviousPacket(Packet.SIDECALIBRATE);
	}

	public void frontCalibrate() {
		sc.sendPacket(Packet.FRONTCALIBRATE);
		setPreviousPacket(Packet.FRONTCALIBRATE);
	}
	
	public void initialCalibrate() {
		sc.sendPacket(Packet.INITIALCALIBRATE);
	}
		
	public boolean createFullMovementPacketToArduino(Queue<Integer> instructions) {
		//create one whole command for multiple movement
		//send to both android and arduino
		String toSend = null;
		int count = 1;
		int temp = 0;
		if(instructions==null || instructions.isEmpty())
			return false;
		System.out.println("Sending instruction");
		int subinstruct = instructions.remove();
		while(!instructions.isEmpty()) {
			temp = instructions.remove();
			if(subinstruct == temp && count < 10 && subinstruct == Packet.FORWARDi) {
				count++;
				if(!instructions.isEmpty()) {
					continue;
				}
			}
			if(subinstruct == FORWARDi) {
					toSend = Packet.FORWARDCMD +  Packet.Splitter +  count + "$";
//					System.out.println("Sending "+  toSend + "...");
				}
			else if(subinstruct == TURNRIGHTi) {
					toSend = Packet.TURNRIGHTCMD + Packet.Splitter + 0 + "$";
//					System.out.println("Sending "+  toSend + "...");
				}
			else if(subinstruct == TURNLEFTi) {
					toSend = Packet.TURNLEFTCMD + Packet.Splitter +  0 + "$";
//					System.out.println("Sending "+  toSend + "...");
				}
			sc.sendPacket(toSend);
				try {
					Thread.sleep(850);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	
			count = 1;
			if(instructions.isEmpty() && subinstruct!=temp) {
				if(temp == FORWARDi) {
					toSend = Packet.FORWARDCMD +  Packet.Splitter +  count + "$";
//					System.out.println("Sending "+  toSend + "...");
				}
			else if(temp == TURNRIGHTi) {
					toSend = Packet.TURNRIGHTCMD + Packet.Splitter + 0 + "$";
//					System.out.println("Sending "+  toSend + "...");
				}
			else if(temp == TURNLEFTi) {
				toSend = Packet.TURNLEFTCMD + Packet.Splitter +  0 + "$";
					System.out.println("Sending "+  toSend + "...");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sc.sendPacket(toSend);
				break;
			}
			subinstruct = temp;

		}
		sideTurnCalibrate();
		return true;
		
		//either send here or return string...(unsure)
	}
	
	

	public int getWaypoint_X() {
		return waypoint_x;
	}

	public int getWaypoint_Y() {
		return waypoint_y;
	}

	public void sendWholeMap(Map mapP) {
		 //transpose the array...
		int[][] map = mapP.getMapArray();
		String mapCmd = Packet.MAPDESCRIPTORCMD + "[";
		int[][] newMapArray = new int[Map.WIDTH][Map.HEIGHT];
		for(int i = 0 ; i < Map.HEIGHT; i++) {
			for(int j = 0; j < Map.WIDTH; j++) {
				 newMapArray[j][i] = map[i][j] ;
			}
		}
		for(int i = 0 ; i < Map.WIDTH; i++) {
		  mapCmd += Arrays.toString(newMapArray[i]);
		  if(i != Map.WIDTH-1)
			  mapCmd += ",";
		}
		mapCmd += "]$";
		sc.sendPacket(mapCmd);
		//transpose finished
		
		//send array to android. 
	}
	
	public boolean createOneMovementPacketToArduino(int instruction) {
		//for one by one exploration
		//create one Packet for one movement
		//send to both android and arduino
		String instructionString = null;
		String instructionString2 = null;
		if(instruction == FORWARDi) {
			instructionString = Packet.FORWARDCMD + Packet.Splitter + "1"+ "$";
			instructionString2 = Packet.FORWARDCMDANDROID + Packet.Splitter + "1" + "$";
			System.out.println("Sending a Forward Packet");
		}
		else if(instruction == TURNRIGHTi) {
			instructionString = Packet.TURNRIGHTCMD + Packet.Splitter + "1"+ "$";
			instructionString2 = Packet.TURNRIGHTCMDANDROID + Packet.Splitter + "1" + "$";
			System.out.println("Sending a Turn right Packet");
		}
		else if(instruction == TURNLEFTi) {
			instructionString = Packet.TURNLEFTCMD+ Packet.Splitter + "1"+ "$";
			instructionString2 = Packet.TURNLEFTCMDANDROID + Packet.Splitter + "1" + "$";
			System.out.println("Sending a Turn Left Packet");
		}
		else if(instruction == REVERSEi) {
			instructionString = Packet.REVERSECMD + Packet.Splitter + "1"+ "$";
			instructionString2 = Packet.REVERSECMDANDROID + Packet.Splitter + "1" + "$";

			System.out.println("Sending a Reverse Packet");
		}
		else {
			System.out.println("Error: Wrong format");
			return false;
		}
		
		sc.sendPacket(instructionString);
		sc.sendPacket(instructionString2);
		setPreviousPacket(instructionString);
		return true;
		//either send here or return string...(unsure)

	}
	public void sendCMD(String cmd) {
		sc.sendPacket(cmd);
	}


	public void waitForAck() {
		System.out.println("+++++++++++++++++++++++++++++++++++++++Waiting for Acknowledgement++++++++++++++++++++++++++++++++++++++++++\n");
		String ackrecv = "test";
		while(!ackrecv.equalsIgnoreCase(Packet.ACKKNOWLEDGE)) {
			ackrecv = sc.receivePacket(false, null);			
		}
		//NOPE WAIT AND THROW.
		// TODO Auto-generated method stub
		
	}





}