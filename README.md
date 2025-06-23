# Birthday Reminder Bot

Birthday Reminder Bot is an automated event reminder system (e.g., birthdays) with Telegram integration and a user-friendly web interface.  
The application is available at: [https://belnitskii.ru/apps/reminder/login](https://belnitskii.ru/login).  
You can use a test account: `user@mail.ru` / `user` or register using your own email.

### Web Application

- Email registration with verification
- Telegram account linking via token generation
- Add, edit, and delete events (birthdays)
- Choose reminder level (low / high)
- Personal dashboard showing:
  - user role
  - Telegram linking status
  - buttons to link / unlink Telegram

An admin panel is also available to manage all users and events.

### Telegram Bot

- Receive daily notifications if there are events with active reminders
- Command to view **all events**, sorted by date (soonest first)
- Command to view the **next three upcoming events**

### Roles

- **User:** register, add/edit birthdays, receive Telegram notifications  
- **Administrator:** manage all users and events via the web interface

## Technologies

- **Backend:** Java 17, Spring Boot, Spring Security  
- **Frontend:** Thymeleaf, Bootstrap 5  
- **Database:** MySQL + Flyway for migrations  
- **Telegram API:** TelegramBots Java Library  
- **Build:** Maven  
- **CI/CD:** GitHub Actions + Docker + docker-compose

## How to Run

1. Create a `.env` file in the project root with the following variables:

   ```env
   MYSQL_DATABASE=
   MYSQL_USER=
   MYSQL_PASSWORD=
   MYSQL_ROOT_PASSWORD=
   BIRTHDAY_BOT_NAME=
   BIRTHDAY_BOT_TOKEN=
   ```

2. Build and run the project:

    ```bash
    docker-compose build --no-cache
    docker-compose up -d
    ```

3. The application will be available at: [http://localhost:8080](http://localhost:8080)
   - `user@mail.ru` / `user`
   - `admin@mail.ru` / `admin`
