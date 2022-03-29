package edu.caltech.cs2.datastructures;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;
import edu.caltech.cs2.interfaces.ISet;

import java.io.FileReader;
import java.io.IOException;

public class BeaverMapsGraph extends Graph<Long, Double> {
    private IDictionary<Long, Location> ids;
    private ISet<Location> buildings;
    private IDictionary<Long, Location> visited;

    public BeaverMapsGraph() {
        super();
        this.buildings = new ChainingHashSet<>();
        this.ids = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        this.visited = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
    }

    /**
     * Reads in buildings, waypoinnts, and roads file into this graph.
     * Populates the ids, buildings, vertices, and edges of the graph
     * @param buildingsFileName the buildings filename
     * @param waypointsFileName the waypoints filename
     * @param roadsFileName the roads filename
     */
    public BeaverMapsGraph(String buildingsFileName, String waypointsFileName, String roadsFileName) {
        this();
        JsonElement bs = fromFile(buildingsFileName);
        JsonElement ws = fromFile(waypointsFileName);
        JsonElement rs = fromFile(roadsFileName);
        for (JsonElement b : bs.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            this.ids.put(loc.id, loc);
            this.buildings.add(loc);
            this.addVertex(loc.id);
        }
        for (JsonElement w : ws.getAsJsonArray()) {
            Location loc = new Location(w.getAsJsonObject());
            this.ids.put(loc.id, loc);
            this.addVertex(loc.id);
        }
        for (JsonElement r : rs.getAsJsonArray()) {
            for (int i = 0; i < r.getAsJsonArray().size() - 1; i++) {
                Long n1 = r.getAsJsonArray().get(i).getAsLong();
                Long n2 = r.getAsJsonArray().get(i+1).getAsLong();
                this.addUndirectedEdge(n1, n2, this.ids.get(n1).getDistance(this.ids.get(n2)));
            }
        }
    }

    /**
     * Returns a deque of all the locations with the name locName.
     * @param locName the name of the locations to return
     * @return a deque of all location with the name locName
     */
    public IDeque<Location> getLocationByName(String locName) {
        IDeque<Location> locations = new ArrayDeque<>();
        for (Location loc : this.ids.values()) {
            if (loc.name != null && loc.name.equals(locName)) {
                locations.addBack(loc);
            }
        }
        return locations;
    }

    /**
     * Returns the Location object corresponding to the provided id
     * @param id the id of the object to return
     * @return the location identified by id
     */
    public Location getLocationByID(long id) {
        return this.ids.get(id);
    }

    /**
     * Adds the provided location to this map.
     * @param n the location to add
     * @return true if n is a new location and false otherwise
     */
    public boolean addVertex(Location n) {
        this.ids.put(n.id, n);
        if (n.type == Location.Type.BUILDING) {
            this.buildings.add(n);
        }
        return super.addVertex(n.id);
    }

    /**
     * Returns the closest building to the location (lat, lon)
     * @param lat the latitude of the location to search near
     * @param lon the longitute of the location to search near
     * @return the building closest to (lat, lon)
     */
    public Location getClosestBuilding(double lat, double lon) {
        IPriorityQueue<Location> heap = new MinFourHeap<>();
        for (Location loc : this.buildings) {
            double distance = loc.getDistance(lat, lon);
            IPriorityQueue.PQElement<Location> element = new IPriorityQueue.PQElement<>(loc, distance);
            heap.enqueue(element);
        }
        return heap.peek().data;
    }

    /**
     * Returns a set of locations which are reachable along a path that goes no further than `threshold` feet from start
     * @param start the location to search around
     * @param threshold the number of feet in the search radius
     * @return
     */
    public ISet<Location> dfs(Location start, double threshold) {
        this.visited = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        IDictionary<Location, Long> locations = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        this.dfsHelper(start, start, threshold, locations);
        return locations.keySet();
    }

    private void dfsHelper(Location start, Location current, double threshold, IDictionary<Location, Long> locations) {
        this.visited.put(current.id, current);
        locations.put(current, current.id);
        for (Long neighbor : this.neighbors(current.id)) {
            if (this.ids.get(neighbor).getDistance(start) <= threshold) {
                if (!this.visited.containsKey(neighbor)) {
                    this.dfsHelper(start, this.ids.get(neighbor), threshold, locations);
                }
            }
        }
    }

    /**
     * Returns a list of Locations corresponding to
     * buildings in the current map.
     * @return a list of all building locations
     */
    public ISet<Location> getBuildings() {
        return this.buildings;
    }

    /**
     * Returns a shortest path (i.e., a deque of vertices) between the start
     * and target locations (including the start and target locations).
     * @param start the location to start the path from
     * @param target the location to end the path at
     * @return a shortest path between start and target
     */
    public IDeque<Location> dijkstra(Location start, Location target) {
        this.visited = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        IDictionary<Long, Double> shortestLength = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        IDictionary<Long, Long> shortestPathParent = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        IPriorityQueue<Location> worklist = new MinFourHeap<>();
        Location current = start;
        shortestLength.put(start.id, 0.0);
        shortestPathParent.put(start.id, start.id);
        worklist.enqueue(new IPriorityQueue.PQElement<>(start, 0.0));

        while (!current.equals(target) && worklist.size() != 0) {
            worklist.dequeue();
            IDictionary<Long, Double> neighbors = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
            for (Long neighbor : this.neighbors(current.id)) {
                if (this.ids.get(neighbor).type != Location.Type.BUILDING || neighbor.equals(target.id)) {
                    if (!this.visited.containsKey(neighbor)) {
                        neighbors.put(neighbor, this.adjacent(current.id, neighbor));
                    }
                }
            }
            for (Long neighbor : neighbors) {
                double length = shortestLength.get(current.id) + this.adjacent(current.id, neighbor);
                if (!shortestLength.containsKey(neighbor)) {
                    shortestLength.put(neighbor, length);
                    worklist.enqueue(new IPriorityQueue.PQElement<>(this.ids.get(neighbor), length));
                    shortestPathParent.put(neighbor, current.id);
                } else {
                    if (shortestLength.get(neighbor) > length) {
                        shortestLength.put(neighbor, length);
                        worklist.decreaseKey(new IPriorityQueue.PQElement<>(this.ids.get(neighbor), length));
                        shortestPathParent.put(neighbor, current.id);
                    }
                }
            }
            this.visited.put(current.id, current);
            if (worklist.size() != 0) {
                current = worklist.peek().data;
            }
        }

        if (shortestPathParent.get(target.id) == null) {
            return null;
        }

        IDeque<Location> path = new ArrayDeque<>();
        Long backTrackCurrent = target.id;
        path.addFront(this.ids.get(backTrackCurrent));
        while (!shortestPathParent.get(backTrackCurrent).equals(backTrackCurrent)) {
            backTrackCurrent = shortestPathParent.get(backTrackCurrent);
            path.addFront(this.ids.get(backTrackCurrent));
        }
        return path;
    }

    /**
     * Returns a JsonElement corresponding to the data in the file
     * with the filename filename
     * @param filename the name of the file to return the data from
     * @return the JSON data from filename
     */
    private static JsonElement fromFile(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            return null;
        }
    }
}
