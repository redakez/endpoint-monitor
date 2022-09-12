package applifting.endpointmonitor.integration;

import applifting.endpointmonitor.model.User;
import applifting.endpointmonitor.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static applifting.endpointmonitor.TestData.GET_USER_1;
import static applifting.endpointmonitor.TestData.GET_USER_2;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserIntegrationTests {

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String usersPath = "/users";

    @Test
    public void getAllUsers() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        User user_2 = userService.createUser(GET_USER_2());
        mockMvc.perform(MockMvcRequestBuilders
                    .get(usersPath))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].username",
                        Matchers.containsInAnyOrder(user_1.getUsername(), user_2.getUsername())))
                .andExpect(jsonPath("$[*].email",
                        Matchers.containsInAnyOrder(user_1.getEmail(), user_2.getEmail())))
                .andExpect(jsonPath("$[*].accessToken",
                        Matchers.containsInAnyOrder(user_1.getAccessToken(), user_2.getAccessToken())));
    }

    @Test
    public void getSpecificUser() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        mockMvc.perform(MockMvcRequestBuilders
                    .get(usersPath + "/" + user_1.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user_1.getUsername()))
                .andExpect(jsonPath("$.email").value(user_1.getEmail()))
                .andExpect(jsonPath("$.accessToken").value(user_1.getAccessToken()));
    }

    @Test
    public void nonExistingUserIsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(usersPath + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void userGetsCreated() throws Exception {
        User user_1 = GET_USER_1();

        assertEquals(userService.getAllUsers().size(), 0);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(usersPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user_1)))
                .andExpect(status().isCreated());
        assertEquals(userService.getAllUsers().size(), 1);
        User dbUser = userService.getAllUsers().stream().findAny().get();
        assertEquals(dbUser.getUsername(), user_1.getUsername());
        assertEquals(dbUser.getEmail(), user_1.getEmail());
        assertEquals(dbUser.getAccessToken(), user_1.getAccessToken());
    }

    @Test
    public void nonValidUserDoesNotGetCreated() throws Exception {
        User user_1 = GET_USER_1();

        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1L, "a", user_1.getEmail(), user_1.getAccessToken(), null));
        users.add(new User(1L, user_1.getUsername(), "not_mail", user_1.getAccessToken(), null));
        users.add(new User(1L, user_1.getUsername(), user_1.getEmail(), "not_uuid", null));

        for (User nonValidUser: users) {
            mockMvc.perform(MockMvcRequestBuilders
                            .post(usersPath)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(nonValidUser)))
                    .andExpect(status().isBadRequest());
        }
        assertEquals(userService.getAllUsers().size(), 0);
    }

    @Test
    public void userIsUpdated() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        User user_2 = GET_USER_2();

        mockMvc.perform(MockMvcRequestBuilders
                        .put(usersPath + "/" + user_1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user_2)))
                .andExpect(status().isOk());

        User updatedUser = userService.getUserById(user_1.getId());
        assertEquals(updatedUser.getId(), user_1.getId());
        assertEquals(updatedUser.getUsername(), user_2.getUsername());
        assertEquals(updatedUser.getEmail(), user_2.getEmail());
        assertEquals(updatedUser.getAccessToken(), user_2.getAccessToken());
    }

    @Test
    public void userGetsDeleted() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        User user_2 = userService.createUser(GET_USER_2());

        assertEquals(userService.getAllUsers().size(), 2);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(usersPath + "/" + user_1.getId()))
                .andExpect(status().isOk());
        assertEquals(userService.getAllUsers().size(), 1);

        User userStillInDatabase = userService.getAllUsers().get(0);
        assertEquals(userStillInDatabase.getId(), user_2.getId());
        assertEquals(userStillInDatabase.getUsername(), user_2.getUsername());
        assertEquals(userStillInDatabase.getEmail(), user_2.getEmail());
        assertEquals(userStillInDatabase.getAccessToken(), user_2.getAccessToken());
    }

    @Test
    public void nonExistingUserIsNotDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(usersPath + "/1"))
                .andExpect(status().isNotFound());
    }

}
