package implementations;

import interfaces.Graph;
import java.io.IOException;
import java.util.*;


public class IncidenceMatrixGraph implements Graph {
    private final List<String> vertices = new ArrayList<>();
    private final List<String[]> edges = new ArrayList<>();

    @Override
    public void addVertex(String vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
        }
    }

    @Override
    public void removeVertex(String vertex) {
        vertices.remove(vertex);
        edges.removeIf(e -> e[0].equals(vertex) || e[1].equals(vertex));
    }

    @Override
    public void addEdge(String from, String to) {
        addVertex(from);
        addVertex(to);
        edges.add(new String[]{from, to});
    }

    @Override
    public void removeEdge(String from, String to) {
        edges.removeIf(e -> e[0].equals(from) && e[1].equals(to));
    }

    @Override
    public List<String> getNeighbors(String vertex) {
        List<String> res = new ArrayList<>();
        for (String[] e : edges) {
            if (e[0].equals(vertex)) {
                res.add(e[1]);
            }

        }
        return res;
    }

    @Override
    public void readFromFile(String filename) throws IOException {
        AdjacencyListGraph temp = new AdjacencyListGraph();
        temp.readFromFile(filename);
        for (String v : temp.topologicalSort()) {
            addVertex(v);
        }
        for (String v : temp.topologicalSort()) {
            for (String n : temp.getNeighbors(v)) {
                addEdge(v, n);
            }
        }
    }


    @Override
    public List<String> topologicalSort() {
        AdjacencyListGraph tmp = new AdjacencyListGraph();
        for (String[] e : edges) {
            tmp.addEdge(e[0], e[1]);
        }
        return tmp.topologicalSort();
    }

    @Override
    public Map<String, List<String>> getAdjacencyRepresentation() {
        Map<String, List<String>> representation = new HashMap<>();

        for (String vertex : vertices) {
            representation.put(vertex, new ArrayList<>());
        }

        for (String[] edge : edges) {
            representation.get(edge[0]).add(edge[1]);
        }

        for (List<String> neighbors : representation.values()) {
            Collections.sort(neighbors);
        }

        return representation;
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
    public int hashCode() {
        int result = vertices.hashCode();
        List<List<String>> myList = edges.stream()
                .map(e -> List.of(e[0], e[1]))
                .sorted(Comparator.comparing((List<String> l) -> l.get(0))
                        .thenComparing(l -> l.get(1)))
                .toList();
        result = 31 * result + myList.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Vertices: " + vertices + "\nEdges:\n");
        for (String[] e : edges) {
            sb.append(e[0]).append(" -> ").append(e[1]).append("\n");
        }
        return sb.toString();
    }
}

