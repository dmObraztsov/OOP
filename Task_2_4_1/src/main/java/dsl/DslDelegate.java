package dsl;

import core.model.ExtraPoints;
import groovy.lang.Closure;
import core.model.CourseConfiguration;

import java.io.File;
import java.util.Map;

public class DslDelegate {
    private final CourseConfiguration config;
    private final ConfigurationLoader loader;

    public DslDelegate(CourseConfiguration config, ConfigurationLoader loader) {
        this.config = config;
        this.loader = loader;
    }

    public void importConfig(String fileName) {
        try {
            ConfigurationLoader loader = new ConfigurationLoader();
            loader.loadInto(new File(fileName), this.config);
        } catch (Exception e) {
            System.err.println("Error importing config: " + fileName);
        }
    }

    public void tasks(Closure<?> closure) {
        TaskDelegate delegate = new TaskDelegate(config);
        executeClosure(delegate, closure);
    }

    public void groups(Closure<?> closure) {
        GroupDelegate delegate = new GroupDelegate(config);
        executeClosure(delegate, closure);
    }

    public void extra(Closure<?> closure) {
        closure.setDelegate(new Object() {
            public void bonus(Map<String, Object> args) {
                String studentId = (String) args.get("student");
                double points = ((Number) args.get("points")).doubleValue();
                String reason = (String) args.get("reason");

                config.addExtraPoints(new ExtraPoints(studentId, null, points, reason));
            }
        });
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }

    private void executeClosure(Object delegate, Closure<?> closure) {
        closure.setDelegate(delegate);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        closure.call();
    }
}