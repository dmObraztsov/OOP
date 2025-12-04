package interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Graph {
    void addVertex(String vertex);

    void removeVertex(String vertex);

    void addEdge(String from, String to);

    void removeEdge(String from, String to);

    List<String> getNeighbors(String vertex);

    void readFromFile(String filename) throws IOException;

    List<String> topologicalSort();

    boolean equals(Object o);

    String toString();

    Map<String, List<String>> getAdjacencyRepresentation();

}
