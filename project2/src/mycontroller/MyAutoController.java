package mycontroller;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
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
	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;

	private CarState state;
	private ControllerStrategyFactory strategyFactory;
	private AdapterFactory adapterFactory;
//	private iControllerStrategy strategy;
//	private ArrayList<Coordinate> destinations;

	private float fuel;
//	private Coordinate parcelPos;
	private int turn;

	private HashMap<CarState, iControllerStrategy> controllerStrategies;

//	private Coordinate prevPos;
	private float threshold;

	// record the path cost for different types of tiles
	private HashMap<TileType, Integer> costTable;
	private Simulation.StrategyMode mode;

	private MyMap maps;

	public MyAutoController(Car car) {
		super(car);
		fuel = car.getFuel();
		mode = Simulation.toConserve();
		state = CarState.EXPLORING;
		strategyFactory = ControllerStrategyFactory.getInstance();
		adapterFactory = AdapterFactory.getInstance();
//		destinations = new ArrayList<>();
		controllerStrategies = new HashMap<>();
		costTable = new HashMap<>();
		maps = new MyMap(getMap());
//		System.out.println("+++" + maps.getDes().size());
		initCostTable(mode);
		controllerStrategies.put(state, strategyFactory.getStrategy(state, maps.getMap(), SearchAlgorithmType.Dijkstra, maps.getDes(), costTable));
		turn = 0;

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
//		exploreMap = new Integer[World.MAP_HEIGHT][World.MAP_WIDTH];
//		for(int i = 0; i < exploreMap.length; i++) {
//			for(int j = 0; j < exploreMap[i].length; j++) {
//				exploreMap[i][j] = 0;
//			}
//		}
//
//		for(Coordinate next: maps.getMap().keySet()) {
//			if(myMap.get(next).isType(MapTile.Type.FINISH)) {
//				destinations.add(next);
//			}
//			else if(myMap.get(next).isType(MapTile.Type.WALL)) {
//				exploreMap[next.y][next.x] = 1;
//			}
//		}

//		controllerStrategies.get(state).updateMap(new Coordinate(getPosition()));

//		printMap(exploreMap);

//		parcelPos = null;
//		prevPos = null;
	}

	@Override
	public void update() {

		maps.printMap();

//		System.out.println(">>>>>>>>>>>>>>>>" + exploreMap[0][0]);
		turn++;
		System.out.println(World.MAP_WIDTH + " -- " + World.MAP_HEIGHT);
		HashMap<Coordinate, MapTile> myView = getView();
		maps.updateMap(myView);
		Coordinate currPos = new Coordinate(getPosition());

//		for(Coordinate next: myView.keySet()) {
//			if(0 <= next.x && next.x < World.MAP_WIDTH &&
//					0 <= next.y && next.y < World.MAP_HEIGHT) {
//				exploreMap[next.y][next.x] = 1;
//			}
//		}

		if(maps.getDes().contains(currPos)) {
			applyBrake();
			return;
		}

//		if(currPos.equals(prevPos)) {
//			applyReverseAcceleration();
//			return;
//		}
		ArrayList<Coordinate> parcelPos = maps.getParcel();
//		System.out.println("found:    " + numParcelsFound());
//		System.out.println("to find:   " + numParcels());
		if(numParcelsFound() < numParcels()) {
//			System.out.println("found:    " + numParcelsFound());
//			System.out.println("to find:   " + numParcels());
			if(parcelPos.size() > 0) {
				updateState(CarState.COLLECTING);
			}
			else if(parcelPos.size() == 0) {
				updateState(CarState.EXPLORING);
			}
		}
//		for(Coordinate next: parcelPos) {
//			System.out.println(next);
//		}

//		if(currPos.equals(parcelPos)) {
//		if(!parcelPos.contains(currPos)) {
//			System.out.println("~~~~~~~~~");
//			parcelPos = null;
//			updateState(CarState.EXPLORING);
//		}

		if(numParcels() == numParcelsFound()) {
			updateState(CarState.EXITING);
		}

		if(mode == Simulation.StrategyMode.HEALTH) {
			if(getHealth() <= threshold) {
				updateState(CarState.HEALING);
			}
		}
		else if(mode == Simulation.StrategyMode.FUEL) {
			if(fuel <= threshold) {
				updateState(CarState.EXITING);
			}
		}

		ArrayList<Coordinate> goal;
		if(state == CarState.COLLECTING) {
			if(!controllerStrategies.containsKey(state)) {
//				controllerStrategies.put(state, strategyFactory.getStrategy(state, maps.getMap(), SearchAlgorithmType.Dijkstra, parcelPos, costTable));
			}
			goal = parcelPos;
		}
		else {
			if(!controllerStrategies.containsKey(state)) {
//				controllerStrategies.put(state, strategyFactory.getStrategy(state, maps.getMap(), SearchAlgorithmType.Dijkstra, destinations, costTable));
			}
			goal = maps.getDes();
//			System.out.println("////" + goal.size());
		}
		if(!controllerStrategies.containsKey(state)) {
			controllerStrategies.put(state, strategyFactory.getStrategy(state, maps.getMap(), SearchAlgorithmType.Dijkstra, goal, costTable));
		}
		Coordinate nextPos;
		if(state == CarState.COLLECTING) {
			PickParcelStrategy temp = (PickParcelStrategy) controllerStrategies.get(state);
			if(temp.reachable(maps.getMap(), currPos, parcelPos)) {
				nextPos = temp.getNextPosition(fuel, currPos, parcelPos, maps.getMap(), maps.getVisited());
			}
			// parcel can't be reached, give up, get back to previous state
			else {
				updateState(CarState.EXPLORING);
				nextPos = controllerStrategies.get(state).getNextPosition(fuel, currPos, maps.getDes(), maps.getMap(), maps.getVisited());
			}
		}
		else {
			nextPos = controllerStrategies.get(state).getNextPosition(fuel, currPos, maps.getDes(), maps.getMap(), maps.getVisited());
		}

		System.out.print(currPos + " -> " + nextPos);
		System.out.println();


		if(getSpeed() < CAR_MAX_SPEED && turn < 2) {
			// first start
			applyForwardAcceleration();
		} else {
			makeMove(currPos, nextPos);
		}
//		prevPos = currPos;
//		controllerStrategies.get(state).updateMap(currPos);
		System.out.println("health >>> " + getHealth());
		System.out.println(state);
		fuel--;
	}

	// under different modes, same tile has different path cost
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

	private void makeMove(Coordinate start, Coordinate des) {
		Direction direction = getOrientation();
		if(start.x < des.x) {
			if(direction == Direction.NORTH) {
				turnRight();
			}
			else if(direction == Direction.SOUTH) {
				turnLeft();
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
			}
			else if(direction == Direction.SOUTH) {
				turnRight();
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
			}
			else {
				turnLeft();
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
			}
			else {
				turnRight();
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
