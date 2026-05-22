package core.model;

import java.time.LocalDate;

public record Task(
        String id,
        String name,
        double maxScore,
        LocalDate softDeadline,
        LocalDate hardDeadline
) {
}