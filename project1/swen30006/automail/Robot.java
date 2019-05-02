package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import strategies.IMailPool;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * The robot delivers mail!
 */
public class Robot implements IRobot {

    private IMailDelivery delivery;
    protected final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private IMailPool mailPool;
    private boolean receivedDispatch;
    
    private MailItem deliveryItem = null;
    private MailItem tube = null;
    
    private int deliveryCounter;

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     */
    public Robot(IMailDelivery delivery, IMailPool mailPool){
    	id = "R" + hashCode();
        // current_state = RobotState.WAITING;
    	current_state = RobotState.RETURNING;
        current_floor = Building.MAILROOM_LOCATION;
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
    }
    
    public void dispatch() {
    	receivedDispatch = true;
    }

    /**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the
     *                                    capacity of the tube without refilling
     */
    public void step() throws ExcessiveDeliveryException {    	
        switch(current_state) {
    		// This state is triggered when the robot is returning to
            // the mailroom after a delivery
    		case RETURNING:
    			// If its current position is at the mailroom,
                // then the robot should change state
                if(current_floor == Building.MAILROOM_LOCATION){
                	if (tube != null) {
                		mailPool.addToPool(tube);
                        System.out.printf("T: %3d > old addToPool [%s]%n",
                                          Clock.Time(), tube.toString());
                        tube = null;
                	}
        			// Tell the sorter the robot is ready
        			mailPool.registerWaiting(this);
                	changeState(RobotState.WAITING);
                } else {
                	// If the robot is not at the mailroom floor
                    // yet, then move towards it!
                    moveTowards(Building.MAILROOM_LOCATION);
                	break;
                }
    		case WAITING:
                // If the StorageTube is ready and the Robot is
                // waiting in the mailroom then start the delivery
                if(!isEmpty() && receivedDispatch){
                	receivedDispatch = false;
                    // reset delivery counter
                	deliveryCounter = 0;
        			setRoute();
                	changeState(RobotState.DELIVERING);
                }
                break;
    		case DELIVERING:
                // If already here drop off either way
    			if(current_floor == destination_floor){
                    // Delivery complete, report this to the simulator!
                    delivery.deliver(deliveryItem);
                    deliveryItem = null;
                    deliveryCounter++;
                    // Implies a simulation bug
                    if(deliveryCounter > 2){
                    	throw new ExcessiveDeliveryException();
                    }
                    // Check if want to return, i.e. if
                    // there is no item in the tube
                    if(tube == null){
                    	changeState(RobotState.RETURNING);
                    }
                    else{
                        // If there is another item, set the robot's route to
                        // the location to deliver the item
                        deliveryItem = tube;
                        tube = null;
                        setRoute();
                        changeState(RobotState.DELIVERING);
                    }
    			} else {
	        		// The robot is not at the destination yet, move towards it!
	                moveTowards(destination_floor);
    			}
                break;
    	}
    }

    /**
     * Sets the route for the robot
     */
    private void setRoute() {
        /** Set the destination floor */
        destination_floor = deliveryItem.getDestFloor();
    }

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    private void moveTowards(int destination) {
        if(current_floor < destination){
            current_floor++;
        } else {
            current_floor--;
        }
    }
    
    private String getIdTube() {
    	return String.format("%s(%1d)", id, (tube == null ? 0 : 1));
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
    	assert(!(deliveryItem == null && tube != null));
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n",
                              Clock.Time(), getIdTube(),
                              current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(),
                              getIdTube(), deliveryItem.toString());
    	}
    }

	public MailItem getTube() {
		return tube;
	}
    
	static private int count = 0;
	static private Map<Integer, Integer> hashMap = new TreeMap<Integer, Integer>();

	@Override
	public int hashCode() {
		Integer hash0 = super.hashCode();
		Integer hash = hashMap.get(hash0);
		if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
		return hash;
	}

	public boolean isEmpty() {
		return (deliveryItem == null && tube == null);
	}

	public void addToHand(MailItem mailItem) {
		assert(deliveryItem == null);
		deliveryItem = mailItem;
	}

	public void addToTube(MailItem mailItem){
		assert(tube == null);
		tube = mailItem;
	}

	public ArrayList<String> getID() {
	    ArrayList<String> out = new ArrayList<>();
	    out.add(id);
	    return out;
    }
}
