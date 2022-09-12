package applifting.endpointmonitor.mock;

import applifting.endpointmonitor.dao.UserDao;
import applifting.endpointmonitor.model.User;
import applifting.endpointmonitor.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static applifting.endpointmonitor.TestData.GET_USER_1;
import static applifting.endpointmonitor.TestData.GET_USER_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTests {

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService userService;

    @MockBean
    private UserDao userDao;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void findsAllUsers() {
        User user_1 = GET_USER_1();
        User user_2 = GET_USER_2();
        Mockito.when(userDao.findAll()).thenReturn(Arrays.asList(user_1, user_2));
        List<User> users = userService.getAllUsers();
        assertEquals(users.size(), 2);
        assertThat(users, containsInAnyOrder(user_1, user_2));
    }

    @Test
    public void findsSpecificUser() {
        User user_1 = GET_USER_1();
        Mockito.when(userDao.findById(2L)).thenReturn(Optional.of(user_1));
        User retrievedUser = userService.getUserById(2L);
        assertEquals(retrievedUser.getUsername(), user_1.getUsername());
        assertEquals(retrievedUser.getEmail(), user_1.getEmail());
        assertEquals(retrievedUser.getAccessToken(), user_1.getAccessToken());
    }

    @Test
    public void doesNotFindNonExistingUser() {
        Mockito.when(userDao.findById(4L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.getUserById(4L));
        assertEquals(ex.getRawStatusCode(), 404);
    }

    @Test
    public void userIsSaved() {
        User user_1 = GET_USER_1();

        Mockito.when(userDao.save(user_1)).thenReturn(user_1);

        User retrievedUser = userService.createUser(user_1);
        assertEquals(retrievedUser.getUsername(), user_1.getUsername());
        assertEquals(retrievedUser.getEmail(), user_1.getEmail());
        assertEquals(retrievedUser.getAccessToken(), user_1.getAccessToken());

        verify(userDao, times(1)).save(user_1);
    }

    @Test
    public void doesNotUpdateNonExistingUser() {
        User user_1 = GET_USER_1();

        Mockito.when(userDao.findById(4L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.updateUser(4L, user_1));
        assertEquals(ex.getRawStatusCode(), 404);
    }

    @Test
    public void doesNotDeleteNonExistingUser() {
        Mockito.when(userDao.findById(4L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.deleteUser(4L));
        assertEquals(ex.getRawStatusCode(), 404);
    }

    @Test
    public void deletesUser() {
        User user_1 = GET_USER_1();

        Mockito.when(userDao.findById(3L)).thenReturn(Optional.of(user_1));
        userService.deleteUser(3L);
        verify(userDao, times(1)).delete(user_1);
    }

}
