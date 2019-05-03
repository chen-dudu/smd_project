package automail;

import exceptions.ExcessiveDeliveryException;

import java.util.ArrayList;

/**
 * Team: W9-5
 * This class is the common interface implemented by different
 * types of robots and a composite robot class to achieve composite design
 */
public interface IRobot {

    /**
     * dispatch a robot/team which has mailItem loaded
     */
    void dispatch();

    /**
     * call for each time step
     * @throws ExcessiveDeliveryException exception thrown when robot tries to
     *                                    deliver more mailItems than its capacity
     */
    void step() throws ExcessiveDeliveryException;

    /**
     * check if a robot/team is delivering mailItem
     * @return true if there is no mailItem at hand and in the tube
     */
    boolean isEmpty();

    /**
     * add new mailItem to the tube of robot
     * @param mailItem mailItem to be added
     */
    void addToHand(MailItem mailItem);

    /**
     * add new mailItem to the hand of robot/team
     * @param mailItem mailItem to be added
     */
    void addToTube(MailItem mailItem);

    /**
     * get the IDs of a robot or robots in the team
     * @return an ArrayList of IDs of robots
     */
    ArrayList<String> getID();
}
