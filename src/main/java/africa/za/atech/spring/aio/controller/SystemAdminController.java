package africa.za.atech.spring.aio.controller;

import africa.za.atech.spring.aio.exceptions.GenericException;
import africa.za.atech.spring.aio.functions.assistant.AssistantService;
import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import africa.za.atech.spring.aio.functions.users.BulkRegistrationService;
import africa.za.atech.spring.aio.functions.users.RegType;
import africa.za.atech.spring.aio.functions.users.SecurityRole;
import africa.za.atech.spring.aio.functions.users.UsersService;
import africa.za.atech.spring.aio.functions.users.dto.DepartmentDTO;
import africa.za.atech.spring.aio.functions.users.dto.OrganisationDTO;
import africa.za.atech.spring.aio.functions.users.dto.UserProfileDTO;
import africa.za.atech.spring.aio.functions.users.dto.WhitelistRegDTO;
import africa.za.atech.spring.aio.functions.users.model.RegistrationWhitelist;
import africa.za.atech.spring.aio.functions.users.repo.DepartmentRepo;
import africa.za.atech.spring.aio.utils.Alert;
import africa.za.atech.spring.aio.utils.OutputTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SystemAdminController {

    private final UsersService service;
    private final AssistantService assistantService;
    private final BulkRegistrationService bulkRegistrationService;
    private final DepartmentRepo departmentRepo;
    private List<Alert> alertList;

    @Value("${atech.app.register.type}")
    private String regType;

    @GetMapping(value = {"/admin/organisation"})
    public String showOrgHome(Model model) {
        List<OrganisationDTO> recordsList = service.getAllOrganisation();
        model.addAttribute("recordList", recordsList);
        return "admin/organisation/organisation_list";
    }

    @GetMapping(value = {"/admin/organisation/add"})
    public String showOrgInsertForm(Model model) {
        model.addAttribute("formObject", new OrganisationDTO());
        return "admin/organisation/organisation_insert";
    }

    @PostMapping(value = {"/admin/organisation"})
    public String processOrgInsertForm(
            @Validated @ModelAttribute(name = "formObject") OrganisationDTO form,
            RedirectAttributes redirectAttributes) {

        alertList = new ArrayList<>(1);

        OutputTool outputTool = service.addOrganisation(SecurityContextHolder.getContext().getAuthentication().getName(), form);
        if (outputTool.getResult().equals(OutputTool.Result.EXCEPTION)) {
            redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment())));
            return "redirect:/admin/organisation";
        }
        redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/admin/organisation";
    }

    @GetMapping(value = {"/admin/organisation/update"})
    public String showOrgUpdateForm(
            Model model,
            @RequestParam(name = "id") String maskedId) {
        model.addAttribute("formObject", service.getOrganisation(maskedId));
        return "admin/organisation/organisation_update";
    }

    @PostMapping(value = {"/admin/organisation/update"})
    public String processOrgUpdateForm(
            @Validated @ModelAttribute(name = "formObject") OrganisationDTO form,
            RedirectAttributes redirectAttributes) {
        OutputTool outputTool = service.updateOrganisation(SecurityContextHolder.getContext().getAuthentication().getName(), form);
        if (outputTool.getResult().equals(OutputTool.Result.EXCEPTION)) {
            redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment())));
            return "redirect:/admin/organisation";
        }
        redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/admin/organisation";
    }

    @GetMapping(value = {"/admin/organisation/delete/{id}"})
    public String processOrgDelete(@PathVariable(name = "id") String maskedId, RedirectAttributes redirectAttributes) {
        OutputTool outputTool = service.deleteOrganisation(SecurityContextHolder.getContext().getAuthentication().getName(), maskedId);
        redirectAttributes.addFlashAttribute("recordList", service.getAllOrganisation());
        if (outputTool.getResult().equals(OutputTool.Result.EXCEPTION)) {
            redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment())));
            return "redirect:/admin/organisation";
        }
        redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/admin/organisation";
    }

    @GetMapping(value = {"/admin/department/{id}"})
    public String showDepartments(@PathVariable(name = "id") String organisationMaskedId, Model model) {
        OrganisationDTO organisation = service.getOrganisation(organisationMaskedId);
        model.addAttribute("orgName", WordUtils.capitalizeFully(organisation.getName()));
        model.addAttribute("orgMaskedId", organisation.getMaskedId());
        model.addAttribute("recordList", organisation.getOrganisationMetaDTO().getListOfDepartments());
        return "admin/organisation/department_list";
    }

    @GetMapping(value = {"/admin/department/add/{id}"})
    public String showDepartmentForm(@PathVariable(name = "id") String organisationMaskedId, Model model) {
        OrganisationDTO org = service.getOrganisation(organisationMaskedId);
        DepartmentDTO form = new DepartmentDTO();
        form.setOrganisationMaskedId(org.getMaskedId());
        form.setOrganisationName(org.getName());
        model.addAttribute("formObject", form);
        return "admin/organisation/department_insert";
    }

    @PostMapping(value = {"/admin/department/add"})
    public String processDepartmentInsertForm(
            @Validated @ModelAttribute(name = "formObject") DepartmentDTO form,
            RedirectAttributes redirectAttributes) {

        OutputTool outputTool = service.addDepartmentForOrg(SecurityContextHolder.getContext().getAuthentication().getName(), form);
        if (outputTool.getResult().equals(OutputTool.Result.PROCESS_RULE)) {
            redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment())));
            return "redirect:/admin/department/" + form.getOrganisationMaskedId();
        }
        redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/admin/department/" + form.getOrganisationMaskedId();
    }

    @GetMapping(value = {"/admin/department/update/{id}"})
    public String showDepartmentUpdateForm(@PathVariable(name = "id") String departmentMaskedId, Model model) {
        DepartmentDTO form = service.getDepartment("", departmentMaskedId);
        model.addAttribute("formObject", form);
        return "admin/organisation/department_update";
    }

    @PostMapping(value = {"/admin/department/update"})
    public String processDepartmentUpdateForm(
            @Validated @ModelAttribute(name = "formObject") DepartmentDTO form,
            RedirectAttributes redirectAttributes) {
        OutputTool outputTool = service.updateDepartmentForOrg("", form);
        if (outputTool.getResult().equals(OutputTool.Result.EXCEPTION)) {
            redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment())));
            return "redirect:/admin/department/" + form.getOrganisationMaskedId();
        }
        redirectAttributes.addFlashAttribute("alertList", List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/admin/department/" + form.getOrganisationMaskedId();
    }

    @GetMapping(value = {"/admin/department/delete/{organisationId}/{id}"})
    public String processDeleteDepartment(
            @PathVariable(name = "organisationId") String organisationMaskedId,
            @PathVariable(name = "id") String departmentMaskedId,
            RedirectAttributes redirectAttributes) {
        OutputTool outputTool = service.deleteDepartmentForOrg("", departmentMaskedId);
        redirectAttributes.addFlashAttribute(List.of(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment())));
        return "redirect:/admin/department/" + organisationMaskedId;
    }

    @GetMapping(value = {"/admin/users"})
    public String showUserHome(Model model) {
        String loggedInRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserProfileDTO> recordsList = service.getProfiles();
        model.addAttribute("recordList", recordsList);

        if (!loggedInRole.contains("ADMIN")) {
            int admin = 0;
            int user = 0;
            for (int i = 0; i < recordsList.size(); i++) {
                if (recordsList.get(i).getUsername().equalsIgnoreCase("admin")) {
                    admin = i;
                }
                if (recordsList.get(i).getUsername().equalsIgnoreCase("user")) {
                    user = i;
                }
            }
            recordsList.remove(admin);
            recordsList.remove(user);
        }
        return "admin/users/users_list";
    }

    @GetMapping(value = {"/admin/users/update"})
    public String showAdminUpdateForm(
            Model model,
            @RequestParam(name = "username") String username) throws GenericException {

        UserProfileDTO profile = service.getProfile(username);
        model.addAttribute("formObject", profile);

        List<String> securityRoles = new ArrayList<>(3);
        securityRoles.add(SecurityRole.USER.getValue());
        securityRoles.add(SecurityRole.MANAGER.getValue());
        securityRoles.add(SecurityRole.ADMIN.getValue());
        model.addAttribute("enumList", securityRoles);

        List<Assistants> assistantsList = assistantService.getAllActiveAssistants();
        List<String> assistantsEnumList = new ArrayList<>(2);
        for (Assistants s : assistantsList) {
            assistantsEnumList.add(s.getName());
        }
        model.addAttribute("assistantsEnumList", assistantsEnumList);

        return "admin/users/users_update";
    }

    @PostMapping(value = {"/admin/users/update"})
    public String processAdminUpdateForm(
            @Validated @ModelAttribute(name = "formObject") UserProfileDTO form,
            RedirectAttributes redirectAttributes) {

        alertList = new ArrayList<>(1);

        // Disable account changes made to own account
        if (form.getUsername().equalsIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName())) {
            alertList.add(new Alert().build(Alert.AlertType.DANGER, "Unable to process action."));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/admin/users?updated=no";
        }
        OutputTool outputTool = service.updateProfile(form);
        alertList.add(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment()));
        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/admin/users?updated=yes";
    }

    @GetMapping("/admin/users/delete/{maskedId}")
    public String deleteChat(
            @PathVariable(name = "maskedId") String maskedId,
            RedirectAttributes redirectAttributes) {
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        alertList = new ArrayList<>(1);

        OutputTool outputTool = service.deleteUser(loggedInUser, maskedId);
        if (outputTool.getResult().equals(OutputTool.Result.EXCEPTION)) {
            alertList.add(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment()));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/admin/users?deleted=false";
        }
        alertList.add(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment()));
        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/admin/users?deleted=true";
    }


    @GetMapping(value = {"/admin/whitelist"})
    public String showWhitelistHome(Model model, RedirectAttributes redirectAttributes) {

        if (regType.equalsIgnoreCase(RegType.PRIVATE.getValue())) {
            List<RegistrationWhitelist> recordsList = service.getWhitelistUsers();
            model.addAttribute("recordList", recordsList);
            return "admin/whitelisting/whitelist_list";
        } else {
            alertList = new ArrayList<>(1);
            alertList.add(new Alert().build(Alert.AlertType.WARNING, "Registration is open to the public. Whitelisting is disabled."));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/home?whitelist=disabled";
        }
    }

    @GetMapping(value = {"/admin/whitelist/insert"})
    public String showWhitelistInsertForm(
            Model model,
            RedirectAttributes redirectAttributes) {

        WhitelistRegDTO formObject = new WhitelistRegDTO();
        model.addAttribute("formObject", formObject);

        List<Assistants> assistantsList = assistantService.getAllActiveAssistants();
        if (assistantsList.isEmpty()) {
            alertList = new ArrayList<>(1);
            alertList.add(new Alert().build(Alert.AlertType.DANGER, "No assistants are captured. Please capture an assistant before whitelisting users."));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/admin/whitelist?assistants=false";
        }

        List<String> assistantsEnumList = new ArrayList<>(2);
        for (Assistants s : assistantsList) {
            assistantsEnumList.add(s.getName());
        }
        model.addAttribute("assistantsEnumList", assistantsEnumList);
        return "admin/whitelisting/whitelist_insert";
    }


    @PostMapping(value = {"/admin/whitelist"})
    public String processWhitelistInsertForm(
            Model model,
            @Validated @ModelAttribute(name = "formObject") WhitelistRegDTO form,
            RedirectAttributes redirectAttributes) {

        alertList = new ArrayList<>(1);
        boolean hasErrors = false;

        if (form.getCustomPropertyA().isBlank()) {
            alertList.add(new Alert().build(Alert.AlertType.DANGER, "Please select an assistant to continue."));
            hasErrors = true;
        }

        if (!hasErrors) {
            // Adding null for null constraints of unused fields
            if (form.getEmailAddress() == null) {
                form.setEmailAddress("null");
            }
            form.setCustomPropertyB("null");
            form.setCustomPropertyC("null");
            form.setCustomPropertyD("null");

            OutputTool outputTool = service.addWhitelistEntry(SecurityContextHolder.getContext().getAuthentication().getName(), form);
            if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
                alertList.add(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment()));
                hasErrors = true;
            } else {
                alertList.add(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment()));
            }
        }

        if (hasErrors) {
            List<Assistants> assistantsList = assistantService.getAllActiveAssistants();
            List<String> assistantsEnumList = new ArrayList<>(2);
            for (Assistants s : assistantsList) {
                assistantsEnumList.add(s.getName());
            }
            model.addAttribute("formObject", form);
            model.addAttribute("assistantsEnumList", assistantsEnumList);
            model.addAttribute("alertList", alertList);
            return "admin/whitelisting/whitelist_insert";
        }

        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/admin/whitelist?added=true";
    }


    @GetMapping(value = {"/admin/whitelist/update/{username}"})
    public String showWhitelistUpdateForm(
            Model model,
            @PathVariable(name = "username") String username,
            RedirectAttributes redirectAttributes) {
        alertList = new ArrayList<>(1);

        OutputTool outputTool = service.getWhitelistRecord(username);
        if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
            alertList.add(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment()));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/admin/whitelist?updated=no";
        }

        RegistrationWhitelist record = (RegistrationWhitelist) outputTool.getObject();
        WhitelistRegDTO formObject = new WhitelistRegDTO();
        formObject.setUsername(record.getUsername());
        formObject.setName(record.getName());
        formObject.setSurname(record.getSurname());
        formObject.setEmailAddress(record.getEmailAddress());
        formObject.setCustomPropertyA(record.getCustomPropertyA());
        formObject.setCustomPropertyB(record.getCustomPropertyB());
        formObject.setCustomPropertyC(record.getCustomPropertyC());
        formObject.setCustomPropertyD(record.getCustomPropertyD());

        List<Assistants> assistantsList = assistantService.getAllActiveAssistants();
        List<String> assistantsEnumList = new ArrayList<>(2);
        for (Assistants s : assistantsList) {
            assistantsEnumList.add(s.getName());
        }
        model.addAttribute("formObject", formObject);
        model.addAttribute("assistantsEnumList", assistantsEnumList);
        model.addAttribute("alertList", alertList);
        return "admin/whitelisting/whitelist_update";
    }

    @PostMapping(value = {"/admin/whitelist/update"})
    public String processWhitelistUpdateForm(
            @Validated @ModelAttribute(name = "formObject") WhitelistRegDTO form,
            RedirectAttributes redirectAttributes) {
        alertList = new ArrayList<>(1);
        OutputTool outputTool = service.updateWhitelistEntry(SecurityContextHolder.getContext().getAuthentication().getName(), form);
        alertList.add(new Alert().build(Alert.AlertType.SUCCESS, outputTool.getComment()));
        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/admin/whitelist?updated=true";
    }


    @GetMapping(value = {"/admin/whitelist/delete/{username}"})
    public String deleteWhitelistCall(
            @PathVariable(value = "username") String usernameToDelete,
            RedirectAttributes redirectAttributes) {
        // Using get mapping for record deletion
        alertList = new ArrayList<>(1);

        OutputTool outputTool = service.getWhitelistRecord(usernameToDelete);
        if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
            alertList.add(new Alert().build(Alert.AlertType.DANGER, outputTool.getComment()));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/admin/whitelist?updated=no";
        }

        service.deleteWhitelistEntry(usernameToDelete);
        alertList.add(new Alert().build(Alert.AlertType.SUCCESS, "Whitelist record with username: [" + usernameToDelete + "] has been deleted."));
        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/admin/whitelist?deleted=true";
    }


    @GetMapping(value = {"/admin/whitelist/insert/bulk"})
    public String showWhitelistBulkForm(RedirectAttributes redirectAttributes) {
        List<Assistants> assistantsList = assistantService.getAllAssistants();
        if (assistantsList.isEmpty()) {
            alertList = new ArrayList<>(1);
            alertList.add(new Alert().build(Alert.AlertType.DANGER, "No assistants are captured. Please capture an assistant before whitelisting users."));
            redirectAttributes.addFlashAttribute("alertList", alertList);
            return "redirect:/admin/whitelist?error=true";
        }
        return "admin/whitelisting/whitelist_bulk";
    }


    @PostMapping("/admin/whitelist/insert/bulk")
    public String handleFileUpload(
            @RequestParam("formObject") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        log.info("File received with name: {} and content type: {}", file.getOriginalFilename(), file.getContentType());

        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        alertList = new ArrayList<>(1);

        bulkRegistrationService.processCsv(loggedInUser, file);

        alertList.add(new Alert().build(Alert.AlertType.INFO, "File uploaded, the results will be emailed to you."));
        redirectAttributes.addFlashAttribute("alertList", alertList);
        return "redirect:/admin/whitelist";
    }

}
