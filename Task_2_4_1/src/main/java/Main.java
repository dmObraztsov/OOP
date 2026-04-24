import core.logic.Semester1Strategy;
import core.model.CourseConfiguration;
import core.model.StudentResult;
import dsl.ConfigurationLoader;
import infrastructure.build.GradleBuildRunner;
import infrastructure.build.TestResultParser;
import infrastructure.git.ConsoleGitClient;
import infrastructure.report.HtmlReportGenerator;
import service.GradingManager;
import service.ValidationService;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            ConfigurationLoader loader = new ConfigurationLoader();
            CourseConfiguration config = loader.load(new File("grading.groovy"));
            System.out.println("[SYSTEM] Loaded tasks: " + config.getTasks().size());
            System.out.println("[SYSTEM] Loaded groups: " + config.getGroups().size());

            Path workspacePath = Path.of(System.getProperty("user.home"))
                    .resolve("grading_temp")
                    .toAbsolutePath();

            System.out.println("[SYSTEM] Workspace established at: " + workspacePath);

            if (config.getGroups().isEmpty()) {
                System.out.println("[SYSTEM] WARNING: No groups found! Check your DSL parser.");
            } else {
                config.getGroups().forEach(g ->
                        System.out.println("[SYSTEM] Group: " + g.name() + ", Students: " + g.students().size())
                );
            }
            GradingManager manager = new GradingManager(
                    new ConsoleGitClient(),
                    new GradleBuildRunner(),
                    new Semester1Strategy(),
                    new ValidationService(),
                    new TestResultParser(),
                    workspacePath
            );

            List<StudentResult> results = manager.processAll(config);
            HtmlReportGenerator reportGenerator = new HtmlReportGenerator();
            reportGenerator.generate(results, Path.of("index.html"));

            System.out.println("Check end. Generating result...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}