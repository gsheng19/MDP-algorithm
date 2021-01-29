import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


public class Map{
	int[][] SimulatedmapArray;
	int[][] mapArray;
	int[][] mapScoreArray;

	boolean turnoffgrid=false;
	boolean turnoffgrid2=false;
	boolean turnoffgrid3=false;
	
	int gridExplored= 0;
	double exploredPercentage;
	final static int WIDTH = 15;
	final static int HEIGHT = 20;
	final static int sizeofsquare = 38;
	public static Node[][] NodeArray = new Node[HEIGHT][WIDTH];

 public Map(){
   gridExplored = 0;
   this.mapArray = new int[][]{
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
		{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}
		
	};
	setScoreArray();
 }


 public Map(int[][] mapDisplay){
	//this is not used

	mapArray = mapDisplay;
	initializeNodes();
	initializeNeighbors();
	setScoreArray();

}
 
 public void setScoreArray() {
	   mapScoreArray = new int[HEIGHT][WIDTH];

		for(int y = 0 ; y < mapScoreArray.length; y++){
			for(int x = 0; x < mapScoreArray[y].length;x++){
				mapScoreArray[y][x] = 0;

	    }
	  }
		int confirmed = -1000;
		mapScoreArray[17][0] = confirmed;
		mapScoreArray[19][0] = confirmed;
		mapScoreArray[18][0] = confirmed;
		mapScoreArray[17][1] = confirmed;
		mapScoreArray[19][1] = confirmed;
		mapScoreArray[18][1] = confirmed;
		mapScoreArray[17][2] = confirmed;
		mapScoreArray[19][2] = confirmed;
		mapScoreArray[18][2] = confirmed;

		mapScoreArray[0][12] = confirmed;
		mapScoreArray[0][13] = confirmed;
		mapScoreArray[0][14] = confirmed;
		mapScoreArray[1][12] = confirmed;
		mapScoreArray[1][13] = confirmed;
		mapScoreArray[1][14] = confirmed;
		mapScoreArray[2][12] = confirmed;
		mapScoreArray[2][13] = confirmed;
		mapScoreArray[2][14] = confirmed;
		SimulatedmapArray = new int[HEIGHT][WIDTH];
		

 }

