# Cursor Clash Backend

This is the backend for Cursor Clash, a web-based collaborative text editor. 
This project is part of the academic coursework for the "Advanced Programming Techniques" course at Cairo University
Faculty of Engineering.

The backend is built using the Java Spring Boot framework.

The frontend for this project can be found [here](). [Not created yet]


> The project is still under development and is not yet ready for production use.



## How to run the project

### Prerequisites
- Java Version 21 or higher installed on your machine (OpenJDK is recommended)
- Maven installed on your machine

Note: If you are using IntelliJ IDEA, you can use the built-in Maven and Java tools.

### Running the project

1. Clone the repository
    ```bash
    git clone <repo-url>
    cd cursor-clash-backend
    ```
1. Open the project in your favorite IDE (IntelliJ IDEA is recommended)
1. Create a new file named `application.properties` in the `src/main/resources` directory copied from the `application.properties.example` file
   
   Or you can run the following command to create the file
   ```bash
    cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
1. Set the undefined properties in the `application.properties` file
1. Run the project
1. The server will start on `localhost:<PORT>` where `<PORT>` is the port specified in the `application.properties` file


## Contribution Guidelines

Please refer to the [CONTRIBUTING.md](CONTRIBUTING.md) file for the contribution guidelines.