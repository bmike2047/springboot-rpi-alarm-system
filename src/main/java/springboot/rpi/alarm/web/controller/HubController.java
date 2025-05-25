package springboot.rpi.alarm.web.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.rpi.alarm.fsm.Hub;
import springboot.rpi.alarm.web.model.HubModel;

/**
 * This class is the main controller for the alarm web interface.
 */
@Controller
@RequestMapping("/hub")
public class HubController {
    private static final Logger LOG = LoggerFactory.getLogger(HubController.class);
    /**
     * Main hub.
     */
    @Autowired
    private Hub hub;

    /**
     * Handle index page.
     *
     * @param model Model
     * @return destination page
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("hub", hub);
        return "hub";
    }

    /**
     * Handle status refresh. This is called via JQuery and Ajax.
     *
     * @param model model
     * @return destination page
     */
    @GetMapping("/status")
    public String armStatus(Model model) {
        model.addAttribute("hub", hub);
        return "hub :: #alarmHubStatus";
    }

    /**
     * Handle arm button.
     * Post/Redirect/Get (PRG) pattern is applied here.
     *
     * @param model         model
     * @param redirectAttrs RedirectAttributes
     * @return destination page
     */
    @GetMapping("/arm")
    public String arm(Model model, RedirectAttributes redirectAttrs) {
        try {
            hub.arm();
            model.addAttribute("hub", hub);
            redirectAttrs.addAttribute("opSuccess", true);
        } catch (Exception e) {
            LOG.error("Got error: ", e);
            redirectAttrs.addAttribute("opSuccess", false);
        }
        return "redirect:/hub";
    }

    /**
     * Handle disarm button.
     * Post/Redirect/Get (PRG) pattern is applied here.
     *
     * @param model         model
     * @param redirectAttrs RedirectAttributes
     * @return destination page
     */
    @GetMapping("/disarm")
    public String disarm(Model model, RedirectAttributes redirectAttrs) {
        try {
            hub.disarm();
            model.addAttribute("hub", hub);
            redirectAttrs.addAttribute("opSuccess", true);
        } catch (Exception e) {
            LOG.error("Got error: ", e);
            redirectAttrs.addAttribute("opSuccess", false);
        }
        return "redirect:/hub";
    }

    /**
     * Handle panic button.
     * Post/Redirect/Get (PRG) pattern is applied here.
     *
     * @param model         model
     * @param redirectAttrs RedirectAttributes
     * @return destination page
     */
    @GetMapping("/panic")
    public String panic(Model model, RedirectAttributes redirectAttrs) {
        try {
            hub.panic();
            model.addAttribute("hub", hub);
            redirectAttrs.addAttribute("opSuccess", true);
        } catch (Exception e) {
            LOG.error("Got error: ", e);
            redirectAttrs.addAttribute("opSuccess", false);
        }
        return "redirect:/hub";
    }

    /**
     * Handle edit button.
     *
     * @param model model
     * @return destination page
     */
    @GetMapping("/edit")
    public String edithub(Model model) {
        model.addAttribute("hub", new HubModel());
        return "edit";
    }

    /**
     * Handle save button.
     * Model validation and error reporting is done here.
     * Post/Redirect/Get (PRG) pattern is applied here.
     *
     * @param model         model
     * @param hubModel           hubModel
     * @param bindingResult BindingResult
     * @param redirectAttrs RedirectAttributes
     * @return destination page
     */
    @PostMapping("/save")
    public String savehub(Model model,
                          @ModelAttribute("hub")
                          @Valid HubModel hubModel,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttrs) {
        try {
            if (bindingResult.hasErrors()) {
                return "edit";
            }
            hub.changePin(hubModel.getPin());
            redirectAttrs.addAttribute("opSuccess", true);
            LOG.info("Saved a new pin");
        } catch (Exception e) {
            LOG.error("Got error: ", e);
            redirectAttrs.addAttribute("opSuccess", false);
        }
        return "redirect:/hub";
    }
}
