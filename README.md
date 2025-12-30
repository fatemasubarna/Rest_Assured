# Rest Assured Automation Project

## Project Overview
This project automates API testing for **Daily Finance Management System** using **Rest Assured** in Java.  
It follows the **Page Object Model (POM) architecture** for better maintainability and scalability.

APIs under test include:

- Register a new user
- Login by admin
- Get user list
- Search user by ID
- Edit user info (firstname, phone number)
- Login by any user
- Get item list
- Add an item
- Edit item
- Delete item

Both **positive** and **negative test cases** have been implemented, such as invalid login credentials, missing fields, and duplicate user registration.


---
## Negative Test Cases Implemented
Admin login with invalid credentials
User login with invalid credentials
User registration with existing email or phone
Registration without mandatory fields
Update user with invalid/missing data

---

## Project Structure

Rest_Assured/
├── build.gradle
├── settings.gradle
├── src/
│ ├── main/java/
│ │ └── config/ # Setup.java, UserModel.java, Utils.java
│ ├── test/java/
│ │ └── testrunner/ # Test classes like UserTestRunner.java
│ │ └── controller/ # API controllers like UserController.java
│ └── test/resources/
│ └── config.properties
├── screenshots/ # Allure screenshots
└── README.md


---

## Tools & Frameworks
- **Java 11+**
- **Rest Assured**
- **TestNG**
- **Faker** (for dynamic test data)
- **Gradle**
- **Allure Report**
- **Postman** (for API inspection)

---

## Environment Setup

Create `config.properties` in `src/test/resources`:

```properties
baseUrl=https://dailyfinance.roadtocareer.net/
adminEmail=admin@test.com
adminPassword=admin123

---

## Postman Collection
Postman collection link: https://elements.getpostman.com/redirect?entityId=26556120-dd1146e7-0942-4bba-926e-40c6966969c9&entityType=collection
Postman Documentation: https://www.postman.com/fatemasubarna/public-workspace/documentation/26556120-dd1146e7-0942-4bba-926e-40c6966969c9

-----
## Test Case Documentation
Test case link (replace with your actual link):
Test Cases

----
## Allure Report
Allure report screenshot: https://drive.google.com/file/d/1Zv7LejlnGlSC87TlefsJ-sONnhkFmxAl/view?usp=sharing

-----
## Notes

All API requests are automated using Rest Assured.
Dynamic test data is generated using Faker.
Utils.java handles environment variables and reusable methods.
POM architecture is followed for maintainability.