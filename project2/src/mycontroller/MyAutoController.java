package mycontroller;

import tiles.*;
import world.Car;
import utilities.Coordinate;

import controller.CarController;
import world.WorldSpatial.Direction;
import swen30006.driving.Simulation;

import mycontroller.adapters.*;
import mycontroller.strategies.*;
import mycontroller.algorithms.*;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Team: W9-5
 * Description: this is the class representing the autoController we designed
 *              and implemented
 */
public class MyAutoController extends CarController {

	// fuel the car has
	private float fuel;
	// a threshold used to determine when to change state
	private float threshold;
	// the health value the car initially has
	private float originalHealth;

	private boolean initStart;
	private boolean wallAhead;
	private boolean startEngine;

	// record the path cost for different types of tiles
	private HashMap<TileType, Integer> costTable;
	// all the strategies the car has
	private HashMap<CarState, iControllerStrategy> controllerStrategies;

	private MyMap maps;

	private CarState currState;
	private CarState prevState;

	private Simulation.StrategyMode mode;

	private ControllerStrategyFactory strategyFactory;

	public MyAutoController(Car car) {
		super(car);
		fuel = car.getFuel();
		mode = Simulation.toConserve();
		currState = CarState.EXPLORING;
		strategyFactory = ControllerStrategyFactory.getInstance();
		controllerStrategies = new HashMap<>();
		costTable = new HashMap<>();
		maps = new MyMap(getMap());
		initCostTable(mode);
		controllerStrategies.put(currState, strategyFactory.getStrategy(currState, SearchAlgorithmType.Dijkstra, costTable));

		if (mode == Simulation.StrategyMode.HEALTH) {
			threshold = getHealth() / 4;
		}
		else if (mode == Simulation.StrategyMode.FUEL) {
			threshold = fuel / 4;
		}
		else {
			// for completeness only
			System.out.println("unsupported conserve type");
			System.exit(0);
		}

		originalHealth = getHealth();
		startEngine = false;
		prevState = null;
		initStart = true;
	}

	@Override
	public void update() {

		HashMap<Coordinate, MapTile> myView = getView();
		maps.updateMap(myView);
		Coordinate currPos = new Coordinate(getPosition());

		if (maps.getDes().contains(currPos)) {
			applyBrake();
			return;
		}

		ArrayList<Coordinate> parcelPos = maps.getParcel();
		updateState(parcelPos);

		if (!controllerStrategies.containsKey(currState)) {
			controllerStrategies.put(currState, strategyFactory.getStrategy(currState, SearchAlgorithmType.Dijkstra, costTable));
		}

		Coordinate nextPos;
		if (currState == CarState.COLLECTING) {
			PickParcelStrategy temp = (PickParcelStrategy) controllerStrategies.get(currState);
			if (temp.reachable(maps, currPos, parcelPos)) {
				nextPos = temp.getNextPosition(currPos, parcelPos, maps);
			}
			// parcel can't be reached, give up, keep exploring map
			else {
				changeState(CarState.EXPLORING);
				nextPos = controllerStrategies.get(currState).getNextPosition(currPos, maps.getDes(), maps);
			}
		} else {
			nextPos = controllerStrategies.get(currState).getNextPosition(currPos, maps.getDes(), maps);
		}

		if(initStart || prevState == CarState.HEALING) {
			// engine is off at the beginning, also during recovering
			startEngine = true;
			initStart = false;
		}
		wallAhead = checkWallAhead(currPos, maps.getMap());
		makeAction(currPos, nextPos);
		fuel--;
	}

