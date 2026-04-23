package core.model;

public record Student(
        String githubId,
        String fullName,
        String repoUrl,
        String groupName
) {
}