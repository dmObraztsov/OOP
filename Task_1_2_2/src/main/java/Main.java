import java.util.ConcurrentModificationException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        HashTable<String, Integer> table = new HashTable<>();

        table.put("one", 1);
        table.put("two", 2);
        table.put("three", 3);

        System.out.println(table);
        System.out.println(table.size());
        System.out.println(table.containsKey("two"));
        System.out.println(table.get("three"));

        table.remove("two");
        System.out.println(table);

        try {
            for (Map.Entry<String, Integer> e : table) {
                System.out.println(e.getKey() + " -> " + e.getValue());
                table.put("four", 4);
            }
        } catch (ConcurrentModificationException ex) {
            System.out.println("diff while iteration");
        }

        HashTable<String, Integer> another = new HashTable<>();
        another.put("one", 1);
        another.put("three", 3);
        System.out.println(table.equals(another));
    }
}
