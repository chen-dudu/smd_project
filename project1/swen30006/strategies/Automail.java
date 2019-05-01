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

public class Automail {

    public IMailPool mailPool;

    private LinkedList<IRobot> individualRobots;
    private LinkedList<RobotTeam> teamRobots;

    public Automail(IMailPool mailPool, IMailDelivery delivery, int numRobots) {
        // Initialize the MailPool
        this.mailPool = mailPool;

        // Initialize robots
        individualRobots = new LinkedList<>();
        teamRobots = new LinkedList<>();
        for(int i = 0; i < numRobots; i++)
            individualRobots.add(new Robot(delivery, mailPool));
    }

    public void step() throws ItemTooHeavyException, ExcessiveDeliveryException {
        mailPool.step();

        ListIterator<IRobot> i = individualRobots.listIterator();
        ListIterator<RobotTeam> j = teamRobots.listIterator();
        while (i.hasNext())
            i.next().step();

        while (j.hasNext()) {
            RobotTeam team = j.next();
            team.step();
            if(team.getStatus()) {
                dismiss(team);
            }
        }
    }

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

    private void dismiss(RobotTeam team) {
        ArrayList<IRobot> members = team.getMembers();
        individualRobots.addAll(members);
        teamRobots.remove(team);
    }
}
