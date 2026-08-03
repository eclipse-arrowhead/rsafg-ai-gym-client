# rsafg-ai-gym-client
`ai-gym-cleint` is a client library for connecting **Apache Zeppelin** notebooks with the **Eclipse Arrowhead** ecosystem through a REST-based adapter.

It is intended for the AI-gym workflow described in the **An AI-Gym for Industry 4.0** paper, where Zeppelin is used for model development, the adapter bridges Zeppelin and Arrowhead, and the Arrowhead local cloud provides service registry, authorization, and event handling for secure IIoT communication.

## What `ai-gym-client` is for

The library supports communication between Zeppelin and Arrowhead by exposing REST APIs for:

- service registration
- authorization requests
- publish/subscribe event exchange
- data exchange with IIoT services

The paper describes this client as the bridge between Zeppelin notebooks and the Arrowhead local cloud, using REST APIs and the Event Handler publish/subscribe model.

## Prerequisites

Before using `ai-gym-client`, make sure the following components are running:

- **Arrowhead local cloud**
- **Arrowhead EventHandler**
- **Apache Zeppelin**
- a **RESTFul web service** or adapter endpoint that enables communication between Arrowhead and Zeppelin

The integration workflow in the paper shows Zeppelin notebooks being registered as Arrowhead services, then authorized, and finally connected to IIoT data streams through the EventHandler. 

## Setup concept

The setup follows a three-layer architecture:

1. **Model Development Layer** — Apache Zeppelin for notebook-based AI/model development.
2. **Integration Layer** — the Eclipse Arrowhead Client for connecting Zeppelin to Arrowhead services.
3. **IIoT Data Layer** — the Arrowhead local cloud for live data streams, service registry, authorization, and event handling.

This architecture is presented in the paper as a controlled environment for developing, training, testing, and refining AI models using realistic data streams before production deployment. 

## Workflow

### 1. Start Arrowhead local cloud and EventHandler
Run the Arrowhead local cloud components first, including the EventHandler and the services needed for service registration and authorization.

### 2. Start Apache Zeppelin
Open Zeppelin and prepare the notebook that contains your model, analysis logic, or control workflow.

### 3. Configure the REST endpoint
Make sure the REST service exposed by `RESTFul web service` is reachable from Zeppelin and from Arrowhead.

### 4. Register the Zeppelin service in Arrowhead
Use the client to register the Zeppelin notebook or service in the Arrowhead Service Registry.

The service registration includes metadata such as service definition, URI, version, and provider information. 

### 5. Request authorization
After registration, the client requests authorization so the Zeppelin-side service can consume the required Arrowhead resources.

### 6. Exchange data through publish/subscribe
Use the Arrowhead EventHandler to publish sensor readings and subscribe to topics such as control commands.

The EventHandler is used for sensor readings, control commands, and publish/subscribe communication between Zeppelin models and IoT devices.

## Example use case

A typical flow looks like this:

- a sensor sends a reading to the Arrowhead EventHandler
- the RESTul web service forwards the data to a Zeppelin notebook service
- the notebook analyzes the data and produces a recommendation
- the result is sent back through Arrowhead to an actuator or another IIoT service

## Getting started

1. Clone the repository.
2. Build the project.
3. Start Arrowhead local cloud, EventHandler, and Zeppelin.
4. Add the built client as a JAR dependency to Apache Zeppelin.
5. Register your Zeppelin-based service.
6. Request authorization.
7. Subscribe to live data or publish control commands.

## Notes

- The client is intended for use in a controlled Arrowhead environment.
- Communication is REST-based and aligned with Arrowhead services.
- The setup is especially useful for testing AI models with live IIoT data before production deployment.


**Paper**:
An AI-Gym for Industry 4.0: https://ieeexplore.ieee.org/document/11073648
