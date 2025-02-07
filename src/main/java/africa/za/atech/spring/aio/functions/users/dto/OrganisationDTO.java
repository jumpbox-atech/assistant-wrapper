package africa.za.atech.spring.aio.functions.users.dto;

import africa.za.atech.spring.aio.functions.users.model.Organisation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrganisationDTO {

    private String uid;
    private LocalDateTime createdDateTime;
    private String createdBy;
    private String name;
    private boolean disabled;
    private LocalDateTime updateDatetime;
    private String updateBy;
    private OrganisationMetaDTO organisationMetaDTO;

    public OrganisationDTO build(Organisation record) {
        this.uid = record.getUid();
        this.createdDateTime = record.getCreatedDateTime();
        this.createdBy = record.getCreatedBy();
        this.name = record.getName();
        this.disabled = record.isDisabled();
        this.updateDatetime = record.getUpdateDatetime();
        this.updateBy = record.getUpdateBy();
        return this;
    }

    public OrganisationDTO build(Organisation record, OrganisationMetaDTO organisationMetaDTO) {
        this.uid = record.getUid();
        this.createdDateTime = record.getCreatedDateTime();
        this.createdBy = record.getCreatedBy();
        this.name = record.getName();
        this.disabled = record.isDisabled();
        this.updateDatetime = record.getUpdateDatetime();
        this.updateBy = record.getUpdateBy();
        this.organisationMetaDTO = organisationMetaDTO;
        return this;
    }

}

