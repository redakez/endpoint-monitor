package applifting.endpointmonitor.mock;

import applifting.endpointmonitor.model.User;
import applifting.endpointmonitor.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static applifting.endpointmonitor.TestData.GET_USER_1;
import static applifting.endpointmonitor.TestData.GET_USER_2;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    UserService userService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String userPath = "/users";

    @Test
    public void getAllUsers() throws Exception {
        User user_1 = GET_USER_1();

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(user_1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(userPath)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].username").value(user_1.getUsername()))
                        .andExpect(jsonPath("$[0].email").value(user_1.getEmail()))
                        .andExpect(jsonPath("$[0].accessToken").value(user_1.getAccessToken()));
    }

    @Test
    public void getSpecificUser() throws Exception {
        User user_1 = GET_USER_1();

        Mockito.when(userService.getUserById(any(Long.class))).thenReturn(user_1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(userPath + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.username").value(user_1.getUsername()))
                        .andExpect(jsonPath("$.email").value(user_1.getEmail()))
                        .andExpect(jsonPath("$.accessToken").value(user_1.getAccessToken()));
    }

    @Test
    public void createUser() throws Exception {
        User user_1 = GET_USER_1();

        Mockito.when(userService.createUser(any(User.class))).thenReturn(user_1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(userPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user_1)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.username").value(user_1.getUsername()))
                        .andExpect(jsonPath("$.email").value(user_1.getEmail()))
                        .andExpect(jsonPath("$.accessToken").value(user_1.getAccessToken()));
    }

    @Test
    public void updateUser() throws Exception {
        User user_2 = GET_USER_2();

        Mockito.when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(user_2);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(userPath + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user_2)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.username").value(user_2.getUsername()))
                        .andExpect(jsonPath("$.email").value(user_2.getEmail()))
                        .andExpect(jsonPath("$.accessToken").value(user_2.getAccessToken()));
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(userPath + "/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").doesNotExist());
    }

}
