package africa.za.atech.spring.aio.functions.users.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrganisationMetaDTO {

    private List<String> listOfDepartments;
    private List<String> listOfAssistants;
    private List<String> listOfUsers;
    private List<String> listOfChats;

}

