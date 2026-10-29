# Testing Plan and Backlog

**Status:** M0–M7 are approved and closed. The approved M8 documentation deliverable, `SUBMISSION_INDEX.md`, exists; no separate formal M8 closure approval is recorded. The M7 suite result (23 tests) is historical milestone evidence. Existing Gradle XML reports currently record 29 tests, 0 failures, 0 errors, and 0 skipped; they were inspected during documentation reconciliation, not rerun.

## Assignment-traced acceptance checklist

| ID | Acceptance criterion | Evidence planned |
| --- | --- | --- |
| A-01 | The project uses the required Kotlin/Spring Boot/Spring for GraphQL stack and delivers a working Books/Authors GraphQL API. | **M1:** Java 17-compatible Kotlin/Spring Boot/Gradle build foundation, wrapper, and resolved dependencies build and start successfully; this verifies the stack only. **M2–M5:** schema/queries, persistence/relationships, mutations, pagination, and N+1 behavior are implemented and verified under A-02–A-10 and A-12. **M7:** demonstrate the completed API with documented run and GraphQL steps. |
| A-02 | SDL defines `Book`, `Author`, and input types. | Schema inspection and GraphQL execution tests |
| A-03 | `books` returns a paginated result. | M5 GraphQLTester pagination tests pass for first/next/beyond-end pages, invalid bounds, stable ordering, and no duplicates across adjacent pages under the provisional contract |
| A-04 | `bookById(id)` resolves an individual book. | GraphQLTester lookup test; missing-ID behavior once specified |
| A-05 | `authors` resolves author records. | GraphQLTester query test |
| A-06 | `addBook`, `updateBook`, and `deleteBook` work. | M4 GraphQLTester tests against the user-authorized provisional signatures, error cases, and H2 persistence/relationship effects; see M4 contract below |
| A-07 | Data persists through Spring Data JPA and H2. | H2 integration tests |
| A-08 | At least three automated tests exist. | M6 clean run: 21 tests, 0 failures, 0 errors, 0 skipped; inventory appears in the M6 coverage audit |
| A-09 | Books-with-authors path avoids N+1 queries. | Recorded M5 comparison: 5 Books => root=1, author=1; 20 Books => root=1, author=1; ΔN=15, ΔR=0. The current regression assertion requires ΔR/ΔN < 0.5; the recorded ratio is 0. This practical check is not universal proof. Nested response assertions alone do not satisfy A-09. |
| A-10 | Client fetches nested Book–Author data in one GraphQL request. | M3 GraphQLTester submits one operation selecting a book and nested author data in that same request. The M2 `books { id title }` test does not satisfy A-10. |
| A-11 | Complete tested project can be demoed. | Documented startup and demo steps actually exercised |
| A-12 | Explicit `Book.author` field relationship is defined and resolved. | M3 SDL defines `Book.author: Author!`; `BooksQueryTests.books query returns persisted books and nested authors` selects and resolves that field. This field-level check is distinct from A-10's one-request capability and neither proves A-09. |

Other precise fields, pagination shape, unrelated validation constraints, and exact error payload details remain unresolved; tests must not encode invented requirements. The user-authorized provisional mutation contract is documented under M4 below. Book–Author cardinality, optionality, and relationship mutation semantics are resolved by explicit user approval as recorded below. A-10 and A-12 are separate because the screenshots separately state the one-request nested-data checkpoint and the `Book.author` resolution lab. One GraphQLTester test may provide evidence for both if it explicitly checks both acceptance points.

## Verification principles

- Run commands only when their milestone is authorized and environment permits; record actual results.
- Distinguish passing evidence from commands merely proposed.
- Keep assignment requirements separate from engineering improvements and stretch goals as in `REQUIREMENTS_AND_DECISIONS.md`.
- Keep implementation within the currently authorized milestone. M0–M7 are approved and closed. Do not begin another milestone without explicit user approval.

## Requirement classification

