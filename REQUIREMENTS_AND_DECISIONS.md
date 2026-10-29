# Requirements and Decisions

**Status:** M0–M7 are approved and closed. The approved M8 documentation deliverable, `SUBMISSION_INDEX.md`, is present; no separate formal M8 closure approval is recorded here. Requirements below are transcribed from the three training-plan screenshots supplied in this conversation. The screenshots give a project checklist and learning outcomes, but do not define a complete GraphQL SDL or detailed field constraints.

## Project scope

Build a small, demoable Books/Authors GraphQL API with Kotlin and Spring Boot/Spring for GraphQL. The mini-project checklist requires Book and Author types, input types, three named queries, three named book mutations, H2 persistence via Spring Data JPA, at least three automated tests, and no N+1 queries on the books-with-authors query.

The current user's project objective further fixes Java 17, Hibernate, Gradle Kotlin DSL, JUnit 5, and GraphQLTester. Those are current project constraints, not all textually specified in the screenshots. M0–M7 closure and M8 index-deliverable status are recorded below.

## Source and classification

The provided images show a document titled **“Kotlin + GraphQL: Training Plan”** and sections for Kotlin fundamentals, GraphQL concepts and Spring Boot setup, queries/mutations/relationships, testing/best practices/mini project, a mini-project checklist, and stretch goals. The checklist and hands-on labs are treated as project requirements/outcomes. “Topics to learn,” prerequisites, resource suggestions, and stretch goals are not silently promoted to mandatory API features.

## Explicit project requirements from the assignment

| ID | Requirement | Source wording / location | Classification |
| --- | --- | --- | --- |
| A-01 | Project is a small Books/Authors GraphQL API built with Kotlin, Spring Boot, and Spring for GraphQL. | Training Plan intro | Mandatory project scope |
| A-02 | GraphQL schema includes `Book` and `Author` types and input types. | Mini Project Checklist | Mandatory |
| A-03 | Provide `books` query, paginated. | Mini Project Checklist; pagination topic | Mandatory |
| A-04 | Provide `bookById(id)` query. | Mini Project Checklist; queries/mutations lab | Mandatory operation; `id` argument shown, return/null semantics unspecified |
| A-05 | Provide `authors` query. | Mini Project Checklist; queries/mutations lab | Mandatory operation |
| A-06 | Provide `addBook`, `updateBook`, and `deleteBook` mutations. | Mini Project Checklist | Mandatory operations; exact input/output shapes unspecified |
| A-07 | Persist using H2 via Spring Data JPA. | Mini Project Checklist; hands-on lab | Mandatory |
| A-08 | Include at least three automated tests. | Mini Project Checklist | Mandatory minimum; exact test cases unspecified |
| A-09 | Have no N+1 queries on the books-with-authors query. | Mini Project Checklist | Mandatory behavior |
| A-10 | Fetch nested Book–Author data in one GraphQL request. | Queries, Mutations and Relationships end-of-day checkpoint: “fetch nested author data in one request” | Mandatory end-to-end request capability |
| A-11 | A complete, tested Books/Authors API should be demoable. | Testing section end-of-day checkpoint | Mandatory outcome |
| A-12 | Define and resolve the explicit `Book.author` relationship field. | Queries, Mutations and Relationships hands-on lab: “Introduce an Author entity and resolve Book.author.” | Mandatory schema/resolver capability; cardinality and optionality are unspecified |

A-10 and A-12 are retained as distinct checks because the screenshots give them different source statements and scopes: A-12 checks that the explicit `Book.author` field is defined and resolved; A-10 checks that a client can fetch nested Book–Author data in one GraphQL request. A relationship test for A-12 may also exercise A-10, but the acceptance evidence should identify both the field-level resolution and the one-request nested selection. Neither check proves N+1 prevention; that is A-09 and is measured separately in M5.

## Other explicit user project constraints

These come from the current project objective, not the assignment screenshots:

| ID | Requirement | Classification |
| --- | --- | --- |
| U-01 | Use Java 17. | Technology constraint; user-selected within screenshot's JDK 17 or 21 option |
| U-02 | Use Gradle Kotlin DSL. | Technology constraint; screenshot says Kotlin/Gradle but does not specify DSL |
| U-03 | Use Hibernate. | Technology constraint; implied by JPA ecosystem but separately explicit in user's objective |
| U-04 | Use JUnit 5 and GraphQLTester. | Technology constraint; screenshots mention GraphQLTester and `@GraphQlTest`, not JUnit version |
| U-05 | Follow milestone-based implementation, actual verification, documented decisions, and meaningful Git history. | Workflow requirements from user |

## Recommended engineering practices in the screenshots

These are training topics or lab directions; apply where useful, but distinguish their exact scope from the checklist:

