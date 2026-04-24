package infrastructure.build;

import infrastructure.build.BuildResult;
import infrastructure.build.BuildRunner;

import java.nio.file.Files;
import java.nio.file.Path;

public class GradleBuildRunner implements BuildRunner {
    @Override
    public BuildResult run(Path projectPath) {
        String executable = System.getProperty("os.name").toLowerCase().contains("win")
                ? "gradlew.bat" : "./gradlew";

        Path gradlePath = projectPath.resolve(executable).toAbsolutePath();

        if (!Files.exists(gradlePath)) {
            System.err.println("[DEBUG] [!] Gradle executable not found at: " + gradlePath);
            return new BuildResult(false, false, 0, 0, 0);
        }

        int exitCode = executeGradle(projectPath,
                gradlePath.toString(),
                "-Dfile.encoding=UTF-8",
                "clean",
                "compileJava",
                "test");

        return new BuildResult(exitCode == 0, exitCode == 0, 0, 0, 0);
    }

    private int executeGradle(Path projectPath, String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(projectPath.toFile());

            pb.inheritIO();

            Process process = pb.start();
            return process.waitFor();
        } catch (Exception e) {
            System.err.println("[DEBUG] [!] Failed to execute Gradle: " + e.getMessage());
            return -1;
        }
    }
}