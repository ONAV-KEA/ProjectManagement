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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import dk.kea.projectmanagement.service.ProjectService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDate;

import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;



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

    // We mock the session object
    MockHttpSession session = new MockHttpSession();


    @BeforeEach
    public void setUp() {
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
    public void tearDown() {
        userService.deleteUserByUsername("testUsername");
    }


    @Test
    public void projectCrudTest() throws Exception {
        // We prepare a project and set some values for it - name and dates
        Project form = new Project();
        form.setName("Test Project");
        form.setStartDate(LocalDate.now());
        form.setEndDate(LocalDate.now().plusDays(7));

        // We go to the /createproject and the prepared project formula is entered
        this.mockMvc.perform(post("/createproject").session(session)
                        .flashAttr("project", form)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/dashboard"));

        // We then test opening the projects page
        this.mockMvc.perform(get("/projects").session(session))
                .andExpect(status().isOk());

        // Then we take the projectId from the /projects page and set it as int
        Project project = projectService.getProjectByUserId(((User) session.getAttribute("user")).getId()).get(0);
        int projectId = project.getId();

        // Which lets us set the id variable in the project/ url to the project and see if it works correctly
        this.mockMvc.perform(get("/project/" + projectId).session(session))
                .andExpect(status().isOk());

        // Prepare data formula that will be used to update the project with
        Project updatedForm = new Project();
        updatedForm.setName("Updated Test Project");
        updatedForm.setStartDate(LocalDate.now().plusDays(1));
        updatedForm.setEndDate(LocalDate.now().plusDays(8));

        // We perform the project update with the updatedForm data, and then redirect user back to dashboard
        this.mockMvc.perform(post("/project/" + projectId + "/projectsettings").session(session)
                        .flashAttr("project", updatedForm)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/project/" + projectId));

        // Finally, we delete the project
        this.mockMvc.perform(get("/project/" + projectId + "/deleteproject").session(session))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/projects"));
    }
}


