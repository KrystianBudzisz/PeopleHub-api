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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImportService {

    private final Map<String, PersonCreationStrategy> strategyMap;
    private final ImportRepository importRepository;
    private final PersonRepository personRepository;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public ImportService(List<PersonCreationStrategy> strategies,
                         ImportRepository importRepository,
                         PersonRepository personRepository,
                         PlatformTransactionManager transactionManager) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PersonCreationStrategy::getType, Function.identity()));
        this.importRepository = importRepository;
        this.personRepository = personRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Async
    @Transactional
    public CompletableFuture<ImportStatusDTO> importCsv(ImportCommand command) {
        final ImportStatus[] statusHolder = new ImportStatus[1];

        try {
            transactionTemplate.execute(status -> {
                ImportStatus newStatus = new ImportStatus();
                newStatus.setStatus("STARTED");
                newStatus.setCreationTimestamp(LocalDateTime.now());
                importRepository.saveAndFlush(newStatus);
                statusHolder[0] = newStatus;
                return newStatus;
            });

            final int batchSize = 10000;
            List<Person> batch = new ArrayList<>(batchSize);
            final long[] rowsProcessed = {0};

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(command.getFile().getInputStream()))) {
                CSVReader csvReader = new CSVReader(reader);
                csvReader.readNext();
                String[] nextRecord;
                statusHolder[0].setStartTimestamp(LocalDateTime.now());

                while ((nextRecord = csvReader.readNext()) != null) {
                    String personType = nextRecord[0].toUpperCase();
                    PersonCreationStrategy strategy = strategyMap.get(personType);
                    if (strategy != null) {
                        Person person = strategy.createFromCsvRecord(nextRecord);
                        batch.add(person);
                        rowsProcessed[0]++;
                        if (rowsProcessed[0] % batchSize == 0) {
                            saveBatchAndUpdateStatus(batch, statusHolder[0], rowsProcessed[0]);
                            batch.clear();
                        }
                    } else {
                        throw new ImportCsvException("Unknown person type: " + personType);
                    }
                }
                if (!batch.isEmpty()) {
                    saveBatchAndUpdateStatus(batch, statusHolder[0], rowsProcessed[0]);
                }
            } catch (IOException | CsvValidationException e) {
                markImportAsFailed(statusHolder[0]);
                throw new ImportCsvException("Error processing CSV import", e);
            }

            markImportAsCompleted(statusHolder[0]);
        } catch (Exception e) {
            throw new ImportCsvException("Unexpected error during import", e);
        }

        return CompletableFuture.completedFuture(ImportStatusMapper.toDTO(statusHolder[0]));
    }

    private void saveBatchAndUpdateStatus(List<Person> batch, ImportStatus status, long rowsProcessed) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            personRepository.saveAll(batch);
            status.setRowsProcessed(rowsProcessed);
            importRepository.saveAndFlush(status);
        });
    }

    private void markImportAsFailed(ImportStatus status) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            status.setStatus("FAILED");
            importRepository.saveAndFlush(status);
        });
    }

    private void markImportAsCompleted(ImportStatus status) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            status.setStatus("COMPLETED");
            status.setCompletionTimestamp(LocalDateTime.now());
            importRepository.saveAndFlush(status);
        });
    }

    public ImportStatusDTO getImportStatus(String importId) {
        ImportStatus status = importRepository.findById(Long.parseLong(importId))
                .orElseThrow(() -> new ImportStatusNotFoundException("ImportStatus not found with id: " + importId));
        return ImportStatusMapper.toDTO(status);
    }


}
