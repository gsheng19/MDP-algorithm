import javax.lang.model.type.NullType;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.time.Duration;
import java.time.Instant;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


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
	private static JFrame _appFrame = null;         // application JFrame
	private static JPanel _mapCards = null;         // JPanel for map views
	private static JPanel _buttons = null;          // JPanel for buttons
	private static boolean simulator = false;    //IMPORTANT VARIABLE
	private static boolean imageRec = false;    //IMPORTANT VARIABLE
	private static int timeLimit = 3600;            // time limit
	private static int coverageLimit = 300;         // coverage limit
	private static State currentState = null;
	private static Instant starts = null;
	private static Instant end = null;
	private static Visualization viz = new Visualization();
	private static PacketFactory pf = null;
	private static Queue<Packet> recvPackets = null;
	private static Astar as = null;
	private static Node waypoint = null;
	private static Exploration exe = null;
	private static Map map = null;


	public static void main(String[] args) {
		RobotInterface theRobot;
		Visualization viz = new Visualization();
		PacketFactory pf = null;
		Queue<Packet> recvPackets = null;
		Astar as = null;
		Node waypoint = null;
		JFrame frame = null;
		String PFHexString1, PFHexString2 = null;
		currentState = State.AWAITINGUSERINPUT;

		String OS = System.getProperty("os.name").toLowerCase();

		Map map = new Map();
		String fileName;

		OperatingSystem theOS = OperatingSystem.Windows;
		int wayx = 1;
		int wayy = 1;

		if (OS.indexOf("win") >= 0)
			theOS = OperatingSystem.Windows;
		else if ((OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0))
			theOS = OperatingSystem.Linux;

		if (theOS == OperatingSystem.Windows) {
			frame= new JFrame("Arena Simulator for MDP");
			frame.setSize(560, 760);
			frame.setLocationRelativeTo(null);
		}


		// the simulator requires the rendering frame to be activated
		if (simulator) {
			// the class and initialisation for the simulated robot
			theRobot = new Robot(1, 18, Direction.UP, map);
			// ***Potentially need to change.


			// 3 front, 2 right, 1(Long range) left
			Sensor s1 = new Sensor(3, SensorLocation.FACING_TOP, -1, -1, theRobot.x, theRobot.y);
			Sensor s2 = new Sensor(3, SensorLocation.FACING_TOP, 0, -1, theRobot.x, theRobot.y);
			Sensor s3 = new Sensor(3, SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);
			Sensor s4 = new Sensor(3, SensorLocation.FACING_LEFT, -1, 1, theRobot.x, theRobot.y);
			Sensor s5 = new Sensor(3, SensorLocation.FACING_LEFT, -1, -1, theRobot.x, theRobot.y);
			Sensor s6 = new Sensor(6, SensorLocation.FACING_RIGHT, 1, 0, theRobot.x, theRobot.y);

			Sensor[] Sensors = { s1, s2, s3, s4, s5, s6 };

			theRobot.addSensors(Sensors);

			viz.setRobot(theRobot);
			theRobot.setViz(viz);

			if (theOS == OperatingSystem.Windows) {
				frame.getContentPane().add(viz);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(true);
			}
		}
		else  //real robot run
		{
			int[][] test= new int[][]
					{
							/*{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
							{0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0},
							{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},*/


							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
							{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
					};
			//for debugging purposes
			MapIterator.printExploredResultsToFile(test, "C://Users//Guan Sheng//Desktop//test.txt");
			MapIterator.ArraytoHex((test));
			map.setMapArray(test);
			//waypoint = new Node(13, 13);  // may be this setting the extra clearance box
			//map.setWaypointClear(13, 13);



			//System.out.print("Enter PF1: ");
			/*PFHexString1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"; //ENTER PF1
			//System.out.print("Enter PF2: ");
			PFHexString2 = "00200040008000002001C0400380000001000200C78180020000000008011000020004000C005w6we"; //ENTER PF2
			String PFBinString1 = strToMapDescriptor(PFHexString1);
			String PFBinString2 = strToMapDescriptor(PFHexString2);
			//System.out.println("PF1 Bin: "+ PFBinString1);
			//System.out.println("PF2 Bin: "+ PFBinString2);
			int[][] MDFLoadedMap = map.loadFromMDF(PFBinString2);
			System.out.println("Printing Map Loaded from MDF Strings:");
			for (int i = 0; i < 20; i++) {
				for (int j = 0; j < 15; j++) {
					System.out.print(MDFLoadedMap[i][j]);  //print custom array
				}
				System.out.println();  //print custom array
			}
			MapIterator.printExploredResultsToFile(MDFLoadedMap,
					"C://Users//Guan Sheng//Desktop//test.txt");
			MapIterator.ArraytoHex((MDFLoadedMap));
			map.setMapArray(MDFLoadedMap);*/

			recvPackets = new LinkedList<Packet>();
			pf = new PacketFactory(recvPackets);
			theRobot = new RealRobot(1, 18, Direction.UP, map, pf);
			// 3 front(RIGHT), 2 right(DOWN), 1(Long range) left (TOP)
			RealSensor s1 = new RealSensor(3,SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);
			RealSensor s2 = new RealSensor(3,SensorLocation.FACING_TOP, 0, -1, theRobot.x, theRobot.y);
			RealSensor s3 = new RealSensor(3,SensorLocation.FACING_TOP, -1, -1, theRobot.x, theRobot.y);
			RealSensor s4 = new RealSensor(3,SensorLocation.FACING_LEFT, -1, -1, theRobot.x, theRobot.y);
			RealSensor s5 = new RealSensor(3,SensorLocation.FACING_LEFT, -1, 1, theRobot.x, theRobot.y);
			RealSensor s6 = new RealSensor(6,SensorLocation.FACING_RIGHT, 0, -1, theRobot.x, theRobot.y);

			RealSensor[] Sensors = { s1, s2, s3, s4, s5, s6 };
			theRobot.addSensors(Sensors);
			viz.setRobot(theRobot);
			theRobot.setViz(viz);

			if (theOS == OperatingSystem.Windows) {
				frame.getContentPane().add(viz);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(true);
			}

		}
		// init the algo classes
		System.out.println("initialize exe");
		Exploration exe = new Exploration(null, simulator, theRobot, viz, map);
		exe.initStartPoint(1, 18);
		//System.out.println("Executing initial calibration...");
		//pf.sendCMD("ARD|INITIALCALIBRATE#");

		while (currentState != State.DONE) {
			switch (currentState) {

				case IDLE:
					break;

				case AWAITINGUSERINPUT:
					System.out.println(
							"\n------------------------------AWAITINGUSERINPUT Case------------------------------\n");
					if (simulator) {
						Scanner sc = new Scanner(System.in);
						System.out.println("Please enter state:");
						System.out.println("1) Select Arena");
						System.out.println("2) Set Waypoint");
						System.out.println("3) Set robot position");
						System.out.println("4) Start Exploration");
						System.out.println("5) Start Fastest Path");
						System.out.println("6) Stop Instruction");
						System.out.println("7) Reset Instruction");
						System.out.println("8) Get Map Descriptor");
						System.out.println("9) Change Speed of Robot");
						System.out.println("10) Change Time Given to Robot");
						System.out.println("11) Change Percentage Explored");
						int scanType = sc.nextInt();
						// sc.close();
						if (scanType == 1) {
							Scanner sc2 = new Scanner(System.in);
							System.out.println("Choose your arena:");
							System.out.println("1) TestingArena");
							System.out.println("2) SampleArena1");
							System.out.println("3) SampleArena2");
							System.out.println("4) SampleArena3");
							System.out.println("5) SampleArena4");
							System.out.println("6) SampleArena5");
							System.out.println("7) Enter MDF Strings");
							int scanType2 = sc2.nextInt();
							if (scanType2 == 1)
								fileName = "TestingArena";
							else if (scanType2 == 2)
								fileName = "SampleArena1";
							else if (scanType2 == 3)
								fileName = "SampleArena2";
							else if (scanType2 == 4)
								fileName = "SampleArena3";
							else if (scanType2 == 5)
								fileName = "SampleArena4";
							else if (scanType2 == 6)
								fileName = "SampleArena5";
							else if(scanType2 == 7)
							{
								fileName="loadFromMDF";
								Scanner sc3 = new Scanner(System.in);
								System.out.print("Enter PF1: ");
								PFHexString1 = sc3.nextLine();
								System.out.print("Enter PF2: ");
								PFHexString2 = sc3.nextLine();
								//System.out.println("PF1: "+ PFHexString1);
								//System.out.println("PF2: "+ PFHexString2);
								String PFBinString1 = strToMapDescriptor(PFHexString1);
								String PFBinString2 = strToMapDescriptor(PFHexString2);
								//System.out.println("PF1 Bin: "+ PFBinString1);
								//System.out.println("PF2 Bin: "+ PFBinString2);
								int[][] MDFLoadedMap = map.loadFromMDF(PFBinString2);
								System.out.println("Printing Map Loaded from MDF Strings:");
								for (int i = 0; i < 20; i++) {
									for (int j = 0; j < 15; j++) {
										System.out.print(MDFLoadedMap[i][j]);  //print custom array
									}
									System.out.println();  //print custom array
								}
								MapIterator.printExploredResultsToFile(MDFLoadedMap,
										"C://Users//Guan Sheng//Desktop//test.txt");
								MapIterator.ArraytoHex((MDFLoadedMap));
								map.setMapArray(MDFLoadedMap);
								System.out.println("Map set to "+fileName);
							}
							else
								fileName = "TestingArena";

							if(fileName != "loadFromMDF"){
								int[][] chosenMap = map.loadMapFromFile(fileName);
								MapIterator.printExploredResultsToFile(chosenMap,
										"C://Users//Guan Sheng//Desktop//mapIterator.txt");
								MapIterator.ArraytoHex((chosenMap));
								map.setMapArray(chosenMap);
								System.out.println("Map set to " + fileName);
							}
						}
						if (scanType == 2) {
							System.out.println("Please enter x coordinate: ");
							wayx = sc.nextInt();
							System.out.println("Please enter y coordinate: ");
							wayy = sc.nextInt();
							// set robot waypoint
							System.out.println("setting waypoint position at: " + wayx + ", " + wayy);
							waypoint = new Node(wayx, wayy);
							map.setWaypointClear(wayx, wayy);
						} else if (scanType == 3) {
							// set robot position
							System.out.println("Please enter x coordinate: ");
							int getx = sc.nextInt();
							System.out.println("Please enter y coordinate: ");
							int gety = sc.nextInt();
							System.out.println("Moving robot to:" + getx + ", " + gety);
							theRobot.setRobotPos(getx, gety, Direction.UP);
							exe.initStartPoint(getx, gety);

							Sensor s1 = new Sensor(3, SensorLocation.FACING_TOP, -1, -1, theRobot.x, theRobot.y);
							Sensor s2 = new Sensor(3, SensorLocation.FACING_TOP, 0, -1, theRobot.x, theRobot.y);
							Sensor s3 = new Sensor(3, SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);
							Sensor s4 = new Sensor(3, SensorLocation.FACING_LEFT, -1, 1, theRobot.x, theRobot.y);
							Sensor s5 = new Sensor(3, SensorLocation.FACING_LEFT, -1, -1, theRobot.x, theRobot.y);
							Sensor s6 = new Sensor(6, SensorLocation.FACING_RIGHT, 1, 0, theRobot.x, theRobot.y);

							Sensor[] Sensors = { s1, s2, s3, s4, s5, s6 };
							/*theRobot.addSensors(Sensors);*/
						} else if (scanType == 4) {
							starts = Instant.now();
							currentState = State.EXPLORATION;
						}
						else if(scanType == 5) {
							starts = Instant.now();
							currentState = State.FASTESTPATH;
						}
						else if(scanType == 6) {
							//					currentState = State.FASTESTPATHHOME;
						}
						else if(scanType == 7) {
							//					currentState = State.RESETFASTESTPATHHOME;
							System.out.println("Reseting Map...");
							//map.resetMap();
							theRobot.setface(Direction.UP);
							theRobot.x = 1;
							theRobot.y = 18;
							map.resetMap();
							viz.repaint();
						}
						else if (scanType == 8)
							theRobot.sendMapDescriptor();

						else if (scanType == 9){
							System.out.println("Please enter speed (default 30): ");
							int speed = sc.nextInt();
							exe.setStepsPerSecond(speed);
						}

						else if (scanType == 10){
							System.out.println("Please enter time (minutes): ");
							int minute = sc.nextInt();
							System.out.println("Please enter time (seconds): ");
							int second = sc.nextInt();
							exe.setTime(minute, second);
						}

						else if (scanType == 11){
							System.out.println("Please enter percetage to be explored: ");
							float explored = sc.nextFloat();
							exe.setPercentage(explored);
						}
						break;
					}
					else{

						System.out.print("\nListening\n");
						pf.listen();
						if(recvPackets.isEmpty())
							continue;
						Packet pkt = recvPackets.remove();
						System.out.println(pkt.getType());
						if(pkt.getType() == Packet.SetWayPointi) {
							wayx = pkt.getX();
							wayy = pkt.getY();
							//set robot waypoint
							System.out.println("setting waypoint position at: " + wayx+ ", " + wayy);
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
							theRobot.setface(Direction.UP);
							theRobot.x = 1;
							theRobot.y = 18;
							map.resetMap();
							viz.repaint();
						}
						else if (pkt.getType() == Packet.GETMAPi)
							theRobot.sendMapDescriptor();
						else {
							System.out.println("Invalid Packet!!");
							continue;
						}
						break;
					}
				case EXPLORATION:
					//init an explore algo class and call StartExploration()
					System.out.println("---------------------------------Exploration case---------------------------------\n");
					if (simulator==false) //was !simulator
						theRobot.LookAtSurroundings();
					int DoSimulatorExplorationResult = exe.DoSimulatorExploration();

					if(simulator)
					{
						//will return true once the exploration is done(when the robot reaches the starting point again)
						if(DoSimulatorExplorationResult == 1)
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
						//	currentState = State.WAITINGFORCOMMAND;
					}
					else //else simulator is false, so real robot
					{

						//theRobot.LookAtSurroundings();
						//will return true once the exploration is done(when the robot reaches the starting point again)
						if(DoSimulatorExplorationResult == 1)
						{
							//send the packet to say that exploration is done
							System.out.println("DoSimulatorExplorationResult: "+ DoSimulatorExplorationResult +". Ending Exploration...");
							MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
							MapIterator.printExploredResultsToHex("ExplorationHex.txt");  //Print P1 string
							MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
							MapIterator.printObstacleResultsToHex("ObstacleHex.txt"); //Print P2 string

							//theRobot.sendMapDescriptor();
							end = Instant.now();
							System.out.println("Time: " + Duration.between(starts, end));

							//((RealRobot)theRobot).sendMapDescriptortoRpi();  //solve this later

							pf.sc.sendPacket("AND|BEFin#");
							//pf.sc.sendPacket(Packet.StartExplorationTypeFin + "$");

							// Send map descriptor
							System.out.println("------------------------------Sending map descriptor------------------------------\n");
							System.out.println("doing map descriptor...");
							MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
							MapIterator.printExploredResultsToHex("ExplorationHex.txt");
							MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
							MapIterator.printObstacleResultsToHex("ObstacleHex.txt");
							/*pf.sc.sendPacket("AND|Exploration mdf:" + MapIterator.mapDescriptorP1Hex + "#");
							pf.sc.sendPacket("AND|Obstacle mdf:" + MapIterator.mapDescriptorP2Hex + "#");
							pf.sc.sendPacket("AND|finish_exe_mdf#");*/
							pf.sendCMD("AND|EMDF:" + MapIterator.mapDescriptorP1Hex + "#");  // Exploration MDF
							pf.sendCMD("AND|OMDF:" + MapIterator.mapDescriptorP2Hex + "#");  //Obstacle MDF
							pf.sendCMD("AND|FEMDF#"); //Finished Exploration MDF
							currentState = State.AWAITINGUSERINPUT;

							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							theRobot.initial_Calibrate();
							pf.setFlag(false);

							//send to wait for command to wait for next phase(fastestpath)
							//currentState = State.SENDINGMAPDESCRIPTOR;
						}else if (DoSimulatorExplorationResult == -1) {
							System.out.println("Robot wants to reset prematurely. Resetting exe and robot...");
							System.out.println("PLEASE BRING ROBOT BACK TO 1,18 FACING LEFT, THEN SEND IC COMMAND, THEN START EXPLORE (IT SHOULD BE RIGHT FACING AFTER IC)!");

//						viz = new Visualization();
							currentState = State.AWAITINGUSERINPUT;
//						pf = null;
//						recvPackets = null;
							as = null;
							waypoint = null;

//						RobotInterface theRobot;
							theRobot = new RealRobot(1,18, Direction.UP, map, pf);

							RealSensor s1 = new RealSensor(3,SensorLocation.FACING_TOP, 1, -1, theRobot.x, theRobot.y);
							RealSensor s2 = new RealSensor(3,SensorLocation.FACING_TOP, 0, -1, theRobot.x, theRobot.y);
							RealSensor s3 = new RealSensor(3,SensorLocation.FACING_TOP, -1, -1, theRobot.x, theRobot.y);
							RealSensor s4 = new RealSensor(3,SensorLocation.FACING_LEFT, -1, -1, theRobot.x, theRobot.y);
							RealSensor s5 = new RealSensor(3,SensorLocation.FACING_LEFT, -1, 1, theRobot.x, theRobot.y);
							RealSensor s6 = new RealSensor(6,SensorLocation.FACING_RIGHT, 0, -1, theRobot.x, theRobot.y);

							RealSensor[] Sensors = {s1,s2,s3,s4,s5,s6};
							theRobot.addSensors(Sensors);
							viz.setRobot(theRobot);
							theRobot.setViz(viz);

							map.resetMap();
//						theRobot.setface(Direction.RIGHT);
//						theRobot.x = 1;
//						theRobot.y = 18;
							// REINTIALIZE
//						map = new Map(); //
							exe = new Exploration(null, simulator, theRobot, viz, map);
							exe.initStartPoint(1,18);
						}
					}
					if(DoSimulatorExplorationResult != 1)
						currentState = State.AWAITINGUSERINPUT;
					break;

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
					else //real robot
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
					//init fastest path from startNode to goalNode
					System.out.println("-------------------------------------FastestPath case-----------------------------------\n");
					if(simulator)
					{
						theRobot.initial_Calibrate();
						//update the map nodes, then create a new astar path
						map.updateMap();
						waypoint = map.getNodeXY(wayx, wayy);
						Astar as31 = new Astar(map.getNodeXY(theRobot.x, theRobot.y),waypoint);
						Astar as2 = new Astar(waypoint, map.getNodeXY(13, 1));
						Stack<Node> as31GFP = as31.getFastestPath();
						if(as31GFP.isEmpty()){
							Astar as4 = new Astar(map.getNodeXY(theRobot.x, theRobot.y),map.getNodeXY(13,1));
							PathDrawer.update(theRobot.x, theRobot.y, as4.getFastestPath());
							theRobot.getFastestInstruction(as4.getFastestPath());
							PathDrawer.removePath();
						}
						else {
							PathDrawer.update(theRobot.x, theRobot.y, as31GFP);
							theRobot.getFastestInstruction(as31.getFastestPath());
							PathDrawer.update(theRobot.x, theRobot.y, as2.getFastestPath());
							theRobot.getFastestInstruction(as2.getFastestPath());
							PathDrawer.removePath();
							//send it to the robot to handle the instruction
						}
						currentState = State.SENDINGMAPDESCRIPTOR;
						System.out.print("finished fastest path TO GOAL");

					}
					else
					{
						//update the map nodes, then create a new astar path
						//testing empty map
						//set empty
						//pf.sc.sendPacket("ARD|FRONTCALIBRATE#");

						pf.sendCMD("AND|BFOk#"); // Packet.StartFastestPathTypeOkANDROID + "$"
						pf.sendCMD("ARD|BFOk#"); // Packet.StartFastestPathTypeOkARDURINO + "$"
						//NOTE
						map.updateMap();
						pf.sendWholeMap(map);

						Stack<Node> stack, stack2 = null;
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
							Astar as2 = new Astar(map.getNodeXY(x1,y1), map.getNodeXY(13, 1));
							//PathDrawer.update(theRobot.x, theRobot.y, as.getFastestPath());
							//PathDrawer.update(theRobot.x, theRobot.y, as2.getFastestPath());
							stack = as.getFastestPath();
							stack2 = as2.getFastestPath();

							if(!stack.isEmpty() && !stack2.isEmpty()) {
								System.out.println("going to waypoint...");
								stack2.addAll(stack);
								theRobot.getFastestInstruction(stack2);
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
						PathDrawer.update(theRobot.x, theRobot.y, as.getFastestPath());

						viz.repaint();
						end = Instant.now();
//						System.out.println("Time : " +Duration.between(starts, end));//    THIS CAUSE ANDROID TO CRASH
//						currentState = State.SENDINGMAPDESCRIPTOR;
						currentState = State.AWAITINGUSERINPUT; // TODO: @JARRETT REMOVED COS OF STUPID BUG WHICH ROBOT DIES BEFORE REACHING END GOAL

					}
					break;

				case SENDINGMAPDESCRIPTOR:
					System.out.println("------------------------------Sending this map descriptor------------------------------\n");
					System.out.println("doing map descriptor");

					MapIterator.printExploredResultsToFile(map.getMapArray(), "theExplored.txt");
					MapIterator.printExploredResultsToHex("ExplorationHex.txt");

					MapIterator.printObstacleResultsToFile(map.getMapArray(), "theObstacle.txt");
					MapIterator.printObstacleResultsToHex("ObstacleHex.txt");
					if(!simulator){ //real robot
						pf.sendCMD("AND|EMDF:" + MapIterator.mapDescriptorP1Hex + "#");  // Exploration MDF
						pf.sendCMD("AND|OMDF:" + MapIterator.mapDescriptorP2Hex + "#");  //Obstacle MDF
						pf.sendCMD("AND|FEMDF#"); //Finished Exploration MDF

					}
					currentState = State.AWAITINGUSERINPUT;
			}
		}
	}

	static String strToMapDescriptor(String s) {
		String res = "";
		for (int i = 0 ; i < s.length() ; i++) {
			//temp += s[i];
			res += hexToBin(s.charAt(i));
		}
		return res;
	}

	static String hexToBin(char c) {
		switch(c) {
			case '0': return "0000";
			case '1': return "0001";
			case '2': return "0010";
			case '3': return "0011";
			case '4': return "0100";
			case '5': return "0101";
			case '6': return "0110";
			case '7': return "0111";
			case '8': return "1000";
			case '9': return "1001";
			case 'A': return "1010";
			case 'B': return "1011";
			case 'C': return "1100";
			case 'D': return "1101";
			case 'E': return "1110";
			case 'F': return "1111";
			default: return "0000";
		}
	}
}