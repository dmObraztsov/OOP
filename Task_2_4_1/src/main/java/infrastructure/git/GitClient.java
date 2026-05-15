package infrastructure.git;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public interface GitClient {
    void cloneRepository(String url, Path destination);

    void checkoutBranch(Path repoPath, String branchName);

    boolean testConnection(String url);

    void fetchAll(Path repoPath); // Добавляем сюда!

    List<String> getCommitDates(Path repoPath);
    LocalDate getFirstCommitDate(Path repoPath, String branchName);
    LocalDate getLastCommitDate(Path repoPath, String branchName);
}