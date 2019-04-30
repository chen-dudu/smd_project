package automail;


import exceptions.ExcessiveDeliveryException;

import java.util.ArrayList;

/**
 * Team: W9-5
 * Description:
 */
public class RobotTeam implements IRobot {

    private MailItem mail_to_deliver;
    private ArrayList<IRobot> robotMembers;
    private int current_floor;
    private boolean iteamDelivered;
    private int stepWaited;

    public RobotTeam(MailItem mail_to_deliver) {
        robotMembers = new ArrayList<>();
        this.mail_to_deliver = mail_to_deliver;
        current_floor = Building.MAILROOM_LOCATION;
        iteamDelivered = false;
        stepWaited = 0;
    }

    public void addToTeam(IRobot robot) {
        robotMembers.add(robot);
    }

    public void dispatch(){
        for (IRobot robot: robotMembers) {
            robot.dispatch();
        }
    }

    public void step() throws ExcessiveDeliveryException {
        stepWaited++;
        if(stepWaited == 3) {
            for (IRobot robot: robotMembers) {
                robot.step();
            }

            if(current_floor < mail_to_deliver.getDestFloor()){
                current_floor++;
            } else {
                current_floor--;
            }

            if(current_floor == mail_to_deliver.getDestFloor())
                iteamDelivered = true;

            stepWaited = 0;
        }
//        else {
//            stepWaited++;
//        }
    }

    public boolean isEmpty() {
        for (IRobot robot: robotMembers) {
            if (!robot.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void addToHand(MailItem mailItem) {
        for (IRobot robot: robotMembers) {
            robot.addToHand(mailItem);
        }
    }

    public void addToTube(MailItem mailItem) { }

    public ArrayList<IRobot> getMembers() {
        return robotMembers;
    }

    public ArrayList<String> getID() {
        ArrayList<String> out = new ArrayList<>();
        for(IRobot next: robotMembers) {
            out.add(next.getID().get(0));
        }
        return out;
    }

    public boolean getStatus() {
        return iteamDelivered;
    }
}
