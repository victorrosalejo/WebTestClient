import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

public class TestDemo1 {

    // Creamos un cliente WebTestClient para hacer peticiones HTTP
    private final WebTestClient webTestClient = WebTestClient
            .bindToServer()
            .baseUrl("jsonplaceholder.typicode.com")
            .build();

    @Test
    public void testBasic1_Status() {
        webTestClient
                // FETCH: Realizamos una petición GET a la URL especificada
                .get().uri("/users/1")
                // WAIT: Realizamos la petición y esperamos una respuesta
                .exchange()
                // ASSERT: Verificamos que el estado de la respuesta sea 200 OK
                .expectStatus().isEqualTo(200); // Verificamos que el estado de la respuesta sea 200 OK

        /*
            OTROS ESTADOS QUE SE PUEDEN VERIFICAR:
            .isNotFound() // 404 Not Found
            .isBadRequest() // 400 Bad Request
            .isUnauthorized() // 401 Unauthorized
            .isForbidden() // 403 Forbidden
            .isInternalServerError() // 500 Internal Server Error
            .isCreated() // 201 Created
            .isAccepted() // 202 Accepted
            .isNoContent() // 204 No Content
            
            .is2xxSuccessful() // 2xx Successful
            .is3xxRedirection() // 3xx Redirection
            .is4xxClientError() // 4xx Client Error
            .is5xxServerError() // 5xx Server Error
        
            .isEqualTo(HttpStatus.OK) // Verifica que el estado sea igual al especificado
            .isNotEqualTo(HttpStatus.NOT_FOUND) // Verifica que el estado no sea igual al especificado
        
            .expectStatus().isEqualTo(200);
         */
    }

    @Test
    public void testBasic2_Response() {
        webTestClient
                // FETCH: Realizamos una petición GET a la URL especificada
                .get().uri("/users/1")
                // WAIT: Realizamos la petición y esperamos una respuesta
                .exchange()
                // ASSERT: Verificamos que la respuesta tenga un cuerpo no vacío y el campo `id` sea `1`
                .expectBody() // Especificamos que esperamos un cuerpo en la respuesta
                .jsonPath("$.id").isEqualTo(1); // Verificamos que el campo `id` sea igual a `1`

        /*
            OTRAS COMPROBACIONES QUE SE PUEDEN HACER EN UN CAMPO JSON:
            .isEmpty() // Verifica que el cuerpo de la respuesta esté vacío
            .isNotEmpty() // Verifica que el cuerpo de la respuesta no esté vacío
            .isEqualTo("Texto esperado") // Verifica que el cuerpo de la respuesta sea igual al texto esperado
            .isNotEqualTo("Texto no esperado") // Verifica que el cuerpo de la respuesta no sea igual al texto esperado
            
         */
    }

    @Test
    public void testBasic3_PostWithBody() {
        webTestClient
                // FETCH: Realizamos una petición POST a la URL especificada
                .post().uri("/posts")
                .bodyValue("{'title':'foo','body':'bar','userId':1}")
                // WAIT: Realizamos la petición y esperamos una respuesta
                .exchange()
                // ASSERT: Verificamos que el estado de la respuesta sea 201 Created
                .expectStatus().isCreated()
                .expectBody(String.class) // Especificamos que esperamos un cuerpo de tipo String para luego procesarlo en el `consumeWith`
                .consumeWith(response -> {
                    String responseBody = response.getResponseBody();
                    System.out.println("Response Body: " + responseBody);
                });
    }

