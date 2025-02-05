package africa.za.atech.spring.aio.utils;


import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class OutputTool {

    private Result result;
    private String comment;
    private Object object;

    public OutputTool build(Result result, String comment, @Nullable Object object) {
        OutputTool outputTool = new OutputTool();
        outputTool.setResult(result);
        outputTool.setComment(comment);
        outputTool.setObject(object);
        return outputTool;
    }

    public enum Result {
        SUCCESS,
        PROCESS_RULE,
        EXCEPTION,
        TIMEOUT
    }

}
