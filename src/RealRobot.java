import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class RealRobot extends RobotInterface {
	Sensor[] Sen;
	boolean wantToReset = false;
	//	ArrayList<Node> TraverseNodes = new ArrayList();
	PacketFactory pf = null;
	boolean hitWallFront=false;
	boolean hitWallRight=false;
	boolean stepByStep = true;
	boolean fastestcalibrate = false; //needs to calibrate for fastest
	int count = 0;
	int numsteps = 0;
	int[][] mapConfirmed = new int[][]{
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 1, 1, 1},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 1, 1, 1},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 1, 1, 1},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{1, 1, 1, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{1, 1, 1, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7},
		{1, 1, 1, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7}
	};


	public RealRobot(int x, int y, Direction facing, Map map, PacketFactory pf){
		//starting postiion
		super();
		this.pf = pf;
		this.x = x;
		this.y = y;
		this.facing = facing;
		this.map = map;
		SenseRobotLocation();

		//this.mapArray = mapArray;
		//map = new Map(mapArray);
	}



	public void addSensors(Sensor[] sensors) {
		this.Sen = sensors;
	}

	public void updateSensor() {
		for(int i=0;i < Sen.length; i++) {
			Sen[i].updateRobotLocation(x, y);
		}
	}

	public void deactivateSensors()
	{
		Sen = new Sensor[0];
	}


	public void moveRobot(){
		numsteps = 1;
		System.out.print("moving robot\n");
		int movementDistance  = 1;

		if(facing == Direction.UP) {
			y -= movementDistance;	
			mapConfirmed[y+1][x-1] = 1;
			mapConfirmed[y+1][x] = 1;
			mapConfirmed[y+1][x+1] = 1;
		}
		else if(facing == Direction.DOWN) {
			y += movementDistance;
			mapConfirmed[y-1][x-1] = 1;
			mapConfirmed[y-1][x] = 1;
			mapConfirmed[y-1][x+1] = 1;
		}
		else if(facing == Direction.RIGHT) {
			x += movementDistance;			
			mapConfirmed[y][x-1] = 1;
			mapConfirmed[y][x] = 1;
			mapConfirmed[y][x+1] = 1;
		}
		else if(facing == Direction.LEFT) {
			x -= movementDistance;
			mapConfirmed[y][x-1] = 1;
			mapConfirmed[y][x] = 1;
			mapConfirmed[y][x+1] = 1;
		}


		if(stepByStep) {

			//			count++;
			//			if(count % 4 == 0) {
						sendMapDescriptor();	
			//			}
			pf.createOneMovementPacketToArduino(Packet.FORWARDi);
			//update the location for the robot in the sensors
			updateSensor();

			//make sensors "sense" the surrounding
			LookAtSurroundings();

		}

		viz.repaint();

	}

	public void reverse(){
		int movementDistance  = 1;

		if(facing == Direction.UP)
			y += movementDistance;
		else if(facing == Direction.DOWN)
			y -= movementDistance;
		else if(facing == Direction.RIGHT)
			x -= movementDistance;
		else if(facing == Direction.LEFT)
			x += movementDistance;

		if(stepByStep) {


			pf.createOneMovementPacketToArduino(Packet.REVERSEi);
			//update the location for the robot in the sensors
			updateSensor();

			//make sensors "sense" the surrounding
			LookAtSurroundings();

		}

		viz.repaint();
	}

	public boolean getWantToReset() {
		return this.wantToReset;
	}

	public void LookAtSurroundings() {
		boolean sensePlaceHolder;
		boolean sensePlaceHolder1;
		Packet pck = null;
		System.out.println("Waiting for Sensor Packets");
		while(pck == null || pck.type != Packet.setObstacle) {
			pf.listen();
			System.out.println("++++++++++++++++++++++++++++++++++++++Dequeues buffer++++++++++++++++++++++++++++++++++++++++++\n");
			pck = pf.getLatestPacket();
			if(pck == null) {
				System.out.println("++++++++++++++++++++++++++++++++++++++Packet is Null (Need to Reset Instruction)+++++++++++++++++++++++++++++++++++++++++++\n");
				continue;
			}
			System.out.println(pck.getType());
			if(pck.type== Packet.ResetInstruction) {
				this.map.resetMap();
				x = 1;
				y = 18;
				facing = Direction.UP;
				this.viz.repaint();
			}
		}
		System.out.println("+++++++++++++++++++++++++++++++++++++Getting Sensor Data+++++++++++++++++++++++++++++++++++++++\n");
		int[] data = pck.getSensorData();
		for(int i=0;i < Sen.length; i++) {
			sensePlaceHolder = Sen[i].Sense(map, data[i],mapConfirmed);
			sensePlaceHolder1 = Sen[i].SenseRight(map, data[i], mapConfirmed);
			if((i<=1||i==3) && sensePlaceHolder)
				hitWallFront=true;
				//System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$hit wall front$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n");}
			else if((i==2||i==4) && sensePlaceHolder1)
				hitWallRight=true;
				//System.out.println("::::::::::::::::::::::::::::::::::::hit wall right:::::::::::::::::::::::::::::::::::::::\n");}
		}
		if(hitWallFront && hitWallRight){
			System.out.println("Sending calibration command to arduino.\n");
			front_Calibrate();
			side_Calibrate();
			hitWallFront=false;
			hitWallRight=false;
		}
		/*for(int i=0;i < Sen.length; i++) {
			Sen[i].Sense(map, data[i],mapConfirmed);// for now
		}*/
		viz.repaint();

	}


	//for testing purpose currently


	public void turnRight() {
		System.out.print("turn right robot\n");
		//use this to change direction for robot instead
		switch(facing) {
		//turn right
		case RIGHT:		
			facing = Direction.DOWN;
			break;
		case LEFT:
			facing = Direction.UP;
			break;
		case UP:
			facing = Direction.RIGHT;
			break;
		case DOWN:
			facing = Direction.LEFT;
			break;
		}


		//use this to change direction for robot instead
		for(int i=0;i < Sen.length; i++) {
			Sen[i].ChangeDirectionRight();
		}
		if(stepByStep) {
			pf.createOneMovementPacketToArduino(Packet.TURNRIGHTi);
			//update location for the robot in the sensors
			updateSensor();

			//make sensors "sense" the surrounding
			LookAtSurroundings();

		}
		viz.repaint();

		//wait for sensor update sensor data
	}
	public void turnLeft() {
		System.out.print("turn left robot\n");


		//use this to change direction for robot instead
		switch(facing) {
		case RIGHT:		
			facing = Direction.UP;
			break;
		case LEFT:
			facing = Direction.DOWN;
			break;
		case UP:
			facing = Direction.LEFT;
			break;
		case DOWN:
			facing = Direction.RIGHT;
			break;
		}
		//change sensor direction to follow robot
		for(int i=0;i < Sen.length; i++) {
			Sen[i].ChangeDirectionLeft();
		}
		if(stepByStep) {

			pf.createOneMovementPacketToArduino(Packet.TURNLEFTi);

			//update the location for the robot in the sensors
			updateSensor();
			//make sensors "sense" the surrounding
			LookAtSurroundings();


		}
		viz.repaint();

	}

	public void SenseRobotLocation() {

		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
				map.getMapArray()[y+i][x+j] = ExplorationTypes.toInt("EMPTY");
		}
	}

	public boolean getFastestInstruction(Stack<Node> fast) {
		stepByStep = false;
		int numberofPacketCalibrate = 10;
		int counttocalibrate = 0;
		Queue<Integer> instruction = new LinkedList<Integer>();
		System.out.println("starting Fastest Path");

		if (fast==null) {
			System.out.println("NULL DATA! no fastest path.");
			return false;    		
		}
		while(!fast.isEmpty()) {

			Node two = (Node) fast.pop();
			counttocalibrate++;
			System.out.println("Y" + two.getY());
			if(two.getX() > x) {
					switch(facing) {
					//turn right the fastest way
					case RIGHT:
						break;
					case LEFT:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					case UP:
						turnRight();
						instruction.add(Packet.TURNRIGHTi);
						break;
					case DOWN:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					}

			}
			else if(two.getX() < x) {
					switch(facing) {
					//turn right the fastest way
					//to face left
					case RIGHT:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					case LEFT:
						break;
					case UP:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					case DOWN:
						turnRight();
						instruction.add(Packet.TURNRIGHTi);
						break;
					}

			}
			else if(two.getY() < y) {
				//facing up
					switch(facing) {
					//to face up
					case RIGHT:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					case LEFT:
						turnRight();
						instruction.add(Packet.TURNRIGHTi);
						break;
					case UP:
						break;
					case DOWN:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					}

			}
			else if(two.getY() > y) {
				while(facing!= Direction.DOWN) {
					switch(facing) {
					//turn right the fastest way
					case RIGHT:
						turnRight();
						instruction.add(Packet.TURNRIGHTi);						
						break;
					case LEFT:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					case UP:
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						turnLeft();
						instruction.add(Packet.TURNLEFTi);
						break;
					case DOWN:
						break;
					}
				}

			}

			moveRobot();
			instruction.add(Packet.FORWARDi);
//			if(counttocalibrate >= numberofPacketCalibrate &&fastestcalibrate) {
//				if(calibrateWallFastestPath()) {
//					instruction.add(Packet.CALIBRATEi);
//					counttocalibrate = 0;
//				}
//			}

		}
		stepByStep = true;
		pf.createFullMovementPacketToArduino(instruction);//sends data.
		return true;

	}

	public boolean calibrateWallFastestPath() {
		boolean thirdflag = false;
		boolean fourthflag = false;
		boolean fifthflag = false;
		//use this to change direction for robot instead
		switch(facing) {
		case RIGHT:
			if(isBlocked(x-1, y-2))
				thirdflag = true;
			if(isBlocked(x, y-2))
				fourthflag = true;
			if(isBlocked(x+1, y-2))
				fifthflag = true;		
			break;
		case LEFT:
			if(isBlocked(x-1, y+2))
				thirdflag = true;
			if(isBlocked(x, y+2))
				fourthflag = true;
			if(isBlocked(x+1, y+2))
				fifthflag = true;
			break;
		case UP:
		if(isBlocked(x+2, y-1))
			thirdflag = true;
		if(isBlocked(x+2, y))
			fourthflag = true;
		if(isBlocked(x+2, y+1))
			fifthflag = true;
			break;
		case DOWN:
		if(isBlocked(x-2, y-1))
			thirdflag = true;
		if(isBlocked(x-2, y))
			fourthflag = true;
		if(isBlocked(x-2, y+1))
			fifthflag = true;
		break;
		}
		if(thirdflag && fourthflag && fifthflag) {
			return true;
		}
		return false;

	}
	
	public void finalIC() {
		switch(facing) {
		case RIGHT:
			break;
		case LEFT:
			turnLeft();
			turnLeft();
			break;
		case UP:
			turnRight();
			break;
		case DOWN:
			turnLeft();
			break;
		}
		pf.initialCalibrate();
	}






	public boolean doStepFastestPath()
	{
		stepByStep = true;
		//		getFastestInstruction();
		return false;
	}



	public void sideOnly_Calibrate() {
	}


	@Override
	public void side_Calibrate() {
		pf.sideCalibrate();			
		LookAtSurroundings();
	}




	@Override
	public void front_Calibrate() {
		System.out.println("front calibrating");
		pf.frontCalibrate();
		LookAtSurroundings();
	}


	@Override
	public void initial_Calibrate() {
		// TODO Auto-generated method stub
		switch(facing) {
		//make sure the robot is facing the left wall for calibration.
		case RIGHT:
			turnLeft();
			turnLeft();
			break;
		case LEFT:
			break;
		case UP:
			turnLeft();
			break;
		case DOWN:
			turnRight();
			break;

		}
		pf.initialCalibrate();
		System.out.println("#########################################initial Calibrating...#########################################");
			facing = Direction.RIGHT;
			//update the android orientation
			String instructionString2 = "AND|l#";
			pf.sc.sendPacket(instructionString2);
			pf.sc.sendPacket(instructionString2);

		viz.repaint();

	}



	@Override
	public void sendMapDescriptor() {
		pf.sendWholeMap(map);

	}


}