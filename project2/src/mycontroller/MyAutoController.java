package mycontroller;

import controller.CarController;
import mycontroller.strategies.CarState;
import mycontroller.strategies.ControllerStrategyFactory;
import org.lwjgl.Sys;
import sun.plugin.dom.core.CoreConstants;
import tiles.*;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial.Direction;
import swen30006.driving.Simulation;

import mycontroller.strategies.*;
import mycontroller.adapters.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

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
	HashMap<Coordinate, MapTile> myMap;

	private Coordinate prevPos;
	private float threshold;

	private HashMap<TileType, Integer> costTable;
	private Simulation.StrategyMode mode;

	public MyAutoController(Car car) {
		super(car);
		fuel = car.getFuel();
		state = CarState.EXPLORING;
		strategyFactory = ControllerStrategyFactory.getInstance();
		adapterFactory = AdapterFactory.getInstance();
		destinations = new ArrayList<>();
		controllerStrategies = new HashMap<>();
		myMap = getMap();

		exploreMap = new Integer[World.MAP_HEIGHT][World.MAP_WIDTH];
		for(int i = 0; i < exploreMap.length; i++) {
			for(int j = 0; j < exploreMap[i].length; j++) {
				exploreMap[i][j] = 0;
			}
		}

		for(Coordinate next: myMap.keySet()) {
			if(myMap.get(next).isType(MapTile.Type.FINISH)) {
				destinations.add(next);
			}
			else if(myMap.get(next).isType(MapTile.Type.WALL)) {
				exploreMap[next.y][next.x] = 1;
			}
		}

		controllerStrategies.put(state, strategyFactory.getStrategy(state, getMap(), SearchAlgorithmType.Dijkstra, destinations.get(0)));
//		controllerStrategies.get(state).updateMap(new Coordinate(getPosition()));

//		printMap(exploreMap);

		parcelPos = null;
		turn = 0;
		prevPos = null;

		mode = Simulation.toConserve();
		if(mode == Simulation.StrategyMode.HEALTH) {
			threshold = getHealth()/4;
		}
		else if(mode == Simulation.StrategyMode.FUEL){
			threshold = fuel/4;
		}
		else {
			System.out.println("unsuported conserve type");
			System.exit(0);
		}

		initCostTable(mode);
	}

	// Coordinate initialGuess;
	// boolean notSouth = true;
	@Override
	public void update() {

		printMap(exploreMap);

//		System.out.println(">>>>>>>>>>>>>>>>" + exploreMap[0][0]);
		turn++;
		System.out.println(World.MAP_WIDTH + " -- " + World.MAP_HEIGHT);
		HashMap<Coordinate, MapTile> myView = getView();
		updateMap(myMap, myView);
		Coordinate currPos = new Coordinate(getPosition());

		for(Coordinate next: myView.keySet()) {
			if(0 <= next.x && next.x < World.MAP_WIDTH &&
					0 <= next.y && next.y < World.MAP_HEIGHT) {
				exploreMap[next.y][next.x] = 1;
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

		for(Coordinate next: myView.keySet()) {
			MapTile tile = myView.get(next);
			if(adapterFactory.getAdapter(tile).getType(tile) == TileType.PARCEL && numParcelsFound() < numParcels()) {
				parcelPos = next;
				updateState(CarState.COLLECTING);
				break;
			}
		}

		if(currPos.equals(parcelPos)) {
//			System.out.println("~~~~~~~~~");
			parcelPos = null;
			updateState(CarState.EXPLORING);
		}

		if(numParcels() == numParcelsFound()) {
			updateState(CarState.EXITING);
		}

		if(mode == Simulation.StrategyMode.HEALTH) {
			if(getHealth() < threshold) {
				updateState(CarState.HEALING);
			}
		}
		else if(mode == Simulation.StrategyMode.FUEL) {
			if(fuel < threshold) {
				updateState(CarState.EXITING);
			}
		}

		if(parcelPos != null) {
			if(!controllerStrategies.containsKey(state)) {
				controllerStrategies.put(state, strategyFactory.getStrategy(state, myMap, SearchAlgorithmType.Dijkstra, parcelPos));
			}
		}
		else {
			if(!controllerStrategies.containsKey(state)) {
				controllerStrategies.put(state, strategyFactory.getStrategy(state, myMap, SearchAlgorithmType.Dijkstra, destinations.get(0)));
			}
		}

		Coordinate nextPos;
		if(state == CarState.COLLECTING) {
			PickParcelStrategy temp = (PickParcelStrategy) controllerStrategies.get(state);
			if(temp.reachable(myMap, currPos, parcelPos)) {
				nextPos = temp.getNextPosition(fuel, currPos, parcelPos, myMap, exploreMap);
			}
			else {
				updateState(CarState.EXPLORING);
				nextPos = controllerStrategies.get(state).getNextPosition(fuel, currPos, parcelPos, myMap, exploreMap);
			}
		}
		else {
			nextPos = controllerStrategies.get(state).getNextPosition(fuel, currPos, parcelPos, myMap, exploreMap);
		}



		System.out.print(currPos + " -> " + nextPos);
		System.out.println();


		if(getSpeed() < CAR_MAX_SPEED && turn < 2) {
			applyForwardAcceleration();
		} else {
			makeMove(currPos, nextPos);
		}
		prevPos = currPos;
//		controllerStrategies.get(state).updateMap(currPos);
		System.out.println("health >>> " + getHealth());
		System.out.println(state);

		fuel--;
	}

	private void printMap(Integer[][] map) {
//		for(int i = map.length - 1; i >= 0; i--) {
//			System.out.println(Arrays.toString(map[i]));
//		}
		for(int i = map.length - 1; i >= 0; i--) {
			for(int j = 0; j < map[i].length; j++) {
				if(map[i][j] == 1) {
					System.out.print(map[i][j] + ", ");
				}
				else if(map[i][j] == 0) {
					System.out.print(" , ");
				}
			}
			System.out.println("");
		}
	}

	private void initCostTable(Simulation.StrategyMode mode) {
		if(mode == Simulation.StrategyMode.FUEL) {
			costTable.put(TileType.LAVA, 1);
			costTable.put(TileType.ROAD, 1);
			costTable.put(TileType.WATER, 1);
			costTable.put(TileType.HEALTH, 1);
		}
		else if(mode == Simulation.StrategyMode.HEALTH) {
			costTable.put(TileType.LAVA, 999);
			costTable.put(TileType.ROAD, 1);
			costTable.put(TileType.WATER, 999);
			costTable.put(TileType.HEALTH, 999);
		}
	}

	private void updateState(CarState state) {
		this.state = state;
	}

	private void updateMap(HashMap<Coordinate, MapTile> seenWorld, HashMap<Coordinate, MapTile> view) {
		for(Coordinate next: view.keySet()) {
			MapTile tile = view.get(next);
			switch (AdapterFactory.getInstance().getAdapter(tile).getType(tile)) {
				case LAVA:
					seenWorld.put(next, new LavaTrap());
					break;
				case WATER:
					seenWorld.put(next, new WaterTrap());
					break;
				case HEALTH:
					seenWorld.put(next, new HealthTrap());
					break;
				case PARCEL:
					seenWorld.put(next, new ParcelTrap());
					break;
			}
		}
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
