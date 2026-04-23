package infrastructure.build;

import java.nio.file.Path;

public class GradleBuildRunner implements BuildRunner {
    @Override
    public BuildResult run(Path projectPath) {
        // gradlew для Unix или gradlew.bat для Windows
        String executable = System.getProperty("os.name").toLowerCase().contains("win")
                ? "gradlew.bat" : "./gradlew";

        int exitCode = executeGradle(projectPath, executable, "clean", "compileJava", "checkstyleMain", "test");

        return new BuildResult(exitCode == 0, true, 10, 0, 0);
    }

    private int executeGradle(Path projectPath, String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(projectPath.toFile());
            return pb.start().waitFor();
        } catch (Exception e) {
            return -1;
        }
    }
}