 public int[][] getMapArray() {
	return mapArray;
}


public void setMapArray(int[][] mapArray) {
	this.mapArray = mapArray;
	//SimulatedmapArray = mapArray.clone();
	initializeNodes();
	initializeNeighbors();

	for(int y = 0 ; y < SimulatedmapArray.length; y++){
		for(int x = 0; x < SimulatedmapArray[y].length;x++){
			SimulatedmapArray[y][x] = mapArray[y][x];
			//System.out.print(SimulatedmapArray[y][x]);
		}
		//System.out.print("\n");
	}
}
public void setMapScore(int x, int y, int score)
{
	//System.out.print(" X = " + x + " Y + " + y + " \n");
	mapScoreArray[y][x] += score;



}
public void updateMapWithScore()
{
	
	
	/*if(turnoffgrid)mapScoreArray[1][3] = 1;
	if(turnoffgrid2)mapScoreArray[3][3] = 2;
	if(turnoffgrid3)mapScoreArray[3][2] = 2;*/
	
	for(int y = 0 ; y < mapScoreArray.length; y++){
		for(int x = 0; x < mapScoreArray[y].length;x++){
			if(mapScoreArray[y][x] == 0)
				mapArray[y][x] = ExplorationTypes.toInt("UNEXPLORED_EMPTY");
			else if(mapScoreArray[y][x] > 0)
				mapArray[y][x] = ExplorationTypes.toInt("OBSTACLE");
			else if(mapScoreArray[y][x] < 0)
				mapArray[y][x] = ExplorationTypes.toInt("EMPTY");
     }
   }
	
	
}
public void TEMPupdatescore2(int[][] theMap)
{
	for(int y = 0 ; y < mapScoreArray.length; y++){
		for(int x = 0; x < mapScoreArray[y].length;x++){
			if(theMap[y][x] == ExplorationTypes.toInt("UNEXPLORED_EMPTY"))
				mapScoreArray[y][x] = -50;
			else if(theMap[y][x] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE"))
				mapScoreArray[y][x] = 50;
     }
   }
	
	updateMapWithScore();
}
public void updateMap() {
	initializeNodes();
	initializeNeighbors();
	calculateClearance();
}
public boolean isObstacle(int x, int y)
{
	//returns true if its a wall or its out of bounds
	if(x > WIDTH -1 || y > HEIGHT -1 || x < 0 || y < 0)
		return true;

	if(mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE") ||
		mapArray[y][x] == ExplorationTypes.toInt("OBSTACLE"))
		return true;
	else return false;
}
public void setExplored(int x, int y)
{
	if(mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_EMPTY")) {
		mapArray[y][x] = ExplorationTypes.toInt("EMPTY");
	}
	else if(mapArray[y][x] == ExplorationTypes.toInt("UNEXPLORED_OBSTACLE")) {
		mapArray[y][x] = ExplorationTypes.toInt("OBSTACLE");
	}
}

public Node getNodeXY(int x , int y) {
	return NodeArray[y][x];
}

public void initializeNodes() {
    for (int r = 0; r < HEIGHT; r++) {
        for (int c = 0; c < WIDTH; c++) {
       	 NodeArray[r][c] = new Node(c, r);
       	 if(mapArray[r][c] != 0) {
       		NodeArray[r][c].setObstacle(true);
       	 }
       	 else // if(mapArray[r][c] == 0)
       		NodeArray[r][c].setObstacle(false);
        }
    }
}

public void initializeNeighbors() {

    for (int r = 0; r < HEIGHT; r++) {
        for (int c = 0; c < WIDTH; c++) {
            if (c > 0) { //move left
                Node left = NodeArray[r][c-1];
                NodeArray[r][c].addNeighbors(left);
                NodeArray[r][c].setLeft(left);
            }
            if (c < WIDTH - 1) { //move right
                Node right = NodeArray[r][c + 1];
                NodeArray[r][c].addNeighbors(right);
                NodeArray[r][c].setRight(right);

            }
            if (r < HEIGHT-1) { // down
                Node down = NodeArray[r + 1][c];
                NodeArray[r][c].addNeighbors(down);
                NodeArray[r][c].setDown(down);
            }
            if (r > 0) { // up
                Node up = NodeArray[r - 1][c];
                NodeArray[r][c].addNeighbors(up);
                NodeArray[r][c].setUp(up);
            }
        }
    }
}

public void printClearence() {
	for (int i = 0; i < NodeArray.length; i++) {
		for (int j = 0; j < NodeArray[i].length; j++) {
			System.out.println(NodeArray[i][j].isObstacle);
		}
	}
}


public void calculateClearance() {
    Node node;
    for (int r = 0; r < HEIGHT; r++) {
        columnloop:
        for (int c = 0; c < WIDTH; c++) {
            node = NodeArray[r][c];
            node.setClearance(0);

            if (node.isObstacle()) {
                node.setClearance(0);
                continue;
            }


                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        if (r + i >= HEIGHT || r + i < 0 || c + j >= WIDTH || c + j < 0) {
                        	continue columnloop;
                        }
                        if(NodeArray[r + i][c + j].isObstacle())
                    		continue columnloop;


                    }
                }



            node.setClearance(3);

        }

    }
}


public void MapUpdate(int x, int y, int flag) {
//for receiving of Data
	if(x > 0 || y > 0 || x < WIDTH  || y < HEIGHT) {
		mapArray[y][x] = flag;
	}
}

 public void calculatePercentageExplored(){
   for(int y = 0 ; y < mapArray.length; y++){
     for(int x = 0; x < mapArray[y].length;x++){
       if(mapArray[y][x] != 2 || mapArray[y][x] != ExplorationTypes.toInt("EMPTY")){
         gridExplored++;
       }
     }
   }
   exploredPercentage = gridExplored/WIDTH*HEIGHT;
 }

 public void paintGrid(Graphics g){
   if(mapArray == null)
   paintMapGridEmpty(g);
   else{
     paintMap(g);
   }
 }
 public void setWaypointClear(int x, int y) {
		int confirmed = -9999;
		for(int i = -1; i < 2;i++) {
			for(int j = -1; j < 2; j++) {
				if(y+i < HEIGHT && x+j < WIDTH && (y+i) >=0 && (x+j) >=0)
				mapScoreArray[y+i][x+j] = confirmed;
			}
		}


 }


  public void paintMapGridEmpty(Graphics g){
    //change for actual robot
    int distanceY = 0;
    int distanceX = 0;
    for(int i=0; i < 20;i++){
      //draw Y
      distanceX = 0;
      g.drawRect(10, 10+distanceY, sizeofsquare, sizeofsquare);

        for(int j=0; j < 15;j++){
          //draw X
          g.drawRect(10+distanceX, 10+distanceY, sizeofsquare, sizeofsquare);
          distanceX += sizeofsquare;
        }
        distanceY += sizeofsquare;
    }
  }