- Use Spring Web and Spring for GraphQL in the generated Spring project; place schema at `src/main/resources/graphql/schema.graphqls`.
- Start with a `Book` type and `books` query backed by an in-memory list, then replace the list with JPA/H2.
- Use `@QueryMapping`, `@MutationMapping`, and `@Argument`; use an input type for `addBook`.
- Resolve nested Book-to-Author data using `@SchemaMapping`.
- Add basic validation and error handling; the testing section calls for input validation and consistent error responses.
- Write tests for one query and one mutation; use GraphQLTester and `@GraphQlTest` (the checklist separately sets a minimum of three automated tests).
- Fix an N+1 case using `@BatchMapping`; the acceptance evidence must measure relationship query counts as the number of books increases, rather than merely assert nested response data.
- Add pagination to `books`; the training topic allows offset or cursor style.
- Enable GraphiQL with `spring.graphql.graphiql.enabled=true` for first-query exploration, if desired for the demo.

The screenshots' learning outcomes do not specify exact validation constraints, error body shape, or pagination argument names. The user-authorized provisional M4 error handling and M5 pagination contract are design decisions, not assignment requirements; other details remain undecided.

## Optional enhancements / stretch goals

The screenshot explicitly labels these “Stretch Goals”; they are not required for the mini-project checklist:

- Kotlin coroutines with GraphQL resolvers.
- Apollo Kotlin as a GraphQL client.
- GraphQL subscriptions.
- Alternative stack: Ktor with KGraphQL.

Spring Security is an overview topic, not a mini-project checklist requirement. Authentication/security is therefore not in the required project scope unless separately requested.

## Functional and non-functional requirements

### Functional

- Query a paginated collection of books (`books`).
- Query an individual book by `id` (`bookById(id)`).
- Query authors (`authors`).
- Add, update, and delete books (`addBook`, `updateBook`, `deleteBook`).
- Represent Book and Author in the GraphQL schema and allow nested author data to be fetched with a book in one request.
- Persist book/author data with H2 and Spring Data JPA.

### Non-functional / quality

- Kotlin/Spring Boot/Spring for GraphQL application; Java 17, Gradle Kotlin DSL, Hibernate, JUnit 5 and GraphQLTester as additionally required by the user objective.
- At least three automated tests.
- Avoid N+1 query behavior on books-with-authors.
- Application is complete, tested, and suitable for a demo.
- Exact performance threshold, deployment environment, security, and production readiness are not specified.

## Acceptance criteria

### Assignment-level acceptance

1. Schema defines `Book`, `Author`, and relevant input types.
2. GraphQL exposes `books` with pagination, `bookById(id)`, and `authors`.
3. GraphQL exposes `addBook`, `updateBook`, and `deleteBook`.
4. Book/Author records are persisted using Spring Data JPA and H2.
5. A single GraphQL request can fetch books and their nested authors.
6. Books-with-authors resolution does not produce N+1 queries; the screenshot specifically suggests fixing this with `@BatchMapping`.
7. At least three automated tests exist; recommended explicit coverage includes one query and one mutation using GraphQLTester/`@GraphQlTest`.
8. The API can be run and demonstrated as a complete tested project.

### Not specified by the assignment screenshots

Exact Book/Author scalar fields; identifier type; relationship cardinality; input fields; update/delete return values; pagination arguments and response wrapper; sort/filter behavior; field validation constraints; error payload/codes; sample data; endpoint route; test coverage percentage; packaging requirements.

## Assumptions and unresolved questions

### Assumptions used only to plan

- `bookById(id)` takes an argument literally named `id`, as shown in the screenshot; the GraphQL scalar type is unknown.
- Because the checklist names only book mutations, author creation/update/deletion mutations are not required absent further source material.
- The user-selected Java 17 resolves the screenshot's “version 17 or 21” option for this project.
- Training topics and hands-on progression may inform architecture, but their pedagogical details are not all final API requirements (for example, the initial in-memory list is a learning step, not the final persistence requirement).

### Unresolved questions (do not invent answers)

1. What other fields belong to `Book` and `Author`, and what are their GraphQL/JPA types and nullability? The approved relationship field is `Book.author: Author!`; all other field details remain open.
2. Assignment signatures and error shapes remain unspecified; the user has authorized provisional M4 contracts recorded below. Those choices are implementation contracts, not assignment requirements.
3. Other validation constraints, error codes/extensions/messages, and behavior not covered by the M4 contract below remain unspecified. Standard Spring for GraphQL error handling is authorized for the specified invalid-reference/null/missing-Book cases; exact error payload details remain unspecified.
4. The assignment leaves pagination arguments and result shape unspecified. The user authorizes a provisional M5 page/size contract documented below; it is not an assignment-mandated signature.
5. What additional validation constraints and exact error payload fields/codes, if any, does an evaluator expect beyond M4’s user-authorized standard Spring for GraphQL error handling?
6. What should the three-or-more automated tests cover beyond the recommended one query and one mutation?
7. Does the evaluator require a specific Gradle/Java/Spring version, endpoint, additional handoff documentation beyond the Demo Guide and Submission Index, or sample/demo data?

## Requirements traceability matrix

