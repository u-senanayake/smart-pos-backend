package lk.udcreations.user.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class RoleApiTest {

	private static int createdRoleId;

	@LocalServerPort
	private int port;

	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = "http://localhost";
	}

	@Test
	@Order(1)
	void testCreateRole() {
		String requestBody = 
				"""
					{
						"roleName": "Test User",
						"description": "Test User role",
						"enabled": true
					}
				""";

		Response response = given()
			.port(port)
			.contentType(ContentType.JSON)
			.body(requestBody)
		.when()
			.post("/api/v1/role")
		.then()
			.statusCode(201)
			.body("roleName", equalTo("Test User"))
			.body("description", equalTo("Test User role"))
			.body("enabled", equalTo(true))
			.body("deleted", equalTo(false))
			.extract().response();

		createdRoleId = response.jsonPath().getInt("roleId");
	}

	@Test
	@Order(2)
	void testGetAllRoles() {
		given()
			.port(port)
		.when()
			.get("/api/v1/role")
		.then()
			.statusCode(200)
			.body("size()", greaterThan(2)); // At least 3 roles (2 from data.sql + our created one)
	}

	@Test
	@Order(3)
	void testGetAllRolesIncludingDeleted() {
		given()
			.port(port)
		.when()
			.get("/api/v1/role/all")
		.then()
			.statusCode(200)
			.body("size()", greaterThan(3)); // At least 4 roles (3 from data.sql + our created one)
	}

	@Test
	@Order(4)
	void testGetRoleById() {
		given()
			.port(port)
		.when()
			.get("/api/v1/role/" + createdRoleId)
		.then()
			.statusCode(200)
			.body("roleId", equalTo(createdRoleId))
			.body("roleName", equalTo("Test User"));
	}

	@Test
	@Order(5)
	void testGetRoleByIdNotFound() {
		given()
			.port(port)
		.when()
			.get("/api/v1/role/999")
		.then()
			.statusCode(404);
	}

	@Test
	@Order(6)
	void testUpdateRole() {
		String updatedBody = """
				{
					"roleName": "Updated User",
					"description": "Updated description",
					"enabled": false
				}
				""";

		given()
			.port(port)
			.contentType(ContentType.JSON)
			.body(updatedBody)
		.when()
			.put("/api/v1/role/" + createdRoleId)
		.then()
			.statusCode(200)
			.body("roleName", equalTo("Updated User"))
			.body("description", equalTo("Updated description"))
			.body("enabled", equalTo(false));
	}

	@Test
	@Order(7)
	void testUpdateRoleNotFound() {
		String updatedBody = """
				{
					"roleName": "Non-existent Role",
					"description": "This role doesn't exist",
					"enabled": true
				}
				""";

		given()
			.port(port)
			.contentType(ContentType.JSON)
			.body(updatedBody)
		.when()
			.put("/api/v1/role/999")
		.then()
			.statusCode(404);
	}

	@Test
	@Order(8)
	void testDeleteRole() {
		given()
			.port(port)
		.when()
			.delete("/api/v1/role/" + createdRoleId)
		.then()
			.statusCode(204);

		// Verify the role is marked as deleted but still retrievable
		given()
			.port(port)
		.when()
			.get("/api/v1/role/" + createdRoleId)
		.then()
			.statusCode(200)
			.body("deleted", equalTo(true));

		// Verify it's not in the active roles list
		given()
			.port(port)
		.when()
			.get("/api/v1/role")
		.then()
			.statusCode(200)
			.body("findAll { it.roleId == " + createdRoleId + " }", hasSize(0));
	}

	@Test
	@Order(9)
	void testDeleteRoleNotFound() {
		given()
			.port(port)
		.when()
			.delete("/api/v1/role/999")
		.then()
			.statusCode(404);
	}
}
