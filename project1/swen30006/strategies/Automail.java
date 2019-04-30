package strategies;

import automail.IMailDelivery;
import automail.IRobot;
import automail.Robot;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class Automail {

    public Robot[] robots;
    public IMailPool mailPool;

    private LinkedList<IRobot> individualRobots;
    private ArrayList<IRobot> teamRobots;

    public Automail(IMailPool mailPool, IMailDelivery delivery, int numRobots) {
        // Initialize the MailPool
        this.mailPool = mailPool;

        // Initialize robots
        individualRobots = new LinkedList<>();
        teamRobots = new LinkedList<>()
        for(int i = 0; i < numRobots; i++)
            individualRobots.add(new Robot(delivery, mailPool));
    }

    public void step() throws ItemTooHeavyException, ExcessiveDeliveryException {
        mailPool.step();
        ListIterator<IRobot> i = individualRobots.listIterator(0);
        ListIterator<IRobot> j = teamRobots.listIterator(0);
        while (i.hasNext()) i.next().step();
        while (j.hasNext()) j.next().step();
    }
}