| Requirement | Planned design location | Planned verification evidence |
| --- | --- | --- |
| A-01 | M1 project/build foundation; M2–M5 implement the Books/Authors API features; M7 final demo and submission evidence | M1 wrapper/build and resolved dependency evidence verifies the technology foundation only; GraphQL schema/operation, persistence, relationship, mutation, pagination, and N+1 evidence for the API is traced individually under A-02–A-10 and A-12 in later milestones; M7 documents and exercises the completed API demo |
| U-01–U-04 | Build configuration and app architecture (M1); stack recorded in docs | Wrapper/build output, dependency report, Java 17 runtime check |
| A-02 | GraphQL SDL and domain/API model (M2/M3) | Schema inspection and GraphQLTester type/operation execution |
| A-03 | Paginated books query (M5) | GraphQLTester page/boundary, validation, stable-ordering, and no-duplicate assertions using Spring Data paging; provisional contract and M5 evidence below |
| A-04–A-05 | `bookById(id)` and `authors` resolvers (M3) | GraphQLTester success tests cover both operations; missing-Book semantics for `bookById` remain unspecified (current implementation returns null) |
| A-06 | Mutation SDL/resolvers and transactional service layer (M4) | GraphQLTester success/error cases for the user-authorized provisional signatures and persistence/relationship effects; test contracts are recorded under M4 below |
| A-07 | JPA mappings, repositories, H2 config (M3) | Persistence integration tests against H2 |
| A-08 | Test suite (M3–M6) | M6 clean test run: 21 tests, 0 failures, 0 errors, 0 skipped; exceeds the required minimum of three. Inventory and suite breakdown are in the M6 record. |
| A-09 | Relationship loading implementation (M5) | Recorded M5 execution-only SQL comparison: (N_small=5, root=1, R_small=1), (N_large=20, root=1, R_large=1), ΔN=15, ΔR=0. The current test criterion is ΔR/ΔN < 0.5; the recorded ratio is 0. This practical check is not universal proof. |
| A-10 | Nested Book–Author selection in one GraphQL request (M3) | M3 GraphQLTester request selects a book and nested author data in that same request. The M2 `books { id title }` test does not satisfy A-10. |
| A-12 | Explicit `Book.author` schema field and resolver (M3, after the resolved relationship gate) | SDL defines `Book.author: Author!`; GraphQLTester selects and resolves the field in the persisted-books query. The M2 schema/test does not satisfy A-12. |
| A-11, U-05 | Demo and verification documentation, final review (M7) | The Demo Guide and Submission Index provide run/demo steps and traceability; the M7 live rehearsal and verification evidence are recorded below. A separate README is not present or required by the screenshots. |

### M6 requirement coverage audit

| Requirement | Implementation evidence | Automated / other evidence | Status | Remaining gap |
| --- | --- | --- | --- | --- |
| A-01 | Java 17 Kotlin/Spring GraphQL foundation and Books/Authors API implemented across M1–M5 | M1 toolchain/build record; 21-test clean M6 suite; M7 demo remains future work | Partially covered | M7 must document and run the demo procedure. |
| A-02 | `schema.graphqls` defines Book, Author, and input types | GraphQLTester exercises query and mutation fields and input binding | Covered | No further fields/signatures are specified by the assignment. |
| A-03 | Repository-paged `books(page,size)` with stable ID ordering | M5 GraphQLTester first/next/beyond-end, invalid-bound, stable-order, and adjacent-page uniqueness assertions | Covered | Pagination signature remains a provisional project choice. |
| A-04 | `bookById(id)` resolver | `BooksQueryTests.bookById returns the persisted book and its author` | Covered | Missing-ID behavior is unspecified; current implementation returns null. |
| A-05 | `authors` resolver | Query test plus empty-result test | Covered | No author filtering/details are specified. |
| A-06 | Three named Book mutation resolvers and transactional service | 10 `BooksMutationTests` cover success and specified provisional error/relationship effects | Covered | Exact signatures are user-authorized provisional choices, not assignment requirements. |
| A-07 | JPA entities/repositories, H2, non-null Book-to-Author foreign key | Persistence queries, shared-Author test, and `saveAndFlush` test rejecting unknown Author FK | Covered | H2 `create-drop` is a project choice; deployment storage is unspecified. |
| A-08 | JUnit 5 and GraphQLTester suite | Clean run: 21 tests, 0 failures/errors/skips (1 context, 6 query/persistence, 10 mutation, 4 pagination/N+1) | Covered | No test percentage or exact case inventory is assignment-mandated. |
| A-09 | `@BatchMapping` nested Author resolution | Execution-only SQL comparison: N=5/root=1/R=1; N=20/root=1/R=1; ΔN=15/ΔR=0 | Covered | Practical comparison, not universal proof against every N+1 case. |
| A-10 | Nested Book–Author selection in one GraphQL request | `BooksQueryTests.books query returns persisted books and nested authors` | Covered | Nested data proves functionality, not A-09. |
| A-11 | Complete tested API intended to be demoable | All required API operations exist and the clean test suite passed at M7 | Partially covered at M6; superseded by M7 Pass | The historical M6 status preceded the M7 live demo and final review. |
| A-12 | SDL `Book.author: Author!` and resolver | Schema inspection plus GraphQLTester selecting and resolving `author` | Covered | Field/resolver evidence is distinct from A-09 SQL-count evidence. |

**Test isolation and error review:** Stateful integration-test classes create fixtures in `@BeforeEach`; fixture setup clears Books before Authors and repopulates required rows. The M5 SQL collector activates only around GraphQL execution and is removed in `finally`. Mutation error assertions reject service not-found messages, exception names, the application package name, and stack-frame text. The clean suite passes together; no test relies on rows left by another test.

