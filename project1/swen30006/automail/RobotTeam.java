package automail;


import exceptions.ExcessiveDeliveryException;

import java.util.ArrayList;

/**
 * Team: W9-5
 * Robots work as a team to deliver mail
 */
public class RobotTeam implements IRobot {

    private MailItem mail_to_deliver;
    // all the robots in the team
    private ArrayList<IRobot> robotMembers;
    private int current_floor;
    // indicator of whether the team has delivered the mail
    private boolean itemDelivered;
    // number of time steps the team has waited
    private int stepWaited;

    public RobotTeam(MailItem mail_to_deliver) {
        robotMembers = new ArrayList<>();
        this.mail_to_deliver = mail_to_deliver;
        current_floor = Building.MAILROOM_LOCATION;
        itemDelivered = false;
        stepWaited = 0;
    }

    /**
     * add a new robot to the team
     * @param robot the robot to be added to the team
     */
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
            // has waited for three time steps, can make a move now
            for (IRobot robot: robotMembers) {
                robot.step();
            }

            if(current_floor < mail_to_deliver.getDestFloor()){
                current_floor++;
            } else {
                current_floor--;
            }
            // reach the destination, have the mail delivered
            if(current_floor == mail_to_deliver.getDestFloor())
                itemDelivered = true;

            stepWaited = 0;
        }
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

    /**
     * robots in the team do not use tube to deliver mailItem
     * @param mailItem mailItem to be added
     */
    public void addToTube(MailItem mailItem) { }

    /**
     * get all the robots in the team
     * @return an ArrayList of robots in the team
     */
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

    /**
     * return the status of whether the team has delivered the mailItem
     * @return true if the team has delivered the mailItem
     */
    public boolean getStatus() {
        return itemDelivered;
    }
}
