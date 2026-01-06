# ING Baker Experiment
This is a sample project group to demonstrate ing-baker library. [https://github.com/ing-bank/baker]

Each java project has application.properties and one application-sample.properties as profile.
You can copy and use this sample for your projects.

This repository contains several sub projects
- orchestrator (orchestrator-service): The main service which has the baker code (It might change in the future).
- customers (customers-service): A simple SpringBoot application which simulates crud operations for customers
- accounts (accounts-service): A simple SpringBoot application which simulates crud operations for accounting
- communications (communications-service): A simple SpringBoot application which sends email using smtp service
- bank-ui : A simple Vuejs application which connects to services

These applications need apache maven and npm to run on your local.
- Each has its own database using h2 and stored in local files.
- Each java project has application.properties and application-sample.properties to ease development. I am using dev profile and copied sample.

For akka library you need to get a token from Akka account page [https://account.akka.io/key]
You may put the following code (from akka) to your pom or settings.xml in your .m2 folder.

```
<repository>
<id>akka-secure</id>
<name>Akka Secure</name>
<url>https://repo.akka.io/XXXXX/secure</url>
</repository>
```
For your questions and comments.
You can contact with me kenanerarslan@gmail.com