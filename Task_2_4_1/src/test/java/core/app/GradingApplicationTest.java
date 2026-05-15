package core.app;

import core.model.CourseConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradingApplicationTest {

    @Test
    void testFullApplicationFlow() throws IOException {

        GradingApplication app = new GradingApplication();
        CourseConfiguration mockConfig = mock(CourseConfiguration.class);
        Path mockPath = Path.of("test-workspace");

        assertDoesNotThrow(() -> app.run(mockConfig, mockPath));
    }
}