# SpringCart

## Description

SpringCart is a Java-based web API for e-commerce applications. It can be used to perform basic operations for an e-commerce application such as authentication, product management and order management.

## Features

- Customer authentication (sign up and sign in)
- Admin authentication and authorisation (sign in)
- Internal product management (create products, manage inventory, update product details)
- Product retrieval (fetching product data)

Coming Soon:

- Add to cart
- Create orders
- Customer wishlist
- Additional customer details
- Improved secrets management

## Setup

To run the project locally, follow these steps:

1. Clone the repository: `git clone https://github.com/nkhatri7/springcart.git`
2. Navigate to the project directory: `cd springcart`
3. Install Java 17
4. Create a `secrets.properties` file in the [main resources](./src/main/resources) folder and also the [test resources](./src/test/resources) folder. In both files, create property called `jwt.key` and set it equal to an encryption key (I used [this website](https://generate-random.org/encryption-key-generator?count=1&bytes=512&cipher=aes-256-cbc&string=&password=) to generate my key)
5. Run the application - you can do so by either:
   - Opening the project in IntelliJ and clicking the 'Run' button
   - Running it from the command line: `./mvnw spring-boot:run`

## Technologies Used

- Framework: [Spring](https://spring.io/) to run web server with reduced boilerplate code
- Security: [Spring Security](https://spring.io/projects/spring-security) to handle endpoint security
- Authentication: [Java JWT (JJWT)](https://github.com/jwtk/jjwt) to create and authenticate JWT
- Database: [PostgreSQL](https://www.postgresql.org/) and [Spring Data JPA](https://spring.io/projects/spring-data-jpa) to persist data
- DevTools: [Lombok](https://github.com/projectlombok/lombok) to reduce boilerplate code
- Logging: [SLF4J](https://www.slf4j.org/) through [Lombok](https://github.com/projectlombok/lombok) to track info and errors
- Swagger Docs: [springdoc-openapi](https://github.com/springdoc/springdoc-openapi) to generate Swagger documentation
- Hashing: [jBCrypt](https://github.com/jeremyh/jBCrypt) to hash passwords
- CI/CD: [GitHub Actions](https://github.com/features/actions) to build and test application