	private void makeAction(Coordinate start, Coordinate des) {
		Direction direction = getOrientation();
		if(startEngine) {
			if(wallAhead) { applyReverseAcceleration(); }
			else { applyForwardAcceleration(); }
			startEngine = false;
			return;
		}
		if (start.x < des.x) {
			if (direction == Direction.NORTH) {
				turnRight();
			} else if (direction == Direction.SOUTH) {
				turnLeft();
			} else if (direction == Direction.WEST) {
				if (getSpeed() > 0) {
					applyBrake();
				} else {
					applyReverseAcceleration();
				}
			} else {
				applyForwardAcceleration();
			}
		} else if (start.x > des.x) {
			if (direction == Direction.NORTH) {
				turnLeft();
			} else if (direction == Direction.SOUTH) {
				turnRight();
			} else if (direction == Direction.WEST) {
				applyForwardAcceleration();
			} else {
				if (getSpeed() > 0) {
					applyBrake();
				} else {
					applyReverseAcceleration();
				}
			}
		} else if (start.y < des.y) {
			if (direction == Direction.NORTH) {
				applyForwardAcceleration();
			} else if (direction == Direction.SOUTH) {
				if (getSpeed() > 0) {
					applyBrake();
				} else {
					applyReverseAcceleration();
				}
			} else if (direction == Direction.WEST) {
				turnRight();
			} else {
				turnLeft();
			}
		} else if (start.y > des.y) {
			if (direction == Direction.NORTH) {
				if (getSpeed() > 0) {
					applyBrake();
				} else {
					applyReverseAcceleration();
				}
			} else if (direction == Direction.SOUTH) {
				applyForwardAcceleration();
			} else if (direction == Direction.WEST) {
				turnLeft();
			} else {
				turnRight();
			}
		} else {
			applyBrake();
		}
	}

	// under different modes, same tile has different path cost
	private void initCostTable(Simulation.StrategyMode mode) {
		if (mode == Simulation.StrategyMode.FUEL) {
			// everything has the same, always choose shortest path
			costTable.put(TileType.LAVA, 1);
			costTable.put(TileType.ROAD, 1);
			costTable.put(TileType.WATER, 1);
			costTable.put(TileType.HEALTH, 1);
		} else if (mode == Simulation.StrategyMode.HEALTH) {
			costTable.put(TileType.LAVA, 99);
			costTable.put(TileType.ROAD, 1);
			// take health only when it is necessary, so high cost
			costTable.put(TileType.WATER, 999);
			costTable.put(TileType.HEALTH, 999);
		}
	}

	private void updateState(ArrayList<Coordinate> parcelPos) {
		// stays in the healing state until recovers to the original health
		if(currState == CarState.HEALING && getHealth() < originalHealth) {
			return;
		}

		// job not finished
		if (numParcelsFound() < numParcels()) {
			if (parcelPos.size() > 0) {
				// known parcels to be collected
				changeState(CarState.COLLECTING);
			}
			// no known parcels to collect, keep exploring map
			else {
				changeState(CarState.EXPLORING);
			}
		}
		// collected enough parcels, heading to exit point
		else {
			changeState(CarState.EXITING);
		}

		if (mode == Simulation.StrategyMode.HEALTH) {
			// health below threshold, enter healing state
			if (getHealth() <= threshold) {
				changeState(CarState.HEALING);
				// now, lava costs much more
				costTable.put(TileType.LAVA, 999);
			}
		}
		else if (mode == Simulation.StrategyMode.FUEL) {
			// fuel below threshold, go directly to exit
			if (fuel <= threshold) {
				changeState(CarState.EXITING);
			}
		}
	}

	// return true if the car is facing a wall, false otherwise
	private boolean checkWallAhead(Coordinate currPos, HashMap<Coordinate, MapTile> map) {
		Direction currDirection = getOrientation();
		Coordinate coor = null;
		switch (currDirection) {
			case NORTH:
				coor = new Coordinate(currPos.x, currPos.y + 1);
				break;
			case SOUTH:
				coor = new Coordinate(currPos.x, currPos.y - 1);
				break;
			case WEST:
				coor = new Coordinate(currPos.x - 1, currPos.y);
				break;
			case EAST:
				coor = new Coordinate(currPos.x + 1, currPos.y);
				break;
		}
		MapTile tileAhead = map.get(coor);
		return AdapterFactory.getInstance().getAdapter(tileAhead).getType(tileAhead) == TileType.WALL;
	}

	// change to state of car to the specified state, and update previous state
	private void changeState(CarState newState) {
		prevState = currState;
		currState = newState;
	}
}
