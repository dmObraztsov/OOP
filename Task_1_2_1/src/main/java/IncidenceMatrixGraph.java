import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IncidenceMatrixGraph g)) {
            return false;
        }
        if (!vertices.equals(g.vertices)) {
            return false;
        }
        if (edges.size() != g.edges.size()) {
            return false;
        }

        List<List<String>> e1 = edges.stream()
                .map(e -> List.of(e[0], e[1]))
                .sorted(Comparator.comparing((List<String> l) -> l.getFirst())
                        .thenComparing(l -> l.get(1)))
                .toList();

        List<List<String>> e2 = g.edges.stream()
                .map(e -> List.of(e[0], e[1]))
                .sorted(Comparator.comparing((List<String> l) -> l.getFirst())
                        .thenComparing(l -> l.get(1)))
                .toList();

        return e1.equals(e2);
    }

    @Override
    public int hashCode() {
        int result = vertices.hashCode();
        List<List<String>> myList = edges.stream()
                .map(e -> List.of(e[0], e[1]))
                .sorted(Comparator.comparing((List<String> l) -> l.getFirst())
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