- **Assignment requirements:** A-01–A-12 in `REQUIREMENTS_AND_DECISIONS.md`; these include the named operations, JPA/H2 persistence, pagination, nested author data, at least three automated tests, and N+1 prevention.
- **User-selected constraints:** Java 17, Gradle Kotlin DSL, Hibernate, JUnit 5, GraphQLTester, and the milestone/verification workflow, recorded separately as U-01–U-05.
- **Recommendations:** Spring mapping annotations, schema path, temporary in-memory learning step, `@BatchMapping`, and specific test examples. Apply them where compatible with the assignment; they do not resolve unstated schema details.
- **Stretch goals:** Coroutines, Apollo Kotlin, subscriptions, and Ktor/KGraphQL; outside baseline acceptance. Spring Security is an overview topic, not a required feature.

## Pre-M3 Book–Author decision gate — RESOLVED by explicit user approval

The assignment screenshots require nested author data and `Book.author` but do not specify relationship semantics. The following are user-approved project design decisions, not assignment requirements. Keep them recorded and consistent before M3 implementation begins:

1. Each Book has exactly one Author; an Author may be associated with multiple Books.
2. Every Book must have an Author; GraphQL exposes the required singular field `Book.author: Author!`.
3. Map Book to Author as JPA `@ManyToOne(optional = false)` with a non-null Book-to-Author foreign key.
4. `addBook` accepts an existing Author ID, rejects invalid/nonexistent references, and does not create Authors.
5. `updateBook` may change title and Author using an existing Author ID. If the Author ID is omitted, preserve the current association. Clearing is disallowed; explicit null must not clear or be treated as omission.
6. `deleteBook` deletes only the Book and retains the Author; do not cascade Book deletion to Author.

The rationale is to match the approved one-Author-per-Book, reusable-Author cardinality with a JPA many-to-one mapping and non-null foreign key; existing-ID references avoid implicit Author creation; omitted update IDs preserve the association; and Book-only deletion avoids deleting a potentially shared Author.

Plan tests for required nested resolution and association persistence, invalid Author ID rejection, omitted-field preservation, valid reassignment, clearing rejection, and Author retention after Book deletion. The M4 signatures and standard error-handling behavior are now user-authorized provisional decisions recorded below; unrelated SDL details and exact error payload fields remain unspecified. The relationship decision gate was satisfied before M3. M3 has been approved.

## M0: Requirements and decisions

**Scope:** Inspect workspace/toolchain/Git; extract requirements from supplied training screenshots; document the distinction between assignment requirements, the current user's stack/workflow constraints, recommendations, and optional goals.

**Deliverables:** Updated `REQUIREMENTS_AND_DECISIONS.md`, `ARCHITECTURE.md`, this plan, environment findings, and a suggested commit message.

**Acceptance criteria:** Required operation names, minimum tests, pagination, JPA/H2, nested author, and N+1 requirements are traced to the screenshots; ambiguities stay open; architecture stays labeled proposed; no M1 work.

**Verification:** Review the three documents and the user's source images. No build/test applies to this documentation-only milestone. Environment inspection previously observed Java 17.0.16, no Gradle or wrapper, empty initial workspace, and no Git repository.

## M1: Project skeleton and reproducible build

**Scope:** Create the minimal Kotlin/Spring Boot project using Java 17, Gradle Kotlin DSL, Spring Web, Spring for GraphQL, required persistence/test dependencies, and a checked-in wrapper. This milestone must not implement the full API. Select dependency/tool versions only after verifying compatibility among Java 17, Kotlin, Spring Boot, Gradle, and the Kotlin/Spring plugins; record the chosen versions and evidence.

**Deliverables:** Wrapper and build configuration, minimal app entry point, conventional source/resources/test layout, pinned compatible dependency choices, startup instructions.

**Acceptance criteria:** Java 17 build compiles; application context starts; wrapper works; stack includes technologies required by user and assignment; selected Kotlin/Spring Boot/Gradle/plugin versions are a verified compatible combination. Record compatibility basis and actual build output. Do not add GraphQL fields early unless needed for a deliberate startup check.

**Verification commands:** `./gradlew --version`; `./gradlew clean test`; `./gradlew bootRun` with startup observed and process stopped; `./gradlew dependencies` to inspect resolved dependencies. Record actual outcomes.

**M1 implementation and verification record:**

