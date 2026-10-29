# Books and Authors GraphQL API Demo

This guide reflects the current schema and runtime configuration. The pagination shape is a provisional project choice; the assignment requires pagination but does not prescribe its arguments or return shape.

## Prerequisites and startup

- Java 17. The verified environment used Java 17.0.16.
- Use the included Gradle wrapper; system Gradle is not required. The wrapper distribution is Gradle 8.14.4.
- In the inspected environment, the wrapper needed a writable Gradle home and temporary directory:

  ```sh
  export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
  export GRADLE_USER_HOME=/private/tmp/gradle-user-home
  export JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp
  ```

  Use the corresponding Java 17 and writable paths on another machine if these paths are unavailable.

- Run the full test suite:

  ```sh
  ./gradlew clean test --console=plain
  ```

- Start the application:

  ```sh
  ./gradlew bootRun --console=plain
  ```

- Start with the approved demo-only Author seed:

  ```sh
  ./gradlew bootRun --args='--spring.profiles.active=demo' --console=plain
  ```

  This activates `application-demo.yml`; the ordinary/default profile does not load the seed. The demo profile sets `spring.sql.init.mode=always`, `spring.sql.init.data-locations=classpath:demo-data.sql`, and `spring.jpa.defer-datasource-initialization=true`. The SQL uses an H2 `MERGE` keyed by the Author primary key, so running the initializer against the same initialized schema does not create duplicate IDs.

- Spring GraphQL accepts HTTP POST requests at `http://localhost:8080/graphql`; the browser UI is at `http://localhost:8080/graphiql`. For example, the same endpoint can be queried with `curl`:

  ```sh
  curl -X POST http://localhost:8080/graphql \
    -H 'Content-Type: application/json' \
    --data-binary '{"query":"query { authors { id name } }"}'
  ```

- Stop the foreground application with Ctrl+C.

## OpenAPI and Swagger UI

When the application is running, Swagger UI is available at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html), and the OpenAPI JSON document is at [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs). The document metadata is titled “Books & Authors API,” version `1.0.0`.

This application exposes its Books and Authors operations through GraphQL, not REST controllers. springdoc documents applicable HTTP REST endpoints; it does not automatically describe the GraphQL query and mutation fields. With no REST controllers, Swagger UI may show an empty `paths` section. That is expected and does not indicate that GraphQL is unavailable.

After adding OpenAPI support, the full suite was run with the configured Java 17 runtime using `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain`. The recorded result was `BUILD SUCCESSFUL`: 26 tests, 0 failures, 0 errors, and 0 skipped. This included HTTP checks for the OpenAPI JSON, Swagger UI, and GraphQL endpoint, plus the existing default/demo profile tests.

## GraphiQL browser interface

