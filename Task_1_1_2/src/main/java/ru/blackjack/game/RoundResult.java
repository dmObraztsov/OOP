package ru.blackjack.game;

/**
 * Итог раунда в Блэкджеке.
 */
public enum RoundResult {
    /** Раунд выиграл игрок. */
    PLAYER_WINS,
    /** Раунд выиграл дилер. */
    DEALER_WINS,
    /** Ничья в раунде. */
    PUSH
}
