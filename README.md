<!-- //-*- coding: utf-8 -*- -->
# casual-java-statistics

## Prerequisites

Uses current LTS - Java 21.

## What is it?

This is an application that connects to ( 1 - n) # of casual event servers to aggregate service call statistics.

Note that the aggregated data is in memory only, that is - it is transient.

It relies on a configuration file which is pointed to by the environment variable `CASUAL_STATISTICS_CONFIGURATION_FILE`.
Note, it should be the absolute path.

There's an example configuration file, `config.json` in the root of the repository.
It looks like this:
```json
{
    "addresses":[         
        {"hostName":"connectionOne.foo.bar.com", "portNumber":7698}
    ]
}
```

## Example execution

```sh
CASUAL_STATISTICS_CONFIGURATION_FILE=~/casual-java-statistics/config.json java -jar ./build/casual-java-statistics-0.0.1.jar
```

## Maven central

### Gradle

```gradle
dependencies {
    implementation 'se.laz.casual:casual-java-statistics:0.0.1:uber-jar'
}
```

Note that the actual uber-jar artifact from maven central will thus have the following name - `casual-java-statistics-0.0.1-uber-jar.jar`.

## How to query for information

The endpoints that can be used for querying of information are as follows:
* `http://localhost:8080/statistics` - list all connections

Example output:
```json
[
  "connectionOne.foo.bar.com:8778",
  "connectionTwo.foo.bar.com:8778"
]
```

* `http://localhost:8080/statistics/connectionOne.foo.bar.com:8778` - statistics for the connection `connectionOne.foo.bar.com:8778`

Example output:
```json
[
  {
    "connection": {
      "connectionName": "connectionOne.foo.bar.com:8778"
    },
    "entries": [
      {
        "serviceCall": {
          "serviceName": "javaEcho",
          "order": "SEQUENTIAL"
        },
        "accumulatedData": {
          "numberOfServiceCalls": 9,
          "averageTime": 0.002,
          "minTime": 0.001,
          "maxTime": 0.003,
          "numberOfPending": 9,
          "pendingAverageTime": 0.001,
          "lastCall": "2024-06-11T10:43:14.038054+02:00"
        }
      },
      {
        "serviceCall": {
          "serviceName": "javaEcho",
          "order": "CONCURRENT"
        },
        "accumulatedData": {
          "numberOfServiceCalls": 9,
          "averageTime": 0.009,
          "minTime": 0.004,
          "maxTime": 0.015,
          "numberOfPending": 0,
          "pendingAverageTime": 0.0,
          "lastCall": "2024-06-11T10:43:14.034087+02:00"
        }
      }
    ]
  }
]
```
 
Legend for the accumulated data:
* numberOfServiceCalls - the number of service calls
* averageTime - the average time, in seconds
* minTime - the minimum time, in seconds
* maxTime - the maximum time, in seconds
* numberOfPending - number of pending calls
* pendingAverageTime - the average pending time, in seconds
* lastCall - when the last call was called as using system default zone id in utc offset format
  

# Quarkus information ( Developer information - if you are not a dev you can stop reading here)
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/casual-java-statistics-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

