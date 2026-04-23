package core.model;

import java.util.*;


public class CourseConfiguration {
    private final Map<String, Task> tasks = new HashMap<>();
    private final List<Group> groups = new ArrayList<>();
    private final Map<String, List<String>> studentTasks = new HashMap<>();
    private final List<ExtraPoints> extraPoints = new ArrayList<>();

    public void addTask(Task task) {
        tasks.put(task.id(), task);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void addExtraPoints(ExtraPoints extraPoints) {
        this.extraPoints.add(extraPoints);
    }

    public Map<String, Task> getTasks() {
        return Collections.unmodifiableMap(tasks);
    }

    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public List<ExtraPoints> getExtraPoints() {
        return Collections.unmodifiableList(extraPoints);
    }


}