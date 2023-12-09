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

## Setup

To run the project locally, follow these steps:

1. Clone the repository: `git clone https://github.com/nkhatri7/springcart.git`
2. Navigate to the project directory: `cd springcart`
3. Install Java 17
4. Run the application - you can do this by opening the project in IntelliJ and clicking the 'Run' button or running it from the command line like so `./mvnw spring-boot:run`

## Technologies Used

- Framework: [Spring](https://spring.io/)
- Security: [Spring Security](https://spring.io/projects/spring-security)
- Authentication: [Java JWT (JJWT)](https://github.com/jwtk/jjwt)
- Database: [PostgreSQL](https://www.postgresql.org/) and [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- DevTools: [Lombok](https://github.com/projectlombok/lombok)
- Logging: [SLF4J](https://www.slf4j.org/) through [Lombok](https://github.com/projectlombok/lombok)
- Swagger Docs: [springdoc-openapi](https://github.com/springdoc/springdoc-openapi)
- Hashing: [JBCrypt](https://github.com/jeremyh/jBCrypt)
- CI/CD: [GitHub Actions](https://github.com/features/actions)