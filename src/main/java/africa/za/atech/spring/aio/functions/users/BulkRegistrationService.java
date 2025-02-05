package africa.za.atech.spring.aio.functions.users;

import africa.za.atech.spring.aio.functions.assistant.AssistantService;
import africa.za.atech.spring.aio.functions.assistant.database.model.Assistants;
import africa.za.atech.spring.aio.functions.users.dto.BulkRegistrationDTO;
import africa.za.atech.spring.aio.functions.users.dto.RegisterDTO;
import africa.za.atech.spring.aio.functions.users.dto.WhitelistRegDTO;
import africa.za.atech.spring.aio.utils.EmailTools;
import africa.za.atech.spring.aio.utils.HelperTools;
import africa.za.atech.spring.aio.utils.OutputTool;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkRegistrationService {

    private final UsersService usersService;
    private final AssistantService assistantService;
    private final EmailTools emailTools;

    @SneakyThrows
    @Async
    public void processCsv(String user, MultipartFile csvFile) {
        // Read csv data and remove header
        String tempFile = System.getProperty("java.io.tmpdir") + "/" + csvFile.getOriginalFilename();
        List<String[]> csvData = HelperTools.readCsv(csvFile, System.getProperty("java.io.tmpdir"));
        csvData.remove(0);
        List<BulkRegistrationDTO> registerDAOS = new ArrayList<>(csvData.size());
        for (String[] sa : csvData) {
            registerDAOS.add(new BulkRegistrationDTO().build(sa));
        }
        FileUtils.delete(new File(tempFile));

        // Validate assistants
        List<Assistants> assistants = assistantService.getAllAssistants();
        for (BulkRegistrationDTO dao : registerDAOS) {
            for (Assistants ass : assistants) {
                if (dao.getAssistantName().equalsIgnoreCase(ass.getName())) {
                    dao.setAssistantNameValidated(true);
                    break;
                }
            }
        }

        String[] header = {"username", "name", "surname", "emailAddress", "assistantName",
                "assistantNameValidated", "whitelistResult", "registrationResult"};
        List<String[]> outputCsv = new ArrayList<>(registerDAOS.size());
        outputCsv.add(header);

        // Whitelist and register
        for (BulkRegistrationDTO item : registerDAOS) {
            if (item.isAssistantNameValidated()) {
                WhitelistRegDTO whitelistRegDTO = new WhitelistRegDTO().buildInsert(
                        item.getUsername(),
                        item.getName(),
                        item.getSurname(),
                        item.getEmailAddress(),
                        item.getAssistantName(),
                        "null",
                        "null",
                        "null"
                );
                OutputTool whitelistResult = usersService.addWhitelistEntry("system-bulk-registration", whitelistRegDTO);
                if (whitelistResult.getResult().equals(OutputTool.Result.SUCCESS)) {
                    item.setWhitelistResult(whitelistResult.getResult().toString().toLowerCase());
                    RegisterDTO registerDTO = new RegisterDTO().build(item);
                    OutputTool register = usersService.registerNewUser(registerDTO);
                    if (register.getResult().equals(OutputTool.Result.SUCCESS)) {
                        item.setRegistrationResult(register.getResult().toString().toLowerCase());
                    } else {
                        item.setRegistrationResult(register.getResult() + " - " + register.getComment());
                    }
                } else {
                    item.setWhitelistResult(whitelistResult.getResult() + " - " + whitelistResult.getComment());
                    item.setRegistrationResult("n/a");
                }
            } else {
                item.setWhitelistResult("n/a");
                item.setRegistrationResult("n/a");
            }
            outputCsv.add(new String[]{
                    item.getUsername(),
                    item.getName(),
                    item.getSurname(),
                    item.getEmailAddress(),
                    item.getAssistantName(),
                    String.valueOf(item.isAssistantNameValidated()),
                    item.getWhitelistResult(),
                    item.getRegistrationResult()
            });
        }

        // Create CSV
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeAll(outputCsv);
        writer.flush();

        // CSV to multipart file
        byte[] data = outputStream.toByteArray();
        MultipartFile file = new MockMultipartFile(
                "bulk-result.csv",
                "bulk-result.csv",
                "text/csv",
                data);

        // Send email
        emailTools.send(List.of(usersService.getProfile(user).getEmailAddress()), "Bulk registration result", null, null, null, List.of(file));
        log.info("Bulk process completed successfully");
    }

}
