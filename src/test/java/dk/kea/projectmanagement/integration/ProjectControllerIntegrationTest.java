package dk.kea.projectmanagement.integration;

import dk.kea.projectmanagement.model.Project;
import dk.kea.projectmanagement.model.User;
import dk.kea.projectmanagement.repository.ProjectRepository;
import dk.kea.projectmanagement.repository.utility.DBManager;
import dk.kea.projectmanagement.repository.utility.TestDBManager;
import dk.kea.projectmanagement.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import dk.kea.projectmanagement.service.ProjectService;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


/*
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    // Mock session object
    MockHttpSession session = new MockHttpSession();


    @BeforeEach
    public void setUp() throws Exception {
        User form = new User();
        form.setUsername("testUsername");
        form.setPassword("testPassword");
        form.setFirstName("Test");
        form.setLastName("User");
        form.setBirthday(LocalDate.now());
        form.setRole("project_manager");
        userService.createUser(form);
        User testUser = userService.getUserByID(form.getId());
        session.setAttribute("user", testUser);
    }

    @AfterEach
    public void tearDown() throws Exception {
        userService.deleteUser(((User) session.getAttribute("user")).getId());
    }

    @Test
    public void testProjectCreationAndView() throws Exception {
        // Test project creation
        Project form = new Project();

        this.mockMvc.perform(post("/createproject").session(session)
                        .flashAttr("project", form)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/dashboard"));

        // Test retrieving all projects for the user
        this.mockMvc.perform(get("/projects").session(session))
                .andExpect(status().isOk());

        // Retrieve project id after creation
        Project project = projectService.getProjectByUserId(((User) session.getAttribute("user")).getId()).get(0);
        int projectId = project.getId();

        // Test retrieving the created project
        this.mockMvc.perform(get("/project/" + projectId).session(session))
                .andExpect(status().isOk());
    }


}

*/
