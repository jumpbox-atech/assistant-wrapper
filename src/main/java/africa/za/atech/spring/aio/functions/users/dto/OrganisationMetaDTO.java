package africa.za.atech.spring.aio.functions.users.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrganisationMetaDTO {

    private List<DepartmentDTO> listOfDepartments;
    private List<String> listOfAssistants;
    private List<String> listOfUsers;
    private List<String> listOfChats;
    private int departmentsCount;
    private int assistantsCount;
    private int usersCount;
    private int chatsCount;
    private boolean delete;

}

