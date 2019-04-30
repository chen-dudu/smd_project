package automail;

import exceptions.ExcessiveDeliveryException;

import java.util.ArrayList;

/**
 * Team: W9-5
 * Description:
 */
public interface IRobot {

    void dispatch();
    void step() throws ExcessiveDeliveryException;
    boolean isEmpty();
    void addToHand(MailItem mailItem);
    void addToTube(MailItem mailItem);
    ArrayList<String> getID();
}
