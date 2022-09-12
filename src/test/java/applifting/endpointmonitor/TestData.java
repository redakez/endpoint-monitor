package applifting.endpointmonitor;

import applifting.endpointmonitor.model.MonitoredEndpoint;
import applifting.endpointmonitor.model.User;

import java.util.Date;

/**
 * Data used in tests
 * All the entities are valid (they do not violate any constraints)
 */
public class TestData {


    public static User GET_USER_1() {
        return new User(null, "Jorji", "jorji.costava@yahoo.com",
                "4f8b4078-238e-4852-adbd-e098d99e5462", null);
    }

    public static User GET_USER_2() {
        return new User(null, "Dedusmuln", "paper.cup@gmail.com",
                "a18623f7-7a48-4337-8492-c43e0d95d078", null);
    }

    public static MonitoredEndpoint GET_ENDPOINT_1() {
        return new MonitoredEndpoint(null, "LolFlash", "https://www.lolflash.com/",
                new Date(), new Date(),600, null, null);
    }

    public static MonitoredEndpoint GET_ENDPOINT_2() {
        return new MonitoredEndpoint(null, "BestGame", "http://thebestgameintheworld.com",
                new Date(), new Date(), 3600, null, null);
    }

}