- Selected Java 17.0.16; Kotlin JVM and Spring plugins 2.2.21; Spring Boot plugin/BOM 3.5.16; Gradle wrapper 8.14.4 with its official SHA-256 pinned in `gradle-wrapper.properties`. Compatibility sources and rationale are recorded in `REQUIREMENTS_AND_DECISIONS.md`.
- Resolved dependency report confirmed Spring GraphQL 1.4.6, Spring Data JPA 3.5.13, Hibernate 6.6.53.Final, H2 2.3.232, JUnit Jupiter 5.12.2, and GraphQLTester support 1.4.6. Direct test dependencies are Spring Boot Test and its test auto-configuration module, Spring Test, JUnit Jupiter/platform launcher, and Spring GraphQL Test.
- `java -version` reported OpenJDK 17.0.16.
- `./gradlew --version` reported Gradle 8.14.4 running on Java 17.0.16 after wrapper distribution checksum verification.
- `./gradlew clean test` completed with `BUILD SUCCESSFUL`; the context smoke-test report shows 1 test, 0 failures, 0 errors, 0 skipped.
- `./gradlew bootRun --console=plain` logged Spring Boot 3.5.16 startup on Java 17.0.16, Tomcat listening on port 8080, H2 datasource creation, Hibernate ORM initialization, GraphQL schema loading, and the `/graphql` POST endpoint. The process was stopped after observing startup.
- `./gradlew dependencies --configuration testRuntimeClasspath --console=plain` completed successfully and verified resolved dependency versions.
- The Gradle distribution was downloaded to temporary storage because no system Gradle was present. The initial sandbox network attempt failed; the authorized fetch/checksum and subsequent Gradle plugin/dependency resolution succeeded. No system Gradle installation was performed.

M2 replaced the temporary M1 placeholder with the initial `books` query. M1 evidence alone did not complete later API acceptance criteria.

## M2: GraphQL schema and first working query

**Scope:** Add schema-first SDL at `src/main/resources/graphql/schema.graphqls`, define initial Book/Author types and an input type, and implement the first query path. The training progression starts with `Book` and `books` using an in-memory list. This data source is temporary only and must be removed/replaced by Spring Data JPA/H2 in M3; it must not become the final or parallel source of truth. The screenshots do not specify scalar fields, nullability, or input shape; M2's minimum schema choices are provisional and must not settle later behavior.

**Deliverables:** Initial SDL, `books` query resolver returning a small in-memory list, GraphQL endpoint configuration, one GraphQLTester test, documentation of the known schema contract. (Completed and closed before M3.)

**Acceptance criteria:** Service starts and returns books through GraphQL; schema is discovered; GraphQLTester exercises the query and checks selected data/response structure. The in-memory source is explicitly transitional and is removed/replaced in M3. `books` must ultimately be paginated per A-03; pagination is deferred to M5 and need not be guessed prematurely. M2 does not add `Book.author`; M3 implements it according to the user-approved decision record and after M3 is explicitly authorized.

**Verification commands:** `./gradlew test`; `./gradlew bootRun` and submit a GraphQL request; enable GraphiQL with `spring.graphql.graphiql.enabled=true` only if useful for the demo. Record actual response.

**M2 implementation and verification record:** The M1 `Query.m1Placeholder` was replaced with `Query.books: [Book!]!`. SDL currently defines provisional `Book(id, title)`, `Author(id, name)`, and schema-only `BookInput(title)` fields; the sample values and nullability are not assignment-specified and remain provisional. `BooksQueryController` delegates to `InMemoryBookCatalog`, which returns two fixed sample records. The catalog is temporary and must be removed/replaced by Spring Data JPA/H2 in M3. No relationship field, mutation, JPA entity/repository, persistence path, pagination, or N+1 work was added. The relationship decision is now recorded as user-approved in the pre-M3 gate; implementation remains reserved for M3.

