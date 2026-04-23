package core.model;

import java.util.List;

public record Group(
        String name,
        List<Student> students
) {
}