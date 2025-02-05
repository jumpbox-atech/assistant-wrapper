package africa.za.atech.spring.aio.utils.openai;

import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import africa.za.atech.spring.aio.utils.openai.dto.AddMessageToThread;
import africa.za.atech.spring.aio.utils.openai.dto.RunThreadMessage;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OpenAiClient {

    public static String openAiBaseApiUrl;
    public static int openApiAssistantsBetaVersion;
    public static boolean useRelaxedHttps;

    public static boolean logoutCreateThread;
    public static boolean logoutAddMessage;
    public static boolean logoutRunThread;
    public static boolean logoutGetRunStatus;
    public static boolean logoutGetMessages;
    public static boolean logoutGetStepsForRun;

    private static Map<String, String> setHeaders(String referenceId, Assistants assistant) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Authorization", "Bearer " + assistant.getApiKey());
        headers.put("OpenAI-Organization", assistant.getOpenaiOrganisationId());
        headers.put("OpenAI-Beta", "assistants=v" + openApiAssistantsBetaVersion);
        headers.put("aTech-reference", referenceId);
        // Custom headers
        return headers;
    }

    public static Response createThread(String referenceId, Assistants assistant) {
        Function function = Function.CREATE_THREAD;
        String url = openAiBaseApiUrl + "/threads";
        logoutCall(referenceId, function, url);

        RequestSpecBuilder req = new RequestSpecBuilder();
        if (useRelaxedHttps) {
            req.setRelaxedHTTPSValidation();
        }
        req.setBaseUri(url);
        req.addHeaders(setHeaders(referenceId, assistant));
        req.setBody("");
        Response response;
        if (logoutCreateThread) {
            response = RestAssured.given().spec(req.build()).log().all().post();
        } else {
            response = RestAssured.given().spec(req.build()).post();

        }
        logoutResponse(referenceId, function, response, logoutCreateThread);
        return response;
    }

    public static Response addMessage(String referenceId, String threadId, Assistants assistant, Role role, String content) {
        Function function = Function.ADD_MESSAGE;
        String url = openAiBaseApiUrl + "/threads/" + threadId + "/messages";
        logoutCall(referenceId, function, url);

        RequestSpecBuilder req = new RequestSpecBuilder();
        if (useRelaxedHttps) {
            req.setRelaxedHTTPSValidation();
        }
        req.setBaseUri(url);
        req.addHeaders(setHeaders(referenceId, assistant));
        req.setBody(new AddMessageToThread().build(role, content));

        Response response;
        if (logoutAddMessage) {
            response = RestAssured.given().spec(req.build()).log().all().post();
        } else {
            response = RestAssured.given().spec(req.build()).post();
        }
        logoutResponse(referenceId, function, response, logoutAddMessage);
        return response;
    }

    /**
     * @param additionalInstruction Added to the base additional instructions set in the AssistantConfig
     */
    public static Response runThread(String referenceId, String threadId, Assistants assistant, String additionalInstruction) {
        Function function = Function.RUN_THREAD;
        String url = openAiBaseApiUrl + "/threads/" + threadId + "/runs";
        logoutCall(referenceId, function, url);

        RequestSpecBuilder req = new RequestSpecBuilder();
        if (useRelaxedHttps) {
            req.setRelaxedHTTPSValidation();
        }
        req.setBaseUri(url);
        req.addHeaders(setHeaders(referenceId, assistant));
        req.setBody(new RunThreadMessage().build(assistant.getOpenaiAssistantId(), (assistant.getAdditionalInstructions() + " " + additionalInstruction).trim()));

        Response response;
        if (logoutRunThread) {
            response = RestAssured.given().spec(req.build()).log().all().post();
        } else {
            response = RestAssured.given().spec(req.build()).post();
        }
        logoutResponse(referenceId, function, response, logoutRunThread);
        return response;
    }

    public static Response getRunStatus(String referenceId, String threadId, String runId, Assistants assistant) {
        Function function = Function.GET_RUN_STATUS;
        String url = openAiBaseApiUrl + "/threads/" + threadId + "/runs/" + runId;

        RequestSpecBuilder req = new RequestSpecBuilder();
        if (useRelaxedHttps) {
            req.setRelaxedHTTPSValidation();
        }
        req.setBaseUri(url);
        req.addHeaders(setHeaders(referenceId, assistant));

        Response response;
        if (logoutGetRunStatus) {
            response = RestAssured.given().spec(req.build()).log().all().get();
        } else {
            response = RestAssured.given().spec(req.build()).get();
        }
        logoutResponse(referenceId, function, response, logoutGetRunStatus);
        return response;
    }

    public static Response getMessages(String referenceId, String threadId, Assistants assistant) {
        Function function = Function.GET_MESSAGES;
        String url = openAiBaseApiUrl + "/threads/" + threadId + "/messages";
        logoutCall(referenceId, function, url);

        RequestSpecBuilder req = new RequestSpecBuilder();
        if (useRelaxedHttps) {
            req.setRelaxedHTTPSValidation();
        }
        req.setBaseUri(url);
        req.addHeaders(setHeaders(referenceId, assistant));

        Response response;
        if (logoutGetMessages) {
            response = RestAssured.given().spec(req.build()).log().all().get();
        } else {
            response = RestAssured.given().spec(req.build()).get();
        }
        logoutResponse(referenceId, function, response, logoutGetMessages);
        return response;
    }

    @SuppressWarnings("unused")
    public static Response getStepsForRun(String referenceId, String threadId, String runId, Assistants assistant) {
        Function function = Function.GET_STEPS_FOR_RUN;
        String url = openAiBaseApiUrl + "/threads/" + threadId + "/runs/" + runId + "/steps";
        logoutCall(referenceId, function, url);

        RequestSpecBuilder req = new RequestSpecBuilder();
        if (useRelaxedHttps) {
            req.setRelaxedHTTPSValidation();
        }
        req.setBaseUri(url);
        req.addHeaders(setHeaders(referenceId, assistant));

        Response response;
        if (logoutGetStepsForRun) {
            response = RestAssured.given().spec(req.build()).log().all().get();
        } else {
            response = RestAssured.given().spec(req.build()).get();
        }
        logoutResponse(referenceId, function, response, logoutGetStepsForRun);
        return response;
    }

    private static void logoutCall(String referenceId, Function function, String url) {
        log.info("{} - {} - Calling openAI:: {}", referenceId, function, url);
    }

    private static void logoutResponse(String referenceId, Function function, Response response, boolean detailed) {
        if (detailed) {
            log.info("{} - {} - Response status code:: {} and body:: {}", referenceId, function, response.statusCode(), response.body().asString());
        } else {
            log.info("{} - {} - Response status code:: {}", referenceId, function, response.statusCode());
        }
    }

}
