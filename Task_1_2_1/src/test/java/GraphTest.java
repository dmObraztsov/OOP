import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private List<Graph> createGraphs() {
        return List.of(
                new AdjacencyListGraph(),
                new AdjacencyMatrixGraph(),
                new IncidenceMatrixGraph()
        );
    }

    @Test
    void testAddAndRemoveVertex() {
        for (Graph g : createGraphs()) {
            g.addVertex("A");
            g.addVertex("B");
            assertTrue(g.getNeighbors("A").isEmpty());
            g.removeVertex("B");
            assertTrue(g.getNeighbors("B").isEmpty());
        }
    }

    @Test
    void testAddAndRemoveEdge() {
        for (Graph g : createGraphs()) {
            g.addEdge("A", "B");
            g.addEdge("A", "C");
            assertEquals(Set.of("B", "C"), new HashSet<>(g.getNeighbors("A")));
            g.removeEdge("A", "B");
            assertEquals(List.of("C"), g.getNeighbors("A"));
        }
    }

    @Test
    void testTopologicalSort() {
        for (Graph g : createGraphs()) {
            g.addEdge("A", "B");
            g.addEdge("B", "C");
            g.addEdge("A", "C");
            List<String> order = g.topologicalSort();

            // A должно быть до B, B — до C
            assertTrue(order.indexOf("A") < order.indexOf("B"));
            assertTrue(order.indexOf("B") < order.indexOf("C"));
        }
    }

    @Test
    void testEqualsAndToString() {
        for (Graph g1 : createGraphs()) {
            Graph g2;
            if (g1 instanceof AdjacencyListGraph)
                g2 = new AdjacencyListGraph();
            else if (g1 instanceof AdjacencyMatrixGraph)
                g2 = new AdjacencyMatrixGraph();
            else
                g2 = new IncidenceMatrixGraph();

            g1.addEdge("A", "B");
            g2.addEdge("A", "B");

            assertEquals(g1, g2, "Graphs with same structure must be equal");
            assertTrue(g1.toString().contains("A"), "toString must include vertices");
        }
    }

    @Test
    void testCycleDetectionInTopologicalSort() {
        for (Graph g : createGraphs()) {
            g.addEdge("A", "B");
            g.addEdge("B", "A");
            assertThrows(RuntimeException.class, g::topologicalSort,
                    "Cyclic graph must throw exception in topological sort");
        }
    }

    @Test
    void testReadFromFile() throws IOException {
        Path temp = Files.createTempFile("graph", ".txt");
        Files.writeString(temp, "A B\nB C\nC D\n");

        for (Graph g : createGraphs()) {
            g.readFromFile(temp.toString());
            assertEquals(Set.of("B"), new HashSet<>(g.getNeighbors("A")));
            assertEquals(Set.of("C"), new HashSet<>(g.getNeighbors("B")));
        }

        Files.deleteIfExists(temp);
    }
}
