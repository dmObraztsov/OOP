package ru.blackjack.ui;


public final class RussianPhrases implements Phrases {

    @Override public String welcome() { return "Добро пожаловать в Блэкджек!"; }
    @Override public String roundPrefix() { return "Раунд "; }
    @Override public String dealt() { return "Дилер раздал карты"; }
    @Override public String yourCardsPrefix() { return "Ваши карты: "; }
    @Override public String dealerCardsPrefix() { return "Карты дилера: "; }
    @Override public String closedCardToken() { return "<закрытая карта>"; }
    @Override public String greaterSignWithSpaces() { return " > "; }

    @Override public String playerTurnHeader() { return "Ваш ход\n-------"; }
    @Override public String dealerTurnHeader() { return "Ход дилера\n-------"; }

    @Override public String askHitOrStand() {
        return "Введите \"1\", чтобы взять карту, и \"0\", чтобы остановиться: ";
    }
    @Override public String playerDrewPrefix() { return "Вы открыли карту "; }
    @Override public String dealerRevealsPrefix() { return "Дилер открывает закрытую карту "; }
    @Override public String dealerDrewPrefix() { return "Дилер открывает карту "; }
    @Override public String playerBusted() { return "Перебор у игрока: больше 21."; }
    @Override public String dealerBusted() { return "Перебор у дилера: больше 21."; }

    @Override public String roundPlayerWinPrefix() { return "Вы выиграли раунд!"; }
    @Override public String roundPlayerLosePrefix() { return "Вы проиграли раунд."; }
    @Override public String roundPushPrefix() { return "Ничья в раунде."; }

    @Override
    public String scoreSuffixInYourFavor(int playerWins, int dealerWins) {
        return String.format(" Счет %d:%d в вашу пользу.", playerWins, dealerWins);
    }
    @Override
    public String scoreSuffixInDealerFavor(int playerWins, int dealerWins) {
        return String.format(" Счет %d:%d в пользу дилера.", playerWins, dealerWins);
    }
    @Override
    public String scoreSuffixTied(int playerWins, int dealerWins) {
        return String.format(" Счет %d:%d.", playerWins, dealerWins);
    }

    @Override public String playAgain() { return "Сыграем ещё? Введите \"1\" — да, \"0\" — нет: "; }
    @Override public String goodbye(int playerWins, int dealerWins) {
        return String.format("Игра завершена. Финальный счет %d:%d. Спасибо за игру!",
                playerWins, dealerWins);
    }
    @Override public String shuffleNotice() {
        return "Перетасовали колоды: в башмаке мало карт.";
    }
}
