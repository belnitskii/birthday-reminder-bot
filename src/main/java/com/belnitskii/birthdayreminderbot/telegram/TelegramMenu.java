package com.belnitskii.birthdayreminderbot.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.belnitskii.birthdayreminderbot.telegram.TelegramCommands.*;

@Component
public class TelegramMenu {

    /**
     * Создаёт и возвращает список доступных команд для Telegram-бота.
     * <p>
     * Эти команды отображаются пользователю в интерфейсе Telegram как кнопки с командами
     * (при вводе "/", Telegram покажет их автоматически).
     *
     * @return объект {@link SetMyCommands}, содержащий команды бота с описаниями
     */
    public SetMyCommands createBotCommands() {
        List<BotCommand> commands = Arrays.stream(TelegramCommands.values())
                .filter(cmd -> !Objects.equals(cmd.command, DEFAULT.command))
                .map(cmd -> new BotCommand(cmd.command, cmd.description))
                .toList();
        return new SetMyCommands(commands, new BotCommandScopeDefault(), null);
    }

    public ReplyKeyboardMarkup createReplyKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton(LIST.buttonName));
        row1.add(new KeyboardButton(NEXT.buttonName));
        keyboardRows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton(HELP.buttonName));
        row2.add(new KeyboardButton(ABOUT.buttonName));
        keyboardRows.add(row2);

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
