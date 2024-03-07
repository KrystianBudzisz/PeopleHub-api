package org.example.peoplehubapi.csvimport;

import org.example.peoplehubapi.csvImport.ImportRepository;
import org.example.peoplehubapi.csvImport.ImportService;
import org.example.peoplehubapi.csvImport.model.ImportCommand;
import org.example.peoplehubapi.csvImport.model.ImportStatus;
import org.example.peoplehubapi.csvImport.model.ImportStatusDTO;
import org.example.peoplehubapi.person.PersonRepository;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.strategy.creation.PersonCreationStrategy;
import org.example.peoplehubapi.strategy.model.Employee;
import org.example.peoplehubapi.strategy.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private ImportRepository importRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonCreationStrategy studentStrategy;

    @Mock
    private PersonCreationStrategy employeeStrategy;

    @Mock
    private PersonCreationStrategy retireeStrategy;

    private ImportService importService;

    @BeforeEach
    void setUp() {
        studentStrategy = mock(PersonCreationStrategy.class);
        employeeStrategy = mock(PersonCreationStrategy.class);
        retireeStrategy = mock(PersonCreationStrategy.class);

        when(studentStrategy.getType()).thenReturn("STUDENT");
        when(employeeStrategy.getType()).thenReturn("EMPLOYEE");
        when(retireeStrategy.getType()).thenReturn("RETIREE");

        List<PersonCreationStrategy> strategies = Arrays.asList(studentStrategy, employeeStrategy, retireeStrategy);

        importService = new ImportService(strategies, importRepository, personRepository);

    }

    @Test
    void testImportCsv() throws Exception {
        String csvContent = "STUDENT,John,Doe,12345678901,180,75,john.doe@example.com,Harvard,2,Computer Science,1000\n" +
                "EMPLOYEE,Jane,Doe,98765432109,165,60,jane.doe@example.com,2021-05-01,Manager,5000,2\n"; // Add more rows as needed
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/plain", inputStream);

        when(studentStrategy.create(any())).thenReturn(new Student());
        when(employeeStrategy.create(any())).thenReturn(new Employee());
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(importRepository.save(any(ImportStatus.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportCommand command = new ImportCommand();
        command.setFile(file);
        importService.importCsv(command);

        Thread.sleep(1000);

        ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository, times(2)).save(personCaptor.capture());

        List<Person> savedPersons = personCaptor.getAllValues();
        assertEquals(2, savedPersons.size(), "Two people should have been saved");


        ArgumentCaptor<ImportStatus> statusCaptor = ArgumentCaptor.forClass(ImportStatus.class);
        verify(importRepository, atLeastOnce()).save(statusCaptor.capture());
        ImportStatus finalStatus = statusCaptor.getAllValues().stream().reduce((first, second) -> second).orElse(null);

        assertNotNull(finalStatus, "Final status should not be null");
        assertEquals("COMPLETED", finalStatus.getStatus(), "Import should be completed");
        assertEquals(2, finalStatus.getRowsProcessed(), "Should have processed two rows");
    }


    @Test
    void testGetImportStatus() {
        Long importId = 1L;
        ImportStatus expectedStatus = new ImportStatus();
        expectedStatus.setId(importId);
        expectedStatus.setStatus("COMPLETED");
        expectedStatus.setCreationTimestamp(LocalDateTime.now().minusHours(1));
        expectedStatus.setStartTimestamp(LocalDateTime.now().minusMinutes(30));
        expectedStatus.setCompletionTimestamp(LocalDateTime.now());
        expectedStatus.setRowsProcessed(100);

        when(importRepository.findById(importId)).thenReturn(Optional.of(expectedStatus));

        ImportStatusDTO resultStatus = importService.getImportStatus(importId.toString());

        assertNotNull(resultStatus, "The result should not be null");
        assertEquals(expectedStatus.getId(), resultStatus.getId());
        assertEquals(expectedStatus.getStatus(), resultStatus.getStatus());
        assertEquals(expectedStatus.getRowsProcessed(), resultStatus.getRowsProcessed());

        verify(importRepository).findById(importId);
    }


}