# Proposed Architecture

**Status: M0–M7 approved and closed.** M2 initially used a temporary in-memory catalog, now removed. The training screenshots require the named GraphQL operations and persistence behavior, but leave several schema details unspecified. The provisional M4 contract and M5 pagination choice are recorded below; unrelated details remain open.

## Request flow

```text
GraphQL client
  → Spring for GraphQL schema and resolver/controller layer
  → application service layer
  → Spring Data JPA repositories
  → Hibernate / JPA entities
  → H2 database
```

Use Kotlin, Java 17, Spring Boot, Spring Web and Spring for GraphQL, with Gradle Kotlin DSL. The user additionally requires Hibernate, JUnit 5, and GraphQLTester. The selected versions and compatibility rationale are recorded in `REQUIREMENTS_AND_DECISIONS.md`.

For A-01, M1 verifies only the compatible stack, build, and startup foundation. M2–M5 implement and verify the Books/Authors API requirements; M7 demonstrates the completed API. A successful M1 build alone does not satisfy A-01's API deliverable.

## M1 foundation and M2 initial query

The application entry point is `com.example.booksapi.BooksAuthorsApplication`. M2 replaces M1's startup placeholder with schema-first `Query.books: [Book!]!`, mapped by `BooksQueryController` to `InMemoryBookCatalog`. `Book` has provisional `id` and `title` fields; `Author` has provisional `id` and `name` fields; `BookInput` is a provisional schema-only input type. These field names/types/nullability and list shape are not specified by the screenshots and are not final API decisions. The catalog's two sample records are temporary and must be removed when M3 replaces it with Spring Data JPA/H2. M1 includes Web, GraphQL, JPA/Hibernate, H2, Kotlin reflection, JUnit 5, and GraphQLTester dependencies. M2 adds Spring Boot test auto-configuration to configure GraphQLTester. The context smoke test and M2 GraphQL query test passed; M3 test evidence is recorded in `TESTING_AND_BACKLOG.md`.

M2 intentionally has no `Book.author` field/resolver, entities, repositories, persistence use, service-layer business logic, mutations, pagination, or N+1 measurement. The `Author` type is present as required by the initial schema milestone, without linking it to `Book`; M3 implements the now-approved relationship. The M2 `books { id title }` test did not satisfy A-10 or A-12 and was not A-09 query-count evidence. M3 now verifies A-10/A-12 separately; A-09 remains M5 work. The relationship decision gate was resolved by explicit user approval before M3.

## M3 persistence and relationship implementation

`Book` and `Author` are JPA entities with Spring Data repositories and read-only query services. The temporary in-memory catalog has been removed; `books` reads through `BookRepository`. The approved relationship is implemented as required singular `Book.author: Author!`, backed by Book `@ManyToOne(fetch = LAZY, optional = false)` and a non-null `author_id` foreign key. There is no cascade remove.

The SDL also exposes `bookById(id: ID!): Book` and `authors: [Author!]!`. Missing `bookById` returns null in this implementation; that return behavior is provisional because the assignment does not define not-found semantics. H2 is configured as an in-memory development/test database with Hibernate `create-drop`; test fixtures are inserted through repositories, and the application does not seed production records.

The `Book.author` schema resolver loads the referenced Author through the read-only Author query service. Nested `books` and `bookById` requests, plus the `authors` query, are covered by GraphQLTester tests. This per-book Author loading is functionality evidence only; it is not an N+1 fix. Batching and the separate query-count criterion remain M5 work. M3's test result and remaining milestone boundaries are recorded in `TESTING_AND_BACKLOG.md`.

### Selected M1 compatibility

The selected Java 17, Kotlin 2.2.21, Spring Boot 3.5.16, Gradle wrapper 8.14.4, and Kotlin/Spring plugins are documented with compatibility rationale and build evidence in `REQUIREMENTS_AND_DECISIONS.md`.

## GraphQL schema and resolvers

Use schema-first SDL at `src/main/resources/graphql/schema.graphqls`, as specified by the training lab. The schema must define `Book`, `Author`, and appropriate input types. Required root fields by name:

- Queries: `books` (paginated), `bookById(id)`, `authors`.
- Mutations: `addBook`, `updateBook`, `deleteBook`.

Except for the user-approved required `Book.author: Author!` relationship and user-authorized provisional M4 mutation contracts recorded below, other SDL signatures, pagination arguments/result shape, and unrelated fields/constraints remain unspecified by the assignment. Keep them open until clarified or separately approved.

Use thin Spring GraphQL resolver/controller methods, with `@QueryMapping`, `@MutationMapping`, and `@Argument` for root operations. Resolve nested Book-to-Author data using `@SchemaMapping` or, for batching, `@BatchMapping`. Ensure the book/author nested selection works in one GraphQL request.

The explicit `Book.author` field and its resolver satisfy A-12; selecting nested author data in one request satisfies A-10. These are distinct assignment checks. A GraphQL response containing nested data does not establish that the resolver avoids N+1 queries; A-09 requires separate M5 SQL-count evidence.

