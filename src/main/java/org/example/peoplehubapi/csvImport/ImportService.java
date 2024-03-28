package org.example.peoplehubapi.csvImport;

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


    public void importCsv(ImportCommand command) throws ImportCsvException {
        ImportStatus status = initializeImportStatus();
        try {
            List<String> allLines = readAllLines(command);
            processLinesAsync(allLines, status);
        } catch (Exception e) {
            status.setStatus("FAILED");
            finalizeImportStatus(status, "FAILED");
            throw new ImportCsvException("Error initializing CSV import", e);
        }
    }

    private ImportStatus initializeImportStatus() {
        ImportStatus status = new ImportStatus();
        status.setStatus("STARTED");
        status.setCreationTimestamp(LocalDateTime.now());
        status.setStartTimestamp(LocalDateTime.now());
        status.setRowsProcessed(0);
        return importRepository.save(status);
    }

    private List<String> readAllLines(ImportCommand command) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(command.getFile().getInputStream()))) {
            return reader.lines().skip(1).collect(Collectors.toList());
        }
    }

    @Async
    public void processLinesAsync(List<String> lines, ImportStatus status) {
        final int batchSize = 10000;
        List<String> batchLines = new ArrayList<>(batchSize);

        for (String line : lines) {
            batchLines.add(line);
            if (batchLines.size() == batchSize) {
                processBatch(batchLines, status);
                batchLines.clear();
            }
        }

        if (!batchLines.isEmpty()) {
            processBatch(batchLines, status);
        }

        finalizeImportStatus(status, "COMPLETED");
    }

    @Transactional
    protected void processBatch(List<String> lines, ImportStatus status) {
        List<String[]> batchRecords = lines.stream()
                .map(line -> line.split(",", -1))
                .toList();

        for (String[] record : batchRecords) {
            try {
                String personType = record[0].toUpperCase();
                PersonCreationStrategy strategy = strategyMap.get(personType);
                if (strategy != null) {
                    Person person = strategy.createFromCsvRecord(record);
                    personRepository.save(person);
                    status.setRowsProcessed(status.getRowsProcessed() + 1);
                } else {
                    throw new ImportCsvException("Unknown person type: " + personType);
                }
            } catch (Exception e) {
                throw new ImportCsvException("Error processing batch", e);
            }
        }
    }

    private void finalizeImportStatus(ImportStatus status, String finalStatus) {
        status.setStatus(finalStatus);
        status.setCompletionTimestamp(LocalDateTime.now());
        importRepository.save(status);
    }

    public ImportStatusDTO getImportStatus(String importId) {
        ImportStatus status = importRepository.findById(Long.parseLong(importId))
                .orElseThrow(() -> new ImportStatusNotFoundException("ImportStatus not found with id: " + importId));
        return ImportStatusMapper.toDTO(status);
    }


}
