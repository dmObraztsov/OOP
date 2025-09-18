package ru.blackjack.ui;

/**
 *
 */
public final class RussianPhrases {
    private RussianPhrases() {
    }

    public static final String WELCOME = "Добро пожаловать в Блэкджек!";
    public static final String USING_ONE_DECK = "Используется одна колода.";
    public static final String ROUND = "Раунд ";
    public static final String DEALT = "Дилер раздал карты";
    public static final String YOUR_CARDS = "Ваши карты: ";
    public static final String DEALER_CARDS = "Карты дилера: ";
    public static final String CLOSED_CARD = "<закрытая карта>";
    public static final String GREATER_SIGN = " > ";

    public static final String PLAYER_TURN_HEADER = "Ваш ход\n-------";
    public static final String DEALER_TURN_HEADER = "Ход дилера\n-------";

    public static final String ASK_HIT_OR_STAND =
            "Введите \"1\", чтобы взять карту, и \"0\", чтобы остановиться: ";
    public static final String PLAYER_DREW = "Вы открыли карту ";
    public static final String DEALER_REVEALS = "Дилер открывает закрытую карту ";
    public static final String DEALER_DREW = "Дилер открывает карту ";
    public static final String PLAYER_BUSTED = "Перебор у игрока: больше 21.";
    public static final String DEALER_BUSTED = "Перебор у дилера: больше 21.";

    public static final String ROUND_PLAYER_WIN_PREFIX = "Вы выиграли раунд!";
    public static final String ROUND_PLAYER_LOSE_PREFIX = "Вы проиграли раунд.";
    public static final String ROUND_PUSH_PREFIX = "Ничья в раунде.";

    public static final String SCORE_SUFFIX_IN_YOUR_FAVOR = " Счет %d:%d в вашу пользу.";
    public static final String SCORE_SUFFIX_IN_DEALER_FAVOR = " Счет %d:%d в пользу дилера.";
    public static final String SCORE_SUFFIX_TIED = " Счет %d:%d.";

    public static final String PLAY_AGAIN =
            "Сыграем ещё? Введите \"1\" — да, \"0\" — нет: ";
    public static final String GOODBYE = "Игра завершена. Финальный счет %d:%d. Спасибо за игру!";
    public static final String SHUFFLE = "Перетасовали колоды: в башмаке мало карт.";
}
