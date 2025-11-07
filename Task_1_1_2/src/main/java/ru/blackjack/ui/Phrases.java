package ru.blackjack.ui;


public interface Phrases {
    String welcome();
    String roundPrefix();
    String dealt();
    String yourCardsPrefix();
    String dealerCardsPrefix();
    String closedCardToken();
    String greaterSignWithSpaces();

    String playerTurnHeader();
    String dealerTurnHeader();

    String askHitOrStand();
    String playerDrewPrefix();
    String dealerRevealsPrefix();
    String dealerDrewPrefix();
    String playerBusted();
    String dealerBusted();

    String roundPlayerWinPrefix();
    String roundPlayerLosePrefix();
    String roundPushPrefix();

    String scoreSuffixInYourFavor(int playerWins, int dealerWins);
    String scoreSuffixInDealerFavor(int playerWins, int dealerWins);
    String scoreSuffixTied(int playerWins, int dealerWins);

    String playAgain();
    String goodbye(int playerWins, int dealerWins);
    String shuffleNotice();
}
