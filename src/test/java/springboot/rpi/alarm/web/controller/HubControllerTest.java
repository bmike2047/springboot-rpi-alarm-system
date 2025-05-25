package springboot.rpi.alarm.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import springboot.rpi.alarm.ApplicationProperties;
import springboot.rpi.alarm.config.AlarmConfig;
import springboot.rpi.alarm.config.MvcConfig;
import springboot.rpi.alarm.config.WebSecurityConfig;
import springboot.rpi.alarm.fsm.Hub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HubController.class)
@Import({ApplicationProperties.class, AlarmConfig.class, WebSecurityConfig.class, MvcConfig.class})
public class HubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private Hub alarmHub;

    @Test
    void testRedirectsToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/something"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    void testArm() throws Exception {
        mockMvc.perform(get("/hub/arm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hub?opSuccess=true"));
        verify(alarmHub).arm();
    }

    @Test
    @WithMockUser
    void testArmError() throws Exception {
        doThrow(new RuntimeException()).when(alarmHub).arm();
        mockMvc.perform(get("/hub/arm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hub?opSuccess=false"));
        verify(alarmHub).arm();
    }

    @Test
    @WithMockUser
    void testDisarm() throws Exception {
        mockMvc.perform(get("/hub/disarm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hub?opSuccess=true"));
        verify(alarmHub).disarm();
    }

    @Test
    @WithMockUser
    void testPanic() throws Exception {
        mockMvc.perform(get("/hub/panic"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hub?opSuccess=true"));
        verify(alarmHub).panic();
    }

    @Test
    @WithMockUser
    void testEdit() throws Exception {
        mockMvc.perform(get("/hub/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"));
    }

    @Test
    @WithMockUser
    void testSaveErrorFromString() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/hub/save").param("pin", "ssss")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(view().name("edit"))
                .andExpect(MockMvcResultMatchers.model().attributeErrorCount("hub", 1));
    }

    @Test
    @WithMockUser
    void testSaveErrorFromSize() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/hub/save").param("pin", "11111")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(view().name("edit"))
                .andExpect(MockMvcResultMatchers.model().attributeErrorCount("hub", 1));
    }

    @Test
    @WithMockUser
    void testSave() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/hub/save").param("pin", "8890")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hub?opSuccess=true"));
        assertEquals(8890, alarmHub.getPin());
    }
}
