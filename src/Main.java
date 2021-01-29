import java.awt.Insets;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Scanner;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


enum State{
	IDLE,
	AWAITINGUSERINPUT,
	EXPLORATION,
	FASTESTPATHHOME,
	FASTESTPATH,
	DONE,
	RESETFASTESTPATHHOME,
	SENDINGMAPDESCRIPTOR,

}

enum OperatingSystem{
	Windows,
	Linux
}

public class Main {
//	JLabel stepsLabel = new JLabel("No. of Steps to Calibration");
//	JTextField calibrate = new JTextField("");
//	JButton update = new JButton("update");


	public static void main(String[] args){
		String OS = System.getProperty("os.name").toLowerCase();

		OperatingSystem theOS = OperatingSystem.Windows;

		if(OS.indexOf("win") >= 0)
			theOS = OperatingSystem.Windows;
		else if((OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ))
			theOS = OperatingSystem.Linux;

		State currentState;
		JFrame frame = null;

		if(theOS == OperatingSystem.Windows)
		{
			frame= new JFrame("Arena Simulator for MDP");
			frame.setSize(600, 820);
		}
		Instant starts = null;
		Instant end = null;
		Map map = new Map();
		Insets insets = frame.getInsets();
		
		
		//////////////////////IMPORTANT VARIABLE///////////////////////////////////////////////////////////////////////
		boolean simulator = true;
		//////////////////////IMPORTANT VARIABLE//////////////////////////////////////////////////////////////////////
		
		if(simulator) {
			int[][] test= new int[][]
					{
				{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
				{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
					};
					MapIterator.printExploredResultsToFile(test, "C://Users//Guan Sheng//Desktop//test.txt");
					MapIterator.ArraytoHex((test));
			map.setMapArray(test);
		}
		RobotInterface theRobot;
		Visualization viz = new Visualization();
		currentState = State.AWAITINGUSERINPUT;
		PacketFactory pf = null;
		Queue<Packet> recvPackets = null;
		Astar as = null;
		Node waypoint = null;

		//the simulator requires the rendering frame to be activated
		if(simulator) {
			//the class and initialisation for the simulated robot
			theRobot = new Robot(1,18, Direction.UP, map);
			//***Potentially need to change
			//3 front, 2 right, 1(Long range) left
			Sensor s1 = new Sensor(3,SensorLocation.FACING_TOP, -1, -1, theRobot.x, theRobot.y); 
			Sensor s2 = new Sensor(3,SensorLocation.FACING_TOP, 0, -1, theRobot.x, theRobot.y);
			Sensor s3 = new Sensor(3,SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);
			Sensor s4 = new Sensor(3,SensorLocation.FACING_LEFT, -1, 1, theRobot.x, theRobot.y);
			Sensor s5 = new Sensor(3,SensorLocation.FACING_LEFT, -1, -1, theRobot.x, theRobot.y);
			Sensor s6 = new Sensor(6,SensorLocation.FACING_RIGHT, 1, 0, theRobot.x, theRobot.y);


			Sensor[] Sensors = {s1,s2,s3,s4,s5,s6};
			
			theRobot.addSensors(Sensors);

			viz.setRobot(theRobot);
			theRobot.setViz(viz);

			if(theOS == OperatingSystem.Windows)
			{
				frame.getContentPane().add(viz);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(true);
			}
		}
		else
		{
			recvPackets = new LinkedList<Packet>();
			pf = new PacketFactory(recvPackets);
			theRobot = new RealRobot(1,18, Direction.RIGHT, map, pf);
			//3 front(RIGHT), 2 right(DOWN), 1(Long range) left (TOP)
			Sensor s1 = new Sensor(4,SensorLocation.FACING_RIGHT, 1, 1, theRobot.x, theRobot.y);
			Sensor s2 = new Sensor(4,SensorLocation.FACING_RIGHT, 1, 0, theRobot.x, theRobot.y);
			Sensor s3 = new Sensor(4,SensorLocation.FACING_DOWN, 1, 0, theRobot.x, theRobot.y);
			Sensor s4 = new Sensor(4,SensorLocation.FACING_RIGHT, 1, -1, theRobot.x, theRobot.y);
			Sensor s5 = new Sensor(4,SensorLocation.FACING_DOWN, -1, 0, theRobot.x, theRobot.y);
			Sensor s6 = new Sensor(5,SensorLocation.FACING_TOP, 0, 0, theRobot.x, theRobot.y);


			Sensor[] Sensors = {s1,s2,s3,s4,s5,s6};
			theRobot.addSensors(Sensors);
			viz.setRobot(theRobot);
			theRobot.setViz(viz);

			if(theOS == OperatingSystem.Windows)
			{
				frame.getContentPane().add(viz);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(true);
				System.out.print("Waiting for command");
				currentState = State.AWAITINGUSERINPUT;
			}


		}

		//init the algo classes
		Exploration exe = new Exploration(null, simulator, theRobot, viz, map);
		exe.initStartPoint(1,18);

		//System.out.print("printing string");
		//System.out.print(iterator.formatStringToHexadecimal("000000000000000000000000000000000000010000000000000000000000000000000001110010000000000000000000000000000000000000000000000001110000000000000000000000000000000010000000000000000000000000000000000111111000000000000000000001110000000000000000000000000000000000000010000000000000000000000000000000000000"));
		while(currentState != State.DONE)
		{
			switch(currentState){

			case IDLE:
				break;

			case AWAITINGUSERINPUT:
				System.out.println("\n------------------------------AWAITINGUSERINPUT Case------------------------------\n");
				if(simulator) {
					Scanner sc = new Scanner(System.in);
					System.out.println("Please enter state:");
					System.out.println("1) Set Waypoint");
					System.out.println("2) Set robot position");
					System.out.println("3) Start Exploration");
					System.out.println("4) Start Fastest Path");
					System.out.println("5) Stop Instruction");
					System.out.println("6) Reset Instruction");
					System.out.println("7) Get Map Descriptor");
					int scanType = sc.nextInt();
//					sc.close();
					if(scanType == 1) {
						System.out.println("Please enter x coordinate: ");
						int wayx = sc.nextInt();
						System.out.println("Please enter y coordinate: ");
						int wayy = sc.nextInt();
						//set robot waypoint
						System.out.println("setting waypoint position at :" + wayx+ ", " + wayy);
						waypoint = new Node(wayx, wayy);
						map.setWaypointClear(wayx, wayy);
					}
					else if(scanType == 2) {
						//set robot robot position
						System.out.println("Please enter x coordinate: ");
						int getx = sc.nextInt();
						System.out.println("Please enter y coordinate: ");
						int gety = sc.nextInt();
						//set robot waypoint
						System.out.println("Moving robot to:" + getx+ ", " + gety);
						theRobot.setRobotPos(getx, gety, Direction.RIGHT);
					}
					else if(scanType == 3) {
						starts = Instant.now();	
						currentState = State.EXPLORATION;
					}
					else if(scanType == 4) {
						starts = Instant.now();				
						currentState = State.FASTESTPATH;					
					}
					else if(scanType == 5) {
	//					currentState = State.FASTESTPATHHOME;					
					}
					else if(scanType == 6) {
	//					currentState = State.RESETFASTESTPATHHOME;
						System.out.println("Reseting Map...");
						map.resetMap();
						theRobot.setface(Direction.RIGHT);
						theRobot.x = 1;
						theRobot.y = 18;
						map.resetMap();
						viz.repaint();
					}
					else if (scanType == 7)
						theRobot.sendMapDescriptor();
					break;
				}
				else{
					System.out.print("\nListening\n");
					//pf.sc.sendPacket("Donald Trump!");
					pf.listen();
					if(recvPackets.isEmpty())
						continue;
					Packet  pkt = recvPackets.remove();
					System.out.println(pkt.getType());
					if(pkt.getType() == Packet.SetWayPointi) {
						int wayx = pkt.getX();
						int wayy = pkt.getY();
						//set robot waypoint
						System.out.println("setting waypoint position at :" + wayx+ ", " + wayy);
						waypoint = new Node(wayx, wayy);
						map.setWaypointClear(wayx, wayy);
					}
					else if(pkt.getType() == Packet.setRobotPosition) {
						//set robot robot position
						theRobot.setRobotPos(pkt.getX(), pkt.getY(), pkt.getDirection());
					}
					else if(pkt.getType() == Packet.StartExploration) {
						starts = Instant.now();	
						currentState = State.EXPLORATION;
					}
					else if(pkt.getType() == Packet.StartFastestPath) {
						starts = Instant.now();				
						currentState = State.FASTESTPATH;					
					}
					else if(pkt.getType() == Packet.StopInstruction) {
						currentState = State.FASTESTPATHHOME;					
					}
					else if(pkt.getType() == Packet.ResetInstruction) {
						currentState = State.RESETFASTESTPATHHOME;
						System.out.println("Reseting Map...");
						map.resetMap();
						theRobot.setface(Direction.RIGHT);
						theRobot.x = 1;
						theRobot.y = 18;
						map.resetMap();
						viz.repaint();
					}
					else if (pkt.getType() == Packet.GETMAPi)
						theRobot.sendMapDescriptor();
					break;
				}
			case EXPLORATION:
				//init an explore algo class and call StartExploration()
				System.out.println("---------------------------------Exploration case---------------------------------\n");

				if(simulator)
				{
					//will return true once the exploration is done(when the robot reaches the starting point again)
					if(exe.DoSimulatorExploration())
					{
						Scanner sc = new Scanner(System.in);
						theRobot.deactivateSensors();
						System.out.println("Go to fastest path? \n 1=yes \n 2=no");
						int choice = sc.nextInt();
//						sc.close();
						if(choice == 1)
							currentState = State.FASTESTPATH;
						else
							currentState = State.AWAITINGUSERINPUT;
						System.out.println("ending Exploration...");
					}//else
					//	currentState = State.AWAITINGUSERINPUT;
				}
				else
				{
					theRobot.LookAtSurroundings();
					//will return true once the exploration is done(when the robot reaches the starting point again)
					if(exe.DoSimulatorExploration())
					{
						//send the packet to say that exploration is done
						System.out.println("ending Exploration...");
						theRobot.sendMapDescriptor();
						end = Instant.now();
						System.out.println("Time: " + Duration.between(starts, end));
						pf.sc.sendPacket(Packet.StartExplorationTypeFin);
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						theRobot.initial_Calibrate();
						pf.setFlag(false);

						//send to wait for command to wait for next phase(fastestpath)
						currentState = State.SENDINGMAPDESCRIPTOR;
					}
				}
				currentState = State.AWAITINGUSERINPUT;
			case FASTESTPATHHOME:
				//update the map nodes, then create a new astar path
				map.updateMap();
				//Astar as1 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(5, 10));
				Astar as1 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(1, 18));

				//send it to the robot to handle the instruction
				theRobot.getFastestInstruction(as1.getFastestPath());
				System.out.print("finished fastest path home");

				if(simulator)
					currentState = State.FASTESTPATH;
				else
					currentState = State.AWAITINGUSERINPUT;

				break;
			case RESETFASTESTPATHHOME:
				//update the map nodes, then create a new astar path
				map.updateMap();
				Astar as3 = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(1, 18));

				//send it to the robot to handle the instruction
				theRobot.getFastestInstruction(as3.getFastestPath());
				System.out.print("finished fastest path home.. resetting map...");
				map.resetMap();
				theRobot.x = 1;
				theRobot.y = 18;
				//currentState = State.FASTESTPATH;
				currentState = State.AWAITINGUSERINPUT;

				break;
			case FASTESTPATH:
				System.out.println("-------------------------------------FastestPath case-----------------------------------\n");
				if(simulator)
				{
					theRobot.initial_Calibrate();
					//update the map nodes, then create a new astar path
					map.updateMap();
					waypoint = map.getNodeXY(12, 8);
					Astar as31 = new Astar(map.getNodeXY(theRobot.x, theRobot.y),waypoint);
					Astar as2 = new Astar(waypoint, map.getNodeXY(13, 1));
					theRobot.getFastestInstruction(as31.getFastestPath());
					theRobot.getFastestInstruction(as2.getFastestPath());					
					//send it to the robot to handle the instruction
					currentState = State.SENDINGMAPDESCRIPTOR;
					System.out.print("finished fastest path TO GOAL");
					
				}
				else
				{
					//update the map nodes, then create a new astar path
					//testing empty map
					//set empty
					
					pf.sendCMD(Packet.StartFastestPathTypeOkANDROID);
					pf.sendCMD(Packet.StartFastestPathTypeOkARDURINO);
					//NOTE
					map.updateMap();
					
					Stack<Node> stack = null;
					if(waypoint == null) {
						System.out.println("NO waypoint.");
						as = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(13, 1));
						stack = as.getFastestPath();
						theRobot.getFastestInstruction(stack);
						
					}
					else {
						int x1 = waypoint.getX();
						int y1 = waypoint.getY();
						System.out.println("going to fastest path with waypoint of " + x1 + "," + y1);
						waypoint = map.getNodeXY(x1, y1);
						as = new Astar(map.getNodeXY(theRobot.x, theRobot.y), waypoint);
						Astar as2 = new Astar(waypoint, map.getNodeXY(13, 1));
						stack = as2.getFastestPath();
						Stack<Node> stack2 = as.getFastestPath();
						
						if(!stack.isEmpty() && !stack2.isEmpty()) {
							System.out.println("going to waypoint...");
							stack.addAll(stack2);
							theRobot.getFastestInstruction(stack);

						}
						else {
							System.out.println("failed to go to waypoint");
							System.out.println("going to goal without waypoint");
							as = new Astar(map.getNodeXY(theRobot.x, theRobot.y), map.getNodeXY(13, 1));
							stack = as.getFastestPath();
							theRobot.getFastestInstruction(stack);
						}
					}


					//create the int[] frm the stack
					//send the whole entire packet to rpi
					viz.repaint();
					end = Instant.now();
					System.out.println("Time : " +Duration.between(starts, end));
					currentState = State.SENDINGMAPDESCRIPTOR;

				}
				break;

			case SENDINGMAPDESCRIPTOR:
				System.out.println("------------------------------Sending this useless descriptor------------------------------\n");
				System.out.println("doing map descriptor");


				MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
				MapIterator.printExploredResultsToHex("ExplorationHex.txt");

				MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
				MapIterator.printObstacleResultsToHex("ObstacleHex.txt");
			
//				pf.sendCMD("B:Exploration mdf : " + MapIterator.mapDescriptorP1Hex + "$");
//				pf.sendCMD("B:Obstacle mdf : " + MapIterator.mapDescriptorP2Hex);
				currentState = State.AWAITINGUSERINPUT;
			}
		}
	}


	SocketClient cs = new SocketClient("192.168.4.4", 8081);






	//test of iterator
//			static int[][] test= new int[][]
//			{
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0},
//		{1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
//			};
//			MapIterator.printExploredResultsToFile(test, "C:\Users\PIZZA 3.0\Desktop\test.txt");
//			MapIterator.ArraytoHex((test));



}