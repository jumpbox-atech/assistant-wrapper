package africa.za.atech.spring.aio.controller;

import africa.za.atech.spring.aio.exceptions.GenericException;
import africa.za.atech.spring.aio.functions.assistant.AssistantService;
import africa.za.atech.spring.aio.functions.assistant.dto.AssistantDTO;
import africa.za.atech.spring.aio.functions.users.UsersService;
import africa.za.atech.spring.aio.functions.users.dto.UserProfileDTO;
import africa.za.atech.spring.aio.utils.Alert;
import africa.za.atech.spring.aio.utils.OutputTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;
    private final UsersService usersService;

    @GetMapping(value = {"/admin/assistants"})
    public String showAssistantsHome(Model model) {
        List<AssistantDTO> recordsList = assistantService.getAllAssistantsInfo();
        model.addAttribute("recordList", recordsList);
        return "admin/assistants/assistant_list";
    }

    @GetMapping(value = {"/admin/assistants/insert"})
    public String getAssistantAddForm(Model model) throws GenericException {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileDTO u = usersService.getProfile(loggedInUser);

        AssistantDTO formObject = new AssistantDTO();
        formObject.setOrganisationMaskedId(u.getOrganisationMaskedId());
        model.addAttribute("formObject", formObject);
        return "admin/assistants/assistant_insert";
    }

    @PostMapping(value = {"/admin/assistants"})
    public String processAssistantAddForm(
            Model model,
            @Validated @ModelAttribute(name = "formObject") AssistantDTO form,
            RedirectAttributes redirectAttributes) {

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        OutputTool outputTool = assistantService.insertAssistant(loggedInUser, form);
        if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
            model.addAttribute("formObject", form);
            model.addAttribute("alertList", new Alert().build(Alert.AlertType.DANGER, outputTool.getComment()));
            return "admin/assistants/assistant_insert";
        }
        redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/admin/assistants?added=true";
    }

    @GetMapping(value = {"/admin/assistants/update"})
    public String getAssistantUpdateForm(
            Model model, @RequestParam(name = "id") String maskedId) {
        AssistantDTO formObject = (AssistantDTO) assistantService.getAssistant(maskedId).getObject();
        model.addAttribute("formObject", formObject);
        return "admin/assistants/assistant_update";
    }

    @PostMapping(value = {"/admin/assistants/update"})
    public String processAssistantUpdateForm(
            Model model,
            @Validated @ModelAttribute(name = "formObject") AssistantDTO form,
            RedirectAttributes redirectAttributes) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        OutputTool outputTool = assistantService.updateAssistant(loggedInUser, form);
        if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
            model.addAttribute("formObject", form);
            model.addAttribute("alertList", List.of(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment())));
            return "admin/assistants/assistant_update";
        } else {
            redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
            return "redirect:/admin/assistants?updated=true";
        }
    }

}
