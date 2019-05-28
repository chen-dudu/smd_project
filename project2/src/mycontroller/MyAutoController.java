package mycontroller;

import controller.CarController;
import tiles.*;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial.Direction;
import swen30006.driving.Simulation;

import mycontroller.strategies.*;
import mycontroller.adapters.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAutoController extends CarController {

	private CarState state;
	private ControllerStrategyFactory strategyFactory;
	private HashMap<CarState, iControllerStrategy> controllerStrategies;
	// record the path cost for different types of tiles
	private HashMap<TileType, Integer> costTable;
	private Simulation.StrategyMode mode;
	private MyMap maps;
	private float fuel;
	private int move;
	private float threshold;
	private float originnalHealth;
	private boolean startEngin;
	private CarState prevState;

	public MyAutoController(Car car) {
		super(car);
		fuel = car.getFuel();
		mode = Simulation.toConserve();
		state = CarState.EXPLORING;
		strategyFactory = ControllerStrategyFactory.getInstance();
		controllerStrategies = new HashMap<>();
		costTable = new HashMap<>();
		maps = new MyMap(getMap());
		initCostTable(mode);
		controllerStrategies.put(state, strategyFactory.getStrategy(state, maps.getMap(), SearchAlgorithmType.Dijkstra, maps.getDes(), costTable));
		move = 0;

		if (mode == Simulation.StrategyMode.HEALTH) {
			threshold = getHealth() / 4;
		}
		else if (mode == Simulation.StrategyMode.FUEL) {
			threshold = fuel / 4;
		}
		else {
			// for completeness only
			System.out.println("unsuported conserve type");
			System.exit(0);
		}

		originnalHealth = getHealth();
		startEngin = false;
		prevState = null;
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

		ArrayList<Coordinate> goal;
		if (state == CarState.COLLECTING) {
			// in parcel collecting state, goal is parcel
			goal = parcelPos;
		} else {
			// in other state, goal is exit points
			goal = maps.getDes();
		}
		if (!controllerStrategies.containsKey(state)) {
			controllerStrategies.put(state, strategyFactory.getStrategy(state, maps.getMap(), SearchAlgorithmType.Dijkstra, goal, costTable));
		}

		Coordinate nextPos;
		if (state == CarState.COLLECTING) {
			PickParcelStrategy temp = (PickParcelStrategy) controllerStrategies.get(state);
			if (temp.reachable(maps.getMap(), currPos, parcelPos)) {
				nextPos = temp.getNextPosition(fuel, currPos, parcelPos, maps.getMap(), maps.getExploreMap());
			}
			// parcel can't be reached, give up, keep exploring map
			else {
				changeState(CarState.EXPLORING);
				nextPos = controllerStrategies.get(state).getNextPosition(fuel, currPos, maps.getDes(), maps.getMap(), maps.getExploreMap());
			}
		} else {
			nextPos = controllerStrategies.get(state).getNextPosition(fuel, currPos, maps.getDes(), maps.getMap(), maps.getExploreMap());
		}

		if(move < 2 || prevState == CarState.HEALING) {
			// engine is off at the beginning, also during recovering
			startEngin = true;
		}
		makeAction(currPos, nextPos);
		move++;
		fuel--;
	}

	private void makeAction(Coordinate start, Coordinate des) {
		Direction direction = getOrientation();
		if(startEngin) {
			applyForwardAcceleration();
			startEngin = false;
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
		}
		else {
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
		if(state == CarState.HEALING && getHealth() < originnalHealth) {
			return;
		}

		// job not finished
		if (numParcelsFound() < numParcels()) {
			if (parcelPos.size() > 0) {
				// known parecels to be collected
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
			// health below threshode, enter healing state
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

	// change to state of car to the specified state, and update previous state
	private void changeState(CarState state) {
		prevState = this.state;
		this.state = state;
	}
}
