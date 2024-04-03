package org.example.peoplehubapi.csvImport;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.example.peoplehubapi.csvImport.model.ImportCommand;
import org.example.peoplehubapi.csvImport.model.ImportStatus;
import org.example.peoplehubapi.csvImport.model.ImportStatusDTO;
import org.example.peoplehubapi.csvImport.model.ImportStatusMapper;
import org.example.peoplehubapi.exception.ImportCsvException;
import org.example.peoplehubapi.exception.ImportStatusNotFoundException;
import org.example.peoplehubapi.person.PersonRepository;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.strategy.creation.PersonCreationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImportService {

    private final Map<String, PersonCreationStrategy> strategyMap;
    private final ImportRepository importRepository;
    private final PersonRepository personRepository;
    private final AmazonS3 s3Client;
    private final String bucketName;

    public ImportService(List<PersonCreationStrategy> strategies,
                         ImportRepository importRepository,
                         PersonRepository personRepository,
                         AmazonS3 s3Client,
                         @Value("${application.bucket.name}") String bucketName) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PersonCreationStrategy::getType, Function.identity()));
        this.importRepository = importRepository;
        this.personRepository = personRepository;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Async
    @Transactional
    public ImportStatus importCsvTransactional(String fileKey) throws ImportCsvException {
        ImportStatus status = initializeImportStatus();
        try (S3Object s3object = s3Client.getObject(bucketName, fileKey);
             S3ObjectInputStream inputStream = s3object.getObjectContent();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            Stream<String> lines = reader.lines().skip(1);
            processLinesStream(lines, status);
        } catch (Exception e) {
            status.setStatus("FAILED");
            finalizeImportStatus(status, "FAILED");
            throw new ImportCsvException("Error during CSV import: " + e.getMessage(), e);
        }
        return finalizeImportStatus(status, "COMPLETED");
    }


    public ImportStatusDTO importCsv(ImportCommand command) throws ImportCsvException {
        MultipartFile file = command.getFile();
        String fileKey = uploadFileToS3(file);
        ImportStatus importStatus = importCsvTransactional(fileKey);
        return ImportStatusMapper.toDTO(importStatus);
    }

    private ImportStatus initializeImportStatus() {
        ImportStatus status = new ImportStatus();
        status.setStatus("STARTED");
        status.setCreationTimestamp(LocalDateTime.now());
        status.setStartTimestamp(LocalDateTime.now());
        status.setRowsProcessed(0);
        return importRepository.save(status);
    }

    private void processLinesStream(Stream<String> linesStream, ImportStatus status) {
        final int batchSize = 1000;
        List<String> batchLines = new ArrayList<>(batchSize);
        linesStream.forEach(line -> {
            batchLines.add(line);
            if (batchLines.size() == batchSize) {
                processBatch(batchLines, status);
                batchLines.clear();
            }
        });
        if (!batchLines.isEmpty()) {
            processBatch(batchLines, status);
        }
        finalizeImportStatus(status, "COMPLETED");
    }

    protected void processBatch(List<String> lines, ImportStatus status) {
        List<Person> persons = lines.stream()
                .map(this::mapToPerson)
                .collect(Collectors.toList());
        personRepository.saveAll(persons);
        status.setRowsProcessed(status.getRowsProcessed() + persons.size());
    }

    private Person mapToPerson(String line) {
        String[] record = line.split(",", -1);
        String personType = record[0].toUpperCase();
        PersonCreationStrategy strategy = strategyMap.get(personType);
        if (strategy == null) {
            throw new ImportCsvException("Unknown person type: " + personType);
        }
        return strategy.createFromCsvRecord(record);
    }

    private ImportStatus finalizeImportStatus(ImportStatus status, String finalStatus) {
        status.setStatus(finalStatus);
        status.setCompletionTimestamp(LocalDateTime.now());
        return importRepository.save(status);
    }

    private String uploadFileToS3(MultipartFile file) {
        String fileKey = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, fileKey, file.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
        return fileKey;
    }

    public ImportStatusDTO getImportStatus(String importId) {
        ImportStatus status = importRepository.findById(Long.parseLong(importId))
                .orElseThrow(() -> new ImportStatusNotFoundException("ImportStatus not found with id: " + importId));
        return ImportStatusMapper.toDTO(status);
    }


}