`./gradlew clean test --console=plain` completed with `BUILD SUCCESSFUL`; Gradle reports 2 tests, and both XML reports show 0 failures, 0 errors, 0 skipped. `BooksQueryTests` used `@AutoConfigureGraphQlTester` to submit `books { id title }` and assert the returned list and fields. `BooksAuthorsApplicationTests.contextLoads()` remains passing. This M2 root-query test does not satisfy A-10 (nested author in one request) or A-12 (`Book.author` field/resolver), which remain M3 acceptance criteria, and it is not A-09 N+1 query-count evidence, which remains M5. The initial test compilation lacked `spring-boot-test-autoconfigure`; adding that Spring Boot 3.5.16 BOM-managed test dependency resolved compilation. A sandboxed invocation failed to create Gradle's file-lock socket; the authorized invocation completed successfully.

## M3: Persistence and Book–Author relationships

**Entry gate:** The relationship decisions below were resolved by explicit user approval and were documented consistently before M3 implementation began. These are user-approved project design choices, not assignment-specified semantics. The gate is satisfied. M3 was separately authorized, implemented, verified, and approved.

**Scope:** Replace and remove M2's temporary in-memory data source with Book and Author JPA entities, the resolved association, Spring Data repositories, H2 configuration, nested GraphQL relationship resolution, and required `authors` query / `bookById(id)` query.

**Deliverables:** JPA mappings, Spring Data repositories, H2 configuration, services/resolvers, nested author mapping, H2 integration tests.

**Acceptance criteria:** M2's in-memory source is replaced by Spring Data JPA/H2 as the data source; implement the approved non-optional to-one Book-to-Author mapping as JPA `@ManyToOne(optional = false)` with a non-null foreign key; queries `bookById(id)` and `authors` work; SDL defines required singular `Book.author: Author!` and its resolver returns the associated Author (A-12); one GraphQL request selects a Book and nested Author data (A-10). Relationship behavior for `addBook`, `updateBook`, and `deleteBook` must match the approved semantics when those mutations are implemented in M4. Nested response correctness does not by itself verify N+1 prevention; that is tested in M5.

**Verification commands:** `./gradlew test`; include GraphQLTester tests for `bookById`, `authors`, and nested author selection plus persistence integration checks. Record output.

**M3 implementation and verification record:** Replaced and removed `InMemoryBookCatalog`; `books` now reads persisted entities through `BookRepository`. Added Book and Author JPA entities, repositories, and read-only query services. `Book.author` is implemented as the required singular GraphQL `Author!` field backed by `@ManyToOne(fetch = LAZY, optional = false)` and a non-null `author_id` foreign key; cascade remove is not configured. Added `bookById(id: ID!): Book` and `authors: [Author!]!`. Missing `bookById` returns null in this implementation, a provisional choice because the assignment does not specify not-found behavior. M2 scalar fields remain otherwise unchanged.

H2 is configured in-memory for development/test with Hibernate `create-drop`. No production seed records were added; GraphQL integration tests create persistence fixtures through the repositories. Tests cover `books` with nested authors, `bookById` with nested author, and `authors`; the existing context smoke test remains. `./gradlew clean test --console=plain` completed with `BUILD SUCCESSFUL`: 4 tests, 0 failures, 0 errors, 0 skipped. An initial test run found detached lazy Author proxies; the resolver now loads the associated Author by ID through a read-only service transaction.

The nested resolver currently loads Authors as relationship fields resolve. This verifies A-10/A-12 functionality, not A-09; batching and SQL-count proof remain M5. No mutations, pagination, or N+1 optimization/instrumentation were added. M3 and M4 were approved at closure; M5 implementation is recorded below.

## M4: Mutations, validation, and error handling

**Entry:** M3 has been approved. The following M4 contracts are user-authorized provisional project API decisions, not assignment-mandated signatures; the screenshots require the mutation names but do not prescribe these exact forms.

**Contracts:**

```graphql
type Mutation {
  addBook(input: BookInput!): Book!
  updateBook(id: ID!, input: UpdateBookInput!): Book!
  deleteBook(id: ID!): Boolean!
}

input BookInput { title: String!, authorId: ID! }
input UpdateBookInput { title: String, authorId: ID }
```

`addBook` creates a Book referencing an existing Author and never creates an Author; an unknown Author ID is rejected. `updateBook` rejects a missing Book. Omitted title or Author ID preserves the current value; supplied valid values update it. An unknown Author ID is rejected, and explicit `authorId: null` is rejected rather than clearing the required relationship. `deleteBook` rejects a missing Book, deletes only that Book, retains its Author, and returns `true` on success. Use standard Spring for GraphQL error handling for these specified error cases; do not introduce custom error codes or response formatting or leak stack traces.

