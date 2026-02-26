public class MyAtomicBoolean {
    private boolean value;

    public MyAtomicBoolean(boolean initialValue) {
        this.value = initialValue;
    }

    public synchronized boolean get() {
        return value;
    }

    public synchronized void set(boolean newValue) {
        this.value = newValue;
    }
}