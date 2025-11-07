import java.util.*;

public class HashTable<K, V> implements Iterable<Map.Entry<K, V>> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;
    private final float loadFactor;
    private int threshold;
    private int modCount;

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Invalid load factor");
        }
        this.table = (Node<K, V>[]) new Node[powerOfTwoFor(initialCapacity)];
        this.loadFactor = loadFactor;
        this.threshold = (int) (table.length * loadFactor);
        this.size = 0;
        this.modCount = 0;
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTable() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    private static final class Node<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        final int hash;

        Node(K key, V value, Node<K, V> next, int hash) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.hash = hash;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            V old = value;
            value = newValue;
            return old;
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public V put(K key, V value) {
        int hash = hash(key);
        int idx = indexFor(hash, table.length);
        for (Node<K, V> n = table[idx]; n != null; n = n.next) {
            if (n.hash == hash && Objects.equals(n.key, key)) {
                V old = n.value;
                n.value = value;
                return old;
            }
        }
        Node<K, V> newNode = new Node<>(key, value, table[idx], hash);
        table[idx] = newNode;
        size++;
        modCount++;
        if (size > threshold) {
            resize(2 * table.length);
        }
        return null;
    }

    public V get(K key) {
        int hash = hash(key);
        int idx = indexFor(hash, table.length);
        for (Node<K, V> n = table[idx]; n != null; n = n.next) {
            if (n.hash == hash && Objects.equals(n.key, key)) {
                return n.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        return getNode(key) != null;
    }

    public V remove(K key) {
        int hash = hash(key);
        int idx = indexFor(hash, table.length);
        Node<K, V> prev = null;
        for (Node<K, V> n = table[idx]; n != null; prev = n, n = n.next) {
            if (n.hash == hash && Objects.equals(n.key, key)) {
                if (prev == null) {
                    table[idx] = n.next;
                } else prev.next = n.next;
                size--;
                modCount++;
                return n.value;
            }
        }
        return null;
    }

    public boolean remove(K key, V value) {
        int hash = hash(key);
        int idx = indexFor(hash, table.length);
        Node<K, V> prev = null;
        for (Node<K, V> n = table[idx]; n != null; prev = n, n = n.next) {
            if (n.hash == hash && Objects.equals(n.key, key) && Objects.equals(n.value, value)) {
                if (prev == null) {
                    table[idx] = n.next;
                } else {
                    prev.next = n.next;
                }
                size--;
                modCount++;
                return true;
            }
        }
        return false;
    }

    public V update(K key, V value) {
        return put(key, value);
    }

    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        modCount++;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new HashTableIterator();
    }

    private final class HashTableIterator implements Iterator<Map.Entry<K, V>> {
        private int bucketIndex = 0;
        private Node<K, V> next;
        private Node<K, V> lastReturned;
        private int expectedModCount = modCount;

        HashTableIterator() {
            advanceToNextNonEmptyBucket();
        }

        private void advanceToNextNonEmptyBucket() {
            while (bucketIndex < table.length && (next = table[bucketIndex++]) == null) ;
        }

        private void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public boolean hasNext() {
            checkForComodification();
            return next != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            checkForComodification();
            if (next == null) {
                throw new NoSuchElementException();
            }
            lastReturned = next;
            if (next.next != null) {
                next = next.next;
            } else {
                next = null;
                advanceToNextNonEmptyBucket();
            }
            return lastReturned;
        }

        @Override
        public void remove() {
            checkForComodification();
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            K key = lastReturned.key;
            int h = lastReturned.hash;
            int idx = indexFor(h, table.length);
            Node<K, V> prev = null;
            Node<K, V> n = table[idx];
            while (n != null) {
                if (n == lastReturned) {
                    if (prev == null) {
                        table[idx] = n.next;
                    } else {
                        prev.next = n.next;
                    }
                    size--;
                    modCount++;
                    expectedModCount = modCount;
                    lastReturned = null;
                    return;
                }
                prev = n;
                n = n.next;
            }
            throw new ConcurrentModificationException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof @SuppressWarnings("unchecked")HashTable<?, ?> other)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        for (Map.Entry<K, V> e : this) {
            Object key = e.getKey();
            Object value = e.getValue();
            Object otherValue = ((HashTable) other).get(key);
            if (!Objects.equals(value, otherValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (Map.Entry<K, V> e : this) {
            h += Objects.hashCode(e.getKey()) ^ Objects.hashCode(e.getValue());
        }
        return h;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<K, V> e : this) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        sb.append("}");
        return sb.toString();
    }

    private Node<K, V> getNode(K key) {
        int hash = hash(key);
        int idx = indexFor(hash, table.length);
        for (Node<K, V> n = table[idx]; n != null; n = n.next) {
            if (n.hash == hash && Objects.equals(n.key, key)) {
                return n;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Node<K, V>[] old = table;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];
        for (Node<K, V> head : old) {
            Node<K, V> n = head;
            while (n != null) {
                Node<K, V> next = n.next;
                int idx = indexFor(n.hash, newCapacity);
                n.next = newTable[idx];
                newTable[idx] = n;
                n = next;
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
        modCount++;
    }

    private static int powerOfTwoFor(int cap) {
        int n = 1;
        while (n < cap) n <<= 1;
        return n;
    }

    private int hash(K key) {
        int h = (key == null) ? 0 : key.hashCode();
        h ^= (h >>> 16);
        return h;
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }
}
