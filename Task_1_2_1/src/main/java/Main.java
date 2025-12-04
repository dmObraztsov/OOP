import implementations.AdjacencyListGraph;
import implementations.AdjacencyMatrixGraph;
import implementations.IncidenceMatrixGraph;
import interfaces.Graph;

public class Main {
    public static void main(String[] args) {
        Graph graph1 = new AdjacencyListGraph();
        Graph graph2 = new AdjacencyMatrixGraph();
        Graph graph3 = new IncidenceMatrixGraph();

        graph1.addEdge("A", "B");
        graph1.addEdge("B", "C");
        graph1.addEdge("A", "C");

        graph2.addEdge("A", "B");
        graph2.addEdge("B", "C");
        graph2.addEdge("A", "C");

        graph3.addEdge("A", "B");
        graph3.addEdge("B", "C");
        graph3.addEdge("A", "C");

        System.out.println("graph1 equals graph2: " + graph1.equals(graph2)); // true
        System.out.println("graph1 equals graph3: " + graph1.equals(graph3)); // true
        System.out.println("graph2 equals graph3: " + graph2.equals(graph3)); // true

        graph1.addEdge("C", "D");
        System.out.println("After modification:");
        System.out.println("graph1 equals graph2: " + graph1.equals(graph2)); // false
    }
}
