package mycontroller;

import tiles.*;
import world.Car;
import utilities.Coordinate;

import controller.CarController;
import world.WorldSpatial.Direction;
import swen30006.driving.Simulation;

import mycontroller.adapters.*;
import mycontroller.strategies.*;

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

	private MyMap maps;

	private CarState currState;
	private CarState prevState;

	private iControllerStrategy strategy;

	private Simulation.StrategyMode mode;

	public MyAutoController(Car car) {
		super(car);
		fuel = car.getFuel();
		mode = Simulation.toConserve();
		currState = CarState.EXPLORING;
		maps = new MyMap(getMap());

		if (mode == Simulation.StrategyMode.HEALTH) {
			strategy = new CompositeHealthControllerStrategy();
			threshold = getHealth() / 4;
		}
		else if (mode == Simulation.StrategyMode.FUEL) {
			strategy = new CompositeFuelControllerStrategy();
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

		if (maps.getExit().contains(currPos)) {
			applyBrake();
			return;
		}

		ArrayList<Coordinate> parcelPos = maps.getParcel();
		updateState(parcelPos);

		Coordinate nextPos;
		if (currState == CarState.COLLECTING) {
			if (strategy.reachable(currState, currPos, maps)) {
				nextPos = strategy.getNextPosition(currState, currPos, maps);
			}
			// parcel can't be reached, give up, keep exploring map
			else {
				changeState(CarState.EXPLORING);
				nextPos = strategy.getNextPosition(currState, currPos, maps);
			}
		} else {
			nextPos = strategy.getNextPosition(currState, currPos, maps);
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
				strategy.updateTable(TileType.LAVA, 999);
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
