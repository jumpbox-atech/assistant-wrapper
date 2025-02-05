package africa.za.atech.spring.aio.config;

import africa.za.atech.spring.aio.functions.users.RegType;
import africa.za.atech.spring.aio.functions.users.UsersService;
import africa.za.atech.spring.aio.utils.openai.OpenAiClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Initialize {

    @Value("${atech.app.register.type}")
    private String registrationType;

    @Value("${atech.app.openai.api.base-url}")
    private String openAiBaseApiUrl;
    @Value("${atech.app.openai.api.user-relaxed-https}")
    private boolean useRelaxedHttps;
    @Value("${atech.app.openai.api.assistant.beta-version}")
    private int openApiAssistantsBetaVersion;

    @Value("${atech.app.openai.api.assistant.logout.create-thread}")
    private boolean logoutCreateThread;
    @Value("${atech.app.openai.api.assistant.logout.add-message}")
    private boolean logoutAddMessage;
    @Value("${atech.app.openai.api.assistant.logout.run-thread}")
    private boolean logoutRunThread;
    @Value("${atech.app.openai.api.assistant.logout.get-run-status}")
    private boolean logoutGetRunStatus;
    @Value("${atech.app.openai.api.assistant.logout.get-messages}")
    private boolean logoutGetMessages;
    @Value("${atech.app.openai.api.assistant.logout.get-steps-for-run}")
    private boolean logoutGetStepsForRun;

    private final UsersService usersService;

    @PostConstruct
    public void setSystemUsers() {
        usersService.addSystemAdmin();
    }

    @PostConstruct
    public void forceRegistrationType() {
        if (!registrationType.equalsIgnoreCase(RegType.PRIVATE.getValue()) && !registrationType.equalsIgnoreCase(RegType.PUBLIC.getValue())) {
            throw new RuntimeException("Registration type is not supported.");
        }
    }

    @PostConstruct
    public void setOpenAiStatics() {
        if (openAiBaseApiUrl.endsWith("/")) {
            openAiBaseApiUrl = openAiBaseApiUrl.substring(0, openAiBaseApiUrl.length() - 1);
        }
        if (openApiAssistantsBetaVersion != 2) {
            throw new RuntimeException("OpenAPI assistants beta version must be 2");
        }
        OpenAiClient.openAiBaseApiUrl = openAiBaseApiUrl;
        OpenAiClient.openApiAssistantsBetaVersion = openApiAssistantsBetaVersion;
        OpenAiClient.useRelaxedHttps = useRelaxedHttps;

        OpenAiClient.logoutCreateThread = logoutCreateThread;
        OpenAiClient.logoutAddMessage = logoutAddMessage;
        OpenAiClient.logoutRunThread = logoutRunThread;
        OpenAiClient.logoutGetRunStatus = logoutGetRunStatus;
        OpenAiClient.logoutGetMessages = logoutGetMessages;
        OpenAiClient.logoutGetStepsForRun = logoutGetStepsForRun;
    }

}
