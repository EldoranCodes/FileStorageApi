package com.nard.FileStorageApi.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nard.FileStorageApi.dao.AccountRepository;
import com.nard.FileStorageApi.dao.UserRepository;
import com.nard.FileStorageApi.dto.BatchFilesResponseDto;
import com.nard.FileStorageApi.dto.DeleteResponseDto;
import com.nard.FileStorageApi.dto.UploadResponseDto;
import com.nard.FileStorageApi.model.Account;
import com.nard.FileStorageApi.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FullRestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    private User testUser;
    private String validApiKey;
    private String baseUrl;
    private File testFile1;
    private File testFile2;

    @BeforeEach
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + port;

        // Create test user with API key
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        validApiKey = "integration-test-key-123";
        String hashedKey = encoder.encode(validApiKey);

        // Reuse existing test user or create new one
        testUser = userRepository.findByName("integration-test-user").orElse(null);
        
        if (testUser == null) {
            Account account = accountRepository.findAll().stream()
                .filter(acc -> "Test Account".equals(acc.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Account newAccount = new Account();
                    newAccount.setName("Test Account");
                    newAccount.setEmail("test@example.com");
                    newAccount.setCreatedAt(LocalDateTime.now());
                    return accountRepository.save(newAccount);
                });

            testUser = new User();
            testUser.setName("integration-test-user");
            testUser.setStatus("ACTIVE");
            testUser.setAccount(account);
            testUser.setApiKey(hashedKey);
            testUser.setCreatedAt(LocalDateTime.now());
            testUser = userRepository.save(testUser);
        } else {
            // Update API key if user exists
            testUser.setApiKey(hashedKey);
            testUser = userRepository.save(testUser);
        }

        // Create test files
        testFile1 = new File(tempDir.toFile(), "test1.txt");
        Files.write(testFile1.toPath(), "This is test file 1 content".getBytes());

        testFile2 = new File(tempDir.toFile(), "test2.txt");
        Files.write(testFile2.toPath(), "This is test file 2 content".getBytes());
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "ApiKey " + validApiKey);
        return headers;
    }

    @Test
    void testCompleteWorkflow_UploadGetDelete() throws Exception {
        // ========== STEP 1: Test Upload without Auth (should fail) ==========
        System.out.println("\n=== Testing Upload without Authentication ===");
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", new FileSystemResource(testFile1));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/upload",
            requestEntity,
            String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        System.out.println("✓ Upload without auth correctly rejected");

        // ========== STEP 2: Test Upload with Valid Auth (Single File) ==========
        System.out.println("\n=== Testing Upload with Valid Authentication (Single File) ===");
        body = new LinkedMultiValueMap<>();
        body.add("files", new FileSystemResource(testFile1));

        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        requestEntity = new HttpEntity<>(body, headers);

        response = restTemplate.postForEntity(
            baseUrl + "/upload",
            requestEntity,
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("batchId"));
        assertTrue(responseBody.contains("SUCCESS"));

        // Parse response
        UploadResponseDto uploadResponse = objectMapper.readValue(
            response.getBody(),
            UploadResponseDto.class
        );

        assertNotNull(uploadResponse.getBatchId());
        assertEquals("SUCCESS", uploadResponse.getStatus());
        assertEquals(1, uploadResponse.getFiles().size());
        assertEquals("test1.txt", uploadResponse.getFiles().get(0).getOriginalName());
        assertNotNull(uploadResponse.getFiles().get(0).getFileId());
        assertNotNull(uploadResponse.getFiles().get(0).getUuid());

        Long batchId1 = uploadResponse.getBatchId();
        Long fileId1 = uploadResponse.getFiles().get(0).getFileId();

        System.out.println("✓ Single file uploaded successfully");
        System.out.println("  Batch ID: " + batchId1);
        System.out.println("  File ID: " + fileId1);
        System.out.println("  UUID: " + uploadResponse.getFiles().get(0).getUuid());

        // ========== STEP 3: Test Upload Multiple Files ==========
        System.out.println("\n=== Testing Upload Multiple Files ===");
        body = new LinkedMultiValueMap<>();
        body.add("files", new FileSystemResource(testFile1));
        body.add("files", new FileSystemResource(testFile2));

        headers = createAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        requestEntity = new HttpEntity<>(body, headers);

        response = restTemplate.postForEntity(
            baseUrl + "/upload",
            requestEntity,
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        uploadResponse = objectMapper.readValue(response.getBody(), UploadResponseDto.class);

        assertNotNull(uploadResponse.getBatchId());
        assertEquals(2, uploadResponse.getFiles().size());
        Long batchId2 = uploadResponse.getBatchId();
        Long fileId2 = uploadResponse.getFiles().get(0).getFileId();
        Long fileId3 = uploadResponse.getFiles().get(1).getFileId();

        System.out.println("✓ Multiple files uploaded successfully");
        System.out.println("  Batch ID: " + batchId2);
        System.out.println("  File IDs: " + fileId2 + ", " + fileId3);

        // ========== STEP 4: Test Get Batch Files ==========
        System.out.println("\n=== Testing Get Batch Files ===");
        headers = createAuthHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        response = restTemplate.exchange(
            baseUrl + "/files/" + batchId2,
            HttpMethod.GET,
            entity,
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BatchFilesResponseDto batchResponse = objectMapper.readValue(
            response.getBody(),
            BatchFilesResponseDto.class
        );

        assertEquals(batchId2, batchResponse.getBatchId());
        assertEquals(2, batchResponse.getFiles().size());
        assertEquals("test1.txt", batchResponse.getFiles().get(0).getOriginalName());
        assertEquals("test2.txt", batchResponse.getFiles().get(1).getOriginalName());

        System.out.println("✓ Batch files retrieved successfully");
        System.out.println("  Files in batch: " + batchResponse.getFiles().size());

        // ========== STEP 5: Test Get Batch Files - Not Found ==========
        System.out.println("\n=== Testing Get Batch Files - Not Found ===");
        response = restTemplate.exchange(
            baseUrl + "/files/99999",
            HttpMethod.GET,
            entity,
            String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("✓ Non-existent batch correctly returns 404");

        // ========== STEP 6: Test Get Batch Files - Unauthorized ==========
        System.out.println("\n=== Testing Get Batch Files - Unauthorized ===");
        HttpEntity<String> unauthorizedEntity = new HttpEntity<>(new HttpHeaders());

        response = restTemplate.exchange(
            baseUrl + "/files/" + batchId2,
            HttpMethod.GET,
            unauthorizedEntity,
            String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        System.out.println("✓ Unauthorized access correctly rejected");

        // ========== STEP 7: Test Delete Single File ==========
        System.out.println("\n=== Testing Delete Single File ===");
        headers = createAuthHeaders();
        entity = new HttpEntity<>(headers);

        response = restTemplate.exchange(
            baseUrl + "/delete/file/" + fileId3,
            HttpMethod.DELETE,
            entity,
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        DeleteResponseDto deleteResponse = objectMapper.readValue(
            response.getBody(),
            DeleteResponseDto.class
        );

        assertTrue(deleteResponse.isSuccess());
        assertTrue(deleteResponse.getMessage().contains("deleted"));

        System.out.println("✓ Single file deleted successfully");

        // ========== STEP 8: Test Delete File - Not Found ==========
        System.out.println("\n=== Testing Delete File - Not Found ===");
        response = restTemplate.exchange(
            baseUrl + "/delete/file/99999",
            HttpMethod.DELETE,
            entity,
            String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("✓ Non-existent file correctly returns 404");

        // ========== STEP 9: Test Delete Batch ==========
        System.out.println("\n=== Testing Delete Batch ===");
        response = restTemplate.exchange(
            baseUrl + "/delete/batch/" + batchId2,
            HttpMethod.DELETE,
            entity,
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        deleteResponse = objectMapper.readValue(response.getBody(), DeleteResponseDto.class);

        assertTrue(deleteResponse.isSuccess());
        assertTrue(deleteResponse.getMessage().contains("deleted"));

        System.out.println("✓ Batch deleted successfully");

        // ========== STEP 10: Verify Batch is Deleted ==========
        System.out.println("\n=== Verifying Batch is Deleted ===");
        response = restTemplate.exchange(
            baseUrl + "/files/" + batchId2,
            HttpMethod.GET,
            entity,
            String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("✓ Batch deletion verified - batch no longer exists");

        // ========== STEP 11: Test Invalid API Key ==========
        System.out.println("\n=== Testing Invalid API Key ===");
        HttpHeaders invalidHeaders = new HttpHeaders();
        invalidHeaders.set("Authorization", "ApiKey invalid-key-123");
        HttpEntity<String> invalidEntity = new HttpEntity<>(invalidHeaders);

        response = restTemplate.exchange(
            baseUrl + "/files/" + batchId1,
            HttpMethod.GET,
            invalidEntity,
            String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        System.out.println("✓ Invalid API key correctly rejected");

        System.out.println("\n=== All Integration Tests Passed! ===");
    }

    @Test
    void testUploadEmptyFileArray() throws Exception {
        System.out.println("\n=== Testing Upload Empty File Array ===");
        
        // Create empty multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // Don't add any files

        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/upload",
            requestEntity,
            String.class
        );

        // Should return bad request or internal server error
        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST || 
                   response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);
        System.out.println("✓ Empty file array correctly rejected");
    }

    @Test
    void testUnauthorizedAccessToOtherUserBatch() throws Exception {
        System.out.println("\n=== Testing Unauthorized Access to Other User's Batch ===");
        
        // Create another user
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String otherApiKey = "other-user-key";
        String otherHashedKey = encoder.encode(otherApiKey);

        Account otherAccount = new Account();
        otherAccount.setName("Other Account");
        otherAccount.setEmail("other@example.com");
        otherAccount.setCreatedAt(LocalDateTime.now());
        otherAccount = accountRepository.save(otherAccount);

        User otherUser = new User();
        otherUser.setName("other-user");
        otherUser.setStatus("ACTIVE");
        otherUser.setAccount(otherAccount);
        otherUser.setApiKey(otherHashedKey);
        otherUser.setCreatedAt(LocalDateTime.now());
        otherUser = userRepository.save(otherUser);

        // Upload file as first user
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", new FileSystemResource(testFile1));

        HttpHeaders headers = createAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/upload",
            requestEntity,
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UploadResponseDto uploadResponse = objectMapper.readValue(
            response.getBody(),
            UploadResponseDto.class
        );
        Long batchId = uploadResponse.getBatchId();

        // Try to access batch with other user's API key
        HttpHeaders otherHeaders = new HttpHeaders();
        otherHeaders.set("Authorization", "ApiKey " + otherApiKey);
        HttpEntity<String> otherEntity = new HttpEntity<>(otherHeaders);

        response = restTemplate.exchange(
            baseUrl + "/files/" + batchId,
            HttpMethod.GET,
            otherEntity,
            String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        System.out.println("✓ Unauthorized access to other user's batch correctly rejected");

        // Cleanup
        userRepository.delete(otherUser);
        accountRepository.delete(otherAccount);
    }
}

