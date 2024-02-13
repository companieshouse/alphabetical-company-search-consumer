# alphabetical-company-search-consumer
A service that consumes messages from the stream-company-profile topic and upserts the data to the alphabetical ElasticSearch index.

## Build Requirements

In order to build `alphabetical-company-search-consumer` locally you will need the following:

- Java 21
- Maven
- Git

## Environment Variables

| Name                            | Description                                                                                                                  | Mandatory | Default | Example                                      |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------|-----------|---------|----------------------------------------------|
| `BACKOFF_DELAY`                 | The delay in milliseconds between message republish attempts.                                                                | √         | N/A     | `30000`                                      |
| `BOOTSTRAP_SERVER_URL`          | The URLs of the Kafka brokers that the consumers will connect to.                                                            | √         | N/A     | `kafka:9092`                                 |
| `CONCURRENT_LISTENER_INSTANCES` | The number of consumers that should participate in the consumer group. Must be equal to the number of main topic partitions. | √         | N/A     | `1`                                          |
| `GROUP_ID`                      | The group ID of the main consume.                                                                                            | √         | N/A     | `alphabetical-company-search-consumer-group` |
| `INVALID_MESSAGE_TOPIC`         | The topic to which consumers will republish messages if any unchecked exception other than `RetryableException` is thrown.   | √         | N/A     | `stream-company-profile-invalid`             |
| `MAX_ATTEMPTS`                  | The maximum number of times messages will be processed before they are sent to the dead letter topic.                        | √         | N/A     | `4`                                          |
| `SERVER_PORT`                   | Port this application runs on when deployed.                                                                                 | √         | N/A     | `18639`                                      |
| `TOPIC`                         | The topic from which the main consumer will consume messages.                                                                | √         | N/A     | `stream-company-profile`                     |

## Endpoints

| Path                                                  | Method | Description                                                         |
|-------------------------------------------------------|--------|---------------------------------------------------------------------|
| *`/alphabetical-company-search-consumer/healthcheck`* | GET    | Returns HTTP OK (`200`) to indicate a healthy application instance. |