Input binding must distinguish omitted values from explicit null. The implementation uses Spring GraphQL `ArgumentValue` on the relevant input properties, and GraphQLTester verifies the distinction. Resolver methods delegate persistence work to transactional services/repositories.

**Deliverables:** Mutation SDL, Kotlin input binding, resolver/controller methods, transactional mutation service methods, focused GraphQLTester tests, and implementation/test evidence in the three planning documents.

**Acceptance criteria:** All three signatures above are present and operational. Tests verify add success with existing Author and nested result, unknown Author rejection without Author creation, title update and omitted-title preservation, omitted-Author preservation, valid Author reassignment, explicit-null rejection, unknown Author rejection, unknown Book rejection for update/delete, delete success with Author retained, and `true` delete result. Existing `books`, `bookById`, `authors`, and nested `Book.author` query tests remain passing. Total automated test count remains at least three. Error responses use standard Spring for GraphQL handling without a custom format.

**Verification commands:** `./gradlew clean test --console=plain`; inspect test reports for actual counts/failures. Pagination and N+1 query optimization/measurement remain M5 and are not part of M4.

**M4 implementation and verification record:** Implemented the three provisional SDL signatures, input classes, controller mappings, and transactional service operations. `ArgumentValue` differentiates omitted update fields from explicit null; explicit null Author ID is rejected, and null title is rejected as an implementation constraint because the persisted field is non-null. New Book string IDs use generated UUID values because the input contract has no ID field. Standard Spring for GraphQL error handling is used without a custom error format; GraphQLTester verifies errors do not expose internal exception messages/class names.

Ten mutation integration tests cover add success and unknown Author rejection without creating Authors; title update while preserving omitted Author; Author reassignment while preserving omitted title; explicit-null Author/title and invalid Author rejection; missing Book on update and delete; and successful Book-only deletion with Author retention and a `true` result. Existing three query tests and context smoke test also pass. Final command: `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain` — `BUILD SUCCESSFUL`; 14 tests, 0 failures, 0 errors, 0 skipped. M4 was approved at closure. No M5 functionality was part of M4.

## M5: Pagination and N+1 prevention

**Entry:** M1–M4 are approved and closed. M5 is explicitly authorized. The pagination contract below is a provisional project-design choice, not a signature stated by the assignment.

**Provisional pagination contract:** `books(page: Int! = 0, size: Int! = 20): [Book!]!`; page is zero-based and nonnegative, size is 1–100 inclusive, and a valid page beyond the end returns an empty list. Invalid values use standard GraphQL error handling. Fetch with Spring Data `PageRequest` sorted by Book `id` ascending, so equal titles still have deterministic order.

**Scope:** Implement the required pagination on `books` and eliminate N+1 behavior on the books-with-authors path.

**Deliverables:** Chosen pagination contract; repository-side paginated fetch; stable ordering; batch relationship resolver, preferably `@BatchMapping` when compatible; focused tests. Implement repeatable SQL-count measurement using the existing Spring Data JPA/Hibernate and H2 setup. The selected test-only instrumentation is a Hibernate `StatementInspector` collector registered from test resources and enabled only around each GraphQL execution. Verify it captures emitted SQL under this setup; fixtures, schema startup, cleanup, and unrelated activity are outside the capture window. Classify Book-table SELECTs as root fetches and Author-table SELECTs as relationship loads.

**Acceptance criteria:** `books(page, size)` satisfies the provisional page/size contract, uses repository-side pagination and stable ID ordering, and passes first/subsequent/beyond-end/invalid-boundary plus adjacent-page no-duplicate tests. For A-09, compare at least two cases with `N_large > N_small`, using the same schema, GraphQL operation shape, selected relationship fields, H2 configuration, and equivalent fixture rules with comparable relationship coverage. Count SQL only during each GraphQL execution, excluding schema initialization, fixture setup, cleanup, and unrelated statements. Report root Book-fetch and Author relationship-loading counts separately. Let `ΔN = N_large - N_small` and `ΔR = R_large - R_small`. The implemented test passes only when `ΔN > 0` and `ΔR / ΔN < 0.5`; a ratio at or above 0.5 fails pending investigation. This is a practical acceptance threshold, not a universal proof that every possible N+1 issue is absent. A nested response alone proves A-10/A-12 functionality, not A-09.

