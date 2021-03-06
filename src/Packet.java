public class Packet{	
	//for mapping amount of instructions together

	final static String ARDUINO = "ARD";  // A
	final static String ANDROID = "AND";  // B
	final static String PC = "PC"; //P
	final static String RPI = "RPI";  //R
	
	
	
	//packet types
	final static int StartExploration = 1;
	final static int StartFastestPath = 2;
	final static int StopInstruction = 3;
	final static int ResetInstruction = 4;
	final static int SetWayPointi = 5;
	final static int setRobotPosition = 6;
	final static int setObstacle = 7;
	final static int takePhoto = 8;
	
	
	//instruction to map to packet Instruction.
	final static int FORWARDi = 1;
	final static int TURNRIGHTi = 2;
	final static int TURNLEFTi = 3;
	final static int REVERSEi = 4;
	public static final int CALIBRATEi = 5;
	final static int PHOTOi = 6;


	
	//for movement of the robot.
	final static String FORWARD = "f";
	final static String TURNRIGHT = "r";
	final static String TURNLEFT = "l";
	final static String REVERSE = "v";
	
	//start exploration from android
	final static String Ok = "ok";
	final static String Cmd = "cmd";
	final static String Set = "set";
	final static String Splitter = "|";  // :
	final static String Stat = "stat";

	
	final static String StartExplorationType = "explore"; 
	final static String StartExplorationTypeOkARD = "ARD|start_explore";  //A:ok:start_explore
	final static String StartExplorationTypeOk = "AND|start_explore";  //B:ok:start_explore
	final static String StartExplorationTypeFin = "AND|finish_explore"; //after going back to start location  // B:ok:finish_explore

	//start fastestpath  from android
	final static String StartFastestPathType = "path"; //with waypoint from android
	final static String StartFastestPathTypeOk = "start_path"; //send to android
	final static String StartFastestPathTypeFin = "finish_path"; //send to android
	final static String startExplore = ANDROID + Splitter + StartExplorationTypeOk;

	
	//stop the robot from android
	final static String Stop = "stop";//send from android
	final static String StopOk = "AND|stop";//send to android   // B:ok:stop

	final static String Reset = "reset"; //from android
	final static String ResetOK = "AND|reset";//send to android  // B:ok:reset

	//need to process this string to become x y coordinate of robot in map
	final static String SetRobotPos = "startposition";
	final static String SetRobotPosOk = "AND|startposition";  // B:ok:startposition
	final static String SetWayPoint = "waypoint";
	final static String SetWayPointOK = "ok|waypoint";  // ok:waypoint
	
	//for map obstacle
	final static String Map = "map";
	final static String Block = "block";
	
	final static String TURNLEFTCMD = ARDUINO + Splitter + TURNLEFT;
	final static String TURNRIGHTCMD =ARDUINO + Splitter + TURNRIGHT;
	final static String FORWARDCMD = ARDUINO + Splitter + FORWARD;
	final static String REVERSECMD = ARDUINO + Splitter + REVERSE;

	final static String TURNLEFTCMDANDROID = ANDROID + Splitter + TURNLEFT;
	final static String TURNRIGHTCMDANDROID =ANDROID + Splitter + TURNRIGHT;
	final static String FORWARDCMDANDROID = ANDROID + Splitter +  FORWARD;
	final static String REVERSECMDANDROID =  ANDROID + Splitter + REVERSE;

	final static String SIDECALIBRATE = "";  //A:cmd:sc$
	final static String LEFTCALIBRATE = "";  //A:cmd:lsc
	final static String FRONTCALIBRATE = "";  //A:cmd:fc
	final static String INITIALCALIBRATE = "";  //A:cmd:ic
	final static String SIDETURNCALIBRATE = "";  //A:cmd:frontc
	final static String MAPDESCRIPTORCMDRPI = "RPI|MAP#";
	final static String STOPHUG = "ARD|STOPHUG#";

	final static String ACKKNOWLEDGE = "PC|ack"; //P:cmd:ack
	
	final static String MAPDESCRIPTORCMD = "AND|SETMAP:"; //B:map:set:

	final static int GETMAPi = 10;
	
	final static String GETMAP ="getmap";
	

	final static String StartFastestPathTypeOkANDROID = "AND|start_path"; //send to android //A:ok:start_path
	final static String StartFastestPathTypeOkARDURINO = "ARD|start_path"; //send to arduino  //B:ok:start_path





	int type = 0;
	int x = 0;
	int y = 0;
	Direction Direction = null ;
	int[] SensorData = null;
	
	
	public Packet(int type) {
		//for packet with only type 
		this.type = type;
	}
	
	
	public Packet(int type, int x, int y) {
		//for waypoint packets 
		this.type = type;
		this.x = x;
		this.y = y;
	}


	public Packet(int type, int x, int y, Direction direction) {
		//for robot position with direction
		this.type = type;
		this.x = x;
		this.y = y;
		Direction = direction;
	}
	

	

	public Packet(int type, int x, int y, Direction direction, int[] sensorData) {
		//for map obstacle data
		super();
		this.type = type;
		this.x = x;
		this.y = y;
		Direction = direction;
		SensorData = sensorData;
	}


	public Packet(int setobstacle2, int[] data) {
		// TODO Auto-generated constructor stub
		this.type = setobstacle2;
		this.SensorData = data;
	}


	public int[] getSensorData() {
		return SensorData;
	}


	public void setSensorData(int[] sensorData) {
		SensorData = sensorData;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public Direction getDirection() {
		return Direction;
	}


	public void setDirection(Direction direction) {
		Direction = direction;
	}
	
	
}