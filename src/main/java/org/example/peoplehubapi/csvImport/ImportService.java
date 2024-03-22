package org.example.peoplehubapi.csvImport;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.example.peoplehubapi.csvImport.model.ImportCommand;
import org.example.peoplehubapi.csvImport.model.ImportStatus;
import org.example.peoplehubapi.csvImport.model.ImportStatusDTO;
import org.example.peoplehubapi.csvImport.model.ImportStatusMapper;
import org.example.peoplehubapi.exception.ImportCsvException;
import org.example.peoplehubapi.exception.ImportStatusNotFoundException;
import org.example.peoplehubapi.person.PersonRepository;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.strategy.creation.PersonCreationStrategy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImportService {

    private final Map<String, PersonCreationStrategy> strategyMap;
    private final ImportRepository importRepository;
    private final PersonRepository personRepository;

    public ImportService(List<PersonCreationStrategy> strategies, ImportRepository csvImportRepository, PersonRepository personRepository) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PersonCreationStrategy::getType, Function.identity()));
        this.importRepository = csvImportRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    public ImportStatusDTO importCsv(ImportCommand command) throws ImportCsvException {
        ImportStatus status = new ImportStatus();
        status.setStatus("STARTED");
        status.setCreationTimestamp(LocalDateTime.now());
        importRepository.save(status);

        final int batchSize = 100000;
        List<Person> batch = new ArrayList<>(batchSize);

        try (Reader reader = new BufferedReader(new InputStreamReader(command.getFile().getInputStream()))) {
            CSVReader csvReader = new CSVReader(reader);
            csvReader.readNext();

            String[] nextRecord;
            long rowsProcessed = 0;
            status.setStartTimestamp(LocalDateTime.now());

            while ((nextRecord = csvReader.readNext()) != null) {
                String personType = nextRecord[0].toUpperCase();
                PersonCreationStrategy strategy = strategyMap.get(personType);
                if (strategy != null) {
                    Person person = strategy.createFromCsvRecord(nextRecord);
                    batch.add(person);
                    rowsProcessed++;

                    if (rowsProcessed % batchSize == 0) {
                        personRepository.saveAll(batch);
                        batch.clear();
                    }
                } else {
                    throw new ImportCsvException("Unknown person type: " + personType);
                }
            }

            if (!batch.isEmpty()) {
                personRepository.saveAll(batch);
            }

            status.setRowsProcessed(rowsProcessed);
            status.setStatus("COMPLETED");
        } catch (IOException | CsvValidationException e) {
            status.setStatus("FAILED");
            throw new ImportCsvException("Error during CSV import", e);
        } finally {
            status.setCompletionTimestamp(LocalDateTime.now());
            importRepository.save(status);
        }

        return ImportStatusMapper.toDTO(status);
    }




    public ImportStatusDTO getImportStatus(String importId) {
        ImportStatus status = importRepository.findById(Long.parseLong(importId))
                .orElseThrow(() -> new ImportStatusNotFoundException("ImportStatus not found with id: " + importId));
        return ImportStatusMapper.toDTO(status);
    }

}

