package africa.za.atech.spring.aio.utils.openai;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Function {

    CREATE_THREAD("create_thread"),
    ADD_MESSAGE("add_message"),
    RUN_THREAD("run_thread"),
    GET_RUN_STATUS("get_run_status"),
    GET_MESSAGES("get_messages"),
    GET_STEPS_FOR_RUN("get_steps_for_run");

    private final String value;

    public String value() {
        return value;
    }
}
