package org.example.peoplehubapi.csvImport;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.example.peoplehubapi.csvImport.model.ImportCommand;
import org.example.peoplehubapi.csvImport.model.ImportStatus;
import org.example.peoplehubapi.csvImport.model.ImportStatusDTO;
import org.example.peoplehubapi.csvImport.model.ImportStatusMapper;
import org.example.peoplehubapi.exception.ImportCsvException;
import org.example.peoplehubapi.exception.ImportStatusNotFoundException;
import org.example.peoplehubapi.exception.UnsupportedPersonTypeException;
import org.example.peoplehubapi.person.PersonRepository;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.strategy.creation.PersonCreationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImportService {

    private final Map<String, PersonCreationStrategy> strategyMap;
    private final ImportRepository importRepository;
    private final PersonRepository personRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public ImportService(List<PersonCreationStrategy> strategies, ImportRepository csvImportRepository, PersonRepository personRepository) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PersonCreationStrategy::getType, Function.identity()));
        this.importRepository = csvImportRepository;
        this.personRepository = personRepository;
    }


    public ImportStatusDTO importCsv(ImportCommand command) {
        ImportStatus status = new ImportStatus();
        status.setStatus("STARTED");
        status.setCreationTimestamp(LocalDateTime.now());
        importRepository.save(status);

        executorService.submit(() -> {
            try (Reader reader = new BufferedReader(new InputStreamReader(command.getFile().getInputStream()))) {
                CSVReader csvReader = new CSVReader(reader);
                String[] nextRecord;
                long rowsProcessed = 0;
                status.setStartTimestamp(LocalDateTime.now());
                while ((nextRecord = csvReader.readNext()) != null) {
                    CreatePersonCommand createPersonCommand = parseCommand(nextRecord);
                    PersonCreationStrategy strategy = strategyMap.get(createPersonCommand.getType().toUpperCase());
                    if (strategy != null) {
                        Person person = strategy.create(createPersonCommand);
                        personRepository.save(person);
                        rowsProcessed++;
                    } else {
                        throw new UnsupportedPersonTypeException("Unknown person type: " + createPersonCommand.getType());
                    }
                }
                status.setRowsProcessed(rowsProcessed);
                status.setStatus("COMPLETED");
            } catch (IOException e) {
                status.setStatus("FAILED");
                throw new ImportCsvException("IO Error during CSV import", e);
            } catch (CsvValidationException e) {
                status.setStatus("FAILED");
                throw new ImportCsvException("CSV Validation Error", e);
            } finally {
                status.setCompletionTimestamp(LocalDateTime.now());
                importRepository.save(status);
            }
        });

        return ImportStatusMapper.toDTO(status);
    }

    private CreatePersonCommand parseCommand(String[] record) {
        String type = record[0];
        Map<String, String> params = new HashMap<>();

        params.put("firstName", record[1]);
        params.put("lastName", record[2]);
        params.put("pesel", record[3]);
        params.put("height", record[4]);
        params.put("weight", record[5]);
        params.put("email", record[6]);

        switch (type.toUpperCase()) {
            case "STUDENT":
                params.put("universityName", record[7]);
                params.put("yearOfStudy", record[8]);
                params.put("fieldOfStudy", record[9]);
                params.put("scholarship", record[10]);
                break;
            case "RETIREE":
                params.put("pensionAmount", record[7]);
                params.put("yearsWorked", record[8]);
                break;
            case "EMPLOYEE":
                params.put("employmentDate", record[7]);
                params.put("position", record[8]);
                params.put("salary", record[9]);
                params.put("numberOfProfessions", record[10]);
                break;
        }

        return new CreatePersonCommand(type, params);
    }

    public ImportStatusDTO getImportStatus(String importId) {
        ImportStatus status = importRepository.findById(Long.parseLong(importId))
                .orElseThrow(() -> new ImportStatusNotFoundException("ImportStatus not found with id: " + importId));
        return ImportStatusMapper.toDTO(status);
    }

}

