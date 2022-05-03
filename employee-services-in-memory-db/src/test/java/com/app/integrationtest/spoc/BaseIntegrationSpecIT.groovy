package com.app.integrationtest.spoc

import com.app.EmployeeApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = EmployeeApplication.class
)
//@TestPropertySource(locations = "classpath:application-test.properties")
class BaseIntegrationSpecIT extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @LocalServerPort
    int port

    @Shared
    String CONTEXT_PATH = "/EmployeeManagement/"


    URI buildUri(String resource) {
        return buildUri(resource, null)
    }

    URI buildUri(String resource, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromUriString("http://localhost:" + port + CONTEXT_PATH + resource)
                .queryParams(queryParams)
                .build(false)
                .encode()
                .toUri()
    }

    ResponseEntity<String> callService(URI uri, HttpMethod method, Object body) {
        return callService(uri, method, body, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON)
    }

    ResponseEntity<String> callService(URI uri, HttpMethod method, Object body, MediaType acceptHeader, MediaType contentType) {
        HttpHeaders headers = new HttpHeaders()
        headers.setAccept([acceptHeader])
        if (body != null) {
            headers.setContentType(contentType)
        }
        return callService(uri, method, headers, body)
    }

    ResponseEntity<String> callService(URI uri, HttpMethod method, HttpHeaders headers, Object body) {
        return restTemplate.exchange(uri, method, new HttpEntity<Object>(body, headers), String)
    }
}
