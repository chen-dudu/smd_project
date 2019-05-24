package mycontroller.strategies;

import mycontroller.adapters.AdapterFactory;
import mycontroller.adapters.TileType;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.*;

/**
 * Team: W9-5
 * Description:
 */
public class Dijkstra implements iSearchStrategy {
    private ArrayList<Coordinate> wall;
    private ArrayList<Coordinate> path;
    private int cost;

    public Dijkstra(){ }

    public void search(Coordinate start_coord, Coordinate destination_coord, HashMap<Coordinate,MapTile> map) {
        Item start = new Item(start_coord);
        setWall(map);
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
            if (calculate_distance(current.getCoordinate(), destination_coord) == 0) {
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
        this.path = reconstruct_path(came_from, start_coord, destination_coord);
        System.out.print(" |OO ");
//        HashMap<Coordinate, Coordinate> came_from_coordinate = new HashMap<>();
//        for (Item item: came_from.keySet()){
//            came_from_coordinate.put(item.getCoordinate(), came_from.get(item).getCoordinate());
//        }
//        System.out.print(" |OO ");
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public ArrayList<Coordinate> getPath() {
        return path;
    }

    private ArrayList<Coordinate> reconstruct_path(HashMap<Item, Item> came_from,
                                                  Coordinate start, Coordinate goal){
        Item current_item;
        Item destination_item;
        Coordinate current;
        destination_item = new Item(goal);
        current = goal;
        current_item = destination_item;
        ArrayList<Coordinate> path = new ArrayList<>();
        int total_cost = 0;
        while (!(calculate_distance(current, start) == 0)) {
            path.add(current);
            total_cost += 1;
            System.out.println('(' + current.toString() + ')' + ' ');
            current_item = came_from.get(current_item);
            current = current_item.getCoordinate();
        }
        this.cost = total_cost;
        path.add(start);
        Collections.reverse(path);
        System.out.println('a');
        for (Coordinate coordinate: path){
            System.out.println('(' + coordinate.toString() + ')' + ' ');
        }
        return path;
    }

    private void setWall(HashMap<Coordinate,MapTile> map){
        MapTile tile;
        ArrayList<Coordinate> wall = new ArrayList<>();
        AdapterFactory factory = AdapterFactory.getInstance();
        for (Coordinate coordinate: map.keySet()){
            tile = map.get(coordinate);
            if (factory.getAdapter(tile).getType(tile) == TileType.WALL){
                wall.add(coordinate);
            }
        }
        this.wall = wall;
    }

    private int calculate_distance(Coordinate coordinate, Coordinate destination) {
        int dx = Math.abs(coordinate.x - destination.x);
        int dy = Math.abs(coordinate.y - destination.y);
        return dx + dy;
    }

    private ArrayList<Item> neighbours(Item item){
        ArrayList<Item> neighbours = new ArrayList<>();
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

    class Item implements Comparable<Item> {
        private Coordinate coordinate;
        private int priority;

        private Item(Coordinate coordinate){
            this.coordinate = coordinate;
        }

        public Coordinate getCoordinate(){
            return coordinate;
        }

        private void setPriority(int priority){
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
}
