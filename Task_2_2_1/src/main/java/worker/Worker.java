package worker;

public interface Worker extends Runnable {
    enum Role {
        BAKER,
        COURIER
    }

    int getId();

    Role getRole();

    boolean isWorking();

    void stop();
}
