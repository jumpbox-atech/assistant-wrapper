package africa.za.atech.spring.aio.functions.assistant;


import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import africa.za.atech.spring.aio.functions.assistant.database.repo.RepoAssistants;
import africa.za.atech.spring.aio.functions.assistant.dto.AssistantDTO;
import africa.za.atech.spring.aio.utils.OutputTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    private final RepoAssistants repoAssistants;

    public List<Assistants> getAllAssistants() {
        return repoAssistants.findAll();
    }

    public List<Assistants> getAllActiveAssistants() {
        return repoAssistants.findByDisabledIsFalse();
    }

    /**
     * @return On success, it returns and output with the Assistants record object
     */
    public OutputTool getAssistant(long id) {
        Optional<Assistants> recordLookup = repoAssistants.findById(id);
        if (recordLookup.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Assistant with id: [" + id + "] does not exist", null);
        }
        return new OutputTool().build(OutputTool.Result.SUCCESS, "", recordLookup.get());
    }

    /**
     * @return On success, it returns and output with the Assistants record object
     */
    public OutputTool getAssistant(String assistantName) {
        Optional<Assistants> recordLookup = repoAssistants.findByUniqueName(assistantName.toLowerCase());
        if (recordLookup.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Assistant with name: [" + assistantName + "] does not exist", null);
        }
        return new OutputTool().build(OutputTool.Result.SUCCESS, "", recordLookup.get());
    }

    public OutputTool insertAssistant(String username, AssistantDTO form) {
        // Clean inputs
        form.setName(form.getName().trim());
        form.setUniqueName(form.getName().toLowerCase());
        form.setDescription(form.getDescription().trim());
        form.setOrganisationId(form.getOrganisationId().trim());
        form.setAssistantId(form.getAssistantId().trim());
        form.setApiKey(form.getApiKey().trim());
        form.setAdditionalInstructions(form.getAdditionalInstructions().trim());

        Optional<Assistants> lookupRecord = repoAssistants.findByUniqueName(form.getUniqueName());
        if (lookupRecord.isPresent()) {
            if (lookupRecord.get().getName().equalsIgnoreCase(form.getName())) {
                return new OutputTool().build(OutputTool.Result.EXCEPTION,
                        "Assistant with name: [" + form.getName() + "] already exists.", form);
            }
        }
        LocalDateTime dateTime = LocalDateTime.now();
        Assistants record = new Assistants().buildInsert(username, dateTime, form);
        repoAssistants.save(record);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Assistant with name: [" + record.getName() +
                "] has been inserted successfully.", null);
    }

    public OutputTool updateAssistant(String username, AssistantDTO form) {
        Optional<Assistants> recordLookup = repoAssistants.findById(form.getId());
        if (recordLookup.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Assistant with id: [" + form.getId() + "] does not exist", form);
        }
        Assistants record = recordLookup.get();
        LocalDateTime dateTime = LocalDateTime.now();
        if (form.getDescription() != null) {
            form.setDescription(form.getDescription().trim());
            if (!form.getDescription().equalsIgnoreCase(record.getDescription())) {
                record.setDescription(form.getDescription().trim());
            }
        }

        if (form.getApiKey() != null) {
            form.setApiKey(form.getApiKey().trim());
            if (!form.getApiKey().equalsIgnoreCase("masked")) {
                if (!form.getApiKey().isEmpty() || !form.getApiKey().isBlank()) {
                    record.setApiKey(form.getApiKey());
                }
            }
        }

        if (form.getAdditionalInstructions() != null) {
            record.setAdditionalInstructions(form.getAdditionalInstructions().trim());
            if (!form.getAdditionalInstructions().equalsIgnoreCase(record.getAdditionalInstructions())) {
                record.setAdditionalInstructions(form.getAdditionalInstructions());
            }
        }

        if (form.isDisabled() != record.isDisabled()) {
            record.setDisabled(form.isDisabled());
            record.setDisabledBy(username);
            record.setDisabledDatetime(dateTime);
        }

        record.setUpdateBy(username);
        record.setUpdateDatetime(dateTime);
        repoAssistants.save(record);

        return new OutputTool().build(OutputTool.Result.SUCCESS, "Assistant with name: [" + record.getName() +
                "] has been updated successfully.", null);
    }

}
