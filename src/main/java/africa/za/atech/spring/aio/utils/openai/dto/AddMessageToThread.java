package africa.za.atech.spring.aio.utils.openai.dto;


import africa.za.atech.spring.aio.utils.openai.Role;
import lombok.Data;

@Data
public class AddMessageToThread {

    private String role;
    private String content;

    public AddMessageToThread build(Role role, String content) {
        AddMessageToThread obj = new AddMessageToThread();
        obj.setRole(role.getValue());
        obj.setContent(content);
        return obj;
    }

}
