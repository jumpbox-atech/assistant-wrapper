package africa.za.atech.spring.aio.controller;

import africa.za.atech.spring.aio.functions.users.UsersService;
import africa.za.atech.spring.aio.functions.users.dto.ForgotDTO;
import africa.za.atech.spring.aio.functions.users.dto.RegisterDTO;
import africa.za.atech.spring.aio.functions.users.dto.UserProfileDTO;
import africa.za.atech.spring.aio.utils.Alert;
import africa.za.atech.spring.aio.utils.HelperTools;
import africa.za.atech.spring.aio.utils.OutputTool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SystemUserController {

    private final Environment environment;
    private final UsersService service;
    private List<Alert> alertList;

    @GetMapping("/logout")
    public String logout(
            HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/login?logout=true";
    }

    @GetMapping(value = {"/register"})
    public String showRegisterForm(
            Model model,
            @RequestParam(name = "complete", required = false) boolean complete) {
        if (!complete) {
            model.addAttribute("formObject", new RegisterDTO());
            return "register/register_form";
        } else {
            return "register/register_result";
        }
    }

    @PostMapping(value = {"/register"})
    public String processRegistration(
            Model model,
            @Validated @ModelAttribute("formObject") RegisterDTO form,
            RedirectAttributes redirectAttributes) {
        OutputTool outputTool = service.registerNewUser(form);
        if (outputTool.getResult() != OutputTool.Result.SUCCESS) {
            model.addAttribute("formObject", form);
            model.addAttribute("alertList", List.of(new Alert().build(Alert.AlertType.DANGER, "Unable to register. Please try again later.")));
            return "register/register_form";
        }
        redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/register?complete=true";
    }

    @GetMapping(value = {"/forgot"})
    public String showForgotForm(
            Model model,
            @RequestParam(name = "complete", required = false) boolean complete) {
        if (!complete) {
            model.addAttribute("formObject", new ForgotDTO());
            return "forgot/forgot_form";
        } else {
            model.addAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, "Temp password has been sent to your registered email.")));
            return "forgot/forgot_result";
        }

    }

    @PostMapping("/forgot")
    public String processForgotPassword(
            Model model,
            @Validated @ModelAttribute(name = "formObject") ForgotDTO form) {
        OutputTool outputTool = service.forgotPassword(form.getUsername());
        if (outputTool.getResult() == OutputTool.Result.SUCCESS) {
            return "redirect:/forgot?complete=true";
        } else {
            model.addAttribute("formObject", form);
            model.addAttribute("alertList", List.of(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment())));
            return "forgot/forgot_form";
        }
    }

    @GetMapping(value = {"/login"})
    public String showLoginForm(
            @RequestParam(name = "error", required = false) String error,
            Model model) {
        alertList = new ArrayList<>(1);
        if (error != null) {
            model.addAttribute("danger", true);
            alertList.add(new Alert().build(Alert.AlertType.DANGER, "Login Failed. Please try again later."));
        }
        model.addAttribute("alertList", alertList);
        return "login/login_form";
    }

    @GetMapping(value = {"/profile"})
    public String getProfileUpdateForms(
            Model model,
            @RequestParam(name = "pwd", required = false) boolean isPassword) {
        model.addAttribute("formObject", service.getProfile(HelperTools.getLoggedInUsername()));
        if (isPassword) {
            return "profile/profile_password_form";
        }
        return "profile/profile_update_form";
    }

    @PostMapping(value = {"/profile"})
    public String updateUserDetails(
            @Validated @ModelAttribute(name = "profileObject") UserProfileDTO userProfileDTO,
            RedirectAttributes redirectAttributes) {
        // No error expected as front end validation is active for profile and password
        OutputTool outputTool = service.updateProfile(userProfileDTO);
        redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/home";

    }

    @GetMapping(value = {"/", "/home"})
    public String showUserHomePage(Model model) {
        UserProfileDTO userProfileDTO = service.getProfile(HelperTools.getLoggedInUsername());
        model.addAttribute("profileObject", userProfileDTO);
        String welcome = HelperTools.getString("static/html/home-intro.html")
                .replaceAll("~NAME~", userProfileDTO.getName())
                .replaceAll("~APP_NAME~", Objects.requireNonNull(environment.getProperty("atech.app.name")));
        model.addAttribute("welcome", welcome);
        return "home";
    }

}
