# Hello World Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Provide a verified "hello world" HTTP endpoint in the library-backend Spring Boot service.

**Architecture:** A single `@RestController` exposes `GET /hello`, returning the plain string `"hello world"`. The endpoint is covered by an integration test using Spring Boot's `@SpringBootTest` + `MockMvc` to assert status 200 and response body.

**Tech Stack:** Java 17, Spring Boot 3.2.5 (spring-boot-starter-web), Maven, JUnit 5 (via spring-boot-starter-test), MockMvc.

---

## Task 1: Verify the HelloController endpoint

**Files:**
- Read: `src/main/java/com/library/backend/api/controller/HelloController.java`
- Read: `src/main/java/com/library/backend/LibraryBackendApplication.java`
- Read: `src/main/resources/application.yml`

**Context:**
The repository already contains `HelloController.java` implementing `GET /hello` → `"hello world"`. This task confirms the implementation matches the spec before adding tests.

**Steps:**
- [ ] Confirm `HelloController` is annotated with `@RestController` and `@RequestMapping("/hello")`.
- [ ] Confirm the `hello()` method is annotated with `@GetMapping` and returns `"hello world"`.
- [ ] Confirm the controller package `com.library.backend.api.controller` is under the `@SpringBootApplication` scan root `com.library.backend` (component-scan covers it by default).
- [ ] Confirm `application.yml` sets `server.port: 8080`.

## Task 2: Add an integration test for the hello endpoint

**Files:**
- Create: `src/test/java/com/library/backend/api/controller/HelloControllerTest.java`

**Context:**
No test exists yet. Add a Spring Boot integration test using MockMvc to verify the endpoint. Spring Boot 3.2.5 ships JUnit 5 via `spring-boot-starter-test`.

**Steps:**
- [ ] Create the test file with package `com.library.backend.api.controller`.
- [ ] Annotate the class with `@SpringBootTest` and `@AutoConfigureMockMvc`.
- [ ] Inject `MockMvc`.
- [ ] Write `whenHelloEndpoint_thenStatus200_andBodyIsHelloWorld()`:
  - perform `MockMvcRequestBuilders.get("/hello")`
  - `andExpect(status().isOk())`
  - `andExpect(content().string("hello world"))`

**Reference test code:**
```java
package com.library.backend.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenHelloEndpoint_thenStatus200_andBodyIsHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello world"));
    }
}
```

## Task 3: Verify build and tests

**Files:**
- Run: `mvn -q test`

**Context:**
Confirm the implementation and the new test compile and pass.

**Steps:**
- [ ] Run `mvn -q test` from the project root.
- [ ] Confirm `HelloControllerTest` passes and the build succeeds.
- [ ] If build fails for environment reasons (missing JDK, dependency download), fall back to static review of the controller and test code per the test-degradation protocol.

---

## Execution Handoff

Plan complete and saved to `docs/superpowers/plans/2026-07-31-hello-world.md`.

Per the full-pipeline contract, execution proceeds inline (executing-plans style) with checkpoints:
1. Execute Task 2 (Task 1 verification already passed during planning — the controller already exists and matches the spec).
2. Execute Task 3 verification.
