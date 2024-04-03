# Contribution Guidelines

If you would like to contribute to this project, please follow the guidelines below.

## How to contribute

1. If you are going to work on a new feature or fix a bug, please create a new branch from the `main` branch.
1. Commit your changes to your branch, following the [commit message conventions](#commit-message-conventions).
1. Follow the [code style guidelines](#code-style-guidelines).
1. Push your changes to your branch.
1. Create a pull request to the `main` branch.

## Commit message conventions

- Please follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification for your commit messages.
  - Use the following format for your commit messages:
    ```
    <type>[optional scope]: <description>
  
    [optional body]
  
    [optional footer]
    ```
    - where `type` is one of the following: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, or `chore`.
    - The `scope` is optional and should be the name of the module or component that the commit is related to.
    - Make sure that the first line of the commit message is less than 70 characters long.

  - Examples:
    ```
      feat(auth): add login functionality
  
      Add the login functionality to the authentication service.
    ```

## Code style guidelines

- For the folder structure, Make sure to follow the following folder structure:
    ```
    cursor-clash-backend
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com.cursorclash.backend
    │   │           ├── controllers
    │   │           ├── models
    │   │           ├── repositories
    │   │           ├── services
    │   │           |── utils
    │   │           |── configs
    │   │           |── exceptions
    │   │           |── security
    │   │           └── CursorClashApplication.java
    │   │   └── resources
    │   │       ├── static
    │   │       |── templates
    │   │       |── application.properties
    │   │       └── application.template.properties
    │   └── test
    ```
  
