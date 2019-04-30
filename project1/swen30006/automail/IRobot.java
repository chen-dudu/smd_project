package automail;

import exceptions.ExcessiveDeliveryException;

/**
 * Team: W9-5
 * Description:
 */
public interface IRobot {

    void dispatch();
    void step() throws ExcessiveDeliveryException;
    boolean isEmpty();
    void addToHand(MailItem mailItem);
}
