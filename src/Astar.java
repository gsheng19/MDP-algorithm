import java.util.*;

public class Astar {
	Node startNode;
	Node goalNode;	
	Robot robot;
	
	int cost;
	public Astar(Node start, Node end) {
		startNode = start;
		goalNode = end;
		cost = 0;
	}
	public int getCost() {return cost;};
	
    public static class PriorityList extends LinkedList {
        public void add(Comparable object) {
            //Insertion sort
            for (int i = 0; i < size(); i++){
                if (object.compareTo(get(i)) <= 0) {
                    add(i, object);
                    return;
                }
            }
            addLast(object);
        }
    }

    protected Stack<Node> constructPath(Node node) {
        Stack path = new Stack();
        while (node.pathParent != null) {
            path.push(node);
            node = node.pathParent;
            cost++;
        }
        return path;
    }

    public Stack<Node> findPath(Node startNode, Node goalNode) {
        int size = 3;

        PriorityList openList = new PriorityList();
        LinkedList closedList = new LinkedList();

        startNode.costFromStart = 0;
        startNode.estimatedCostToGoal = startNode.getEstimatedCost(goalNode);
        startNode.pathParent = null;
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node node = (Node)openList.removeFirst();
            ((Node)node).setFacing();


            if (node == goalNode) {
                return constructPath(goalNode);
            }
            List neighbors = node.getNeighbors();
            for (int i = 0; i < neighbors.size(); i++) {
                Node neighborNode = (Node)neighbors.get(i);
                boolean isOpen = openList.contains(neighborNode);
                boolean isClosed = closedList.contains(neighborNode);
                 boolean isObstacle = (neighborNode).isObstacle();
                int clearance = neighborNode.getClearance();
                float costFromStart = node.getCost(neighborNode,goalNode) + 1;
                //check if the neighbors has not been traversed OR
                //if a shorter path to the neighbor node is found
                if ((!isOpen && !isClosed) || costFromStart < neighborNode.costFromStart) {
                    neighborNode.pathParent = node;
                    neighborNode.costFromStart = costFromStart;
                    neighborNode.estimatedCostToGoal = neighborNode.getEstimatedCost(goalNode);

//                    Node testNode = neighborNode;
//                    while (testNode.pathParent!= null) {
//                        //System.out.println("X: " + ((Node)testNode.pathParent).getX() + "\nY: " + ((Node)testNode.pathParent).getY());
//                        testNode = testNode.pathParent;
//                    }
                    if (!isOpen && !isObstacle && size == clearance) {
                        openList.add(neighborNode);
                    }
                }

                //System.out.println();
            }
            closedList.add(node);
        }
        //OpenList is empty; no path is found
    	System.out.println("NULL DATA! no fastest path.");
        return new Stack<Node>();
    }

    public Facing getRobotDirection(Node node, Facing facing) {

        Node nodeparent = (Node)node.pathParent;

        if(nodeparent == null) {
            return facing;
        }

        // if nodeX of parent > nodeX of currentNode, means
        // ------current parent -------- <- move left
        if(node.compareX(nodeparent) == 1) {
            return Facing.LEFT;
        }
        else if(node.compareX(nodeparent) == -1) {
            return Facing.RIGHT;
        }
        else if(node.compareY(nodeparent) == 1) {
            return Facing.DOWN;
        }
        else if(node.compareY(nodeparent) == -1) {
            return Facing.UP;
        }

        return null;
    }



      public Stack<Node> getFastestPath(){
            return findPath(startNode, goalNode);
      }
      


}