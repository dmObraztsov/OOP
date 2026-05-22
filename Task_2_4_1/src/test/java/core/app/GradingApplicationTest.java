package core.app;

import core.model.CourseConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradingApplicationTest {

    @Test
    void testFullApplicationFlow() {

        // given
        GradingApplication app = new GradingApplication();
        CourseConfiguration mockConfig = mock(CourseConfiguration.class);
        Path mockPath = Path.of("test-workspace");

        // when
        Runnable action = () -> app.run(mockConfig, mockPath);

        // then
        assertDoesNotThrow(action::run);
    }
}