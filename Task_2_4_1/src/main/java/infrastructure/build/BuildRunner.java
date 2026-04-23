package infrastructure.build;

import java.nio.file.Path;

public interface BuildRunner {
    BuildResult run(Path projectPath);
}
