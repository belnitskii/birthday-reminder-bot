package com.belnitskii.birthdayreminderbot.telegram;

public enum TelegramCommands {
    START("/start",
            "Start",
            generateStartMessage(),
            "Start the bot"),

    ABOUT("/about",
            "About",
            generateAboutMessage(),
            "Bot information"),

    LIST("/list",
            "List",
            "Get list of birthdays",
            "Get list of birthdays"),

    NEXT("/next",
            "Next",
            "Show next few birthdays",
            "Show next few birthdays"),

    HELP("/help",
            "Help",
            generateHelpMessage(),
            "Show commands"),

    DEFAULT("/default",
            null,
            "Unknown command. Type /help to get a list of available commands..",
            "Unknown command");

    public final String command;
    public final String buttonName;
    public final String message;
    public final String description;

    TelegramCommands(String command, String buttonName, String message, String description) {
        this.command = command;
        this.buttonName = buttonName;
        this.message = message;
        this.description = description;
    }

    private static String generateStartMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Welcome");
        return sb.toString();
    }

    private static String generateHelpMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>This bot shows exchange rates.</b>\n")
                .append("Available commands:\n")
                .append("<b>").append(START.command).append("</b> — ").append(START.description).append("\n")
                .append("<b>").append(ABOUT.command).append("</b> — ").append(ABOUT.description).append("\n")
                .append("<b>").append(LIST.command).append("</b> — ").append(LIST.description).append("\n")
                .append("<b>").append(NEXT.command).append("</b> — ").append(NEXT.description);
        return sb.toString();
    }


    private static String generateAboutMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Something about");
        return sb.toString();
    }
}