## Service and repository layers

- **GraphQL resolver layer:** binds schema fields/arguments and delegates to services.
- **Service layer:** owns business rules, transactions, validation coordination, and multi-repository work.
- **Repository layer:** Spring Data JPA interfaces, including paging access for `books`.
- **Entity layer:** Hibernate/JPA mappings for Book and Author.

Do not expose persistence entities as the public API contract by default; use API/input types or mapping where needed. Other model fields and constraints remain open; the Book–Author association is specified in the approved decision record below.

## Book and Author entities and relationship

**Decision status: resolved by explicit user approval; implemented in M3, approved.** These are project design decisions, not requirements stated by the assignment screenshots. Each Book has exactly one required Author, and an Author may be associated with multiple Books. The GraphQL field is singular and non-null: `Book.author: Author!`.

Map the association from Book to Author using JPA `@ManyToOne(optional = false)` and enforce it with a non-null Book-to-Author foreign key. This mapping follows the approved one-Author-per-Book, reusable-Author model; the non-null foreign key enforces the required association. `addBook` accepts an existing Author ID and rejects an invalid/nonexistent reference; it does not create Authors. `updateBook` may change the title and may change the associated Author using an existing Author ID. An omitted Author ID preserves the current association; clearing is disallowed, and explicit null must not be treated as omission. `deleteBook` removes only the Book and does not cascade deletion to its Author, avoiding deletion of an Author that may be shared. Referencing existing Authors avoids implicit Author creation, and omission-preserves-current supports title-only updates.

M3 tests verify the required nested field and persisted association. M4 tests cover invalid Author references, omitted-field preservation, valid reassignment, rejection of clearing, and Author retention after Book deletion. The M4 GraphQL signatures and standard error-handling contract are user-authorized provisional choices recorded below; unrelated validation details remain open. The relationship decision gate was resolved by explicit user approval, and M3 was approved.

## H2 persistence

Persist through Spring Data JPA backed by H2. M1's startup had Spring Boot's default H2 in-memory datasource because the JPA starter and H2 runtime dependency were present, but M2 did not use it as the Books/Authors data source and had no business persistence configuration. M3 now enforces the approved non-null Book-to-Author foreign key without cascading Book deletion to Author. The screenshots do not state in-memory vs file-backed H2, initialization data, schema generation mode, or retention behavior; the current in-memory `create-drop` configuration is a project implementation choice.

## Validation and error handling

The training plan recommends basic error handling/validation and lists input validation and consistent error responses as a testing/best-practices topic. For M4’s specified invalid Author, explicit-null Author, and missing Book cases, use standard Spring for GraphQL handling without a custom format; its default unresolved-error response is intentionally opaque. Other validation constraints and exact payload details remain unspecified. Test success and error paths through GraphQLTester.

### M4 user-authorized provisional mutation contract

These signatures are project API decisions authorized by the user for M4; they are not mandated by the assignment screenshots. Keep unrelated schema fields and validation rules open.

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

`addBook` resolves an existing Author ID and persists a Book; it never creates an Author. `updateBook` rejects a missing Book, applies supplied title and Author ID values, and preserves omitted values. A supplied Author ID must resolve to an existing Author. Explicit `authorId: null` is rejected, not interpreted as omission or relationship clearing. Explicit `title: null` is also rejected because the persisted title is non-null; this is an implementation choice, not an assignment detail. New string IDs are generated as UUIDs because `addBook` accepts no ID. The implementation uses Spring GraphQL's `ArgumentValue` input-property binding to retain the distinction between omitted and explicitly-null input fields, verified with GraphQLTester. `deleteBook` rejects a missing Book, deletes only the Book, retains its Author, and returns `true` after success. Invalid Author IDs, explicit null, and missing Book IDs use standard Spring for GraphQL error handling; do not add custom codes, error formatting, or expose stack traces.

The required relationship remains `Book.author: Author!`, backed by non-optional `@ManyToOne` and a non-null foreign key. Mutation operations belong in resolver/controller methods delegating to transactional application services and repositories. M4 does not add pagination or N+1 optimization/measurement.

### M4 implementation and verification

The SDL, Kotlin input classes, mutation controller, and transactional mutation service implement the contracts above. `ArgumentValue` on update input properties distinguishes omitted values from explicit null. GraphQLTester covers the specified mutation success/error cases and verifies that default Spring for GraphQL errors do not disclose the service exception message or class name. The complete clean test run passed: 14 tests, 0 failures/errors/skips. Exact command and test breakdown are in `REQUIREMENTS_AND_DECISIONS.md`. M4 is approved and closed; M5 is implemented and verified; measured evidence is recorded in the N+1 section.

## Pagination

The assignment requires pagination but does not specify its GraphQL contract. The user-authorized provisional project contract for M5 is `books(page: Int! = 0, size: Int! = 20): [Book!]!`; it is not an assignment-mandated signature. `page` is zero-based and nonnegative; `size` must be 1–100. A valid page beyond the collection returns an empty list. Invalid arguments use standard GraphQL error handling.