**M6 implementation and verification record:** Added three focused tests for empty Books/Authors results, multiple Books resolving the same Author, and H2 foreign-key rejection when a Book references an unknown Author. Strengthened mutation error assertions against internal exception/package/stack-frame leakage. No production code or API contract changed. Exact command: `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain` — `BUILD SUCCESSFUL`, 21 tests, 0 failures, 0 errors, 0 skipped. A-09 was re-run: N_small=5/root=1/R_small=1, N_large=20/root=1/R_large=1, ΔN=15/ΔR=0; the practical check passes. M6 was approved and closed before M7.

### M7 demo rehearsal and final traceability

`DEMO_GUIDE.md` documents Java 17/Gradle wrapper usage, default and demo-profile startup, H2 settings and reset, the actual `/graphql` endpoint, schema operations, pagination, mutation lifecycle, and M5 SQL evidence. Demo-only SQL seeding is enabled by `application-demo.yml` and `demo-data.sql`; the base configuration is unchanged. The default-profile test confirms the demo Author is absent, while `DemoProfileSeedTests` confirms demo-profile seeding and idempotent reapplication. The configured Java 17 / Gradle 8.14.4 clean test command exited 0: 23 tests, 0 failures, 0 errors, 0 skipped. `bootRun --args='--spring.profiles.active=demo'` started on port 8080. The full live lifecycle, nested Books query, and reset were rehearsed; every HTTP request returned 200, and the application was stopped afterward.

| Requirement | M7 status | Evidence / remaining limitation |
| --- | --- | --- |
| A-01 | Pass | Java 17/Kotlin/Spring GraphQL stack, persistence, operations, clean suite, demo-profile startup, and successful live mutation lifecycle verified. |
| A-02 | Pass | SDL defines Book, Author, and inputs; query and mutation tests exercise those types. |
| A-03 | Pass | Pagination tests cover bounds, stable order, page separation, and empty pages; provisional page/size contract is clearly labeled. |
| A-04 | Pass | `bookById` successful lookup is covered by GraphQLTester; missing-ID behavior remains unspecified by the assignment. |
| A-05 | Pass | `authors` query is covered by tests; live demo profile returned the seeded Author, and live default-profile rehearsal returned an empty list. |
| A-06 | Pass | GraphQLTester coverage plus live `addBook` → `updateBook` → `deleteBook` sequence using the actual seeded Author and returned Book ID. |
| A-07 | Pass | H2/JPA persistence, required foreign key, shared Author, and invalid Author FK are tested. |
| A-08 | Pass | Final clean run: 23 tests; 0 failures, 0 errors, 0 skipped. |
| A-09 | Pass (practical comparison) | Current M5 test evidence: N=5/root=1/R=1 and N=20/root=1/R=1; ΔN=15/ΔR=0. Not universal proof. |
| A-10 | Pass | GraphQLTester and a live paginated `books { id title author { id name } }` request returned persisted Book and Author data in one request. |
| A-11 | Pass | Guide rehearsed from demo-profile startup through seed query, create/update/delete, Author-retention check, and process restart/reset. |
| A-12 | Pass | SDL contains `Book.author: Author!`; `@BatchMapping` resolves it and GraphQLTester verifies persisted nested data. |

**M7 verification and closure record (historical milestone result):** The configured command `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain` exited 0 with `BUILD SUCCESSFUL`: 23 tests, 0 failures, 0 errors, 0 skipped (1 default-profile context/isolation, 2 demo-seed, 6 query/persistence, 10 mutation, 4 pagination/N+1). A sandbox attempt failed before build startup because Gradle could not create its file-lock socket; the same command succeeded with execution approval. Demo-profile `bootRun` logged HTTP POST `/graphql`. Live requests verified Author query, nested Book creation, title update with omitted `authorId`, deletion returning `true`, Author retention, empty Books, and `bookById: null` after deletion. A separate live nested Books query returned a persisted Book and Author in one HTTP 200 response. Restarting the demo profile returned exactly one seeded Author and no Books, verifying reset/reseed. The suite rechecked the M5 SQL comparison: N=5/root=1/R=1 and N=20/root=1/R=1, ΔN=15/ΔR=0. M7 is formally approved and closed by the user. The demo-only Author seed remains isolated to the explicitly activated `demo` profile. A-01 through A-12 have the M7 statuses and evidence in the table above; A-09 is a practical measured comparison, not universal proof. Pagination remains a provisional project contract.

**Current test-result artifact observed during the documentation review (not rerun):** the existing Gradle XML reports contain 29 tests, 0 failures, 0 errors, and 0 skipped in total. This is a reading of the present artifacts, not a new Gradle invocation; the 23-test M7 result above remains the historical M7 milestone result.

**M8 documentation deliverable:** `SUBMISSION_INDEX.md` was created under the approved narrow M8 scope and links to existing implementation and evidence. The project records do not show a separate formal M8 closure approval. No application implementation is claimed for M8.

## Engineering decisions (proposed)

