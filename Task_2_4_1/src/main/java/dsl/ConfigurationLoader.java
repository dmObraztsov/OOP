package dsl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;
import core.model.CourseConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigurationLoader {

    public CourseConfiguration load(File file) {
        CourseConfiguration config = new CourseConfiguration();
        loadInto(file, config);
        return config;
    }

    public void loadInto(File file, CourseConfiguration config) {
        try {
            CompilerConfiguration cc = new CompilerConfiguration();
            cc.setScriptBaseClass(DelegatingScript.class.getName());

            GroovyShell sh = new GroovyShell(
                    Thread.currentThread().getContextClassLoader(),
                    new Binding(),
                    cc
            );

            DelegatingScript script = (DelegatingScript) sh.parse(file);
            script.setDelegate(new DslDelegate(config, this));
            script.run();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DSL file: " + file.getName(), e);
        }
    }
}