    @Test
    public void testBasic4_PostWithBodyAsMap() {
        // Creamos un Map para representar el cuerpo de la petición
        Map<String, Object> requestBody = Map.of(
                "title", "foo",
                "body", "bar",
                "userId", 1);

        webTestClient
                // FETCH: Realizamos una petición POST a la URL especificada
                .post().uri("/posts")
                .bodyValue(requestBody) // Enviamos el cuerpo de la petición como un Map
                // WAIT: Realizamos la petición y esperamos una respuesta
                .exchange()
                // ASSERT: Verificamos que el estado de la respuesta sea 201 Created
                .expectStatus().isCreated()
                .expectBody(String.class) // Especificamos que esperamos un cuerpo de tipo String para luego procesarlo en el `consumeWith`
                .consumeWith(response -> {
                    String responseBody = response.getResponseBody();
                    System.out.println("Response Body: " + responseBody);
                });
    }

    @Test
    public void testAdv1_Headers() {
        webTestClient
                // FETCH: Realizamos una petición GET a la URL especificada y agregamos unos Headers
                .get().uri("/users/1")
                .header("Accept", "application/json")
                // WAIT: Realizamos la petición y esperamos una respuesta
                .exchange()
                // ASSERT: Verificamos que el estado de la respuesta sea 200 OK y hacemos unas comprobaciones de los Headers
                .expectStatus().isOk() // Verificamos que el estado de la respuesta sea 200 OK
                .expectHeader().valueEquals("Content-Type", "application/json; charset=utf-8"); // Verificamos que el Header `Content-Type` sea igual al especificado

        /*
            OTROS MÉTODOS PARA VERIFICAR HEADERS:
        
            .expectHeader().exists("Header-Name") // Verifica que el Header exista
            .expectHeader().doesNotExist("Header-Name") // Verifica que el Header no exista
            .expectHeader().valueEquals("Header-Name", "Header-Value") // Verifica que el Header tenga un valor específico
            .expectHeader().valueMatches("Header-Name", "Regex") // Verifica que el Header coincida con una expresión regular
         */
    }

    @Test
    public void testAdv2_OtherAssertions() {
        webTestClient
                // FETCH: Realizamos una petición GET a la URL especificada
                .get().uri("/users/1")
                // WAIT: Realizamos la petición y esperamos una respuesta
                .exchange()
                // ASSERT: Verificamos que el estado de la respuesta sea 200 OK y hacemos unas comprobaciones adicionales
                .expectStatus().isOk()
                // Headers
                .expectHeader()
                .exists("Content-Type")
                // Cookies
                .expectCookie()
                .doesNotExist("sessionId") // Verificamos que la cookie `sessionId` no exista
                // .valueEquals("sessionId", "123456") // Verificamos que la cookie `sessionId` tenga un valor específico
                // Body
                .expectBody()
                .jsonPath("$.id").isEqualTo(1) // Verificamos que el campo `id` sea igual a `1`
                .jsonPath("$.address.city").isNotEmpty() // Verificamos que el campo `address.city` no esté vacío
                .jsonPath("$.email").value(email -> { // Verificamos que el campo `email` contenga un `@`
                    assert ((String) email).contains("@");
                });
    }

    // The idea is to print all the details of the response, including headers, cookies, and body.
    @Test
    public void testAdv3_DEBUG() {
        webTestClient
                // FETCH: Realizamos una petición GET a la URL especificada
                .get().uri("/users/1")
                // WAIT: Realizamos la petición y esperamos una respuesta
                .exchange()
                .expectBody()
                .consumeWith(response -> {
                    // Headers
                    System.out.println("Request Headers: " + response.getRequestHeaders());
                    System.out.println("Response Headers: " + response.getResponseHeaders());
                    // Cookies
                    System.out.println("Response Cookies: " + response.getResponseCookies());
                    // Body
                    System.out.println("Request Body: " + response.getRequestBodyContent());
                    System.out.println("Response Body (Bytes): " + response.getResponseBodyContent());
                    System.out.println("Response Body (String): "
                            + new String(response.getResponseBodyContent(), StandardCharsets.UTF_8));
                    // Other details
                    System.out.println("Response URL: " + response.getUrl());
                    System.out.println("Response STATUS: " + response.getStatus());
                });
    }
}
