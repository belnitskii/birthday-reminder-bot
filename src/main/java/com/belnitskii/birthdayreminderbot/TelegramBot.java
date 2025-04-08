package com.belnitskii.birthdayreminderbot;

import com.belnitskii.birthdayreminderbot.config.BotConfig;
import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.service.PersonService;
import com.belnitskii.birthdayreminderbot.service.TelegramAuthTokenService;
import com.belnitskii.birthdayreminderbot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final BotConfig botConfig;
    private final PersonService personService;
    private final UserService userService;
    private final TelegramAuthTokenService authTokenService;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    public TelegramBot(BotConfig botConfig, PersonService personService, UserService userService, TelegramAuthTokenService authTokenService) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.personService = personService;
        this.userService = userService;
        this.authTokenService = authTokenService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            logger.info("Сообщение от пользователя {} (ID: {}): {}", update.getMessage().getFrom().getFirstName(), update.getMessage().getChatId(), update.getMessage().getText());
            if (text.startsWith("/link")) {
                String[] parts = text.split(" ");
                if (parts.length == 2) {
                    String token = parts[1];
                    Optional<User> userOpt = authTokenService.connectTelegram(token, update.getMessage().getFrom().getId());
                    if (userOpt.isPresent()) {
                        sendMessage(chatId, "Аккаунт успешно привязан!");
                    } else {
                        sendMessage(chatId, "Пользователь не найден.");
                    }
                } else {
                    sendMessage(chatId, "Используйте: /link <your_token>");
                }
            }
            switch (text) {
                case "/start":
                    sendMessage(chatId,"Привет! Я ваш Telegram-бот. Как я могу помочь?");
                    break;
                case "/list":
                    getList(update);
                    break;
                case "/help":
                    sendMessage(chatId,"Доступные команды:\n/start - Запуск бота\n/help - Список команд");
                    break;
                case "/get":
                    sendMessage(chatId, personService.getPerson(1L).toString());;
                    break;
                default:
                    sendMessage(chatId,"Извините, я не понимаю эту команду. Попробуйте /help.");
            }
        }
    }

    private void getList(Update update) {
        long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        User user = userService.getCurrentUserByTelegramId(telegramUserId);

        if (user != null) {
            List<Person> people = personService.getPersonsByTelegramId(telegramUserId);
            if (people.isEmpty()) {
                sendMessage(chatId, "У вас нет записей.");
            } else {
                StringBuilder sb = new StringBuilder("Ваши записи:\n\n");
                for (Person person : people) {
                    sb.append("👤 ").append(person.getName())
                            .append(" — ").append(person.getBirthdayDate()).append("\n");
                }
                sendMessage(chatId, sb.toString());
            }
        } else {
            sendMessage(chatId, "Пожалуйста, сначала привяжите аккаунт: /link <username>");
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }
}