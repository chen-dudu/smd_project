package mycontroller;

import utilities.Coordinate;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Team: W9-5
 * Description:
 */
public class Item implements Comparable<Item> {
    private Coordinate coordinate;
    private int priority;

    public Item(Coordinate coordinate){
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate(){
        return coordinate;
    }

    public int getPriority(){
        return priority;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public int compareTo(Item other)
    {
        return this.priority - other.priority;
    }

    public boolean equals(Object i){
        if(i == this){
            return true;
        }
        if(!(i instanceof Item)){
            return false;
        }
        Item item = (Item) i;
        return (item.getCoordinate().x == this.coordinate.x) && (item.getCoordinate().y == this.coordinate.y);
    }

    public int hashCode(){
        return Objects.hash(this.coordinate.x,this.coordinate.y);
    }

}