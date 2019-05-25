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
import world.WorldSpatial;

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
	private iControllerStrategy strategy;
	private ArrayList<Coordinate> destinations;

	private float fuel;
	private Coordinate parcelPos;

	// 0 for not explored, 1 for yes
	private Integer[][] exploreMap;

	public MyAutoController(Car car) {
		super(car);
		fuel = car.getFuel();
		state = CarState.EXPLORING;
		strategyFactory = ControllerStrategyFactory.getInstance();
		adapterFactory = AdapterFactory.getInstance();
		destinations = new ArrayList<>();
		HashMap<Coordinate, MapTile> emptyMap = getMap();
		for(Coordinate next: emptyMap.keySet()) {
			if(emptyMap.get(next).isType(MapTile.Type.FINISH)) {
				destinations.add(next);
			}
		}
		strategy = strategyFactory.getStrategy(state, getMap(), SearchAlgorithmType.Dijkstra, destinations.get(0));
		strategy.updateMap(new Coordinate(getPosition()));
		exploreMap = new Integer[World.MAP_WIDTH][World.MAP_HEIGHT];
		for(int i = 0; i < exploreMap.length; i++) {
			for(int j = 0; j < exploreMap[i].length; j++) {
				exploreMap[i][j] = 0;
			}
		}
		parcelPos = null;
	}

	// Coordinate initialGuess;
	// boolean notSouth = true;
	@Override
	public void update() {
		System.out.println(World.MAP_WIDTH + " -- " + World.MAP_HEIGHT);
		HashMap<Coordinate, MapTile> map = getView();
		Coordinate currPos = new Coordinate(getPosition());
//		Coordinate goal;

		for(Coordinate next: map.keySet()) {
			MapTile tile = map.get(next);
			if(adapterFactory.getAdapter(tile).getType(tile) == TileType.PARCEL) {
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

		if(parcelPos != null) {
			strategy = strategyFactory.getStrategy(state, getMap(), SearchAlgorithmType.Dijkstra, parcelPos);
		}
		else {
			strategy = strategyFactory.getStrategy(state, getMap(), SearchAlgorithmType.Dijkstra, destinations.get(0));
		}

		Coordinate nextPos = strategy.getNextPosition(fuel, currPos, getMap(), exploreMap);
		System.out.println(currPos);
		System.out.println(nextPos);
		exploreMap[currPos.x][currPos.y] = 1;

		if(getSpeed() < CAR_MAX_SPEED) {
			applyForwardAcceleration();
		} else {
			makeAction(currPos, nextPos);
		}

		strategy.updateMap(currPos);
		System.out.println(state);
		fuel--;
	}

	private void updateState(CarState state) {
		this.state = state;
	}

	private void makeAction(Coordinate current, Coordinate next){
		int dx, dy, delta = 2;
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
