package africa.za.atech.spring.aio.controller;

import africa.za.atech.spring.aio.functions.assistant.AssistantService;
import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import africa.za.atech.spring.aio.functions.assistant.dto.AssistantDTO;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;
    private List<Alert> alertList;

    @GetMapping(value = {"/admin/assistants"})
    public String showAssistantsHome(Model model) {

        List<Assistants> recordsList = assistantService.getAllAssistants();
        model.addAttribute("recordList", recordsList);
        return "admin/assistants/assistant_list";
    }

    @GetMapping(value = {"/admin/assistants/insert"})
    public String getAssistantAddForm(Model model) {
        AssistantDTO formObject = new AssistantDTO();
        model.addAttribute("formObject", formObject);
        return "admin/assistants/assistant_insert";
    }

    @PostMapping(value = {"/admin/assistants"})
    public String processAssistantAddForm(
            Model model,
            @Validated @ModelAttribute(name = "formObject") AssistantDTO form,
            RedirectAttributes redirectAttributes) {
        alertList = new ArrayList<>(1);

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        OutputTool outputTool = assistantService.insertAssistant(loggedInUser, form);
        if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
            alertList.add(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment()));
            model.addAttribute("formObject", form);
            model.addAttribute("alertList", alertList);
            return "admin/assistants/assistant_insert";
        }
        alertList.add(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment()));
        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/admin/assistants?added=true";
    }

    @GetMapping(value = {"/admin/assistants/update"})
    public String getAssistantUpdateForm(
            Model model, @RequestParam(name = "id") long assistantId) {
        alertList = new ArrayList<>(1);

        Assistants record = (Assistants) assistantService.getAssistant(assistantId).getObject();

        AssistantDTO formObject = new AssistantDTO();
        formObject.setId(record.getId());
        formObject.setName(record.getName());
        formObject.setDescription(record.getDescription());
        formObject.setAdditionalInstructions(record.getAdditionalInstructions());
        formObject.setApiKey("masked");
        formObject.setDisabled(record.isDisabled());

        model.addAttribute("formObject", formObject);
        model.addAttribute("alertList", alertList);
        return "admin/assistants/assistant_update";
    }

    @PostMapping(value = {"/admin/assistants/update"})
    public String processAssistantUpdateForm(
            Model model,
            @Validated @ModelAttribute(name = "formObject") AssistantDTO form,
            RedirectAttributes redirectAttributes) {
        alertList = new ArrayList<>(1);

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();

        OutputTool outputTool = assistantService.updateAssistant(loggedInUser, form);
        if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
            alertList.add(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment()));
            model.addAttribute("formObject", form);
            model.addAttribute("alertList", alertList);
            return "admin/assistants/assistant_update";
        } else {
            alertList.add(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment()));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/admin/assistants?updated=true";
        }
    }

}