**Verification commands:** `./gradlew clean test --console=plain`; run focused pagination and query-count integration tests. For each comparable N+1 case, record returned-book count (`N`), fixture-generation rule/relationship coverage, root Book-fetch SQL count, Author-relationship SQL count (`R`), and measurement interval/instrumentation. Calculate `ΔN` and `ΔR`; pass only when `ΔN > 0` and `ΔR/ΔN < 0.5`. A ratio at or above 0.5, or ambiguous/noisy evidence, fails pending investigation. This was the M0 plan; the implementation and recorded M5 measurement are documented below.

**M5 implementation and verification record:** `books` uses the provisional page/size SDL, repository-side `PageRequest`, and stable ascending ID order. Pagination tests cover first/next/beyond-end pages, invalid page/size values, equal-title ordering, and adjacent-page uniqueness. A test-only Hibernate `StatementInspector` is configured from test resources; a `ThreadLocal` collector captures only during GraphQL execution. Identical query shape, selected fields, H2 settings, and equivalent one-unique-Author-per-Book fixture rules were used for both sizes. Smaller run: `N_small=5`, root Book-fetch SQL=1, Author relationship SQL `R_small=1`; larger run: `N_large=20`, root Book-fetch SQL=1, `R_large=1`; unrelated captured SQL=0 for each. `ΔN=15`, `ΔR=0`, so the specific one-query-per-additional-Book pattern is absent and A-09 passes this practical check. This is not universal proof against all N+1 cases.

`./gradlew clean test --console=plain` (with the configured Java/Gradle temporary environment) completed with `BUILD SUCCESSFUL`: 18 tests, 0 failures, 0 errors, 0 skipped, including all M4 mutation and query regressions. M5 was approved and closed; M6 implementation and evidence follow below.

## M6: Automated tests and quality checks

**Scope:** Audit A-01 through A-12 against implementation and test evidence; strengthen focused coverage for empty query results, shared Authors, persistence constraints, safe errors, and isolation. Preserve API contracts, the user-approved relationship choices, and provisional pagination behavior.

**Deliverables:** Requirement coverage matrix; focused GraphQL/JPA tests; full-suite and N+1 evidence; remaining gaps documented without inventing assignment behavior.

**Acceptance criteria:** The matrix identifies implementation and test evidence for every A-01–A-12 requirement. A-09 query-count evidence, A-10 nested data in one request, and A-12 explicit `Book.author` field/resolver remain distinct. At least three automated tests exist; all tests pass from a clean build; error responses do not expose internal exception names, application package names, or stack frames. Assignment-unspecified behavior remains open. Security overview, subscriptions, coroutines, and client tooling are not baseline acceptance requirements.

**Verification command:** `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain` — `BUILD SUCCESSFUL`, 21 tests, 0 failures, 0 errors, 0 skipped (1 context, 6 query/persistence, 10 mutation, 4 pagination/N+1). A sandboxed first attempt could not create Gradle's file-lock socket and did not start the build; the same command succeeded with execution approval.

**Coverage audit:** See the A-01–A-12 matrix in `REQUIREMENTS_AND_DECISIONS.md`. In brief, A-02–A-10 and A-12 have implementation and test/evidence coverage; A-01 and A-11 remain partial until M7 executes the documented demo/review. `bookById` missing-ID semantics, extra fields, exact assignment error payloads, and the provisional pagination signature remain unspecified by assignment.

**Tests added/strengthened:** Three `BooksQueryTests` cases verify empty Books/Authors results, multiple Books resolving the same Author, and database rejection of a Book with an unknown Author FK. `BooksMutationTests.assertSafeGraphQlError` additionally rejects service not-found text, exception names, application package names, and stack-frame text. Existing tests establish per-test fixtures; M5 SQL capture remains execution-scoped and clears its `ThreadLocal` in `finally`.

