//need check data

public class RealSensor extends Sensor{

	
	public RealSensor(int range, SensorLocation currentDirection, int locationOnRobot_x, int locationOnRobot_y,
			int robot_x, int robot_y) {
		super(range, currentDirection, locationOnRobot_x, locationOnRobot_y, robot_x, robot_y);
		// TODO Auto-generated constructor stub
	}
	
	public boolean SenseLocation(Map map, int x, int y, int distanceFromRobot, boolean hitWall){
		int score = 0;

		if(distanceFromRobot == 1)
			score = -34;
		else if(distanceFromRobot == 2)
			score = -21;
		else if(distanceFromRobot == 3)
			score = -8;
		else if(distanceFromRobot == 4)
			score = -5;
		else if(distanceFromRobot == 5)
			score = -2;
		else score = 0;
		
			
		if(x < Map.WIDTH && y < Map.HEIGHT && x >= 0 && y >= 0)
		{
			//flip the score to positive to indicate that it is a block
			if(hitWall)
				score = -score;
			
			map.setMapScore(x,y,score);
		}
		
		return hitWall;
	}
	public boolean Sense(Map map, int data, int[][] mapConfirmed) {
		int nextLocationX = 0;
		int nextLocationY = 0;
		//boolean flag = false;
		
		//is true after robot hits a wall, to prevent it from sensing further
		boolean hitWall = false;
		boolean hitWallret = false;
		
		for(int i = 1; i <= range; i++) {
			
			//make sure it is in the map range and bound.
			if(currentDirection ==SensorLocation.FACING_RIGHT) {
				nextLocationX = robot_x+locationOnRobot_x+i;
				nextLocationY = robot_y+locationOnRobot_y;
			}
			
			else if(currentDirection == SensorLocation.FACING_LEFT) {
				nextLocationX = robot_x+locationOnRobot_x-i;
				nextLocationY = robot_y+locationOnRobot_y;
			}
			else if(currentDirection == SensorLocation.FACING_TOP) {
				nextLocationX = robot_x+locationOnRobot_x;
				nextLocationY = robot_y+locationOnRobot_y-i;
			}
			else{
				nextLocationX = robot_x+locationOnRobot_x;
				nextLocationY = robot_y+locationOnRobot_y+i;
			}
			
			//hitwill will be true when sensor sensed a wall
			if(!hitWall)
			{
				//when the sensor sensed a wall, then everything after that will be given score 0
				if(i == data){
					hitWall = true;
					if(SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall) && i==1)
						hitWallret=true;
					//hitWallret = true;
				}
				SenseLocation(map,nextLocationX, nextLocationY, i, hitWall);
			}
			//send a 0 to signify that this is behind a wall
			else
				SenseLocation(map,nextLocationX, nextLocationY, 0, hitWall);
			}
			
			//update the map score after "sensing"
			map.updateMapWithScore();
		
			return hitWallret;
			
			
			
			
			
			
			//if(nextLocationX < 0 || nextLocationY < 0 || nextLocationX >= Map.WIDTH || nextLocationY >= Map.HEIGHT)
			//	return;
//			if(map.mapArray[nextLocationY][nextLocationX] == ExplorationTypes.toInt("OBSTACLE") )
//				return;
			
//			if(flag && mapConfirmed[nextLocationY][nextLocationX] > 3) {
//				//remove anything behind a block
//				map.MapUpdate(nextLocationX, nextLocationY, ExplorationTypes.toInt("UNEXPLORED_EMPTY"));
//				continue;
//			}
			
			//set empty block
			/*if(i == 1 ) {
				if(data == 1) {
					map.MapUpdate(nextLocationX, nextLocationY, ExplorationTypes.toInt("OBSTACLE"));
				}else
					map.MapUpdate(nextLocationX, nextLocationY, ExplorationTypes.toInt("EMPTY"));
				if((mapConfirmed[nextLocationY][nextLocationX] != 1)) {
					map.MapUpdate(nextLocationX, nextLocationY, ExplorationTypes.toInt("OBSTACLE"));
					//set obstacle
					mapConfirmed[nextLocationY][nextLocationX] = data;
					flag = true;
					return;
				}

			}
			//can move up. why check again?? alamak waste time
			//NOTE to change . optimize. but dont touch now cause dont want fuck it up tmr
/////////////////// need change
			if((mapConfirmed[nextLocationY][nextLocationX] != 1)) {
//			if not immutabled block, set empty
				map.MapUpdate(nextLocationX, nextLocationY, ExplorationTypes.toInt("EMPTY"));
				continue;
			}
			if(map.mapArray[nextLocationY][nextLocationX] == 1)
				flag = true;
*/
			//if the next location is not equal to an obstacle then sense, else no

	}

	public boolean SenseRight(Map map, int data, int[][] mapConfirmed) {
		int nextLocationX = 0;
		int nextLocationY = 0;
		//boolean flag = false;
		
		//is true after robot hits a wall, to prevent it from sensing further
		boolean hitWall = false;
		boolean hitWallret = false;
		
		for(int i = 1; i <= range; i++) {
			
			//make sure it is in the map range and bound.
			if(currentDirection ==SensorLocation.FACING_RIGHT) {
				nextLocationX = robot_x+locationOnRobot_x+i;
				nextLocationY = robot_y+locationOnRobot_y;
			}
			
			else if(currentDirection == SensorLocation.FACING_LEFT) {
				nextLocationX = robot_x+locationOnRobot_x-i;
				nextLocationY = robot_y+locationOnRobot_y;
			}
			else if(currentDirection == SensorLocation.FACING_TOP) {
				nextLocationX = robot_x+locationOnRobot_x;
				nextLocationY = robot_y+locationOnRobot_y-i;
			}
			else{
				nextLocationX = robot_x+locationOnRobot_x;
				nextLocationY = robot_y+locationOnRobot_y+i;
			}
			
			//hitwill will be true when sensor sensed a wall
			if(!hitWall)
			{
				//when the sensor sensed a wall, then everything after that will be given score 0
				if(i == data){
					hitWall = true;
					if(SenseLocation(map, nextLocationX, nextLocationY, 0, hitWall) && i==2)
						hitWallret=true;
					//hitWallret = true;
				}
				SenseLocation(map,nextLocationX, nextLocationY, i, hitWall);
			}
			//send a 0 to signify that this is behind a wall
			else
				SenseLocation(map,nextLocationX, nextLocationY, 0, hitWall);
			}
			
			//update the map score after "sensing"
			map.updateMapWithScore();
		
			return hitWallret;
		}
}