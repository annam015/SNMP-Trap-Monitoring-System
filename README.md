# SNMP Trap Management System

## Description
The SNMP Trap Management System is an integrated solution designed for monitoring and managing network devices using Simple Network Management Protocol (SNMP). This system efficiently captures, processes and stores SNMP traps in MongoDB and provides a REST API for easy data visualization and management.

## Project Structure

### SNMP Trap Receiver
- **Functionality**: Listens for incoming SNMP traps on a specified port. Extracts relevant information and logs it to a MongoDB database.
- **Technologies Used**: Java, SNMP4J, MongoDB

### SNMP Trap Sender
- **Functionality**: Periodically sends SNMP traps, reporting real system metrics like CPU load and memory usage.
- **Technologies Used**: Java, SNMP4J

### Node.js Scripts and REST API
- **Purpose**: Set up and interact with the MongoDB database. The REST API allows for easy visualization and management of the stored SNMP traps.
- **Technologies Used**: Node.js, Express.js, MongoDB

## Docker Integration
- **Overview**: The system is containerized using Docker, ensuring easy deployment and scalability.
- **Setup**: Detailed instructions are provided for setting up Docker containers for both the SNMP Trap Receiver and Sender.

## IDE Setup & Requirements
- **Java Development**: Developed using Eclipse with JavaSE-17.
- **Docker Image**: Based on `critoma/linux-u20-dev-security-ism` from [Docker Hub](https://hub.docker.com/r/critoma/linux-u20-dev-security-ism).
- **Runtime Environment in Docker**:
  - Java JDK 21
  - Node.js v18.18.0
  - MongoDB@4

## Getting Started
Follow the instructions from `cmdCompile.txt` to set up the environment and run the applications. This includes running Docker containers, setting up MongoDB, and executing the Java applications.

## Usage
After completing the setup, the SNMP Trap Receiver starts listening for traps, while the Trap Sender begins sending SNMP traps. The traps are stored in MongoDB and accessible through the REST API for visualization and analysis.
