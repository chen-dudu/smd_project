package mycontroller;

import controller.CarController;
import world.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import tiles.MapTile;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

/**
 * Team: W9-5
 * Description:
 */
public class MyAutoController extends CarController{
		// How many minimum units the wall is away from the player.
		private int wallSensitivity = 1;

		private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.

		// Car Speed to move at
		private final int CAR_MAX_SPEED = 1;

		// array seems to be easier to use
		private MapTile[][] seenWorld;

		AdapterFactory factory;


		public MyAutoController(Car car) {
			super(car);
			seenWorld = new MapTile[World.MAP_WIDTH][World.MAP_HEIGHT];
			HashMap<Coordinate, MapTile> emptyWorld = getMap();
			for(Coordinate point: emptyWorld.keySet()) {
				seenWorld[point.x][point.y] = emptyWorld.get(point);
			}
			factory = AdapterFactory.getInstance();
		}

		// Coordinate initialGuess;
		// boolean notSouth = true;
		@Override
		public void update() {

			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();

			// choose between entrySet and keySet, don't know how to use entrySet
			for(Coordinate point: currentView.keySet()) {
				if(0 <= point.x && point.x < World.MAP_WIDTH && 0 <= point.y && point.y < World.MAP_HEIGHT) {
					seenWorld[point.x][point.y] = currentView.get(point);
				}
			}

			ArrayList<Coordinate> destination = new ArrayList<>();
			for(int i = 0; i < seenWorld.length; i++) {
				for(int j = 0; j < seenWorld[i].length; j++) {
					if(seenWorld[i][j].isType(MapTile.Type.FINISH)) {
						destination.add(new Coordinate(i, j));
					}
				}
			}

			Coordinate currentPosition = new Coordinate(getPosition());
			Set<Coordinate> choices = currentView.keySet();
			Coordinate bestChoice = null;
			for(Coordinate next: choices) {
				if(getType(currentView.get(next)) != TileType.WALL) {
					bestChoice = next;
					break;
				}
			}
			for(int i = 0; i < choices.size(); i++) {

			}

			for(Coordinate next: choices) {
				if(getType(currentView.get(next)) != TileType.WALL) {
					if (evaluate(next, destination) <= evaluate(bestChoice, destination)) {
						bestChoice = next;
					}
				}
			}

			// checkStateChange();
			if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
				applyForwardAcceleration();   // Tough luck if there's a wall in the way
			}

			makeAction(currentPosition, bestChoice);
		}

		private TileType getType(MapTile tile) {
			return factory.getAdapter(tile).getType(tile);
		}

		private void makeAction(Coordinate curr, Coordinate goal) {
			if(curr.x < goal.x) {
				if(getOrientation() == WorldSpatial.Direction.NORTH) {
					turnRight();
					applyForwardAcceleration();
				}
				else if(getOrientation() == WorldSpatial.Direction.SOUTH) {
					turnLeft();
					applyForwardAcceleration();
				}
				else if(getOrientation() == WorldSpatial.Direction.WEST) {
					applyReverseAcceleration();
				}
				else {
					applyForwardAcceleration();
				}
			}
			else if(curr.x > goal.x) {
				if(getOrientation() == WorldSpatial.Direction.NORTH) {
					turnLeft();
					applyForwardAcceleration();
				}
				else if(getOrientation() == WorldSpatial.Direction.SOUTH) {
					turnRight();
					applyForwardAcceleration();
				}
				else if(getOrientation() == WorldSpatial.Direction.WEST) {
					applyForwardAcceleration();
				}
				else {
					applyReverseAcceleration();
				}
			}
			else if(curr.y < goal.y) {
				if(getOrientation() == WorldSpatial.Direction.NORTH) {
					applyForwardAcceleration();
				}
				else if(getOrientation() == WorldSpatial.Direction.SOUTH) {
					applyReverseAcceleration();
				}
				else if(getOrientation() == WorldSpatial.Direction.WEST) {
					turnRight();
					applyForwardAcceleration();
				}
				else {
					turnLeft();
					applyForwardAcceleration();
				}

			}
			else {
				if(getOrientation() == WorldSpatial.Direction.NORTH) {
					applyReverseAcceleration();
				}
				else if(getOrientation() == WorldSpatial.Direction.SOUTH) {
					applyForwardAcceleration();
				}
				else if(getOrientation() == WorldSpatial.Direction.WEST) {
					turnLeft();
					applyForwardAcceleration();
				}
				else {
					turnRight();
					applyForwardAcceleration();
				}
			}
		}

		// greedy search for now (consider dist to des only), change to a* at the end
		private int evaluate(Coordinate p1, ArrayList<Coordinate> des) {
			int minDist = World.MAP_WIDTH + World.MAP_HEIGHT;
			int currDist;
			for (Coordinate next : des) {
				currDist = manhattan_dist(p1, next);
				if (currDist < minDist) {
					minDist = currDist;
				}
			}
			return minDist;
		}

		private int manhattan_dist(Coordinate point1, Coordinate point2) {
			return Math.abs(point1.x - point2.x) + Math.abs(point1.y - point2.y);
		}

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
