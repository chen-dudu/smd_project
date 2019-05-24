package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.*;

/**
 * Team: W9-5
 * Description:
 */
public class Dijkstra {
    private Coordinate start_coordinate;
    private Coordinate destination_coordinate;
    private ArrayList<Coordinate> wall;

    public ArrayList<Coordinate> run() {
        Item start = new Item(this.start_coordinate);
        start.setPriority(0);
        Item current;
        Item next;
        int new_cost;
//        boolean contain = false;

        PriorityQueue<Item> frontier = new PriorityQueue<>();
        frontier.add(start);
        HashMap<Item, Item> came_from = new HashMap<>();
        HashMap<Item, Integer> cost_so_far = new HashMap<>();
        came_from.put(start, null);
        cost_so_far.put(start, 0);
        ArrayList<Item> neighbours;
        Iterator<Item> neighbours_iter;
        while (frontier.peek() != null) {
//            System.out.print(" |0 ");
            current = frontier.poll();
//            System.out.print(current.getCoordinate().toString());
//            System.out.print(" |1 ");
            if (calculate_distance(current.getCoordinate(), destination_coordinate) == 0) {
                break;
            }
            neighbours = neighbours(current);
            neighbours_iter = neighbours.iterator();
//            System.out.print(" |2 ");
            while (neighbours_iter.hasNext()) {
//                System.out.print(" |3 ");
                next = neighbours_iter.next();
//                System.out.print(" |4 ");
                new_cost = cost_so_far.get(current) + 1;
//                System.out.print(" |5 ");

//                contain = true;
//                for (Item item: cost_so_far.keySet()) {
//                    if (item.getCoordinate().x == next.getCoordinate().x && item.getCoordinate().y == next.getCoordinate().y){
//                        contain = true;
//                    }
//                }
                if (!cost_so_far.containsKey(next) || new_cost < cost_so_far.get(next)) {
//                    System.out.print(" |6 ");
                    next.setPriority(new_cost);
                    cost_so_far.put(next, new_cost);
                    frontier.add(next);
                    came_from.put(next, current);
                }
//                System.out.print(" |7 ");
            }
        }
        ArrayList<Coordinate> path = reconstruct_path(came_from, start_coordinate, destination_coordinate);
        System.out.print(" |OO ");
//        HashMap<Coordinate, Coordinate> came_from_coordinate = new HashMap<>();
//        for (Item item: came_from.keySet()){
//            came_from_coordinate.put(item.getCoordinate(), came_from.get(item).getCoordinate());
//        }
//        System.out.print(" |OO ");
        return path;
    }

    public ArrayList<Coordinate> reconstruct_path(HashMap<Item, Item> came_from,
                                                  Coordinate start, Coordinate goal){
        Item current_item;
        Item destination_item;
        Coordinate current;
        destination_item = new Item(goal);
        current = goal;
        current_item = destination_item;
        ArrayList<Coordinate> path = new ArrayList<>();
        while (!(calculate_distance(current, start_coordinate) == 0)) {
            path.add(current);
            System.out.println('(' + current.toString() + ')' + ' ');
            current_item = came_from.get(current_item);
            current = current_item.getCoordinate();
        }
        path.add(start_coordinate);
        Collections.reverse(path);
        System.out.println('a');
        for (Coordinate coordinate: path){
            System.out.println('(' + coordinate.toString() + ')' + ' ');
        }
        return path;
    }

//    def reconstruct_path(came_from, start, goal):
//    current = goal
//    path = []
//            while current != start:
//            path.append(current)
//    current = came_from[current]
//            path.append(start) # optional
//    path.reverse() # optional
//    return path

    public void setStart_coordinate(Coordinate start_coordinate){
        this.start_coordinate = start_coordinate;
    }

    public void setDestination_coordinate(Coordinate destination_coordinate){
        this.destination_coordinate = destination_coordinate;
    }

    public Coordinate getStart_coordinate(){
        return start_coordinate;
    }

    public Coordinate getDestination_coordinate(){
        return destination_coordinate;
    }

    public void setWall(ArrayList<Coordinate> wall){
        this.wall = wall;
    }

    private int calculate_distance(Coordinate coordinate, Coordinate destination) {
        int dx = Math.abs(coordinate.x - destination.x);
        int dy = Math.abs(coordinate.y - destination.y);
        return dx + dy;
    }

    public ArrayList<Item> neighbours(Item item){
        ArrayList<Item> neighbours = new ArrayList<Item>();
        int new_x;
        int new_y;
        Coordinate coordinate = item.getCoordinate();
        Coordinate new_coordinate;
        Item new_item;

        new_x = coordinate.x + 1;
        new_y = coordinate.y;
        new_coordinate = new Coordinate(new_x + "," + new_y);
        if (!wall.contains(new_coordinate)){
            new_item = new Item(new_coordinate);
            neighbours.add(new_item);
        }

        new_x = coordinate.x;
        new_y = coordinate.y + -1;
        new_coordinate = new Coordinate(new_x + "," + new_y);
        if (!wall.contains(new_coordinate)){
            new_item = new Item(new_coordinate);
            neighbours.add(new_item);
        }

        new_x = coordinate.x;
        new_y = coordinate.y + 1;
        new_coordinate = new Coordinate(new_x + "," + new_y);
        if (!wall.contains(new_coordinate)){
            new_item = new Item(new_coordinate);
            neighbours.add(new_item);
        }

        new_x = coordinate.x + -1;
        new_y = coordinate.y;
        new_coordinate = new Coordinate(new_x + "," + new_y);
        if (!wall.contains(new_coordinate)){
            new_item = new Item(new_coordinate);
            neighbours.add(new_item);
        }

        return neighbours;
    }
}





//    private void run_1(){
//        Item current;
//        Item next;
//        int new_cost;
//        PriorityQueue<Item> frontier = new PriorityQueue<Item>();
//        ArrayList<Item> visited = new ArrayList<Item>();
//        frontier.add(this.start_item);
//        start_item.setCome_from(null);
//        start_item.setCost_so_far(0);
//        visited.add(start_item);
//        ArrayList<Item> neighbours;
//        Iterator<Item> neighbours_iter;
//        while (frontier.peek() != null) {
//            current = frontier.poll();
//            if (current.getDistance() == 0){
//                break;
//            }
//            neighbours = current.neighbours();
//            neighbours_iter = neighbours.iterator();
//            while (neighbours_iter.hasNext()) {
//                next = neighbours_iter.next();
//                new_cost = current.getCost_so_far() + 1;
//                if (visited.contains(next) || new_cost < next.getCost_so_far()){
//                    next.setCost_so_far(new_cost);
//                    visited.add(next);
//                    next.setCome_from(current);
//                    frontier.add(next);
//                }
//            }
//        }
//    }