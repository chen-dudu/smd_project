package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import exceptions.MailAlreadyDeliveredException;
import strategies.Automail;
import strategies.IMailPool;
import strategies.MailPool;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Team: W9-5
 * This class simulates the behaviour of AutoMail
 */
public class Simulation {	
	
    /** Constant for the mail generator */
    private static int MAIL_TO_CREATE;
    private static int MAIL_MAX_WEIGHT;
    

    private static ArrayList<MailItem> MAIL_DELIVERED;
    private static double total_score = 0;

    public static void main(String[] args) throws IOException, ItemTooHeavyException {
    	Properties automailProperties = new Properties();
    	String PropertyFileName = "automail.properties";
    	loadProperty(automailProperties, PropertyFileName);

		String seedProp = automailProperties.getProperty("Seed");
		int robots = Integer.parseInt(automailProperties.getProperty("Robots"));

		// not enough robots in the system to perform the task
		if(robots < 1 || (robots < MailPool.PAIR && MAIL_MAX_WEIGHT > MailPool.INDIVIDUAL_MAX_WEIGHT) ||
				(robots < MailPool.TRIPLE && MAIL_MAX_WEIGHT > MailPool.PAIR_MAX_WEIGHT)) {
			throw new ItemTooHeavyException();
		}


		IMailPool mailPool = new MailPool();

        MAIL_DELIVERED = new ArrayList<>();
                
        // Used to see whether a seed is initialized or not
        HashMap<Boolean, Integer> seedMap = new HashMap<>();
        
        // Read the first argument and save it as a seed if it exists
		// No arg
        if (args.length == 0 ) {
			// and no property
        	if (seedProp == null) {
				// so randomise
        		seedMap.put(false, 0);
        	}
			// Use property seed
        	else {
        		seedMap.put(true, Integer.parseInt(seedProp));
        	}
        }
		// Use arg seed - overrides property
        else {
        	seedMap.put(true, Integer.parseInt(args[0]));
        }

        Integer seed = seedMap.get(true);
        System.out.printf("Seed: %s%n", seed == null ? "null" : seed.toString());

        Automail automail = new Automail(mailPool, new ReportDelivery(), robots);

        mailPool.setMailPool(automail);

        MailGenerator mailGenerator = new MailGenerator(MAIL_TO_CREATE, MAIL_MAX_WEIGHT, mailPool, seedMap);
        
        // Initiate all the mail
        mailGenerator.generateAllMail();

        while(MAIL_DELIVERED.size() != mailGenerator.MAIL_TO_CREATE) {
            mailGenerator.step();
            try {
            	automail.step();
			} catch (ExcessiveDeliveryException|ItemTooHeavyException e) {
				e.printStackTrace();
				System.out.println("Simulation unable to complete.");
				System.exit(0);
			}
            Clock.Tick();
        }
        printResults();
    }
    
    static class ReportDelivery implements IMailDelivery {
    	
    	/** Confirm the delivery and calculate the total score */
    	public void deliver(MailItem deliveryItem){
    		if(!MAIL_DELIVERED.contains(deliveryItem)){
    			MAIL_DELIVERED.add(deliveryItem);
                System.out.printf("T: %3d > Delivered(%4d) [%s]%n",
						          Clock.Time(), MAIL_DELIVERED.size(),
						          deliveryItem.toString());
    			// Calculate delivery score
    			total_score += calculateDeliveryScore(deliveryItem);
    		}
    	}
    }
    
    private static double calculateDeliveryScore(MailItem deliveryItem) {
    	// Penalty for longer delivery times
    	final double penalty = 1.2;
    	double priority_weight = 0;
        // Take (delivery time - arrivalTime)**penalty * (1+sqrt(priority_weight))
    	if(deliveryItem instanceof PriorityMailItem){
    		priority_weight = ((PriorityMailItem) deliveryItem).getPriorityLevel();
    	}
        return Math.pow(Clock.Time() - deliveryItem.getArrivalTime(),penalty)*(1+Math.sqrt(priority_weight));
    }

    private static void printResults(){
        System.out.println("T: "+Clock.Time()+" | Simulation complete!");
        System.out.println("Final Delivery time: "+Clock.Time());
        System.out.printf("Final Score: %.2f%n", total_score);
    }

    // this method initialise properties for the system (default or from file)
    private static void loadProperty(Properties property,
									 String propertyFileName) throws IOException {
		// Default properties
		property.setProperty("Robots", "Standard");
		property.setProperty("MailPool", "strategies.SimpleMailPool");
		property.setProperty("Floors", "10");
		property.setProperty("Fragile", "false");
		property.setProperty("Mail_to_Create", "80");
		property.setProperty("Last_Delivery_Time", "100");

		// Read properties
		FileReader inStream = null;
		try {
			inStream = new FileReader(propertyFileName);
			property.load(inStream);
		} finally {
			if (inStream != null) {
				inStream.close();
			}
		}

		// Floors
		Building.FLOORS = Integer.parseInt(property.getProperty("Floors"));
		System.out.printf("Floors: %5d%n", Building.FLOORS);
		// Fragile
		boolean fragile = Boolean.parseBoolean(property.getProperty("Fragile"));
		System.out.printf("Fragile: %5b%n", fragile);
		// Mail_to_Create
		MAIL_TO_CREATE = Integer.parseInt(property.getProperty("Mail_to_Create"));
		System.out.printf("Mail_to_Create: %5d%n", MAIL_TO_CREATE);
		// Mail_to_Create
		MAIL_MAX_WEIGHT = Integer.parseInt(property.getProperty("Mail_Max_Weight"));
		System.out.printf("Mail_Max_Weight: %5d%n", MAIL_MAX_WEIGHT);
		// Last_Delivery_Time
		Clock.LAST_DELIVERY_TIME = Integer.parseInt(property.getProperty("Last_Delivery_Time"));
		System.out.printf("Last_Delivery_Time: %5d%n", Clock.LAST_DELIVERY_TIME);
		// Robots
		int robots = Integer.parseInt(property.getProperty("Robots"));
		System.out.print("Robots: "); System.out.println(robots);
		assert(robots > 0);
	}
}
