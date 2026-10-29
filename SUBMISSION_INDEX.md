# Submission Index

This page is the reviewer entry point for the completed Books and Authors GraphQL API. It links to the existing project records and does not replace the detailed walkthrough or repeat the full requirements matrix. This index was created as the approved M8 documentation deliverable; the project records contain no separate formal M8 closure approval.

## Build and automated tests

Use the included Gradle wrapper and Java 17. The existing commands and environment notes are in [Demo Guide — Prerequisites and startup](DEMO_GUIDE.md#prerequisites-and-startup):

- `./gradlew clean test --console=plain`
- `./gradlew bootRun --console=plain`
- `./gradlew bootRun --args='--spring.profiles.active=demo' --console=plain`

**Previously recorded result (not rerun for this index):** the final clean test run completed with `BUILD SUCCESSFUL`: 23 tests, 0 failures, 0 errors, and 0 skipped. The command, runtime environment, and test breakdown are recorded in [Testing and Backlog — M7](TESTING_AND_BACKLOG.md#m7-documentation-and-submission-review) and [Requirements and Decisions — M7 verification and closure record](REQUIREMENTS_AND_DECISIONS.md#m7-demo-rehearsal-and-final-traceability).

The existing Gradle test-result XML was later inspected during documentation reconciliation and records 29 tests, 0 failures, 0 errors, and 0 skipped. No tests were rerun for that documentation update; the 23-test figure above remains the historical M7 run.

## Live demonstration

Follow [DEMO_GUIDE.md](DEMO_GUIDE.md) for demo-profile startup, the `authors`, `books`, and `bookById` queries, the `addBook` → `updateBook` → `deleteBook` lifecycle, nested Book–Author selection, and the final check that deleting a Book retains its Author. The guide includes the previously recorded live responses and restart/reset rehearsal.

## Acceptance traceability

The table points to existing implementation and verification records. A-01–A-12 final statuses are recorded in [Requirements and Decisions — M7 traceability](REQUIREMENTS_AND_DECISIONS.md#m7-demo-rehearsal-and-final-traceability). Detailed assignment wording and classification are in the [requirements list](REQUIREMENTS_AND_DECISIONS.md#explicit-project-requirements-from-the-assignment).

| ID | Implementation reference | Verification evidence reference |
| --- | --- | --- |
| A-01 | [Build configuration](build.gradle.kts), [application entry point](src/main/kotlin/com/example/booksapi/BooksAuthorsApplication.kt), API items below | [M1 toolchain/build record](REQUIREMENTS_AND_DECISIONS.md#m1-implementation-record), [M7 demo and final traceability](REQUIREMENTS_AND_DECISIONS.md#m7-demo-rehearsal-and-final-traceability) |
| A-02 | [GraphQL schema](src/main/resources/graphql/schema.graphqls) | [Query tests](src/test/kotlin/com/example/booksapi/BooksQueryTests.kt), [mutation tests](src/test/kotlin/com/example/booksapi/BooksMutationTests.kt) |
| A-03 | [`books` schema and resolver](src/main/resources/graphql/schema.graphqls), [query controller](src/main/kotlin/com/example/booksapi/book/BooksQueryController.kt), [query service](src/main/kotlin/com/example/booksapi/book/BookQueryService.kt) | [Pagination tests](src/test/kotlin/com/example/booksapi/BooksPaginationAndNPlusOneTests.kt), [M5 pagination and measurement record](TESTING_AND_BACKLOG.md#m5-pagination-and-n1-prevention) |
| A-04 | [`bookById` schema and resolver](src/main/resources/graphql/schema.graphqls), [query controller](src/main/kotlin/com/example/booksapi/book/BooksQueryController.kt) | [Query tests](src/test/kotlin/com/example/booksapi/BooksQueryTests.kt), [M7 traceability](REQUIREMENTS_AND_DECISIONS.md#m7-demo-rehearsal-and-final-traceability) |
| A-05 | [`authors` schema and resolver](src/main/resources/graphql/schema.graphqls), [query controller](src/main/kotlin/com/example/booksapi/book/BooksQueryController.kt), [Author query service](src/main/kotlin/com/example/booksapi/author/AuthorQueryService.kt) | [Query tests](src/test/kotlin/com/example/booksapi/BooksQueryTests.kt), [live demo evidence](DEMO_GUIDE.md#recorded-live-rehearsal) |
| A-06 | [Mutation schema](src/main/resources/graphql/schema.graphqls), [mutation controller](src/main/kotlin/com/example/booksapi/book/BookMutationController.kt), [mutation service](src/main/kotlin/com/example/booksapi/book/BookMutationService.kt), [input types](src/main/kotlin/com/example/booksapi/book/BookMutationInputs.kt) | [Mutation tests](src/test/kotlin/com/example/booksapi/BooksMutationTests.kt), [live mutation lifecycle](DEMO_GUIDE.md#recorded-live-rehearsal) |
| A-07 | [Book and Author entities](src/main/kotlin/com/example/booksapi/book/Book.kt), [Author entity](src/main/kotlin/com/example/booksapi/author/Author.kt), [repositories](src/main/kotlin/com/example/booksapi/book/BookRepository.kt), [H2 configuration](src/main/resources/application.yml) | [Query/persistence tests](src/test/kotlin/com/example/booksapi/BooksQueryTests.kt), [M7 traceability](REQUIREMENTS_AND_DECISIONS.md#m7-demo-rehearsal-and-final-traceability) |
| A-08 | [JUnit/GraphQLTester test suite](src/test/kotlin/com/example/booksapi/) | [Previously recorded final test result](TESTING_AND_BACKLOG.md#m7-documentation-and-submission-review) |
| A-09 | [`Book.author` batch resolver](src/main/kotlin/com/example/booksapi/book/BooksQueryController.kt), [test-only SQL collector](src/test/kotlin/com/example/booksapi/SqlCaptureStatementInspector.kt) | [SQL-count test](src/test/kotlin/com/example/booksapi/BooksPaginationAndNPlusOneTests.kt), [recorded SQL evidence](DEMO_GUIDE.md#n1-verification-evidence) |
| A-10 | [Book and Author schema](src/main/resources/graphql/schema.graphqls), [nested field resolver](src/main/kotlin/com/example/booksapi/book/BooksQueryController.kt) | [Nested GraphQLTester query](src/test/kotlin/com/example/booksapi/BooksQueryTests.kt), [live nested query](DEMO_GUIDE.md#recorded-live-rehearsal) |
| A-11 | [Run and demo instructions](DEMO_GUIDE.md) | [Final M7 acceptance evidence](REQUIREMENTS_AND_DECISIONS.md#m7-demo-rehearsal-and-final-traceability) |
| A-12 | [`Book.author` schema field](src/main/resources/graphql/schema.graphqls), [batch field resolver](src/main/kotlin/com/example/booksapi/book/BooksQueryController.kt) | [Field-resolution GraphQLTester test](src/test/kotlin/com/example/booksapi/BooksQueryTests.kt), [M7 traceability](REQUIREMENTS_AND_DECISIONS.md#m7-demo-rehearsal-and-final-traceability) |

## Evidence types

- **Automated tests:** The previously recorded clean test suite is summarized above and detailed in [Testing and Backlog — M7](TESTING_AND_BACKLOG.md#m7-documentation-and-submission-review). This result was not rerun while creating the index.
- **Live API rehearsal:** Request/response evidence for the demo-profile mutation lifecycle, nested query, Author retention, and reset is in [Demo Guide — Recorded live rehearsal](DEMO_GUIDE.md#recorded-live-rehearsal).
- **N+1 SQL statement counts:** The separate execution-only comparison is in [Demo Guide — N+1 verification evidence](DEMO_GUIDE.md#n1-verification-evidence) and the [M5 backlog record](TESTING_AND_BACKLOG.md#m5-pagination-and-n1-prevention). It reports root Book fetches separately from Author relationship loads; nested response data alone is not N+1 evidence.

## Provisional project decisions

The `books(page, size)` pagination signature, defaults, bounds, and ordering are a **provisional project contract**. The assignment requires pagination but does not prescribe this API shape. See [Requirements and Decisions — M5 pagination plan](REQUIREMENTS_AND_DECISIONS.md#m5-user-authorized-provisional-pagination-and-measurement-plan) and [Demo Guide — Paginated books](DEMO_GUIDE.md#paginated-books). The approved mutation signatures are also project decisions rather than assignment-mandated signatures; see [Requirements and Decisions — M4 contracts](REQUIREMENTS_AND_DECISIONS.md#m4-user-authorized-provisional-mutation-contracts).

## Historical M6 audit note

The M6 coverage audit in [Requirements and Decisions](REQUIREMENTS_AND_DECISIONS.md#m6-requirement-coverage-audit) predates M7. Its then-partial statuses for A-01 and A-11 reflected pending demo work at that time. The later [M7 final traceability table](REQUIREMENTS_AND_DECISIONS.md#m7-demo-rehearsal-and-final-traceability) supersedes those historical partial statuses and records both as Pass with M7 evidence.