| Decision | Rationale | Status |
| --- | --- | --- |
| Use Spring GraphQL schema-first with SDL under `src/main/resources/graphql/`. | The training lab explicitly specifies `schema.graphqls` at this location and the Spring schema-first flow. | Proposed |
| Use thin GraphQL mapping/controller methods delegating to service and Spring Data repository layers. | Supports the named mapping annotations and keeps persistence/business logic testable. | Proposed |
| Use JPA Book and Author entities with H2; implement the user-approved required Book-to-Author association and keep other unspecified fields open. | Meets explicit persistence requirement and follows the recorded relationship decision without guessing unrelated domain details. | Required persistence; relationship decision approved; other model details open |
| Implement the required query and mutation names exactly; defer their full signatures where screenshots are silent. | Preserves the checklist contract while avoiding invented fields and return types. | Names mandatory; signatures open |
| Implement `books(page, size)` using Spring Data JPA paging with stable `id` ascending order. | The assignment requires pagination but leaves its exact contract open; page/size maps directly to `PageRequest` without loading all Books or inventing a cursor wrapper. | Provisional project decision; A-03 behavior mandatory |
| Investigate `@BatchMapping` for nested author resolution and prove the N+1 requirement with a focused test. | The checklist requires no N+1 on books-with-authors and lab says to fix using `@BatchMapping`. | Proposed approach; behavior mandatory |
| Require a documented, user-approved relationship decision before M3. | Entity mapping and mutations depend on Book–Author cardinality, optionality, and relationship update semantics; do not infer them from conventions. | Gate resolved by explicit user approval recorded below; keep the decision record consistent before M3 starts |
| Use JUnit 5 and GraphQLTester as set by the current user; include at least three automated tests. | Meets user stack constraints and assignment checklist minimum. | Required |
| Do not add security, subscriptions, coroutines, or Apollo/Ktor client work to baseline scope. | Security is only an overview topic; the other items are labeled stretch goals. | Scope decision |

## Environment and Git findings from M0

- Workspace `/Users/ajaypatil/kotlin` was empty at initial inspection; the three planning docs are now present.
- Java 17.0.16 is available; system Gradle and a wrapper are absent.
- Git is not initialized. No existing Git history/status exists.
- Assignment screenshots are supplied in this conversation; no copies were available in the workspace during the initial inspection.
- No code, dependencies, Git repository, or commit were created as part of this review.

Suggested commit message once the user later authorizes Git setup/commit workflow: `docs: capture assignment requirements and project plan`. No commit has been made.

## M1 compatibility and M3 requirement gates

- **M1 dependency compatibility gate:** before selecting versions, verify and record a compatible combination of Java 17, Kotlin, Spring Boot, and Gradle (including the Gradle wrapper distribution and Kotlin/Spring plugins). Confirm the selected toolchain is supported by that combination and demonstrate it with actual wrapper/build output in M1. Do not choose versions from memory or claim compatibility without evidence.
- **Pre-M3 relationship-resolution gate — RESOLVED by explicit user approval:** Keep this decision record present and consistent in the architecture and backlog before M3 implementation begins. The screenshots require nested author data and `Book.author` but do not define these semantics; the following are user-approved project design decisions, not assignment requirements:
  1. **Cardinality:** Each Book has exactly one Author; an Author may be associated with multiple Books.
  2. **Optionality:** Every Book must have an Author.
  3. **GraphQL implication:** `Book.author` is a required singular `Author` field (`Author!`). Exact input type names, field names, and other scalar details remain open unless stated here.
  4. **Persistence implication:** Map Book to Author as JPA `@ManyToOne(optional = false)` and plan a non-null foreign key from Book to Author. An Author can be referenced by multiple Books.
  5. **`addBook`:** Accept an existing Author ID and reject invalid/nonexistent references; do not create Authors as part of `addBook`.
  6. **`updateBook`:** Permit Book title updates and changing the Author by existing Author ID. If the Author ID field is omitted, preserve the current association. Clearing is not allowed; explicit null must not clear or be treated as omission. The exact error payload is unspecified.
  7. **`deleteBook`:** Delete only the Book; retain its Author and do not cascade Book deletion to the Author.
  8. **Future tests:** Verify required nested `Book.author` resolution, persistence of the non-null association, rejection of invalid Author references, preservation when the update Author ID is omitted, association change with a valid existing ID, rejection of clearing, and Book deletion with Author retention. Keep A-10 nested response coverage separate from A-09's M5 SQL-count evidence.

  **Rationale:** The approved model has one required Author per Book and allows an Author to be reused by Books, which maps from Book to Author as `@ManyToOne`; the non-null foreign key enforces the required association. Referencing existing Authors avoids implicit Author creation, omission-preserves-current supports title-only updates, and Book-only deletion avoids deleting an Author that may be shared.

The relationship decision gate was satisfied before M3 and remains the implementation source of truth. M3 has been approved; do not change these approved decisions silently.

## M4 user-authorized provisional mutation contracts

These contracts were explicitly authorized by the user for implementation in M4. They are provisional project API decisions, not signatures mandated by the assignment screenshots. The assignment still requires the mutation names, but does not specify their exact SDL signatures or return shapes.

