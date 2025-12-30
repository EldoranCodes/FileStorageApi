# Step 1: Setup Application
- Spring Boot project via initializer
- Dependencies: web, data-jpa, postgresql, spring-boot-starter-test
- Create entities: users, api_keys, upload_batches, stored_file
- Configure DB connection (PostgreSQL)
- Add application.properties / env variable for base upload path
- Test entities with spring-boot-test
- Add self-test bean (CommandLineRunner or @PostConstruct)
- Check base directory exists & writable
- Verify at least 1 demo user + API key

---



