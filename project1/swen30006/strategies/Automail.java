package strategies;

import automail.IMailDelivery;
import automail.Robot;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;

public class Automail {

    public Robot[] robots;
    public IMailPool mailPool;

    public Automail(IMailPool mailPool, IMailDelivery delivery, int numRobots) {
        // Initialize the MailPool
        this.mailPool = mailPool;

        // Initialize robots
        robots = new Robot[numRobots];
        for(int i = 0; i < numRobots; i++)
            robots[i] = new Robot(delivery, mailPool);
    }

    public void step() throws ItemTooHeavyException, ExcessiveDeliveryException {
        mailPool.step();
        for(int i = 0; i < robots.length; i++)
            robots[i].step();
    }
}