**N+1 regression re-verification:** Same GraphQL query, selected relationship fields, H2 setup, and one-distinct-Author-per-Book fixture rule. Execution-only counts: N_small=5, root Book fetch=1, R_small=1; N_large=20, root Book fetch=1, R_large=1; unrelated statements=0 in both. ΔN=15, ΔR=0, so ΔR/ΔN=0, below the implemented 0.5 threshold. This is not universal proof of absence of every N+1 issue.

**M6 implementation and verification record:** The 21-test clean run passed. No production implementation, API contract, dependency, or relationship decision changed. M6 was approved and closed before M7.

## M7: Documentation and submission review

**Scope:** Align reviewer-facing documentation with implemented behavior and review the submission against the screenshot checklist. The Demo Guide provides run and demo instructions; a separate README was not required by the screenshot checklist and is absent.

**Deliverables:** Run/build instructions, GraphQL operation examples and final schema details, testing evidence, requirements traceability, final demo checklist.

**Acceptance criteria:** Reviewer can build, run, test, and demo the API; every mandatory checklist item has implementation and evidence; docs accurately describe actual behavior; final repository state is reviewed.

**Verification commands:** `./gradlew clean test`; run documented startup and GraphQL demo. Do not use Git commands unless Git has been intentionally initialized. Record all actual results.

**M7 implementation and rehearsal record:** Added `src/main/resources/application-demo.yml` and `demo-data.sql`. Only explicit `--spring.profiles.active=demo` enables SQL initialization; Hibernate schema creation is deferred before that initializer. H2 `MERGE INTO authors (id, name) KEY (id)` provisions `demo-author-001` / `Demo Author`. The default profile remains unchanged; `BooksAuthorsApplicationTests` explicitly uses the default profile and verifies that the seed is absent, while `DemoProfileSeedTests` verifies presence and reapplication without duplicates under demo. Added `DEMO_GUIDE.md` with exact startup/test commands, H2 settings, endpoint, valid operations, pagination behavior, SQL evidence, and lifecycle results. Verified Java 17.0.16 and Gradle wrapper 8.14.4. Final command `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain` exited 0 with `BUILD SUCCESSFUL`: 23 tests, 0 failures, 0 errors, 0 skipped (1 default-profile context/isolation, 2 demo-seed, 6 query/persistence, 10 mutation, 4 pagination/N+1). A sandbox attempt failed before startup because Gradle could not create its file-lock socket; the same command succeeded with execution approval.

`bootRun --args='--spring.profiles.active=demo' --console=plain` started on port 8080 and logged HTTP POST `/graphql`. The live sequence returned HTTP 200 throughout: `authors` returned `demo-author-001`; `addBook` returned Book ID `bd96e563-e66d-47b8-a6b0-51808e3dc675` and nested Author; `updateBook` changed the title while omitted `authorId` remained `demo-author-001`; `deleteBook` returned `true`; a final query showed Author retained, `books` empty, and `bookById` null. A separate persisted-Book `books { id title author { id name } }` query returned both Book and Author fields in one response. After stopping and restarting the process, `authors` returned exactly one seeded Author and `books` was empty. Both processes were stopped after checks.

The full M7 suite reran the M5 SQL comparison: N_small=5/root Book=1/Author=1, N_large=20/root Book=1/Author=1, ΔN=15, ΔR=0; ratio=0, below the implemented 0.5 threshold. This practical evidence is not universal proof. Final A-01–A-12 statuses are in `REQUIREMENTS_AND_DECISIONS.md`; all pass, with A-09 limited to the measured comparison. The live lifecycle confirmed Author retention after Book deletion. The demo-only seed is isolated to the `demo` profile. M7 is formally approved and closed; pagination remains a provisional project contract.

**Current test-result artifact observed during documentation reconciliation (not rerun):** the existing Gradle XML reports record 29 tests, 0 failures, 0 errors, and 0 skipped. This is separate from the historical M7 clean run of 23 tests above. M8's approved `SUBMISSION_INDEX.md` deliverable exists; no separate formal M8 closure approval is recorded. No implementation work is claimed for M8.
