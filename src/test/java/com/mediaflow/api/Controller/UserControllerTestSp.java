package com.mediaflow.api.Controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = com.mediaflow.api.MediaFlowApiApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTestSp {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Variable estática para almacenar el userId entre tests
    private static Integer testUserId;
    private static final String TEST_EMAIL = "test.integration@example.com";
    private static final String TEST_PASSWORD = "SecurePassword123";

    // ==================== TEST 1: REGISTER USER ====================
    @Test
    @Order(1)
    public void test01_RegisterUser_ShouldReturnCreated() throws Exception {
        String jsonBody = """
        {
          "name": "Integration Test User",
          "email": "%s",
          "password": "%s",
          "dateBirth": "1995-06-15",
          "roles": [1],
          "preferredLanguage": "en",
          "location": {
              "country": "Mexico",
              "region": "Puebla",
              "city": "Teziutlan",
              "lat": 19.8160,
              "lng": -97.3591
            }
        }
        """.formatted(TEST_EMAIL, TEST_PASSWORD);

        MvcResult result = mvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test User"))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.roles[0]").exists())
                .andExpect(jsonPath("$.profile").exists())
                .andExpect(jsonPath("$.location").exists())
                .andReturn();

        // Extraer el userId de la respuesta
        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        testUserId = jsonNode.get("user Id").asInt();
        
        System.out.println("User registered with ID: " + testUserId);
    }

    // ==================== TEST 2: REGISTER DUPLICATE EMAIL ====================
    @Test
    @Order(2)
    public void test02_RegisterDuplicateEmail_ShouldReturnConflict() throws Exception {
        String jsonBody = """
        {
          "name": "Duplicate User",
          "email": "%s",
          "password": "AnotherPassword456",
          "dateBirth": "1998-03-20",
          "roles": [1],
          "preferredLanguage": "es",
          "location": {
              "country": "Mexico",
              "region": "CDMX",
              "city": "Mexico City",
              "lat": 19.4326,
              "lng": -99.1332
            }
        }
        """.formatted(TEST_EMAIL);

        mvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
        
        System.out.println("Duplicate email validation working correctly");
    }

    // ==================== TEST 3: LOGIN SUCCESS ====================
    @Test
    @Order(3)
    public void test03_Login_WithValidCredentials_ShouldReturnOk() throws Exception {
        String loginJson = """
        {
          "email": "%s",
          "password": "%s"
        }
        """.formatted(TEST_EMAIL, TEST_PASSWORD);

        mvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.userName").exists())
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.displayName").exists());
        
        System.out.println("Login successful");
    }

    // ==================== TEST 4: LOGIN WITH WRONG PASSWORD ====================
    @Test
    @Order(4)
    public void test04_Login_WithWrongPassword_ShouldReturnUnauthorized() throws Exception {
        String loginJson = """
        {
          "email": "%s",
          "password": "WrongPassword999"
        }
        """.formatted(TEST_EMAIL);

        mvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        
        System.out.println("Wrong password rejected correctly");
    }

    // ==================== TEST 5: LOGIN WITH NON-EXISTENT EMAIL ====================
    @Test
    @Order(5)
    public void test05_Login_WithNonExistentEmail_ShouldReturnNotFound() throws Exception {
        String loginJson = """
        {
          "email": "nonexistent@example.com",
          "password": "AnyPassword123"
        }
        """;

        mvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        
        System.out.println("Non-existent email handled correctly");
    }

    // ==================== TEST 6: GET USER LOCATION ====================
    @Test
    @Order(6)
    public void test06_GetUserLocation_ShouldReturnOk() throws Exception {
        mvc.perform(get("/api/v1/users/{id}/location", testUserId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("Mexico"))
                .andExpect(jsonPath("$.region").value("Puebla"))
                .andExpect(jsonPath("$.city").value("Teziutlan"))
                .andExpect(jsonPath("$.lat").exists())
                .andExpect(jsonPath("$.lng").exists());
        
        System.out.println("User location retrieved successfully");
    }

    // ==================== TEST 7: GET LOCATION FOR NON-EXISTENT USER ====================
    @Test
    @Order(7)
    public void test07_GetUserLocation_ForNonExistentUser_ShouldReturnNotFound() throws Exception {
        mvc.perform(get("/api/v1/users/{id}/location", 999999)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        
        System.out.println("Non-existent user location handled correctly");
    }

    // ==================== TEST 8: UPDATE USER ====================
    @Test
    @Order(8)
    public void test08_UpdateUser_ShouldReturnOk() throws Exception {
        String updateJson = """
        {
          "name": "Updated Test User",
          "email": "%s",
          "password": "%s",
          "dateBirth": "1995-06-15",
          "roles": [1],
          "preferredLanguage": "es",
          "location": {
              "country": "Mexico",
              "region": "Jalisco",
              "city": "Guadalajara",
              "lat": 20.6597,
              "lng": -103.3496
            }
        }
        """.formatted(TEST_EMAIL, TEST_PASSWORD);

        mvc.perform(put("/api/v1/users/update_account/{userId}", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Test User"))
                .andExpect(jsonPath("$.location.city").value("Guadalajara"));
        
        System.out.println("User updated successfully");
    }

    // ==================== TEST 9: UPDATE NON-EXISTENT USER ====================
    @Test
    @Order(9)
    public void test09_UpdateNonExistentUser_ShouldReturnNotFound() throws Exception {
        String updateJson = """
        {
          "name": "Ghost User",
          "email": "ghost@example.com",
          "password": "Password123",
          "dateBirth": "1990-01-01",
          "roles": [1],
          "preferredLanguage": "en",
          "location": {
              "country": "USA",
              "region": "California",
              "city": "Los Angeles",
              "lat": 34.0522,
              "lng": -118.2437
            }
        }
        """;

        mvc.perform(put("/api/v1/users/update_account/{userId}", 999999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        
        System.out.println("Update non-existent user handled correctly");
    }

    // ==================== TEST 10: UPDATE WITH INVALID DATA ====================
    @Test
    @Order(10)
    public void test10_UpdateUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        String updateJson = """
        {
          "name": "Test User",
          "email": "invalid-email-format",
          "password": "Password123",
          "dateBirth": "1995-06-15",
          "roles": [1],
          "preferredLanguage": "en",
          "location": {
              "country": "Mexico",
              "region": "Puebla",
              "city": "Teziutlan",
              "lat": 19.8160,
              "lng": -97.3591
            }
        }
        """;

        mvc.perform(put("/api/v1/users/update_account/{userId}", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        
        System.out.println("Invalid email format validation working");
    }

    // ==================== TEST 11: LOGIN AFTER UPDATE ====================
    @Test
    @Order(11)
    public void test11_Login_AfterUpdate_ShouldStillWork() throws Exception {
        String loginJson = """
        {
          "email": "%s",
          "password": "%s"
        }
        """.formatted(TEST_EMAIL, TEST_PASSWORD);

        mvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("Updated Test User"));
        
        System.out.println("Login still working after update");
    }

    // ==================== TEST 12: DELETE USER ====================
    @Test
    @Order(12)
    public void test12_DeleteUser_ShouldReturnNoContent() throws Exception {
        mvc.perform(delete("/api/v1/users/delete_account/{userId}", testUserId))
                .andDo(print())
                .andExpect(status().isNoContent());
        
        System.out.println("User deleted successfully with ID: " + testUserId);
    }

    // ==================== TEST 13: VERIFY USER IS DELETED ====================
    @Test
    @Order(13)
    public void test13_Login_AfterDelete_ShouldReturnNotFound() throws Exception {
        String loginJson = """
        {
          "email": "%s",
          "password": "%s"
        }
        """.formatted(TEST_EMAIL, TEST_PASSWORD);

        mvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        
        System.out.println("User confirmed deleted - cannot login");
    }

    // ==================== TEST 14: DELETE NON-EXISTENT USER ====================
    @Test
    @Order(14)
    public void test14_DeleteNonExistentUser_ShouldReturnNotFound() throws Exception {
        mvc.perform(delete("/api/v1/users/delete_account/{userId}", 999999))
                .andDo(print())
                .andExpect(status().isNotFound());
        
        System.out.println("Delete non-existent user handled correctly");
    }

    // ==================== TEST 15: REGISTER WITH MISSING REQUIRED FIELDS ====================
    @Test
    @Order(15)
    public void test15_Register_WithMissingFields_ShouldReturnBadRequest() throws Exception {
        String jsonBody = """
        {
          "name": "Incomplete User",
          "email": "incomplete@example.com"
        }
        """;

        mvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        
        System.out.println("Missing required fields validation working");
    }
}