package exceptions;

/**
 * This exception is thrown when a mailitem is too heavy for the robot/team to deliver
 */
public class ItemTooHeavyException extends Exception {
    public ItemTooHeavyException(){
        super("Item too heavy! Dropped by robot.");
    }
}
