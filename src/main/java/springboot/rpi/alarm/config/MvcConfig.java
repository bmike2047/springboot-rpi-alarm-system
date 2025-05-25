package springboot.rpi.alarm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure application MVC.
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Add view controller.
     * @param registry ViewControllerRegistry
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

}