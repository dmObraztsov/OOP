import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class AdjacencyMatrixGraph implements Graph {
    private final List<String> vertices = new ArrayList<>();
    private boolean[][] matrix = new boolean[10][10];

    private void ensureCapacity(int size) {
        if (size > matrix.length) {
            boolean[][] newMatrix = new boolean[size * 2][size * 2];
            for (int i = 0; i < vertices.size(); i++) {
                System.arraycopy(matrix[i], 0, newMatrix[i], 0, vertices.size());
            }
            matrix = newMatrix;
        }
    }

    @Override
    public void addVertex(String vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
            ensureCapacity(vertices.size());
        }
    }

    @Override
    public void removeVertex(String vertex) {
        int idx = vertices.indexOf(vertex);
        if (idx == -1) {
            return;
        }
        vertices.remove(idx);
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = idx; j < vertices.size(); j++) {
                matrix[i][j] = matrix[i][j + 1];
                matrix[j][i] = matrix[j + 1][i];
            }
        }
    }

    @Override
    public void addEdge(String from, String to) {
        addVertex(from);
        addVertex(to);
        int i = vertices.indexOf(from);
        int j = vertices.indexOf(to);
        matrix[i][j] = true;
    }

    @Override
    public void removeEdge(String from, String to) {
        int i = vertices.indexOf(from);
        int j = vertices.indexOf(to);
        if (i != -1 && j != -1) {
            matrix[i][j] = false;
        }
    }

    @Override
    public List<String> getNeighbors(String vertex) {
        int i = vertices.indexOf(vertex);
        if (i == -1) {
            return Collections.emptyList();
        }
        List<String> res = new ArrayList<>();
        for (int j = 0; j < vertices.size(); j++) {
            if (matrix[i][j]) {
                res.add(vertices.get(j));
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
        for (String v : vertices) {
            tmp.addVertex(v);
        }
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (matrix[i][j]) {
                    tmp.addEdge(vertices.get(i), vertices.get(j));
                }
            }
        }
        return tmp.topologicalSort();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AdjacencyMatrixGraph g)) {
            return false;
        }
        return vertices.equals(g.vertices) && Arrays.deepEquals(matrix, g.matrix);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("   ");
        for (String v : vertices) {
            sb.append(v).append(" ");
        }
        sb.append("\n");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append(vertices.get(i)).append(": ");
            for (int j = 0; j < vertices.size(); j++) {
                sb.append(matrix[i][j] ? "1 " : "0 ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
