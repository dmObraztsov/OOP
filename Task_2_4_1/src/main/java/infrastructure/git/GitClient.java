package infrastructure.git;

import java.nio.file.Path;

public interface GitClient {
    void cloneRepository(String url, Path destination);

    void checkoutBranch(Path repoPath, String branchName);

    boolean testConnection(String url);
}