public void paintMap(Graphics g){

	axisInit(g);
	Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    RenderingHints.VALUE_ANTIALIAS_ON);
    String mapScore = "";
      int distanceY = 0;
      int distanceX = 0;
      for(int i=0; i < mapArray.length;i++){
        //draw Y
        distanceX = 0;
        g.drawRect(10, 10+distanceY, sizeofsquare, sizeofsquare);

          for(int j=0; j < mapArray[i].length;j++){
            //draw X
            g.setColor(Color.WHITE);
            g.drawRect(10+distanceX, 10+distanceY, sizeofsquare, sizeofsquare);
            if(mapArray[i][j]==ExplorationTypes.toInt("OBSTACLE")){
            	//paint obstacle
              g.setColor(Color.BLACK);
              g.fillRect(10+distanceX, 10+distanceY, sizeofsquare, sizeofsquare);
            }
            else if((i==18 && j ==0)||(i==18 && j ==1)||(i==18 && j ==2)||
            		 (i==17 && j ==0)||(i==17 && j ==1)||(i==17 && j ==2)||
            		 (i==19 && j ==0)||(i==19 && j ==1)||(i==19 && j ==2))
            {
				//paint start location
				Color customBlue =new Color(0,89,255);
            	g.setColor(customBlue);
                g.fillRect(10+distanceX, 10+distanceY, sizeofsquare, sizeofsquare);
            }
            else if((i==0 && j ==12)||(i==0 && j ==13)||(i==0 && j ==14)||
            		(i==1 && j ==12)||(i==1 && j ==13)||(i==1 && j ==14)||
            		(i==2 && j ==12)||(i==2 && j ==13)||(i==2 && j ==14))
            {
				//paint goal location
				Color customGreen = new Color(37,215,30);
            	g.setColor(customGreen);
                g.fillRect(10+distanceX, 10+distanceY, sizeofsquare, sizeofsquare);
            }
            else if(mapArray[i][j]==ExplorationTypes.toInt("UNEXPLORED_EMPTY") || mapArray[i][j]==ExplorationTypes.toInt("UNEXPLORED_OBSTACLE")){
				Color lightBlue = new Color(144,235,243);
				g.setColor(lightBlue);
              g.fillRect(10+distanceX, 10+distanceY, sizeofsquare, sizeofsquare);
            }
            g.setColor(Color.BLACK);
            g2.drawString(Integer.toString(mapScoreArray[i][j]),20+distanceX,30+distanceY);
            distanceX += sizeofsquare;
          }
          distanceY += sizeofsquare;
      }
	}
	
	private void axisInit(Graphics g) {
    	g.drawString("19", 5, 35);
    	g.drawString("18", 5, 55);
    	g.drawString("17", 5, 75);
    	g.drawString("16", 5, 95);
    	g.drawString("15", 5, 115);
    	g.drawString("14", 5, 135);
    	g.drawString("13", 5, 155);
    	g.drawString("12", 5, 175);
    	g.drawString("11", 5, 195);
    	g.drawString("10", 5, 215);
    	g.drawString("9", 5, 235);
    	g.drawString("8", 5, 255);
    	g.drawString("7", 5, 275);
    	g.drawString("6", 5, 295);
    	g.drawString("5", 5, 315);
    	g.drawString("4", 5, 335);
    	g.drawString("3", 5, 355);
    	g.drawString("2", 5, 375);
    	g.drawString("1", 5, 395);
    	g.drawString("0", 5, 415);
    	
    	g.drawString("0", 25, 435);
    	g.drawString("1", 45, 435);
    	g.drawString("2", 65, 435);
    	g.drawString("3", 85, 435);
    	g.drawString("4", 105, 435);
    	g.drawString("5", 125, 435);
    	g.drawString("6", 145, 435);
    	g.drawString("7", 165, 435);
    	g.drawString("8", 185, 435);
    	g.drawString("9", 205, 435);
    	g.drawString("10", 225, 435);
    	g.drawString("11", 245, 435);
    	g.drawString("12", 265, 435);
    	g.drawString("13", 285, 435);
    	g.drawString("14", 305, 435);
    }

	public void generateMapDescriptorExplored() {
		//for generating MapDescriptorExplored.
		int[][] ExploredMap = new int[HEIGHT][WIDTH];
		for(int y = 0; y < HEIGHT; y++) {
			for(int x = 0; x < WIDTH; x++) {
				if(mapArray[y][x] != ExplorationTypes.toInt("UNEXPLORED_EMPTY")||mapArray[y][x] != ExplorationTypes.toInt("UNEXPLORED_OBSTACLE")) {
					ExploredMap[y][x] = 1;
				}
				else
					ExploredMap[y][x] = 0;
			}
		}
		MapIterator.printExploredResultsToFile(ExploredMap, "ExploredMapDescriptor.txt");
	}

	public void generateMapDescriptor() {
		MapIterator.printExploredResultsToFile(mapArray, "Map.txt");
	}

	public void setEmpty() {
		this.mapArray = new int[][]{
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
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};

	}

	public void resetMap() {
		// TODO Auto-generated method stub
		this.mapArray = new int[][]{
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
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};
		setScoreArray();
		updateMap();
	}

}