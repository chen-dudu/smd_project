package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import exceptions.ItemTooHeavyException;

public class MailPool implements IMailPool {

	private class Item {
		int priority;
		int destination;
		MailItem mailItem;
		// Use stable sort to keep arrival time relative positions
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			destination = mailItem.getDestFloor();
			this.mailItem = mailItem;
		}
	}
	
	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}

	/**
	 * number of robots for a pair team
	 */
	public static final int PAIR = 2;
	/**
	 * number of robots for a triple team
	 */
	public static final int TRIPLE = 3;

	static public final int INDIVIDUAL_MAX_WEIGHT = 2000;
	static public final int PAIR_MAX_WEIGHT = 2600;
	static public final int TRIPLE_MAX_WEIGHT = 3000;

	private LinkedList<Item> pool;
	private LinkedList<Robot> robots;

	public MailPool(){
		// Start empty
		pool = new LinkedList<>();
		robots = new LinkedList<>();
	}

	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}

	/**
	 * This method is called on each time step
	 * @throws ItemTooHeavyException if item to be delivered has weight greater
	 *                               than triple weight limit
	 */
	@Override
	public void step() {
		try{
			// try to load robots when there are robots available and
			// mails to be delivered
			while(robots.size() > 0 && pool.size() > 0) loadRobot();
		} catch (Exception e) { 
            throw e; 
        } 
	}
	
	private void loadRobot() {
		ListIterator<Robot> i = robots.listIterator(0);
		ListIterator<Item> j = pool.listIterator(0);
		if (pool.size() > 0) {
			MailItem mail_to_deliver = j.next().mailItem;
			// move the cursor back
			j.previous();
			try{
				if (mail_to_deliver.getWeight() <= INDIVIDUAL_MAX_WEIGHT)
					loadLightItem(i, j);
				else if (mail_to_deliver.getWeight() <= PAIR_MAX_WEIGHT) {
					loadHeavyItem(i, mail_to_deliver, PAIR);
					j.remove();
				} else if (mail_to_deliver.getWeight() <= TRIPLE_MAX_WEIGHT) {
					loadHeavyItem(i, mail_to_deliver, TRIPLE);
					j.remove();
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	private void loadLightItem(ListIterator<Robot> i, ListIterator<Item> j) {
		Robot robot = i.next();
		assert(robot != null);
		robot.addToHand(j.next().mailItem);
		j.remove();
		// try to load another mailitem to the tube
		if(pool.size() > 0) {
			MailItem newItem = j.next().mailItem;
			if(newItem.getWeight() <= Robot.INDIVIDUAL_MAX_WEIGHT) {
				robot.addToTube(newItem);
				j.remove();
			}
			// too heavy to deliver alone, move the cursor back
			else j.previous();
		}
		robot.dispatch();
		i.remove();
	}

	// required_robot is the number of robot needed to deliver the item
	private void loadHeavyItem(ListIterator<Robot> i, MailItem to_deliver,
                               int required_robot) {
		// not enough robots
        if(robots.size() < required_robot)
            return;
        for(int k = 0; k < required_robot; k++) {
            Robot robot = i.next();
            assert (robot != null);
            robot.addToHand(to_deliver);
            robot.setHeavyItem(true);
            robot.dispatch();
            i.remove();
        }
    }

	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}
}
