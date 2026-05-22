package dsl;

import groovy.lang.Closure;
import core.model.Group;
import core.model.Student;
import core.model.CourseConfiguration;

import java.util.ArrayList;
import java.util.Map;

public class GroupDelegate {
    private final CourseConfiguration config;

    public GroupDelegate(CourseConfiguration config) {
        this.config = config;
    }

    public void group(String name, Closure<?> closure) {
        Group group = new Group(name, new ArrayList<>());

        closure.setDelegate(new Object() {
            public void student(Map<String, String> args) {
                Student s = new Student(
                        args.get("id"),
                        args.get("name"),
                        args.get("repo"),
                        name
                );
                group.students().add(s);
            }
        });

        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();

        config.addGroup(group);
    }
}