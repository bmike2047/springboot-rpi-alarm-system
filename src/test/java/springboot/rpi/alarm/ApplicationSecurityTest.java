package springboot.rpi.alarm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration
public class ApplicationSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void isHealthAllowed() throws Exception {
        this.mockMvc.perform(get("/actuator/health/application"))
                .andExpect(status().isOk());
    }

    @Test
    void isInfoAllowed() throws Exception {
        this.mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }

    @Test
    void otherURLDenied() throws Exception {
        this.mockMvc.perform(get("/actuator/beans"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    void otherURLGood() throws Exception {
        this.mockMvc.perform(get("/hub"))
                .andExpect(status().isOk());
    }

}
