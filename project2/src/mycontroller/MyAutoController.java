package mycontroller;

import controller.CarController;
import mycontroller.strategies.CarState;
import mycontroller.strategies.ControllerStrategyFactory;
import sun.plugin.dom.core.CoreConstants;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial.Direction;

import mycontroller.strategies.*;
import mycontroller.adapters.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAutoController extends CarController{
	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 1;

	private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.

	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;

	private CarState state;
	private ControllerStrategyFactory strategyFactory;
	private AdapterFactory adapterFactory;
//	private iControllerStrategy strategy;
	private ArrayList<Coordinate> destinations;

	private float fuel;
	private Coordinate parcelPos;
	private int turn;

	private HashMap<CarState, iControllerStrategy> controllerStrategies;

	// 0 for not explored, 1 for yes
	private Integer[][] exploreMap;

	private Coordinate prevPos;

	public MyAutoController(Car car) {
		super(car);
		fuel = car.getFuel();
		state = CarState.EXPLORING;
		strategyFactory = ControllerStrategyFactory.getInstance();
		adapterFactory = AdapterFactory.getInstance();
		destinations = new ArrayList<>();
		controllerStrategies = new HashMap<>();
		HashMap<Coordinate, MapTile> emptyMap = getMap();
		for(Coordinate next: emptyMap.keySet()) {
			if(emptyMap.get(next).isType(MapTile.Type.FINISH)) {
				destinations.add(next);
			}
		}

		controllerStrategies.put(state, strategyFactory.getStrategy(state, getMap(), SearchAlgorithmType.Dijkstra, destinations.get(0)));
		controllerStrategies.get(state).updateMap(new Coordinate(getPosition()));
		exploreMap = new Integer[World.MAP_WIDTH][World.MAP_HEIGHT];
		for(int i = 0; i < exploreMap.length; i++) {
			for(int j = 0; j < exploreMap[i].length; j++) {
				exploreMap[i][j] = 0;
			}
		}
		for(Coordinate next: emptyMap.keySet()) {
			if(emptyMap.get(next).isType(MapTile.Type.WALL)) {
				System.out.println(next);
				exploreMap[next.x][next.y] = 1;
			}
		}
		parcelPos = null;
		turn = 0;
		prevPos = null;
	}

	// Coordinate initialGuess;
	// boolean notSouth = true;
	@Override
	public void update() {

//		System.out.println(">>>>>>>>>>>>>>>>" + exploreMap[0][0]);
		turn++;
		System.out.println(World.MAP_WIDTH + " -- " + World.MAP_HEIGHT);
		HashMap<Coordinate, MapTile> map = getView();
		Coordinate currPos = new Coordinate(getPosition());

		for(Coordinate next: map.keySet()) {
			if(0 <= next.x && next.x < World.MAP_WIDTH &&
					0 <= next.y && next.y < World.MAP_HEIGHT) {
				exploreMap[next.x][next.y] = 1;
			}
		}

		if(destinations.contains(currPos)) {
			applyBrake();
			return;
		}

//		if(currPos.equals(prevPos)) {
//			applyReverseAcceleration();
//			return;
//		}

		for(Coordinate next: map.keySet()) {
			MapTile tile = map.get(next);
			if(adapterFactory.getAdapter(tile).getType(tile) == TileType.PARCEL && numParcelsFound() < numParcels()) {
				parcelPos = next;
				updateState(CarState.COLLECTING);
				break;
			}
		}

		if(currPos.equals(parcelPos)) {
			System.out.println("~~~~~~~~~`");
			parcelPos = null;
			updateState(CarState.EXPLORING);
		}

		if(numParcels() == numParcelsFound()) {
			updateState(CarState.EXITING);
		}

		if(parcelPos != null) {
			if(!controllerStrategies.containsKey(state)) {
				controllerStrategies.put(state, strategyFactory.getStrategy(state, getMap(), SearchAlgorithmType.Dijkstra, parcelPos));
			}
		}
		else {
			if(!controllerStrategies.containsKey(state)) {
				controllerStrategies.put(state, strategyFactory.getStrategy(state, getMap(), SearchAlgorithmType.Dijkstra, destinations.get(0)));
			}
		}

		Coordinate nextPos = controllerStrategies.get(state).getNextPosition(fuel, currPos, parcelPos, getMap(), exploreMap);

		System.out.print(currPos + " -> " + nextPos);
		System.out.println();


		if(getSpeed() < CAR_MAX_SPEED && turn < 2) {
			applyForwardAcceleration();
		} else {
			makeMove(currPos, nextPos);
		}
		prevPos = currPos;
		controllerStrategies.get(state).updateMap(currPos);
		System.out.println(state);
		fuel--;
	}

	private void updateState(CarState state) {
		this.state = state;
	}

	private void makeMove(Coordinate start, Coordinate des) {
		Direction direction = getOrientation();
		if(start.x < des.x) {
			if(direction == Direction.NORTH) {
				turnRight();
//				applyForwardAcceleration();
			}
			else if(direction == Direction.SOUTH) {
				turnLeft();
//				applyForwardAcceleration();
			}
			else if(direction == Direction.WEST) {
				if(getSpeed() > 0) {
					applyBrake();
				}
				else {
					applyReverseAcceleration();
				}
			}
			else {
				applyForwardAcceleration();
			}
		}
		else if(start.x > des.x) {
			if(direction == Direction.NORTH) {
				turnLeft();
//				applyForwardAcceleration();
			}
			else if(direction == Direction.SOUTH) {
				turnRight();
//				applyForwardAcceleration();
			}
			else if(direction == Direction.WEST) {
				applyForwardAcceleration();
			}
			else {
				if(getSpeed() > 0) {
					applyBrake();
				}
				else {
					applyReverseAcceleration();
				}
			}
		}
		else if(start.y < des.y) {
			if(direction == Direction.NORTH) {
				applyForwardAcceleration();
			}
			else if(direction == Direction.SOUTH) {
				if(getSpeed() > 0) {
					applyBrake();
				}
				else {
					applyReverseAcceleration();
				}
			}
			else if(direction == Direction.WEST) {
				turnRight();
//				applyForwardAcceleration();
			}
			else {
				turnLeft();
//				applyForwardAcceleration();
			}
		}
		else if(start.y > des.y) {
			if(direction == Direction.NORTH) {
				if(getSpeed() > 0) {
					applyBrake();
				}
				else {
					applyReverseAcceleration();
				}
			}
			else if(direction == Direction.SOUTH) {
				applyForwardAcceleration();
			}
			else if(direction == Direction.WEST) {
				turnLeft();
//				applyForwardAcceleration();
			}
			else {
				turnRight();
//				applyForwardAcceleration();
			}
		}
	}

	private void makeAction(Coordinate current, Coordinate next){
		int dx, dy, delta = 1;
		Direction direction;
		direction = Direction.EAST;
		dx = next.x - current.x;
		dy = next.y - current.y;
		if (dx == delta && dy == 0) {
			direction = Direction.EAST;
		} else if (dx == 0 && dy == delta) {
			direction = Direction.NORTH;
		} else if (dx == 0 && dy == -delta) {
			direction = Direction.SOUTH;
		} else if (dx == -delta && dy == 0) {
			direction = Direction.WEST;
		}


		Direction current_direction = getOrientation();
		if (current_direction == Direction.EAST) {
			if (direction == Direction.SOUTH) {
				turnRight();
			} else if (direction == Direction.NORTH) {
				turnLeft();
			}
		} else if (current_direction == Direction.SOUTH) {
			if (direction == Direction.WEST) {
				turnRight();
			} else if (direction == Direction.EAST) {
				turnLeft();
			}
		} else if (current_direction == Direction.WEST) {
			if (direction == Direction.NORTH) {
				turnRight();
			} else if (direction == Direction.SOUTH) {
				turnLeft();
			}
		} else if (current_direction == Direction.NORTH) {
			if (direction == Direction.EAST) {
				turnRight();
			} else if (direction == Direction.WEST) {
				turnLeft();
			}
		}
	}
}