```graphql
type Mutation {
  addBook(input: BookInput!): Book!
  updateBook(id: ID!, input: UpdateBookInput!): Book!
  deleteBook(id: ID!): Boolean!
}

input BookInput {
  title: String!
  authorId: ID!
}

input UpdateBookInput {
  title: String
  authorId: ID
}
```

- `addBook` creates a Book referring to the existing Author ID and never creates an Author. Unknown Author IDs are rejected through standard Spring for GraphQL error handling.
- `updateBook` rejects an unknown Book ID. Omitted title or author ID preserves the current value; a supplied title or existing Author ID updates it. Unknown Author IDs are rejected. Explicit `authorId: null` is rejected as a GraphQL mutation error and never clears the required relationship.
- `deleteBook` rejects an unknown Book ID, deletes the Book only, retains its Author, and returns `true` after successful deletion.
- Spring GraphQL `ArgumentValue` is used on update input properties to distinguish omitted values from explicit null; GraphQLTester verifies the behavior. Use standard Spring for GraphQL error handling only, without custom error codes/formatting or leaked stack traces.
- Preserve `Book.author: Author!`, non-optional JPA `@ManyToOne`, and the non-null foreign key. Mutations delegate to transactional services and repositories.

Other assignment details remain open: unrelated fields and constraints, evaluator requirements beyond the provisional pagination contract (the screenshots do not mandate its signature), exact error payload fields, and any behavior outside these provisional cases. M4 acceptance requires focused GraphQL tests for each success/error behavior above, relationship persistence effects, and regression coverage for existing queries. Pagination and N+1 optimization/measurement remain M5.
## M5 user-authorized provisional pagination and measurement plan

The assignment requires a paginated `books` query but does not prescribe its argument names, defaults, bounds, ordering, or return wrapper. The following is a provisional project-design contract, authorized for M5 and not an assignment-mandated signature:

- SDL: `books(page: Int! = 0, size: Int! = 20): [Book!]!`.
- `page` is zero-based and must be nonnegative. `size` must be from 1 through 100 inclusive. Invalid values use standard GraphQL handling; valid pages beyond the collection return an empty list.
- Use Spring Data JPA `PageRequest` and stable ascending Book `id` ordering. The query returns only the requested page; it does not fetch all Books and slice the collection in memory.
- Tests cover the first page, a subsequent page, empty pages beyond the end, negative page, invalid size bounds, duplicate titles with stable ID order, and adjacent pages without duplicate IDs for an unchanged fixture.

For the M5 N+1 comparison, select a test-only Hibernate `StatementInspector` collector rather than adding a runtime dependency. Capture SQL only while each identical GraphQL query executes, after fixture setup and before cleanup. Classify Book-table SELECTs as root fetches and Author-table SELECTs as relationship loads. Verify the collector against actual GraphQL/H2 execution and report raw counts. No measurements are claimed until the M5 tests run.

- **N+1 evidence rule:** nested response data proves relationship functionality only. A-09 acceptance separately requires a repeatable query-count comparison across different books-result sizes under the same schema, GraphQL query shape, selected relationship fields, H2 configuration, and comparable fixtures. Count SQL only during GraphQL query execution, excluding fixture setup, cleanup, schema initialization, and unrelated statements; report root Book-fetch statements separately from Author-relationship loading statements. The implemented M5 test passes only when `ΔN > 0` and `ΔR / ΔN < 0.5`; ambiguous, noisy, or higher-ratio results fail pending investigation. The test-only Hibernate `StatementInspector` collector was selected and verified for M5; the recorded measurement appears below.

## M1 implementation record

### Selected toolchain and versions

| Component | Selected version | Rationale / evidence |
| --- | --- | --- |
| Java runtime and toolchain | Java 17; observed runtime `17.0.16` | Spring Boot 3.5.16 requires Java 17 or newer; Java and Kotlin toolchains are both set to 17. |
| Kotlin Gradle plugins (`jvm`, `plugin.spring`) | `2.2.21` | Spring Boot 3.5 requires at least Kotlin 1.7.x and aligns Kotlin libraries with the Kotlin plugin. Kotlin Gradle Plugin 2.2.21 supports Gradle through 8.14; the selected wrapper is Gradle 8.14.4. |
| Spring Boot Gradle plugin and dependency BOM | `3.5.16` | Official system requirements support Java 17 and Gradle 8.4+; Gradle 8.14.4 is within the supported Gradle 8.x line. |
| Gradle wrapper | `8.14.4` | Compatible with Java 17, Spring Boot 3.5.16's Gradle requirement, and Kotlin Gradle Plugin 2.2.21's Gradle 8.14 compatibility. Official distribution SHA-256 is pinned in `gradle-wrapper.properties`. |

