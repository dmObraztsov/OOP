package dsl;


import core.model.Task;
import core.model.CourseConfiguration;

import java.time.LocalDate;
import java.util.Map;

public class TaskDelegate {
    private final CourseConfiguration config;

    public TaskDelegate(CourseConfiguration config) {
        this.config = config;
    }

    public void task(Map<String, Object> args) {
        Task task = new Task(
                (String) args.get("id"),
                (String) args.get("name"),
                ((Number) args.get("max")).doubleValue(),
                LocalDate.parse((String) args.get("soft")),
                LocalDate.parse((String) args.get("hard"))
        );
        config.addTask(task);
    }
}