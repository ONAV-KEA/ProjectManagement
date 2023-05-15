package dk.kea.projectmanagement;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.IProjectRepository;
import dk.kea.projectmanagement.service.ProjectService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.*;
import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProjectServiceTest {
    @Mock
    private IProjectRepository repository;

    @InjectMocks
    private ProjectService projectService;
    @Test
    void createProjectTest() {
        // Arrange
        Project form = new Project(1, "test", "test project", LocalDate.of(2023, 5, 15), LocalDate.of(2023, 6, 15));
        User user = new User("testUser", "password", "Test", "User", LocalDate.of(1990, 1, 1), "Admin");
        when(repository.createProject(any(Project.class), any(User.class))).thenReturn(form);

        // Act
        Project result = projectService.createProject(form, user);

        // Assert
        assertNotNull(result);
        assertEquals(form.getName(), result.getName());
        assertEquals(form.getDescription(), result.getDescription());
        assertEquals(form.getStartDate(), result.getStartDate());
        assertEquals(form.getEndDate(), result.getEndDate());

    }
}