Use `BookRepository.findAll(Pageable)` with `PageRequest.of(page, size, Sort.by(ASC, "id"))` to give deterministic ordering and database-side pagination. This avoids loading the complete collection and slicing it in application memory. Verify first and subsequent pages, beyond-end empty results, invalid values, duplicate-title ordering, and no duplicated IDs across adjacent pages for an unchanged dataset.

## N+1 query prevention

The mini-project checklist requires no N+1 queries on the books-with-authors query, and the lab specifically says to fix an N+1 case using `@BatchMapping`; the M5 implementation uses that mapping. The acceptance evidence compares at least two result sizes with `N_large > N_small` returned books, using the same SDL, GraphQL operation shape, selected relationship fields, H2 configuration, and equivalent fixture-generation rules with comparable relationship coverage and enough books/authors to expose per-book loading. Count SQL statements only during each GraphQL execution; exclude schema initialization, fixture setup/cleanup, and unrelated statements. Report root Book-fetch statements separately from Author-relationship loading statements. Let `R_small` and `R_large` be the relationship-loading statement counts; calculate `ΔN = N_large - N_small` and `ΔR = R_large - R_small`. The measured one-query-per-additional-book pattern is present when `ΔR = ΔN`. Pass this practical check only when comparable, interpretable evidence does not show that equality. If results are ambiguous, noisy, or consistent with one-for-one growth, verification fails pending investigation. This is a practical acceptance check, not a universal proof that all possible N+1 issues are absent. A nested response proves functionality, not N+1 prevention.

**M5 SQL capture and evidence:** A test-only Hibernate `StatementInspector` collector is configured in test resources and activated only around GraphQL execution. Fixture setup/flush, schema startup, and cleanup are excluded. Captured SQL is classified by Book-table root fetch and Author-table relationship load. Under the H2/GraphQL test path, the smaller fixture returned 5 Books with root=1 and Author=1 statements; the larger returned 20 Books with root=1 and Author=1. Therefore `ΔN=15`, `ΔR=0`. Other captured statements were zero in both runs. The practical one-for-one growth check passes (`ΔR != ΔN`); this does not prove absence of every possible N+1 issue.

## Testing strategy

- JUnit 5 test suite, with GraphQLTester for GraphQL operations; `@GraphQlTest` is specifically mentioned as a learning topic.
- At least three automated tests are mandatory. The lab recommends tests for one query and one mutation, but the third exact case is unspecified.
- Add integration tests against H2 for persistence and relationships.
- Cover required operations and nested Book–Author resolution as schema details become known.
- Cover paginated `books` boundaries and test query behavior for the N+1 requirement.
- M1 context smoke test and M2 query test passed. M3 GraphQLTester tests cover persisted books with nested authors, `bookById`, and `authors`; M4 tests cover mutation contracts; M5 tests cover paging and execution-only SQL comparison. M6 adds empty-result, shared-Author, and foreign-key constraint tests and reviews isolation/error leakage. Current clean suite: 21 tests, 0 failures/errors/skips. The current test inventory and A-01–A-12 coverage audit are in `REQUIREMENTS_AND_DECISIONS.md` and `TESTING_AND_BACKLOG.md`.

### M6 test-quality findings

Stateful test classes initialize their own fixtures in `@BeforeEach`; setup removes Books before Authors so foreign-key ordering is respected. The test-only SQL collector captures only within the synchronous GraphQL execution block and clears its `ThreadLocal` in `finally`. Mutation tests reject exposed not-found service messages, exception names, application package names, and stack-frame text. Added H2 integration evidence confirms multiple Books may share an Author and a Book referencing a nonexistent Author violates the foreign key. The full clean suite passed with 21 tests. Assignment-unspecified API fields and payload details remain open.

### M7 deployment and demo boundary

The application starts on port 8080 and exposes Spring GraphQL over HTTP POST `/graphql`. Runtime H2 is in-memory (`jdbc:h2:mem:books_authors`, username `sa`, no configured password) with Hibernate `create-drop`. Only the explicitly activated `demo` profile enables SQL initialization from `classpath:demo-data.sql`, after Hibernate creates the schema; this inserts deterministic Author `demo-author-001` using an H2 `MERGE` keyed by `id`. The base/default configuration remains unseeded, verified by a default-profile test; demo-profile tests verify seeding and idempotent reapplication. `DEMO_GUIDE.md` records the live `/graphql` mutation lifecycle and restart/reset evidence. Final clean test suite: 23 tests, 0 failures, 0 errors, 0 skipped. Nested Book–Author resolution was verified as GraphQL response functionality; the separate M5 SQL-count evidence was rechecked at 5 Books (root=1, Author=1) and 20 Books (root=1, Author=1), ΔN=15 and ΔR=0. M7 is formally approved and closed. Pagination remains a provisional project contract. No API schema, relationship mapping, or mutation behavior changed for M7.
