package com.belnitskii.birthdayreminderbot;

import com.belnitskii.birthdayreminderbot.config.BotConfig;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            logger.info("Сообщение от пользователя {} (ID: {}): {}", update.getMessage().getFrom().getFirstName(), update.getMessage().getChatId(), update.getMessage().getText());
            switch (messageText) {
                case "/start":
                    sendMessage(chatId,"Привет! Я ваш Telegram-бот. Как я могу помочь?");
                    break;
                case "/help":
                    sendMessage(chatId,"Доступные команды:\n/start - Запуск бота\n/help - Список команд");
                    break;
                default:
                    sendMessage(chatId,"Извините, я не понимаю эту команду. Попробуйте /help.");
            }
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