The Spring GraphQL starter exposes the bundled GraphiQL page at [http://localhost:8080/graphiql](http://localhost:8080/graphiql) when `spring.graphql.graphiql.enabled` is `true` in `application.yml`. The GraphQL HTTP endpoint remains [http://localhost:8080/graphql](http://localhost:8080/graphql). The UI uses the schema's introspection to provide its editor and explorer; run the documented queries and mutations below directly from the browser, inspect the returned data, and see GraphQL errors in the response panel. No REST endpoints or GraphQL contracts were added for the UI.

The GraphiQL HTML page is bundled in the Spring GraphQL dependency. Its current UI module, stylesheet, and editor worker imports reference `esm.sh`, so the browser needs internet access to load those assets. The application endpoint itself remains local.

For the GraphiQL change, `env JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home GRADLE_USER_HOME=/private/tmp/gradle-user-home JAVA_OPTS=-Djava.io.tmpdir=/private/tmp/gradle-tmp ./gradlew clean test --console=plain` completed successfully: 28 tests, 0 failures, 0 errors, and 0 skipped. Live browser checks also executed the `authors`, `addBook`, `updateBook`, and `deleteBook` operations; displayed nested Author data and a GraphQL error for a missing Book; and confirmed the Author remained after deleting its Book. This is the result of the current run, not a claim that the tests will pass in every environment.

## Persistence and demo data prerequisite

The application uses H2 in-memory storage at `jdbc:h2:mem:books_authors;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`, username `sa`, and no configured password. Hibernate uses `create-drop`. Data is volatile and is not retained across application-process restarts. Stop the foreground process with Ctrl+C and start a new process to reset the database; with the demo profile, the initializer creates one Author again and no Books.

Normal/default startup has no Author seed and there is no `addAuthor` operation. For the mutation demonstration, explicitly activate the demo profile, then query `authors` and use the ID actually returned. The demo-only seed is `demo-author-001` (`Demo Author`); still obtain it from the query response rather than assuming it is present in another profile.

## Queries

All operations below match `src/main/resources/graphql/schema.graphqls`.

### Paginated books

```graphql
query {
  books(page: 0, size: 5) {
    id
    title
  }
}
```

The project-selected behavior is zero-based pages, default page `0`, default size `20`, accepted sizes `1` through `100`, and stable ascending Book ID ordering. A valid page beyond the results returns an empty list. Negative pages and sizes outside the accepted range produce GraphQL errors through the existing error handling.

### One request with nested Book–Author data

```graphql
query {
  books(page: 0, size: 5) {
    id
    title
    author {
      id
      name
    }
  }
}
```

This operation selects Book and Author fields in the same GraphQL request. A nested response demonstrates A-10/A-12 functionality; it is not N+1 evidence.

### One book by ID

```graphql
query GetBook($id: ID!) {
  bookById(id: $id) {
    id
    title
    author {
      id
      name
    }
  }
}
```

Pass an `id` obtained from a successful `books` response or `addBook` response. The current resolver returns `null` when the ID does not match a Book; the assignment does not specify missing-ID behavior.

### Authors

```graphql
query {
  authors {
    id
    name
  }
}
```

Run this before `addBook`; use an actual returned Author ID. On the current fresh H2 setup, this query returns `[]` because no Author records are seeded.

## Mutations

These signatures are user-authorized provisional project contracts. They are not signatures specified by the assignment screenshots. The examples use variables; replace the marked values only with IDs returned by successful earlier operations.

### Add a Book for an existing Author

```graphql
mutation AddBook($input: BookInput!) {
  addBook(input: $input) {
    id
    title
    author {
      id
      name
    }
  }
}
```

Variables (use the actual ID returned by `authors`):

```json
{
  "input": {
    "title": "Demo title",
    "authorId": "demo-author-001"
  }
}
```

`addBook` references an existing Author and never creates one. An unknown Author ID is rejected using standard GraphQL error handling. A successful response's Book ID can be used in the following operations.

### Update a Book

```graphql
mutation UpdateBook($id: ID!, $input: UpdateBookInput!) {
  updateBook(id: $id, input: $input) {
    id
    title
    author {
      id
      name
    }
  }
}
```

Use the `id` returned by `addBook`. To change only the title, pass (the ID shown here is the ID returned by the recorded rehearsal below; in a new run, use that run's returned ID):

```json
{
  "id": "<returned-book-id>",
  "input": { "title": "Revised demo title" }
}
```

An omitted `authorId` preserves the current Author. To change the Author, include an existing ID returned by `authors`; an omitted `title` preserves the current title. An unknown Book or Author ID produces a GraphQL error. Explicit `authorId: null` is rejected; a Book's Author relationship cannot be cleared. Do not report that rejected request as a successful update.

### Delete a Book

```graphql
mutation DeleteBook($id: ID!) {
  deleteBook(id: $id)
}
```

Pass the ID returned by `addBook` or `updateBook`. Success returns `true` and deletes only the Book; its Author is retained. An unknown Book ID produces a GraphQL error.

## N+1 verification evidence

The M5 integration test executes the same nested GraphQL query against comparable H2 fixtures with one distinct Author per Book. SQL capture is active only during GraphQL execution; fixture setup, schema initialization, cleanup, and unrelated statements are excluded.

| Books returned | Root Book-fetch statements | Author relationship statements |
| ---: | ---: | ---: |
| 5 | 1 | 1 |
| 20 | 1 | 1 |

`ΔN = 20 - 5 = 15`; `ΔR = 1 - 1 = 0`. This tested comparison does not show one relationship query per additional Book. It is practical evidence for the tested query and fixtures, not universal proof that every possible N+1 issue is absent. A nested GraphQL response alone is not this SQL-count evidence.

## Default-profile rehearsal and current limitation

In the recorded default-profile rehearsal, the application started with Java 17.0.16, Spring Boot 3.5.16, and H2. Spring logged the GraphQL endpoint as HTTP POST `/graphql`. Live requests to `authors` and `books(page: 0, size: 5) { id title author { id name } }` returned HTTP 200 and empty lists because default startup has no seed. The separate demo-profile live rehearsal below used the profile-scoped Author seed. The test suite also exercises persisted query results and mutation behavior through GraphQLTester.

### Recorded live rehearsal

The following requests were executed against the demo-profile server at `http://127.0.0.1:8080/graphql`; each returned HTTP 200:

1. `query { authors { id name } }`

   ```json
   {"data":{"authors":[{"id":"demo-author-001","name":"Demo Author"}]}}
   ```

2. `mutation { addBook(input: { title: "M7 Demo Book", authorId: "demo-author-001" }) { id title author { id name } } }`

   ```json
   {"data":{"addBook":{"id":"bd96e563-e66d-47b8-a6b0-51808e3dc675","title":"M7 Demo Book","author":{"id":"demo-author-001","name":"Demo Author"}}}}
   ```

3. `mutation { updateBook(id: "bd96e563-e66d-47b8-a6b0-51808e3dc675", input: { title: "M7 Demo Book Updated" }) { id title author { id name } } }`

   ```json
   {"data":{"updateBook":{"id":"bd96e563-e66d-47b8-a6b0-51808e3dc675","title":"M7 Demo Book Updated","author":{"id":"demo-author-001","name":"Demo Author"}}}}
   ```

   The Author ID was omitted from input, and the response shows it remained unchanged.

4. `mutation { deleteBook(id: "bd96e563-e66d-47b8-a6b0-51808e3dc675") }`

   ```json
   {"data":{"deleteBook":true}}
   ```

5. A final query for `authors`, `books(page: 0, size: 5)`, and `bookById` for the returned Book ID:

   ```json
   {"data":{"authors":[{"id":"demo-author-001","name":"Demo Author"}],"books":[],"bookById":null}}
   ```

For direct A-10 HTTP evidence, a second temporary Book was added with the Author ID returned by `authors`, then this query was executed while the Book existed:

```graphql
query {
  books(page: 0, size: 5) {
    id
    title
    author { id name }
  }
}
```

It returned HTTP 200:

```json
{"data":{"books":[{"id":"9202375a-c55c-49b2-aecc-c9ae436a1d6c","title":"Nested query rehearsal","author":{"id":"demo-author-001","name":"Demo Author"}}]}}
```

That temporary Book was deleted with its returned ID; the mutation returned `{"data":{"deleteBook":true}}`.

After stopping the process and starting a new demo-profile process, the combined `authors` and `books` query returned HTTP 200:

```json
{"data":{"authors":[{"id":"demo-author-001","name":"Demo Author"}],"books":[]}}
```

This verifies reset and seed reapplication. The SQL `MERGE` was executed successfully at startup; a profile test reruns the actual SQL resource and confirms exactly one Author remains.

The clean verification run after adding profile-isolation and idempotence tests completed with `BUILD SUCCESSFUL`, exit status 0: 23 tests, 0 failures, 0 errors, and 0 skipped. The test report includes 1 default-profile context/isolation test, 2 demo-seed tests, 6 query/persistence tests, 10 mutation tests, and 4 pagination/N+1 tests. The M5 SQL comparison in that run remained: 5 Books/root 1/Author 1 and 20 Books/root 1/Author 1 (`ΔN=15`, `ΔR=0`). M7 was formally approved and closed by the user; pagination remains a provisional project contract.
