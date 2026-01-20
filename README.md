# ContinousDev - Hello World Application

A simple Spring Boot application demonstrating a "Hello World" REST API endpoint.

## Prerequisites

- Java 17 or higher
- Gradle (wrapper included)

## Building the Application

To build the application, run:

```bash
./gradlew build
```

## Running the Application

To run the application, execute:

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## Hello World Endpoint

The application provides a simple Hello World REST endpoint:

### GET /hello

Returns a greeting message.

**Parameters:**
- `name` (optional): The name to greet. Defaults to "World" if not provided.

**Examples:**

```bash
# Default greeting
curl http://localhost:8080/hello
# Returns: Hello World!

# Custom greeting
curl http://localhost:8080/hello?name=Alice
# Returns: Hello Alice!
```

## Running Tests

To run all tests:

```bash
./gradlew test
```

## License

See LICENSE file for details.
