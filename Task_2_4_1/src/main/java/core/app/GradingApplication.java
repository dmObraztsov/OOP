package core.app;

import core.logic.*;
import core.model.CourseConfiguration;
import core.model.StudentResult;
import infrastructure.build.GradleBuildRunner;
import infrastructure.build.TestResultParser;
import infrastructure.git.ConsoleGitClient;
import infrastructure.report.HtmlReportGenerator;
import service.GradingManager;
import service.ValidationService;
import core.util.Logger;

import java.nio.file.Path;
import java.util.List;

public class GradingApplication {
    public void run(CourseConfiguration config, Path workspacePath) {
        Logger.info("Инициализация компонентов...");

        ConsoleGitClient gitClient = new ConsoleGitClient();
        GradleBuildRunner buildRunner = new GradleBuildRunner();
        TestResultParser testParser = new TestResultParser();
        HtmlReportGenerator reportGenerator = new HtmlReportGenerator();

        Semester1Strategy s1 = new Semester1Strategy();
        Semester2Strategy s2 = new Semester2Strategy();

        GradingManager manager = new GradingManager(
                gitClient, buildRunner, s1, s2,
                new ValidationService(), testParser, workspacePath
        );

        Logger.info("Запуск оценки студентов в параллельном режиме...");
        List<StudentResult> results = manager.processAll(config, s1, s2);

        Logger.info("Генерация финального отчета...");
        try {
            Path reportFile = Path.of("index.html");
            reportGenerator.generate(results, reportFile);
            Logger.info("Отчет готов: " + reportFile.toAbsolutePath());
        } catch (Exception e) {
            Logger.error("Ошибка генерации отчета: " + e.getMessage());
        }
    }
}