package mycontroller.algorithms;

import tiles.MapTile;
import utilities.Coordinate;
import mycontroller.adapters.TileType;
import mycontroller.adapters.AdapterFactory;

import java.util.*;

/**
 * Team: W9-5
 * Description: Dijkstra compute the shortest path from the start to destinations
 */
public class Dijkstra implements iSearchStrategy {

    // record all of the coordinates of wall tile
    private ArrayList<Coordinate> wall;

    // an up-to-date map
    private HashMap<Coordinate, MapTile> map;

    // a table contain cost for different tiles
    private HashMap<TileType, Integer> costTable;

    public Dijkstra(){ }

    @Override
    public ArrayList<Coordinate> search(Coordinate start_coord, ArrayList<Coordinate> destination_coords,
                                        HashMap<Coordinate,MapTile> map, HashMap<TileType, Integer> costTable) {

        // return path as ArrayList of [start, start] when start coordinate exist in destinations
        for (Coordinate c: destination_coords){
            if (start_coord.equals(c)){
                ArrayList<Coordinate> path_r = new ArrayList<>();
                path_r.add(c);
                path_r.add(c);
                return path_r;
            }
        }

        // update map and costTable
        this.map = map;
        this.costTable = costTable;

        Coordinate destination_coord = destination_coords.get(0);
        int num_found = 0;
        boolean found_path;
        found_path = false;

        // use map to find all wall tiles and store it as wall
        setWall(map);

        // initialize variables
        Item start;
        Item current;
        Item next;
        int new_cost;
        PriorityQueue<Item> frontier = new PriorityQueue<>();
        HashMap<Item, Item> came_from = new HashMap<>();
        HashMap<Item, Integer> cost_so_far = new HashMap<>();
        ArrayList<Item> neighbours;
        Iterator<Item> neighbours_iter;

        // put start item in priority queue
        start = new Item(start_coord);
        start.setPriority(0);
        frontier.add(start);
        came_from.put(start, null);
        cost_so_far.put(start, 0);

        // while the priority queue is not empty, poll one item and process its neighbours
        while (frontier.peek() != null) {
            current = frontier.poll();

            // break if find the shortest path and set found_path to ture
            for (Coordinate destination: destination_coords){
                if (calculate_distance(current.getCoordinate(), destination) == 0) {
                    num_found++;
                    if (num_found == 1){
                        destination_coord = destination;
                    }
                    if (cost_so_far.get(new Item(destination)) < cost_so_far.get(new Item(destination_coord))){
                        destination_coord = destination;
                    }
                    found_path = true;
                    if (num_found == destination_coords.size()){
                        break;
                    }
                }
            }

            // assign cost(priority) to each neighbour to current item
            neighbours = neighbours(current);
            neighbours_iter = neighbours.iterator();
            while (neighbours_iter.hasNext()) {
                next = neighbours_iter.next();
                new_cost = cost_so_far.get(current) + getCost(next.coordinate);
                if (!cost_so_far.containsKey(next) || new_cost < cost_so_far.get(next)) {
                    next.setPriority(new_cost);
                    cost_so_far.put(next, new_cost);
                    frontier.add(next);
                    came_from.put(next, current);
                }
            }
        }

        // reconstruct path if there is a path from start to destination
        ArrayList<Coordinate> path;
        if (found_path){
            path = reconstruct_path(came_from, start_coord, destination_coord);
        } else {
            path = null;
        }
        return path;

    }

    // use costTable to get cost according to the tile type
    private int getCost(Coordinate coordinate){
        MapTile tile;
        TileType tileType;
        AdapterFactory factory = AdapterFactory.getInstance();
        tile = map.get(coordinate);
        tileType = factory.getAdapter(tile).getType(tile);
        int cost = 1;
        if (costTable.keySet().contains(tileType)){
            cost = costTable.get(factory.getAdapter(tile).getType(tile));
        }
        return cost;
    }


    // came_from record each item and the place where it came from, this function reconstruct path from start to goal
    private ArrayList<Coordinate> reconstruct_path(HashMap<Item, Item> came_from,
                                                   Coordinate start, Coordinate goal){
        Item current_item;
        Item destination_item;
        Coordinate current;
        destination_item = new Item(goal);
        current = goal;
        current_item = destination_item;
        ArrayList<Coordinate> path = new ArrayList<>();
        while (!(calculate_distance(current, start) == 0)) {
            path.add(current);
            current_item = came_from.get(current_item);
            current = current_item.getCoordinate();
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    // use map to find all wall tiles and store it as wall
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

    // calculate the distance between to coordinates
    private int calculate_distance(Coordinate coordinate, Coordinate destination) {
        int dx = Math.abs(coordinate.x - destination.x);
        int dy = Math.abs(coordinate.y - destination.y);
        return dx + dy;
    }

    // find all neighbour items of the item
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
        if (!wall.contains(new_coordinate) && map.keySet().contains(new_coordinate)){
            new_item = new Item(new_coordinate);
            neighbours.add(new_item);
        }

        new_x = coordinate.x;
        new_y = coordinate.y + -1;
        new_coordinate = new Coordinate(new_x + "," + new_y);
        if (!wall.contains(new_coordinate) && map.keySet().contains(new_coordinate)){
            new_item = new Item(new_coordinate);
            neighbours.add(new_item);
        }

        new_x = coordinate.x;
        new_y = coordinate.y + 1;
        new_coordinate = new Coordinate(new_x + "," + new_y);
        if (!wall.contains(new_coordinate) && map.keySet().contains(new_coordinate)){
            new_item = new Item(new_coordinate);
            neighbours.add(new_item);
        }

        new_x = coordinate.x + -1;
        new_y = coordinate.y;
        new_coordinate = new Coordinate(new_x + "," + new_y);
        if (!wall.contains(new_coordinate) && map.keySet().contains(new_coordinate)){
            new_item = new Item(new_coordinate);
            neighbours.add(new_item);
        }

        return neighbours;
    }

    // Item contains coordinate and priority of this item
    class Item implements Comparable<Item> {
        private Coordinate coordinate;
        private int priority;

        private Item(Coordinate coordinate){
            this.coordinate = coordinate;
        }

        public Coordinate getCoordinate(){
            return coordinate;
        }

        // set the priority for priority queue to compare
        private void setPriority(int priority){
            this.priority = priority;
        }

        public int compareTo(Item other)
        {
            return this.priority - other.priority;
        }

        /**
         * Defined in order to use it as keys in a hashmap
         */
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