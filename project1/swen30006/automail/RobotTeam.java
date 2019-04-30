package automail;


import exceptions.ExcessiveDeliveryException;

import java.util.ArrayList;

/**
 * Team: W9-5
 * Description:
 */
public class RobotTeam implements IRobot{

    private ArrayList<IRobot> robotGroup = new ArrayList<IRobot>();

    public void dispatch(){
        for (IRobot robot: robotGroup) {
            robot.dispatch();
        }
    }

    public void step() throws ExcessiveDeliveryException {
        for (IRobot robot: robotGroup) {
            robot.step();
        }
    }

    public boolean isEmpty() {
        for (IRobot robot: robotGroup) {
            if (!robot.isEmpty()) {
                return False;
            }
        }
        return True;
    }

    public void addToHand(MailItem mailItem) {
        for (IRobot robot: robotGroup) {
            robot.addToHand(mailItem);
        }
    }

}
