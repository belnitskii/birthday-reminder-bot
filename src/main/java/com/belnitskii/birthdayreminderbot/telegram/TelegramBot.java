package com.belnitskii.birthdayreminderbot.telegram;

import com.belnitskii.birthdayreminderbot.config.BotConfig;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.service.*;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@Component
public class TelegramBot extends Executor {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
    private final BotConfig botConfig;
    private final TelegramAuthTokenService authTokenService;
    private final TelegramNotificationService telegramNotificationService;
    private final TelegramMenu telegramMenu;
    private final TelegramMessageBuilderService telegramMessageBuilderService;
    private final Map<String, BiConsumer<Long, Update>> commandHandlers = new HashMap<>();

    public TelegramBot(BotConfig botConfig, TelegramAuthTokenService authTokenService, TelegramNotificationService telegramNotificationService, TelegramMenu telegramMenu, TelegramMessageBuilderService telegramMessageBuilderService) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.authTokenService = authTokenService;
        this.telegramNotificationService = telegramNotificationService;
        this.telegramMenu = telegramMenu;
        this.telegramMessageBuilderService = telegramMessageBuilderService;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @PostConstruct
    @SneakyThrows
    public void initCommands() {
        executeSafely(telegramMenu.createBotCommands());
        commandHandlers.put(TelegramCommands.START.command, (chatId, update) -> sendMessageWithKeyboard(chatId, TelegramCommands.START.message));
        commandHandlers.put(TelegramCommands.START.buttonName, (chatId, update) -> sendMessageWithKeyboard(chatId, TelegramCommands.START.message));
        commandHandlers.put(TelegramCommands.HELP.command, (chatId, update) -> sendMessageWithKeyboard(chatId, TelegramCommands.HELP.message));
        commandHandlers.put(TelegramCommands.HELP.buttonName, (chatId, update) -> sendMessageWithKeyboard(chatId, TelegramCommands.HELP.message));
        commandHandlers.put(TelegramCommands.ABOUT.command, (chatId, update) -> sendMessageWithKeyboard(chatId, TelegramCommands.ABOUT.message));
        commandHandlers.put(TelegramCommands.ABOUT.buttonName, (chatId, update) -> sendMessageWithKeyboard(chatId, TelegramCommands.ABOUT.message));
        commandHandlers.put(TelegramCommands.LIST.command, (chatId, update) -> getList(update));
        commandHandlers.put(TelegramCommands.LIST.buttonName, (chatId, update) -> getList(update));
        commandHandlers.put(TelegramCommands.NEXT.buttonName, (chatId, update) -> getNext(update));
        commandHandlers.put(TelegramCommands.NEXT.buttonName, (chatId, update) -> getNext(update));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            logger.info("Message from user {} (ID: {}): {}", update.getMessage().getFrom().getFirstName(), update.getMessage().getChatId(), update.getMessage().getText());
            if (commandHandlers.containsKey(text)) {
                commandHandlers.get(text).accept(chatId, update);
                return;
            }

            if (text.startsWith("/link")) {
                String[] parts = text.split(" ");
                if (parts.length == 2) {
                    String token = parts[1];
                    Optional<User> userOpt = authTokenService.connectTelegram(token, update.getMessage().getFrom().getId());
                    if (userOpt.isPresent()) {
                        sendMessage(chatId, "Account successfully linked!");
                    } else {
                        sendMessage(chatId, "User not found.");
                    }
                } else {
                    sendMessage(chatId, "Use: /link <your_token>");
                }
            }
        }
    }

    private void getList(Update update) {
        sendMessageHtml(update.getMessage().getChatId(),
                telegramMessageBuilderService.buildBirthdayListMessage(update.getMessage().getChatId()));
    }

    private void getNext(Update update) {
        sendMessage(update.getMessage().getChatId(),
                telegramMessageBuilderService.buildBirthdayNextMessage(update.getMessage().getChatId()));
    }

    @Scheduled(cron = "0 0 9 * * *")
    private void sendDailyMessage() throws InterruptedException {
        for (SendMessage sendMessage : telegramNotificationService.getDailyNotification()) {
            sendMessage.setParseMode("Markdown");
            executeSafely(sendMessage);
            Thread.sleep(1000);
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("Markdown");
        executeSafely(message);
    }

    private void sendMessageHtml(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("HTML");
        executeSafely(message);
    }

    private void sendMessageWithKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("HTML");
        message.setReplyMarkup(telegramMenu.createReplyKeyboard());
        executeSafely(message);
    }
}