package africa.za.atech.spring.aio.functions.users;

import africa.za.atech.spring.aio.functions.chats.ChatService;
import africa.za.atech.spring.aio.functions.users.dto.*;
import africa.za.atech.spring.aio.functions.users.model.Department;
import africa.za.atech.spring.aio.functions.users.model.Organisation;
import africa.za.atech.spring.aio.functions.users.model.RegistrationWhitelist;
import africa.za.atech.spring.aio.functions.users.model.Users;
import africa.za.atech.spring.aio.functions.users.repo.DepartmentRepo;
import africa.za.atech.spring.aio.functions.users.repo.OrganisationRepo;
import africa.za.atech.spring.aio.functions.users.repo.UsersRepo;
import africa.za.atech.spring.aio.functions.users.repo.WhitelistRegRepo;
import africa.za.atech.spring.aio.utils.EmailTools;
import africa.za.atech.spring.aio.utils.HelperTools;
import africa.za.atech.spring.aio.utils.OutputTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    @Value("${atech.app.config.user.admin.username}")
    private String systemAdminUsername;
    @Value("${atech.app.config.user.admin.create}")
    private boolean createSystemAdminUser;
    @Value("${atech.app.config.user.admin.name}")
    private String systemAdminName;
    @Value("${atech.app.config.user.admin.surname}")
    private String systemAdminSurname;
    @Value("${atech.app.config.user.admin.email}")
    private String systemAdminEmailAddress;
    @Value("${atech.app.config.user.admin.pwd}")
    private String systemAdminPwd;

    @Value("${atech.app.register.type}")
    private String regType;
    @Value("${atech.app.email.from.email-address}")
    private String fromEmailAddress;
    @Value("${atech.app.name}")
    private String appName;
    @Value("${atech.app.url.login}")
    private String appUrl;

    private final UsersRepo repoUsers;
    private final WhitelistRegRepo repoWhiteList;
    private final OrganisationRepo repoOrganisation;
    private final DepartmentRepo repoDepartment;
    private final EmailTools emailTools;
    private final ChatService chatService;

    private final PasswordEncoder encoder;

    private static String systemOrgUuid;
    private static String unassignedOrgUuid;

    public List<OrganisationDTO> getAllOrganisation() {
        return repoOrganisation.findAll()
                .stream()
                .map(this::getOrganisation)
                .toList();
    }

    public OrganisationDTO getOrganisation(String organisationId) {
        return getOrganisation(repoOrganisation.findByUid(organisationId).get());
    }

    private OrganisationDTO getOrganisation(Organisation record) {
        OrganisationMetaDTO meta = new OrganisationMetaDTO();
        meta.setListOfDepartments(repoDepartment.findAllByOrganisationId(record.getId())
                .stream()
                .map(department -> new DepartmentDTO().build(record, department))
                .toList());
        meta.setListOfAssistants(new ArrayList<>());
        meta.setListOfUsers(new ArrayList<>());
        meta.setListOfChats(new ArrayList<>());
        meta.setDepartmentsCount(meta.getListOfDepartments().size());
        meta.setAssistantsCount(meta.getListOfAssistants().size());
        meta.setUsersCount(meta.getListOfUsers().size());
        meta.setChatsCount(meta.getListOfChats().size());

        if (meta.getDepartmentsCount() == 0 && meta.getAssistantsCount() == 0 && meta.getUsersCount() == 0) {
            meta.setDelete(true);
        }
        return new OrganisationDTO().build(record, meta);
    }

    public OutputTool addOrganisation(OrganisationDTO form) {
        Optional<Organisation> lookup = repoOrganisation.findByNameIgnoreCase(form.getName());
        if (lookup.isPresent()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Organisation " + HelperTools.wrapVar(form.getName()) + " already exists.", null);
        }
        Organisation record = new Organisation().buildInsert(HelperTools.getLoggedInUsername(), form);
        repoOrganisation.save(record);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Organisation added successfully.", new OrganisationDTO().build(record, new OrganisationMetaDTO()));
    }

    public OutputTool updateOrganisation(OrganisationDTO form) {
        Optional<Organisation> lookup = repoOrganisation.findByName(form.getName());
        if (lookup.isPresent()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Organisation " + HelperTools.wrapVar(form.getName()) + " already exists.", null);
        }
        Optional<Organisation> record = repoOrganisation.findByUid(form.getUid());
        if (record.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Organisation does not exist.", null);
        }
        record.get().setName(form.getName());
        record.get().setUpdateBy(HelperTools.getLoggedInUsername());
        record.get().setUpdateDatetime(LocalDateTime.now());
        repoOrganisation.save(record.get());
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Organisation updated successfully.", null);
    }

    public OutputTool deleteOrganisation(String maskedId) {
        Optional<Organisation> organisationRecord = repoOrganisation.findByUid(maskedId);
        if (organisationRecord.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Organisation does not exist.", null);
        }
        repoOrganisation.delete(organisationRecord.get());
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Organisation deleted successfully.", null);
    }

    public OutputTool addDepartmentForOrg(DepartmentDTO form) {
        Optional<Organisation> orgRecord = repoOrganisation.findByUid(form.getOrganisationUid());
        List<Department> existingDepartments = repoDepartment.findAllByOrganisationId(orgRecord.get().getId());
        if (!existingDepartments.isEmpty()) {
            for (Department d : existingDepartments) {
                if (d.getName().equalsIgnoreCase(form.getName())) {
                    return new OutputTool().build(OutputTool.Result.PROCESS_RULE, "Department " + HelperTools.wrapVar(form.getName()) + " already exists for the '" + form.getOrganisationName() + "' organisation.", null);
                }
            }
        }
        Department record = new Department().buildInsert(HelperTools.getLoggedInUsername(), orgRecord.get().getId(), form);
        repoDepartment.save(record);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Department added successfully to the '" + record.getName() + "' organisation.", null);
    }

    public DepartmentDTO getDepartment(String maskedId) {
        Department departmentRecord = repoDepartment.findByMaskedId(maskedId).get();
        Organisation orgRecord = repoOrganisation.findById(departmentRecord.getOrganisationId()).get();
        return new DepartmentDTO().build(orgRecord, departmentRecord);
    }

    public OutputTool updateDepartmentForOrg(DepartmentDTO form) {
        Optional<Organisation> orgRecord = repoOrganisation.findByUid(form.getOrganisationUid());
        List<Department> existingDepartments = repoDepartment.findAllByOrganisationId(orgRecord.get().getId());
        if (!existingDepartments.isEmpty()) {
            for (Department d : existingDepartments) {
                if (d.getName().equals(form.getName())) {
                    return new OutputTool().build(OutputTool.Result.EXCEPTION, "Department " + HelperTools.wrapVar(form.getName()) + " already exists for the '" + form.getOrganisationName() + "' organisation.", null);
                }
            }
        }
        Optional<Department> record = repoDepartment.findByMaskedId(form.getUid());
        if (record.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Department does not exist.", null);
        }
        record.get().setName(form.getName());
        record.get().setUpdateBy(HelperTools.getLoggedInUsername());
        record.get().setUpdateDatetime(LocalDateTime.now());
        repoDepartment.save(record.get());
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Department updated successfully.", null);
    }

    public OutputTool deleteDepartmentForOrg(String departmentMaskedId) {
        repoDepartment.deleteAllByMaskedId(departmentMaskedId);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Department deleted successfully", null);
    }

    public Users addUser(String organisationUuid, String createdBy, String username, String name,
                         String surname, String emailAddress, SecurityRole role, String password,
                         List<String> assistantsUuids) {
        Users userRecord = new Users().buildInsert(organisationUuid, createdBy, username, name, surname,
                emailAddress, role.getValue(), encoder.encode(password), assistantsUuids);
        repoUsers.save(userRecord);
        log.info("User with username: [{}] has been created with the role: [{}]", userRecord.getUsername(), role.getValue());
        return userRecord;
    }

    public OutputTool registerNewUser(RegisterDTO registerDTO) {
        Optional<Users> usernameLookup = repoUsers.findByUsernameIgnoreCase(registerDTO.getUsername());
        if (usernameLookup.isPresent()) {
            log.info("Account exists for the username: {}", registerDTO.getUsername());
            return new OutputTool().build(OutputTool.Result.PROCESS_RULE, "Account exists for the username: " + registerDTO.getUsername(), null);
        }

        Optional<RegistrationWhitelist> whiteListing = repoWhiteList.findByUsernameIgnoreCase(registerDTO.getUsername());
        if (regType.equalsIgnoreCase(RegType.PRIVATE.getValue())) {
            if (whiteListing.isEmpty()) {
                log.warn("User not whitelisted for registration: {}", registerDTO.getUsername());
                return new OutputTool().build(OutputTool.Result.PROCESS_RULE, "User not allowed to register", null);
            }
        }

        // Registration process for private and public policy
        String password = HelperTools.generatePassword(9, 2, 0);
        Users userRecord = addUser(unassignedOrgUuid, "system",
                registerDTO.getUsername().toLowerCase(),
                registerDTO.getName(),
                registerDTO.getSurname(),
                registerDTO.getEmailAddress(),
                SecurityRole.USER,
                password,
                new ArrayList<>());

        // Send Email
        String subject = "Registration Successful: Welcome to ~APP_NAME~!"
                .replaceAll("~APP_NAME~", appName);

        String body = HelperTools.getString("static/html/email-register_user.html");
        body = body.replaceAll("~USER~", registerDTO.getName() + " " + registerDTO.getSurname());
        body = body.replaceAll("~USERNAME~", registerDTO.getUsername());
        body = body.replaceAll("~APP_NAME~", appName);
        body = body.replaceAll("~TEMP_PASSWORD~", password);
        body = body.replaceAll("~APP_URL~", appUrl);
        body = body.replaceAll("~FROM_EMAIL_ADDRESS~", fromEmailAddress);

        OutputTool outputTool = emailTools.send(List.of(userRecord.getEmailAddress()), subject, body, null, List.of(fromEmailAddress), null);
        if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
            return outputTool;
        }

        if (regType.equalsIgnoreCase(RegType.PRIVATE.getValue())) {
            whiteListing.ifPresent(repoWhiteList::delete);
        }

        return new OutputTool().build(OutputTool.Result.SUCCESS, "Registration completed successfully. " +
                "Please check your email for additional information.", null);
    }

    public List<UserProfileDTO> getProfiles() {
        UserProfileDTO requestingUser = getProfile(HelperTools.getLoggedInUsername());
        List<UserProfileDTO> allUsers = new ArrayList<>(repoUsers.findAll().stream().map(u -> new UserProfileDTO().build(repoOrganisation.findByUid(u.getOrganisationUid()).get().getName(), u)).toList());

        Iterator<UserProfileDTO> iterator = allUsers.iterator();
        UserProfileDTO dto;
        if (requestingUser.getRoles().equalsIgnoreCase(SecurityRole.ORG_ADMIN.getValue())) {
            while (iterator.hasNext()) {
                dto = iterator.next();
                if (!dto.getOrganisationUuid().equals(requestingUser.getOrganisationUuid()) || dto.getRoles().equalsIgnoreCase(SecurityRole.ADMIN.getValue())) {
                    iterator.remove();
                }
            }
        }
        if (requestingUser.getRoles().equalsIgnoreCase(SecurityRole.MANAGER.getValue())) {
            while (iterator.hasNext()) {
                dto = iterator.next();
                if (!dto.getOrganisationUuid().equals(requestingUser.getOrganisationUuid()) || !dto.getRoles().equalsIgnoreCase(SecurityRole.USER.getValue())) {
                    iterator.remove();
                }
            }
        }
        return allUsers;
    }

    public UserProfileDTO getProfile(String username) {
        Users record = repoUsers.findByUsernameIgnoreCase(username).get();
        Organisation organisation = repoOrganisation.findByUid(record.getOrganisationUid()).get();
        return new UserProfileDTO().build(organisation.getName(), record);
    }

    public OutputTool updateProfile(UserProfileDTO form) {
        Users userRecord = repoUsers.findByUid(form.getUid()).get();

        // If new password field is mot null, password update required
        if (form.getNewPassword() != null) {
            if (!form.getNewPassword().isEmpty() && !form.getConfirmPassword().isEmpty()) {
                userRecord.setPassword(encoder.encode(form.getNewPassword()));
            }
            repoUsers.save(userRecord);
            return new OutputTool().build(OutputTool.Result.SUCCESS, "Password updated successfully.", null);
        } else {
            // change organisation
            Organisation organisation = repoOrganisation.findByUid(form.getOrganisationUuid()).get();
            userRecord.setOrganisationUid(organisation.getUid());

            // Change name
            if (!userRecord.getName().equalsIgnoreCase(form.getName())) {
                userRecord.setName(form.getName());
            }
            // Change surname
            if (!userRecord.getSurname().equalsIgnoreCase(form.getSurname())) {
                userRecord.setSurname(form.getSurname());
            }
            // Change email
            if (!userRecord.getEmailAddress().equalsIgnoreCase(form.getEmailAddress())) {
                userRecord.setEmailAddress(form.getEmailAddress());
            }
            // Change Assistant
            if (form.getAssistantId() != null) {
                if (!userRecord.getAssistantsUuids().equalsIgnoreCase(form.getAssistantId())) {
                    userRecord.setAssistantsUuids(form.getAssistantId());
                }
            }
            // Change Role
            if (form.getRoles() != null) {
                if (!userRecord.getRole().equalsIgnoreCase(form.getRoles())) {
                    userRecord.setRole(form.getRoles().toUpperCase());
                }
            }
            // Account disable function
            if (userRecord.isDisabled() != form.isDisabled()) {
                userRecord.setDisabled(form.isDisabled());
            }
            repoUsers.save(userRecord);
            return new OutputTool().build(OutputTool.Result.SUCCESS, "Profile updated successfully.", null);
        }
    }

    public OutputTool forgotPassword(String username) {
        Optional<Users> lookupRecord = repoUsers.findByUsernameIgnoreCase(username);
        if (lookupRecord.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Unable to reset password. Account not found.", null);
        }
        // Validate account status and return response if required
        if (lookupRecord.get().isDisabled()) {
            return new OutputTool().build(OutputTool.Result.PROCESS_RULE, "Unable to reset password. Account has been disabled.", null);
        }

        // GENERATE TEMP PASSWORD
        String password = HelperTools.generatePassword(9, 2, 0);
        lookupRecord.get().setPassword(encoder.encode(password));
        repoUsers.save(lookupRecord.get());

        // SEND EMAIL
        String subject = "Temporary Credentials";
        String body = HelperTools.getString("static/html/email-forgot_password.html");
        body = body.replaceAll("~USER~", lookupRecord.get().getName() + " " + lookupRecord.get().getSurname());
        body = body.replaceAll("~APP_NAME~", appName);
        body = body.replaceAll("~TEMP_PASSWORD~", password);
        body = body.replaceAll("~APP_URL~", appUrl);
        body = body.replaceAll("~FROM_EMAIL_ADDRESS~", fromEmailAddress);

        OutputTool emailResult = emailTools.send(List.of(lookupRecord.get().getEmailAddress()), subject, body, null, null, null);
        if (emailResult.getResult().equals(OutputTool.Result.EXCEPTION)) {
            return emailResult;
        }
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Temp password sent to registered email.", null);
    }

    public OutputTool deleteUser(String uuid) {
        // Get User
        Optional<Users> userRecord = repoUsers.findByUid(uuid);
        if (userRecord.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "User record not found.", null);
        }
        if (userRecord.get().getUsername().equalsIgnoreCase(HelperTools.getLoggedInUsername())) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Unable to process action.", null);
        }
        // TODO: change to use users uid
        OutputTool.Result result = chatService.deleteAllUsersChats(userRecord.get().getUsername()).getResult();
        if (result == OutputTool.Result.SUCCESS) {
            repoUsers.delete(userRecord.get());
            return new OutputTool().build(OutputTool.Result.SUCCESS, "User deleted successfully.", null);
        } else {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Unable to delete user record.", null);
        }
    }

    public void addSystemAdmin() {
        Optional<Organisation> systemLookup = repoOrganisation.findByNameIgnoreCase("system");
        Organisation systemOrg;
        if (systemLookup.isEmpty()) {
            systemOrg = new Organisation();
            systemOrg.setUid(UUID.randomUUID().toString());
            systemOrg.setCreatedBy("system");
            systemOrg.setCreatedDateTime(LocalDateTime.now());
            systemOrg.setName("system");
            systemOrg.setDisabled(false);
            repoOrganisation.save(systemOrg);
        } else {
            systemOrg = systemLookup.get();
        }
        systemOrgUuid = systemOrg.getUid();

        Optional<Organisation> unassignedLookup = repoOrganisation.findByNameIgnoreCase("unassigned");
        Organisation unassignedOrg;
        if (unassignedLookup.isEmpty()) {
            unassignedOrg = new Organisation();
            unassignedOrg.setUid(UUID.randomUUID().toString());
            unassignedOrg.setCreatedBy("system");
            unassignedOrg.setCreatedDateTime(LocalDateTime.now());
            unassignedOrg.setName("unassigned");
            unassignedOrg.setDisabled(false);
            repoOrganisation.save(unassignedOrg);
        } else {
            unassignedOrg = unassignedLookup.get();
        }
        unassignedOrgUuid = unassignedOrg.getUid();

        Optional<Users> record = repoUsers.findByOrganisationUidAndUsernameIgnoreCase(systemOrgUuid, systemAdminUsername);
        if (record.isEmpty()) {
            if (createSystemAdminUser) {
                addUser(systemOrgUuid, "system", systemAdminUsername, systemAdminName, systemAdminSurname,
                        systemAdminEmailAddress, SecurityRole.ADMIN, systemAdminPwd, new ArrayList<>());
                log.info("*** System ADMIN has been created");
            }
        }
    }

    public List<RegistrationWhitelist> getWhitelistUsers() {
        return repoWhiteList.findAll();
    }

    /**
     * @return On successful lookup, RegistrationWhitelist object is returned.
     */
    public OutputTool getWhitelistRecord(String username) {
        Optional<RegistrationWhitelist> record = repoWhiteList.findByUsernameIgnoreCase(username.toLowerCase());
        if (record.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Whitelist record for username: [" + username + "] does not exist.", null);
        }
        if (record.get().isRegistered()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Whitelist record for username: [" + username + "] is already registered and cannot be updated.", null);
        }
        return new OutputTool().build(OutputTool.Result.SUCCESS, "", record.get());
    }

    public OutputTool addWhitelistEntry(WhitelistRegDTO whitelistRegDAOS) {
        Optional<RegistrationWhitelist> whitelistRegDTO = repoWhiteList.findByUsernameIgnoreCase(whitelistRegDAOS.getUsername().toLowerCase());
        if (whitelistRegDTO.isPresent()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Whitelisting with " +
                    "username: [" + whitelistRegDAOS.getUsername() + "] already exists.", null);
        }
        RegistrationWhitelist dto = new RegistrationWhitelist().buildInsert(HelperTools.getLoggedInUsername(), whitelistRegDAOS);
        repoWhiteList.save(dto);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Whitelisting for username: [" + dto.getUsername() +
                "] has been inserted successfully", null);
    }

    public OutputTool updateWhitelistEntry(WhitelistRegDTO form) {
        RegistrationWhitelist dto = repoWhiteList.findByUsernameIgnoreCase(form.getUsername().toLowerCase()).get();
        dto.setUsername(form.getUsername());
        dto.setName(form.getName());
        dto.setSurname(form.getSurname());
        dto.setCustomPropertyA(form.getCustomPropertyA());
        repoWhiteList.save(dto);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Whitelisting with username: [" + dto.getUsername() +
                "] has been updated successfully.", null);
    }

    public void deleteWhitelistEntry(String usernameToDelete) {
        repoWhiteList.delete(repoWhiteList.findByUsernameIgnoreCase(usernameToDelete.toLowerCase()).get());
    }

    public List<RegistrationWhitelist> whitelistRegistration(List<WhitelistRegDTO> whitelistRegDTOS) {
        List<RegistrationWhitelist> dtoList = new ArrayList<>();
        for (WhitelistRegDTO d : whitelistRegDTOS) {
            RegistrationWhitelist record = new RegistrationWhitelist().buildInsert(HelperTools.getLoggedInUsername(), d);
            dtoList.add(record);
        }
        repoWhiteList.saveAll(dtoList);
        return repoWhiteList.findAll();
    }

    public Model addProfileToModel(Model model) {
        // Required for profile and password modals
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileDTO userProfileDTO = getProfile(loggedInUser);
        model.addAttribute("profileObject", userProfileDTO);
        model.addAttribute("app_name", appName);
        return model;
    }
}

