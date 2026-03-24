import java.util.*;

public class TrainSystem {

    // Graph structure
    static Map<String, List<Edge>> graph = new HashMap<>();

    static class Edge {
        String dest;
        int weight;

        Edge(String d, int w) {
            dest = d;
            weight = w;
        }
    }

    // ---------- ADD EDGE ----------
    static void addEdge(String u, String v, int w) {
        graph.putIfAbsent(u, new ArrayList<>());
        graph.get(u).add(new Edge(v, w));
    }

    // ---------- DIJKSTRA ----------
    static List<String> dijkstra(String start, String end) {

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();

        // initialize distances
        for (String node : graph.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
            prev.put(node, null);
        }

        dist.put(start, 0);

        PriorityQueue<Pair> pq = new PriorityQueue<>(
                (a, b) -> a.distance - b.distance
        );

        pq.add(new Pair(start, 0));

        while (!pq.isEmpty()) {
            Pair current = pq.poll();
            String node = current.node;

            for (Edge e : graph.getOrDefault(node, new ArrayList<>())) {

                int newDist = dist.get(node) + e.weight;

                if (newDist < dist.get(e.dest)) {
                    dist.put(e.dest, newDist);
                    prev.put(e.dest, node);
                    pq.add(new Pair(e.dest, newDist));
                }
            }
        }

        // reconstruct path
        List<String> path = new ArrayList<>();
        String curr = end;

        while (curr != null) {
            path.add(curr);
            curr = prev.get(curr);
        }

        Collections.reverse(path);
        return path;
    }

    static class Pair {
        String node;
        int distance;

        Pair(String n, int d) {
            node = n;
            distance = d;
        }
    }

    // ---------- TRAIN SIMULATION ----------
    static void simulate(List<Train> trains) {

        Map<String, Integer> trackBusy = new HashMap<>();

        boolean finished = false;

        while (!finished) {
            finished = true;

            for (Train t : trains) {

                if (t.index < t.path.size() - 1) {
                    finished = false;

                    String u = t.path.get(t.index);
                    String v = t.path.get(t.index + 1);

                    String edgeKey = u + "-" + v;

                    int travelTime = getWeight(u, v);

                    // check conflict
                    if (!trackBusy.containsKey(edgeKey) || t.time >= trackBusy.get(edgeKey)) {

                        t.time += travelTime;
                        t.index++;

                        trackBusy.put(edgeKey, t.time);

                        System.out.println(t.name + " moved " + u + " → " + v + " at time " + t.time);

                    } else {
                        // WAIT
                        t.time += 1;
                        System.out.println(t.name + " waiting at " + u + " at time " + t.time);
                    }
                }
            }
        }

        System.out.println("\nFinal Times:");
        for (Train t : trains) {
            System.out.println(t.name + " : " + t.time);
        }
    }

    // ---------- GET EDGE WEIGHT ----------
    static int getWeight(String u, String v) {
        for (Edge e : graph.get(u)) {
            if (e.dest.equals(v)) {
                return e.weight;
            }
        }
        return 0;
    }

    // ---------- TRAIN STRUCT ----------
    static class Train {
        String name;
        List<String> path;
        int index;
        int time;

        Train(String n, List<String> p) {
            name = n;
            path = p;
            index = 0;
            time = 0;
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {

        // Graph
        addEdge("A", "B", 2);
        addEdge("A", "C", 4);
        addEdge("B", "D", 3);
        addEdge("C", "D", 1);
        addEdge("D", "E", 2);

        // Paths using Dijkstra
        List<String> path1 = dijkstra("A", "E");
        List<String> path2 = dijkstra("C", "E");

        // Trains
        Train t1 = new Train("T1", path1);
        Train t2 = new Train("T2", path2);

        List<Train> trains = new ArrayList<>();
        trains.add(t1);
        trains.add(t2);

        simulate(trains);
    }
}