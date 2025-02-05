package africa.za.atech.spring.aio.functions.users;

import africa.za.atech.spring.aio.exceptions.GenericException;
import africa.za.atech.spring.aio.functions.users.dto.RegisterDTO;
import africa.za.atech.spring.aio.functions.users.dto.UserProfileDTO;
import africa.za.atech.spring.aio.functions.users.dto.WhitelistRegDTO;
import africa.za.atech.spring.aio.functions.users.model.RegistrationWhitelist;
import africa.za.atech.spring.aio.functions.users.model.Users;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final EmailTools emailTools;

    private final PasswordEncoder encoder;

    public Users addUser(LocalDateTime createdDateTime,
                         String username,
                         String name,
                         String surname,
                         String emailAddress,
                         String plainPassword,
                         SecurityRole role,
                         String customPropertyA,
                         String customPropertyB,
                         String customPropertyC,
                         String customPropertyD,
                         String insertedBy) {

        Users userRecord = new Users().buildInsert(
                createdDateTime,
                username.toLowerCase(),
                name,
                surname,
                emailAddress,
                role,
                encoder.encode(plainPassword),
                customPropertyA,
                customPropertyB,
                customPropertyC,
                customPropertyD,
                insertedBy);
        repoUsers.save(userRecord);

        log.info("User with username: [{}] has been created with the role: [{}]", userRecord.getUsername(), role.getValue());
        return userRecord;
    }

    public OutputTool registerNewUser(RegisterDTO registerDTO) {
        log.info("Incoming registration: {}", registerDTO);
        String customPropA = "null";
        String customPropB = "null";
        String customPropC = "null";
        String customPropD = "null";

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
            customPropA = whiteListing.get().getCustomPropertyA();
            customPropB = whiteListing.get().getCustomPropertyB();
            customPropC = whiteListing.get().getCustomPropertyC();
            customPropD = whiteListing.get().getCustomPropertyD();
        }

        // Registration process for private and public policy
        String password = HelperTools.generatePassword(9, 2, 0);
        LocalDateTime dateTime = LocalDateTime.now();
        Users userRecord = addUser(
                dateTime,
                registerDTO.getUsername(),
                registerDTO.getName(),
                registerDTO.getSurname(),
                registerDTO.getEmailAddress(),
                password,
                SecurityRole.USER,
                customPropA,
                customPropB,
                customPropC,
                customPropD,
                "system-registration");

        // Send Email
        List<String> to = new ArrayList<>();
        to.add(userRecord.getEmailAddress());

        List<String> bcc = new ArrayList<>();
        bcc.add(fromEmailAddress);

        String subject = "Registration Successful: Welcome to ~APP_NAME~!"
                .replaceAll("~APP_NAME~", appName);

        String body = HelperTools.getString("static/html/email-register_user.html");
        body = body.replaceAll("~USER~", registerDTO.getName() + " " + registerDTO.getSurname());
        body = body.replaceAll("~USERNAME~", registerDTO.getUsername());
        body = body.replaceAll("~APP_NAME~", appName);
        body = body.replaceAll("~TEMP_PASSWORD~", password);
        body = body.replaceAll("~APP_URL~", appUrl);
        body = body.replaceAll("~FROM_EMAIL_ADDRESS~", fromEmailAddress);

        OutputTool outputTool = emailTools.send(to, subject, body, null, bcc, null);
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
        List<Users> usersList = repoUsers.findAll();
        List<UserProfileDTO> profileDAOList = new ArrayList<>(usersList.size());
        for (Users dto : usersList) {
            profileDAOList.add(new UserProfileDTO().build(dto));
        }
        return profileDAOList;
    }

    public UserProfileDTO getProfile(String username) throws GenericException {
        Optional<Users> dto = repoUsers.findByUsernameIgnoreCase(username);
        if (dto.isEmpty()) {
            throw new GenericException("User profile does not exist for username: " + username);
        }
        return new UserProfileDTO().build(dto.get());
    }

    public OutputTool updateProfile(UserProfileDTO form) {
        OutputTool out = new OutputTool();
        // If new password field is mot null, password update required
        boolean isPasswordUpdate = form.getNewPassword() != null;

        Users userRecord = repoUsers.findByUsernameIgnoreCase(form.getUsername()).get();

        if (!isPasswordUpdate) {
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
                if (!userRecord.getCustomPropertyA().equalsIgnoreCase(form.getAssistantId())) {
                    userRecord.setCustomPropertyA(form.getAssistantId());
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
            out.setComment("Profile updated successfully.");
        }

        // Change password
        if (isPasswordUpdate) {
            if (!form.getNewPassword().isEmpty() && !form.getConfirmPassword().isEmpty()) {
                userRecord.setPassword(encoder.encode(form.getNewPassword()));
                out.setComment("Password changed successfully.");
            }
        }

        repoUsers.save(userRecord);
        out.setResult(OutputTool.Result.SUCCESS);
        return out;
    }

    public OutputTool forgotPassword(String username) {
        Optional<Users> lookupRecord = repoUsers.findByUsernameIgnoreCase(username);
        if (lookupRecord.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.PROCESS_RULE, "Unable to reset password. Account not found.", null);
        }

        Users userRecord = lookupRecord.get();
        // Validate account status and return response if required
        if (userRecord.isDisabled()) {
            return new OutputTool().build(OutputTool.Result.PROCESS_RULE, "Unable to reset password. Account has been disabled.", null);
        }

        // GENERATE TEMP PASSWORD
        String password = HelperTools.generatePassword(9, 2, 0);

        userRecord.setPassword(encoder.encode(password));
        repoUsers.save(userRecord);

        // SEND EMAIL
        String subject = "Temporary Credentials";
        String body = HelperTools.getString("static/html/email-forgot_password.html");
        body = body.replaceAll("~USER~", userRecord.getName() + " " + userRecord.getSurname());
        body = body.replaceAll("~APP_NAME~", appName);
        body = body.replaceAll("~TEMP_PASSWORD~", password);
        body = body.replaceAll("~APP_URL~", appUrl);
        body = body.replaceAll("~FROM_EMAIL_ADDRESS~", fromEmailAddress);

        OutputTool outputTool = emailTools.send(List.of(userRecord.getEmailAddress()), subject, body, null, null, null);
        if (!outputTool.getResult().equals(OutputTool.Result.SUCCESS)) {
            return outputTool;
        }
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Temp password send to registered email", null);
    }

    public void addSystemAdmin() {
        Optional<Users> record = repoUsers.findByUsernameIgnoreCase(systemAdminUsername);
        if (record.isEmpty()) {
            if (createSystemAdminUser) {
                addUser(LocalDateTime.now(), systemAdminUsername, systemAdminName, systemAdminSurname, systemAdminEmailAddress, systemAdminPwd, SecurityRole.ADMIN,
                        "null", "null", "null", "null",
                        "system-initialize");
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

    public OutputTool addWhitelistEntry(String loggedInUser, WhitelistRegDTO whitelistRegDAOS) {
        Optional<RegistrationWhitelist> whitelistRegDTO = repoWhiteList.findByUsernameIgnoreCase(whitelistRegDAOS.getUsername().toLowerCase());
        if (whitelistRegDTO.isPresent()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Whitelisting with " +
                    "username: [" + whitelistRegDAOS.getUsername() + "] already exists.", null);
        }
        RegistrationWhitelist dto = new RegistrationWhitelist().buildInsert(loggedInUser, whitelistRegDAOS);
        repoWhiteList.save(dto);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Whitelisting for username: [" + dto.getUsername() +
                "] has been inserted successfully", null);
    }

    public OutputTool updateWhitelistEntry(String loggedInUser, WhitelistRegDTO form) {
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

    public List<RegistrationWhitelist> whitelistRegistration(String loggedInUser, List<WhitelistRegDTO> whitelistRegDTOS) {
        List<RegistrationWhitelist> dtoList = new ArrayList<>();
        for (WhitelistRegDTO d : whitelistRegDTOS) {
            RegistrationWhitelist record = new RegistrationWhitelist().buildInsert(loggedInUser, d);
            dtoList.add(record);
        }
        repoWhiteList.saveAll(dtoList);
        return repoWhiteList.findAll();
    }

    public Model addProfileToModel(Model model) throws GenericException {
        // Required for profile and password modals
        String loggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileDTO userProfileDTO = getProfile(loggedInUser);
        model.addAttribute("profileObject", userProfileDTO);
        model.addAttribute("app_name", appName);
        return model;
    }
}

