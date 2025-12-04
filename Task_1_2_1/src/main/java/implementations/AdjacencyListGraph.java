package implementations;

import interfaces.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AdjacencyListGraph implements Graph {
    private final Map<String, List<String>> adj = new HashMap<>();

    @Override
    public void addVertex(String vertex) {
        adj.putIfAbsent(vertex, new ArrayList<>());
    }

    @Override
    public void removeVertex(String vertex) {
        adj.remove(vertex);
        for (List<String> list : adj.values()) {
            list.remove(vertex);
        }
    }

    @Override
    public void addEdge(String from, String to) {
        addVertex(from);
        addVertex(to);
        adj.get(from).add(to);
    }

    @Override
    public void removeEdge(String from, String to) {
        List<String> list = adj.get(from);
        if (list != null) {
            list.remove(to);
        }
    }

    @Override
    public List<String> getNeighbors(String vertex) {
        return adj.getOrDefault(vertex, Collections.emptyList());
    }

    @Override
    public void readFromFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    addEdge(parts[0], parts[1]);
                } else if (parts.length == 1) {
                    addVertex(parts[0]);
                }
            }
        }
    }

    @Override
    public List<String> topologicalSort() {
        Map<String, Integer> indegree = new HashMap<>();
        for (String v : adj.keySet()) {
            indegree.put(v, 0);
        }
        for (String v : adj.keySet()) {
            for (String n : adj.get(v)) {
                indegree.put(n, indegree.get(n) + 1);
            }
        }

        Queue<String> q = new LinkedList<>();
        for (var e : indegree.entrySet()) {
            if (e.getValue() == 0) {
                q.add(e.getKey());
            }
        }


        List<String> result = new ArrayList<>();
        while (!q.isEmpty()) {
            String v = q.poll();
            result.add(v);
            for (String n : adj.get(v)) {
                indegree.put(n, indegree.get(n) - 1);
                if (indegree.get(n) == 0) {
                    q.add(n);
                }
            }
        }

        if (result.size() != adj.size()) {
            throw new RuntimeException("interfaces.Graph has a cycle!");
        }
        return result;
    }

    @Override
    public Map<String, List<String>> getAdjacencyRepresentation() {
        Map<String, List<String>> copy = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : adj.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            Collections.sort(copy.get(entry.getKey()));
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Graph otherGraph)) {
            return false;
        }

        Map<String, List<String>> thisRep = this.getAdjacencyRepresentation();
        Map<String, List<String>> otherRep = otherGraph.getAdjacencyRepresentation();

        return thisRep.equals(otherRep);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var e : adj.entrySet()) {
            sb.append(e.getKey()).append(" -> ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }
}
