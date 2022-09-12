package applifting.endpointmonitor.integration;

import applifting.endpointmonitor.dao.MonitoredEndpointDao;
import applifting.endpointmonitor.model.MonitoredEndpoint;
import applifting.endpointmonitor.model.User;
import applifting.endpointmonitor.service.MonitoredEndpointService;
import applifting.endpointmonitor.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static applifting.endpointmonitor.TestData.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MonitoredEndpointIntegrationTests {

    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private MonitoredEndpointService monitoredEndpointService;

    @Autowired
    private MonitoredEndpointDao monitoredEndpointDao;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String endpointsPath = "/endpoints";

    @Test
    public void userGetsOnlyEndpointsAssociatedWithThem() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        User user_2 = userService.createUser(GET_USER_2());
        MonitoredEndpoint endpoint_1 = monitoredEndpointService.createEndpoint(GET_ENDPOINT_1(), user_1.getAccessToken());
        MonitoredEndpoint endpoint_2 = monitoredEndpointService.createEndpoint(GET_ENDPOINT_2(), user_2.getAccessToken());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(endpointsPath)
                        .header("accessToken", user_1.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(endpoint_1.getName()))
                .andExpect(jsonPath("$[0].url").value(endpoint_1.getUrl()))
                .andExpect(jsonPath("$[0].monitoringInterval").value(endpoint_1.getMonitoringInterval()));
    }

    @Test
    public void userIsSuccessfullyAuthorizedForTheirEndpoint() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        MonitoredEndpoint endpoint_1 = monitoredEndpointService.createEndpoint(GET_ENDPOINT_1(), user_1.getAccessToken());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(endpointsPath + "/" + endpoint_1.getId())
                        .header("accessToken", user_1.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(endpoint_1.getName()))
                .andExpect(jsonPath("$.url").value(endpoint_1.getUrl()))
                .andExpect(jsonPath("$.monitoringInterval").value(endpoint_1.getMonitoringInterval()));
    }

    @Test
    public void userIsUnauthorizedForEndpointTheyDoNotOwn() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        User user_2 = userService.createUser(GET_USER_2());
        MonitoredEndpoint endpoint_1 = monitoredEndpointService.createEndpoint(GET_ENDPOINT_1(), user_1.getAccessToken());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(endpointsPath + "/" + endpoint_1.getId())
                        .header("accessToken", user_2.getAccessToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void userIsUnauthorizedForNonExistingEndpoint() throws Exception {
        User user_1 = GET_USER_1();

        mockMvc.perform(MockMvcRequestBuilders
                        .get(endpointsPath + "/42")
                        .header("accessToken", user_1.getAccessToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void endpointIsCreatedByUser() throws Exception {
        User user_1 = GET_USER_1();
        MonitoredEndpoint endpoint_1 = GET_ENDPOINT_1();

        User createdUser = userService.createUser(user_1);

        assertEquals(monitoredEndpointDao.findAll().size(), 0);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(endpointsPath)
                        .header("accessToken", user_1.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(endpoint_1)))
                .andExpect(status().isCreated());

        assertEquals(monitoredEndpointDao.findAll().size(), 1);

        MonitoredEndpoint retrievedEndpoint = monitoredEndpointDao.findAll().stream().findAny().get();
        assertEquals(retrievedEndpoint.getName(), endpoint_1.getName());
        assertEquals(retrievedEndpoint.getUrl(), endpoint_1.getUrl());
        assertEquals(retrievedEndpoint.getMonitoringInterval(), endpoint_1.getMonitoringInterval());
        assertEquals(retrievedEndpoint.getOwner().getId(), createdUser.getId());
    }

    @Test
    public void nonValidEndpointIsNotCreated() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        MonitoredEndpoint endpoint_1 = GET_ENDPOINT_1();

        ArrayList<MonitoredEndpoint> endpoint = new ArrayList<>();
        endpoint.add(new MonitoredEndpoint(1L, "a", endpoint_1.getUrl(),
                null, null, endpoint_1.getMonitoringInterval(), null, null));
        endpoint.add(new MonitoredEndpoint(1L, endpoint_1.getName(), "not_url",
                null, null, endpoint_1.getMonitoringInterval(), null, null));
        endpoint.add(new MonitoredEndpoint(1L, endpoint_1.getName(), endpoint_1.getUrl(),
                null, null, 0, null, null));

        for (MonitoredEndpoint nonValidEndpoint: endpoint) {
            mockMvc.perform(MockMvcRequestBuilders
                            .post(endpointsPath)
                            .header("accessToken", user_1.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(nonValidEndpoint)))
                    .andExpect(status().isBadRequest());
        }

        assertEquals(monitoredEndpointDao.findAll().size(), 0);
    }

    @Test
    public void endpointIsUpdatedByUser() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        MonitoredEndpoint endpoint_1 = monitoredEndpointService.createEndpoint(GET_ENDPOINT_1(), user_1.getAccessToken());
        MonitoredEndpoint endpoint_2 = GET_ENDPOINT_2();

        mockMvc.perform(MockMvcRequestBuilders
                        .put(endpointsPath + "/" + endpoint_1.getId())
                        .header("accessToken", user_1.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(endpoint_2)))
                .andExpect(status().isOk());

        MonitoredEndpoint updatedEndpoint = monitoredEndpointDao.findAll().stream().findAny().get();
        assertEquals(updatedEndpoint.getId(), endpoint_1.getId());
        assertEquals(updatedEndpoint.getName(), endpoint_2.getName());
        assertEquals(updatedEndpoint.getUrl(), endpoint_2.getUrl());
        assertEquals(updatedEndpoint.getMonitoringInterval(), endpoint_2.getMonitoringInterval());
        assertEquals(updatedEndpoint.getOwner().getId(), user_1.getId());
    }

    @Test
    public void endpointIsNotUpdatedByUnauthorizedUser() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        User user_2 = userService.createUser(GET_USER_2());
        MonitoredEndpoint endpoint_1 = monitoredEndpointService.createEndpoint(GET_ENDPOINT_1(), user_1.getAccessToken());

        mockMvc.perform(MockMvcRequestBuilders
                        .put(endpointsPath + "/" + endpoint_1.getId())
                        .header("accessToken", user_2.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(GET_ENDPOINT_2())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void nonExistingEndpointIsNotUpdated() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());

        mockMvc.perform(MockMvcRequestBuilders
                        .put(endpointsPath + "/42")
                        .header("accessToken", user_1.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(GET_ENDPOINT_1())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void userDeletesTheirEndpoint() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        MonitoredEndpoint endpoint_1 = monitoredEndpointService.createEndpoint(GET_ENDPOINT_1(), user_1.getAccessToken());

        assertEquals(monitoredEndpointDao.findAll().size(), 1);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(endpointsPath + "/" + endpoint_1.getId())
                        .header("accessToken", user_1.getAccessToken()))
                .andExpect(status().isOk());

        assertEquals(monitoredEndpointDao.findAll().size(), 0);
    }

    @Test
    public void endpointIsNotDeletedByUnauthorizedUser() throws Exception {
        User user_1 = userService.createUser(GET_USER_1());
        User user_2 = userService.createUser(GET_USER_2());
        MonitoredEndpoint endpoint_1 = monitoredEndpointService.createEndpoint(GET_ENDPOINT_1(), user_1.getAccessToken());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(endpointsPath + "/" + endpoint_1.getId())
                        .header("accessToken", user_2.getAccessToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void nonExistingEndpointIsNotDeleted() throws Exception {
        User user_1 =userService.createUser(GET_USER_1());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(endpointsPath + "/42")
                        .header("accessToken", user_1.getAccessToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

}
