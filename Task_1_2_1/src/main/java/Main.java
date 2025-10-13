public class Main {
    public static void main(String[] args) {
        Graph g = new AdjacencyListGraph();

        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");

        System.out.println("Graph:");
        System.out.println(g);

        System.out.println("Topological sort:");
        System.out.println(g.topologicalSort());
    }
}
