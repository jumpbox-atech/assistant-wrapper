package africa.za.atech.spring.aio.functions.assistant;


import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import africa.za.atech.spring.aio.functions.assistant.database.repo.RepoAssistants;
import africa.za.atech.spring.aio.functions.assistant.dto.AssistantDTO;
import africa.za.atech.spring.aio.functions.users.model.Organisation;
import africa.za.atech.spring.aio.functions.users.repo.OrganisationRepo;
import africa.za.atech.spring.aio.functions.users.repo.UsersRepo;
import africa.za.atech.spring.aio.utils.HelperTools;
import africa.za.atech.spring.aio.utils.OutputTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistantService {

    private final OrganisationRepo repoOrganisation;
    private final RepoAssistants repoAssistants;

    private final UsersRepo repoUsers;

    public List<AssistantDTO> getAllAssistants() {
        return repoAssistants.findAll()
                .stream()
                .map(assistants -> new AssistantDTO().build(assistants))
                .toList();
    }

    public List<AssistantDTO> getAllAssistantsInfo() {
        List<AssistantDTO> assistantDTOS = new ArrayList<>();
        for (Assistants assistant : repoAssistants.findAll()) {
            Optional<Organisation> org = repoOrganisation.findById(assistant.getOrganisationId());
            assistantDTOS.add(new AssistantDTO().build(org.get(), assistant));
        }
        return assistantDTOS;
    }

    public List<Assistants> getAllActiveAssistants() {
        return repoAssistants.findAll();
    }

    /**
     * @return On success, it returns and output with the Assistants record object
     */
    public OutputTool getAssistant(String maskedId) {
        Optional<Assistants> recordLookup = repoAssistants.findByMaskedId(maskedId);
        if (recordLookup.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Assistant with id: [" + maskedId + "] does not exist", null);
        }
        Optional<Organisation> organisation = repoOrganisation.findById(recordLookup.get().getOrganisationId());
        if (organisation.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION, "Assistant with id: [" + maskedId + "] is not assigned to any organisation", null);
        }
        return new OutputTool().build(OutputTool.Result.SUCCESS, "", new AssistantDTO().build(organisation.get(), recordLookup.get()));
    }

    public OutputTool insertAssistant(String loggedInUser, AssistantDTO form) {
        Organisation organisation = repoOrganisation.findAllByMaskedId(form.getOrganisationMaskedId()).get();
        Optional<Assistants> lookup = repoAssistants.findByOrganisationIdAndNameIgnoreCase(organisation.getId(), form.getName());
        if (lookup.isPresent()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION,
                    "Assistant " + HelperTools.wrapVar(form.getName()) + " exists for the '" + organisation.getName() + "' organisation.", null);
        }
        Assistants record = new Assistants().buildInsert(loggedInUser, organisation.getId(), form);
        repoAssistants.save(record);
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Assistant added successfully.", null);
    }

    public OutputTool updateAssistant(String loggedInUser, AssistantDTO form) {
        Organisation organisation = repoOrganisation.findAllByMaskedId(form.getOrganisationMaskedId()).get();


        Optional<Assistants> lookup = repoAssistants.findByOrganisationIdAndMaskedId(organisation.getId(), form.getMaskedId());
        if (lookup.isEmpty()) {
            return new OutputTool().build(OutputTool.Result.EXCEPTION,
                    "Assistant " + HelperTools.wrapVar(form.getName()) + " does not exists for the '" + organisation.getName() + "' organisation.", null);
        }

        lookup.get().setName(form.getName().trim());
        lookup.get().setDescription(form.getDescription().trim());
        lookup.get().setAdditionalInstructions(form.getAdditionalInstructions().trim());
        if (!form.getExternalApiKey().equalsIgnoreCase("masked")) {
            lookup.get().setExternalApiKey(form.getExternalApiKey().trim());
        }
        lookup.get().setUpdateBy(loggedInUser);
        lookup.get().setUpdateDatetime(LocalDateTime.now());
        repoAssistants.save(lookup.get());
        return new OutputTool().build(OutputTool.Result.SUCCESS, "Assistant updated successfully.", null);
    }

}
