package ru.blackjack.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.blackjack.cards.Card;
import ru.blackjack.cards.Rank;
import ru.blackjack.cards.Suit;

/**
 * Тесты базового участника игры {@link Participant}.
 * Проверяем получение карты и сброс руки.
 */
class ParticipantTest {

    /**
     * Проверяет, что участник получает карту и что метод сброса очищает руку.
     */
    @Test
    void receiveAndResetHand() {
        Participant p = new Participant("Игрок");
        assertEquals("Игрок", p.getDisplayName());
        assertEquals(0, p.getHand().viewCards().size());

        p.receiveCardFromShoe(new Card(Suit.HEARTS, Rank.SEVEN));
        assertEquals(1, p.getHand().viewCards().size());
        p.resetHand();
        assertEquals(0, p.getHand().viewCards().size());
    }
}
