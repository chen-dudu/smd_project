package mycontroller;

import controller.CarController;
import mycontroller.adapters.TileType;
import world.Car;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import mycontroller.adapters.AdapterFactory;
import world.WorldSpatial.Direction;

public class MyAutoController extends CarController{
	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 1;
	private ArrayList<Coordinate> path;
	private int action_count = 1;

	private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.

	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;

	public MyAutoController(Car car) {
		super(car);
		Coordinate start;
		Coordinate destination;

		HashMap<Coordinate,MapTile> map = getMap();
		ArrayList<Coordinate> wall = new ArrayList<>();
		AdapterFactory factory = AdapterFactory.getInstance();
		Dijkstra dijkstra = new Dijkstra();
		MapTile tile;
		for (Coordinate coordinate: map.keySet()){
			tile = map.get(coordinate);
			if (factory.getAdapter(tile).getType(tile) == TileType.START) {
				start = coordinate;
				dijkstra.setStart_coordinate(start);
			} else if (factory.getAdapter(tile).getType(tile) == TileType.FINISH) {
				destination = coordinate;
				dijkstra.setDestination_coordinate(destination);
			} else if (factory.getAdapter(tile).getType(tile) == TileType.WALL){
				wall.add(coordinate);
			}
		}
		System.out.print(dijkstra.getStart_coordinate().toString());
		System.out.print(dijkstra.getDestination_coordinate().toString());
		dijkstra.setWall(wall);
		this.path = dijkstra.run();

		//			for (Coordinate coordinate: result.keySet()){
		//				Coordinate c = result.get(coordinate);
		//				System.out.print(coordinate.toString());
		//				System.out.print(c.toString());
		//			}
	}

	// Coordinate initialGuess;
	// boolean notSouth = true;
	@Override
	public void update() {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		System.out.println("action--");
		// checkStateChange();
		if (action_count == 1) {
			System.out.println("speed up--");
			if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
				applyForwardAcceleration();   // Tough luck if there's a wall in the way
			}
		} else {
			System.out.println("follow path--");
			follow_path(path);
		}
		action_count += 1;
	}

	private void follow_path(ArrayList<Coordinate> path) {
		Coordinate current_coordinate = new Coordinate(getPosition());
		Coordinate next_coordiante;
		Direction direction;
		boolean make_action = false;
		for (Coordinate coordinate: path){
			if (make_action){
				System.out.println(coordinate);
				next_coordiante = coordinate;
				direction = next_action_direction(current_coordinate, next_coordiante);
				make_action(direction);
				break;
			}
			if (coordinate.equals(current_coordinate)){
				make_action = true;
			}


		}
	}

	private void make_action(Direction next){
		Direction current = getOrientation();
		if (current == Direction.EAST) {
			if (next == Direction.SOUTH) {
				turnRight();
			} else if (next == Direction.NORTH) {
				turnLeft();
			}
		} else if (current == Direction.SOUTH) {
			if (next == Direction.WEST) {
				turnRight();
			} else if (next == Direction.EAST) {
				turnLeft();
			}
		} else if (current == Direction.WEST) {
			if (next == Direction.NORTH) {
				turnRight();
			} else if (next == Direction.SOUTH) {
				turnLeft();
			}
		} else if (current == Direction.NORTH) {
			if (next == Direction.EAST) {
				turnRight();
			} else if (next == Direction.WEST) {
				turnLeft();
			}
		}
	}

	private Direction next_action_direction(Coordinate current, Coordinate next){
//		int count = 1;
//		Coordinate start_coordinate = current;
//		Coordinate next_coordinate = new Coordinate("0,0");
//		for (Coordinate coordinate: path){
//			if (count == 1){
//				start_coordinate = coordinate;
//			} else if (count == 2){
//				next_coordinate = coordinate;
//			}
//			count += 1;
//		}
		int dx, dy;
		dx = next.x - current.x;
		dy = next.y - current.y;
		if (dx == 1 && dy == 0) {
			return Direction.EAST;
		} else if (dx == 0 && dy == 1) {
			return Direction.NORTH;
		} else if (dx == 0 && dy == -1) {
			return Direction.SOUTH;
		} else if (dx == -1 && dy == 0) {
			return Direction.WEST;
		}
		return Direction.EAST;
	}

//	public void update() {
//		// Gets what the car can see
//		HashMap<Coordinate, MapTile> currentView = getView();
//
//		// checkStateChange();
//		if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
//			applyForwardAcceleration();   // Tough luck if there's a wall in the way
//		}
//		if (isFollowingWall) {
//			// If wall no longer on left, turn left
//			if(!checkFollowingWall(getOrientation(), currentView)) {
//				turnLeft();
//			} else {
//				// If wall on left and wall straight ahead, turn right
//				if(checkWallAhead(getOrientation(), currentView)) {
//					turnRight();
//				}
//			}
//		} else {
//			// Start wall-following (with wall on left) as soon as we see a wall straight ahead
//			if(checkWallAhead(getOrientation(),currentView)) {
//				turnRight();
//				isFollowingWall = true;
//			}
//		}
//	}

	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
		switch(orientation){
			case EAST:
				return checkEast(currentView);
			case NORTH:
				return checkNorth(currentView);
			case SOUTH:
				return checkSouth(currentView);
			case WEST:
				return checkWest(currentView);
			default:
				return false;
		}
	}

	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {

		switch(orientation){
			case EAST:
				return checkNorth(currentView);
			case NORTH:
				return checkWest(currentView);
			case SOUTH:
				return checkEast(currentView);
			case WEST:
				return checkSouth(currentView);
			default:
				return false;
		}
	}

	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}

	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}

	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}

	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}

}
