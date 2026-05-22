package core.model;

public record ExtraPoints(
        String studentGithubId,
        String taskId,
        double points,
        String reason
) {
}