Compatibility rationale was checked against the [Spring Boot 3.5 system requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html), [Spring Boot Kotlin support](https://docs.spring.io/spring-boot/3.5/reference/features/kotlin.html), [Kotlin Gradle configuration/compatibility table](https://kotlinlang.org/docs/gradle-configure-project.html), and [Gradle 8.14.4 compatibility information](https://docs.gradle.org/8.14.4/userguide/compatibility.html). The selected combination also resolved and built in this workspace; actual output is recorded in `TESTING_AND_BACKLOG.md`.

### Selected dependencies

The build uses Spring Boot's `3.5.16` BOM to manage dependency versions. Direct application dependencies are Spring Boot Web `3.5.16` (HTTP/MVC runtime), Spring Boot GraphQL `3.5.16`, Spring Boot Data JPA `3.5.16` (Spring Data JPA and Hibernate), Kotlin reflection `2.2.21`, and H2 `2.3.232` at runtime. The test scope uses `spring-boot-test` and `spring-boot-test-autoconfigure` `3.5.16`, Spring Test `6.2.19`, JUnit Jupiter `5.12.2`, JUnit Platform Launcher `1.12.2`, and Spring GraphQL Test `1.4.6` for GraphQLTester. Versions other than plugin/BOM versions are the actual versions shown by the resolved Gradle test-runtime dependency report, not independently pinned build versions.

No dependency-management plugin, JPA compiler plugin, GraphiQL, security, client, or other optional dependency was added. M1 initially included a temporary `Query.m1Placeholder` solely for startup; M2 replaced it with the `books` field. The added `spring-boot-test-autoconfigure` test dependency is used to configure GraphQLTester for the M2 integration test and is managed by the Spring Boot 3.5.16 BOM.

### Verification evidence

- `java -version`: OpenJDK `17.0.16`.
- Official Gradle 8.14.4 distribution SHA-256 matched `f1771298a70f6db5a29daf62378c4e18a17fc33c9ba6b14362e0cdf40610380d`.
- `./gradlew --version`: wrapper downloaded/verified Gradle `8.14.4`; reported launcher JVM `17.0.16`.
- `./gradlew clean test`: `BUILD SUCCESSFUL`; one application-context smoke test, zero failures/errors.
- `./gradlew bootRun --console=plain`: Spring Boot `3.5.16` started on Java `17.0.16`; logs confirmed embedded Tomcat on port 8080, H2 in-memory datasource, Hibernate ORM `6.6.53.Final`, GraphQL schema loading, and HTTP POST `/graphql`. Process was stopped after startup was observed.
- `./gradlew dependencies --configuration testRuntimeClasspath --console=plain`: `BUILD SUCCESSFUL`; confirmed managed dependency versions listed above, including Spring GraphQL `1.4.6`, Spring Data JPA `3.5.13`, H2 `2.3.232`, Hibernate `6.6.53.Final`, and JUnit Jupiter `5.12.2`.

## M2 implementation record

- Replaced `Query.m1Placeholder` with schema-first `Query.books: [Book!]!`, resolved by `BooksQueryController` from the temporary `InMemoryBookCatalog`.
- Added GraphQL `Book` (`id: ID!`, `title: String!`), `Author` (`id: ID!`, `name: String!`), and `BookInput` (`title: String!`) type definitions. The sample scalar fields, nullability, list nullability, and input shape are provisional M2 scaffolding because the screenshots do not define them. The input type is schema-only; no mutation is implemented.
- The catalog returns two fixed sample books. It is explicitly temporary and must be removed/replaced by Spring Data JPA/H2 in M3.
- `Book.author` is intentionally not declared or resolved in M2; its implementation is reserved for M3. The user-approved relationship semantics are recorded in the pre-M3 decision gate below; no relationship implementation has begun.
- `./gradlew clean test --console=plain` completed with `BUILD SUCCESSFUL`: 2 tests, 0 failures, 0 errors, 0 skipped (the M1 application-context smoke test and the M2 GraphQLTester query test). The GraphQLTester test selected `books { id title }` and matched both returned book records.
- The first test compilation attempt exposed the missing `spring-boot-test-autoconfigure` test dependency; after adding that M1-BOM-managed test module, the final clean test run passed. A sandboxed Gradle attempt also could not create its file-lock socket; the authorized Gradle invocation completed successfully.
- No M3 functionality was part of the M2 milestone; M3 implementation is recorded below. No M4 mutations were part of the M2 milestone; M5 pagination or N+1 optimization, and later functionality, were not introduced in M2.

## M3 implementation record

- Replaced and removed `InMemoryBookCatalog`; `books` now reads persisted `Book` entities through `BookRepository` and a read-only query service. Added `Author`/`Book` JPA entities and Spring Data repositories.
- Implemented the approved Book-to-Author relationship as `@ManyToOne(fetch = LAZY, optional = false)` with `@JoinColumn(name = "author_id", nullable = false)`. The entity mapping and generated H2 schema enforce a non-null Book-to-Author foreign key. No cascade remove is configured.
- Added the explicit `Book.author: Author!` SDL field and `@SchemaMapping` resolver. The resolver loads the referenced Author by the foreign-key ID through the read-only Author query service.
- Added the `bookById(id: ID!): Book` and `authors: [Author!]!` query fields. The nullable `bookById` result for a missing ID is an implementation choice because assignment not-found behavior is unspecified; the field signature remains subject to evaluator requirements. No mutations or pagination were added.
- Configured development/test persistence to use H2 in-memory at `jdbc:h2:mem:books_authors` with Hibernate `create-drop`. This is a project configuration choice; the screenshots do not mandate H2 storage mode or schema lifecycle. No production seed data was added; integration tests persist fixtures before querying.
- Updated GraphQLTester coverage to verify persisted `books` with nested authors, `bookById` with its nested author, and `authors`. Together with the application-context smoke test, the suite has four tests.
- `./gradlew clean test --console=plain` completed with `BUILD SUCCESSFUL`: 4 tests, 0 failures, 0 errors, 0 skipped. The first run exposed detached lazy Author proxies; the field resolver was changed to fetch the referenced Author within a read-only service transaction, and the final run passed.
- This resolver currently loads Author records separately by ID as nested fields resolve. It proves nested relationship behavior (A-10/A-12) but is not N+1 prevention evidence; batching and SQL-count measurement remain M5 work (A-09).
- No mutation behavior, add/update/delete resolvers, pagination, or N+1 optimization/instrumentation was added. M3 has been approved; M4 implementation is recorded in the M4 section below, and M5/later remain unstarted.

Gradle was not installed system-wide and was absent before M1. The wrapper was generated using a temporary official Gradle 8.14.4 distribution, then verified through the wrapper. The initial sandboxed network attempt was blocked; the authorized distribution/checksum and Gradle resolution then completed successfully. No unresolved version-compatibility issue remains for the selected M1 build. A Spring Boot startup warning notes that `spring.jpa.open-in-view` defaults to enabled; no change was required for the M1 smoke test.


## M4 implementation and verification record

- Added the user-authorized provisional mutation signatures to the SDL: `addBook(input: BookInput!): Book!`, `updateBook(id: ID!, input: UpdateBookInput!): Book!`, and `deleteBook(id: ID!): Boolean!`. `BookInput` contains required `title` and `authorId`; `UpdateBookInput` contains optional nullable `title` and `authorId`. These signatures are project decisions, not assignment-specified signatures.
- Mutation resolver methods delegate to transactional `BookMutationService` operations. `addBook` and `updateBook` resolve an existing Author; no Author creation path exists. Unknown Author and Book IDs fail without committing partial changes. `deleteBook` deletes only the Book, retains the Author, and returns `true`.
- The entity uses the existing string GraphQL ID contract; because `addBook` does not accept an ID, new IDs are generated as UUID strings. This is an implementation choice, not an assignment requirement. Explicit null `authorId` is rejected. Explicit null `title` is also rejected because the persisted title is non-null; assignment behavior for that case is unspecified.
- Spring GraphQL `ArgumentValue` is used for optional update input properties. GraphQLTester confirms omitted `authorId` preserves the relationship while explicit `authorId: null` is distinguishable and rejected. Errors use default Spring for GraphQL handling; tests confirm internal exception messages/class names are not returned. No custom error format or codes were added.
- Added ten mutation integration tests for add success and invalid Author; title update with omitted Author; Author reassignment with omitted title; explicit-null Author/title and invalid Author rejection; missing Book on update/delete; and Book deletion with Author retention. Existing query regression tests remain included.
- Final command: `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain` — `BUILD SUCCESSFUL`; 14 tests, 0 failures, 0 errors, 0 skipped (1 application-context test, 3 query tests, 10 mutation tests).
- M4 was approved and closed. M5 implementation and evidence are recorded below; M6 and later remain unstarted.


## M5 implementation and verification record

- Added the provisional `books(page: Int! = 0, size: Int! = 20): [Book!]!` contract. Page is zero-based and nonnegative; size is 1–100; valid out-of-range pages return an empty list. The query uses `PageRequest` and ascending Book ID ordering, so pagination is database-side and deterministic. These signatures/bounds are project choices, not assignment-specified API details.
- Replaced per-Book Author resolution with Spring GraphQL `@BatchMapping`; all existing nested Book–Author query tests remain passing.
- SQL instrumentation is a test-only Hibernate `StatementInspector` configured through `src/test/resources/application.properties`. Its `ThreadLocal` collector is activated only immediately around `GraphQlTester.execute()`. Fixture setup/flush, schema startup, and cleanup happen outside capture. Captured SQL is classified by table (`from books` root fetch; `from authors` relationship load); tests fail if unclassified SQL appears in the measurement.
- Pagination and measured N+1 evidence are in `BooksPaginationAndNPlusOneTests`: first page, next page, empty page, invalid page/size, deterministic order with duplicate titles, and no duplicated IDs across adjacent pages.
- Comparable measurement: identical SDL/schema, GraphQL operation and selected fields (`books(page: 0, size: 100) { id author { id name } }`), H2 configuration, and fixture rule (one distinct Author per Book; same title). Small run: `N_small=5`, root Book-fetch statements `=1`, `R_small=1`. Large run: `N_large=20`, root Book-fetch statements `=1`, `R_large=1`. Other captured statements `=0` in both runs. Thus `ΔN=15`, `ΔR=0`; `ΔR != ΔN`, so the documented one-query-per-additional-book pattern is not present in this measured comparison. **A-09 passes this practical acceptance check**, not a universal proof against all N+1 cases.
- Final command: `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain` — `BUILD SUCCESSFUL`; 18 tests, 0 failures, 0 errors, 0 skipped (1 context, 3 query, 10 mutation, 4 M5 pagination/N+1 tests).
- M5 implementation and verification are complete; M5 was approved and closed before M6 began.
