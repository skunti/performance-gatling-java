# Ambient Performance Gatling Java

This project provides a Java-based Gatling performance testing framework for Ambient’s appointment API workflows. 
It is structured for maintainability, extensibility, and ease of execution.

---

## Project Structure
ambient-performance-gatling-java
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.ambient.performance
│   │   │       ├── config
│   │   │       │   └── GatlingConfig.java
│   │   │       └── model
│   │   │           └── AppointmentRequest.java
│   │   └── resources
│   │
│   └── test
│       ├── java
│       │   └── com.ambient.performance
│       │       ├── base
│       │       │   └── BaseSimulation.java
│       │       └── simulations
│       │           ├── CreateAppointmentsSimulation.java
│       │           ├── CreateAppointmentsStaticSimulation.java
│       │           └── QueryAppointmentsSimulation.java
│       │
│       └── resources
│           ├── data
│           │   ├── appointments.json
│           │   └── create_appointment.json
│           ├── config.properties
│           └── gatling.conf
│
├── pom.xml
└── .gitignore

## Features

* Load tests using Gatling Java DSL  
* Configurable test scenarios with external properties  
* Reusable base simulation for common configurations  
* Support for dynamic JSON payloads in test resources  
* Clean separation of config and model classes  
* Easy to extend with additional simulations

---

## Simulations Included

- **CreateAppointmentsSimulation**: dynamic creation of appointments
- **CreateAppointmentsStaticSimulation**: static data-driven appointment creation
- **QueryAppointmentsSimulation**: querying existing appointments
- **MixedWorkflowSimulation**: combined workflows to stress test the system

---

## How to Run

1. **Install dependencies**
   ```bash
   mvn clean install
   
2. **Execute a simulation**
   ```bash
    mvn gatling:test -Dgatling.simulationClass=com.ambient.performance.simulations.CreateAppointmentsSimulation 
Replace CreateAppointmentsSimulation with any other simulation class you wish to run.

3.  **Reports**
    ```After execution, Gatling generates reports under:
    target/gatling/<simulation-name>-<timestamp>/index.html
    
## Configuration

- **`config.properties`**  
  Located under `src/test/resources`. Defines environment-specific parameters such as:
    - base URLs
    - number of virtual users
    - ramp-up durations
    - request timeouts
    - custom headers

- **`gatling.conf`**  
  Located under `src/test/resources`. Allows fine-tuning of Gatling’s engine, including:
    - simulation output
    - report formats
    - throttling options

- **Test Data**  
  JSON payloads for test requests (e.g., appointments) are under `src/test/resources/data`, enabling static or dynamic data-driven scenarios.

---

##  Requirements

- **Java 17** or higher
- **Maven 3.6** or higher
- Internet access to pull dependencies
- Recommended IDE: IntelliJ IDEA or Eclipse

---

##  Extending

You can easily extend the framework:

- **New Simulations**
    - Add a class in `src/test/java/com/ambient.performance/simulations`
    - Extend `BaseSimulation` for a consistent injection profile
    - Implement your custom scenario

- **New Models**
    - Place POJOs in `src/main/java/com/ambient.performance/model`
    - Use them for payload generation or serialization

- **Shared Configs**
    - Add constants or reusable config helpers in `GatlingConfig.java`

- **Test Data**
    - Add JSON files in `src/test/resources/data`
    - Load dynamically with feeders or file-based payloads

---