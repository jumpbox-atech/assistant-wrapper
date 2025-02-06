package africa.za.atech.spring.aio.functions.users.dto;

import africa.za.atech.spring.aio.functions.users.model.Department;
import africa.za.atech.spring.aio.functions.users.model.Organisation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentDTO {

    private String maskedId;
    private String organisationMaskedId;
    private String organisationName;
    private String name;
    private LocalDateTime createdDateTime;
    private String createdBy;
    private boolean disabled;
    private LocalDateTime updateDatetime;
    private String updateBy;


    public DepartmentDTO build(String organisationName, String organisationMaskedId, Department department) {
        this.maskedId = department.getMaskedId();
        this.organisationMaskedId = organisationMaskedId;
        this.organisationName = organisationName;
        this.name = department.getName();
        this.createdDateTime = department.getCreatedDateTime();
        this.createdBy = department.getCreatedBy();
        this.disabled = department.isDisabled();
        this.updateDatetime = department.getUpdateDatetime();
        this.updateBy = department.getUpdateBy();
        return this;
    }

    public DepartmentDTO build(Organisation organisation, Department department) {
        this.maskedId = department.getMaskedId();
        this.organisationMaskedId = organisation.getMaskedId();
        this.organisationName = organisation.getName();
        this.name = department.getName();
        this.createdDateTime = department.getCreatedDateTime();
        this.createdBy = department.getCreatedBy();
        this.disabled = department.isDisabled();
        this.updateDatetime = department.getUpdateDatetime();
        this.updateBy = department.getUpdateBy();
        return this;
    }

}

