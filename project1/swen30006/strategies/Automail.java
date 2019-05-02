package strategies;

import automail.IMailDelivery;
import automail.IRobot;
import automail.Robot;
import automail.RobotTeam;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ArrayList;

/**
 * Team: W9-5
 * This is the central class in the system, responsible for instantiating and
 * updating all the objects in the system
 */
public class Automail {

    private IMailPool mailPool;

    // list of robots working alone
    private LinkedList<IRobot> individualRobots;
    // list of teams
    private LinkedList<RobotTeam> teamRobots;

    public Automail(IMailPool mailPool, IMailDelivery delivery, int numRobots) {
        // Initialize the MailPool
        this.mailPool = mailPool;

        // Initialize robots
        teamRobots = new LinkedList<>();
        individualRobots = new LinkedList<>();
        for(int i = 0; i < numRobots; i++)
            individualRobots.add(new Robot(delivery, mailPool));
    }

    /**
     * called on each time step
     * @throws ItemTooHeavyException exception thrown when there is not enough
     *                               robots to deliver the mailIem
     * @throws ExcessiveDeliveryException exception thrown when robot tries to
     *                                    deliver more mailItems than its capacity
     */
    public void step() throws ItemTooHeavyException, ExcessiveDeliveryException {
        mailPool.step();

        ListIterator<IRobot> i = individualRobots.listIterator();
        ListIterator<RobotTeam> j = teamRobots.listIterator();
        // step each individual robots
        while (i.hasNext())
            i.next().step();

        // step each team
        while (j.hasNext()) {
            RobotTeam team = j.next();
            team.step();
            // the team has successfully delivered the mailItem, dismiss team
            if(team.getStatus()) {
                dismiss(team);
            }
        }
    }

    /**
     * add a new team to the team list
     * @param newTeam the team to be added to the lsit
     */
    public void addTeam(RobotTeam newTeam) {
        ArrayList<String> memberIDs = newTeam.getID();
        for(String memberID: memberIDs) {
            ListIterator<IRobot> i = individualRobots.listIterator();
            while(i.hasNext()) {
                if(memberID.equals(i.next().getID().get(0))) {
                    i.remove();
                    break;
                }
            }
        }
        teamRobots.add(newTeam);
    }

    // dismiss the team
    private void dismiss(RobotTeam team) {
        ArrayList<IRobot> members = team.getMembers();
        // add each team member back to individual robot list
        individualRobots.addAll(members);
        teamRobots.remove(team);
    }
}
