package ru.blackjack.game;

import java.util.Objects;
import ru.blackjack.cards.Card;
import ru.blackjack.cards.Shoe;
import ru.blackjack.hand.Hand;

/**
 * Участник игры (общая логика для игрока и дилера).
 * Хранит имя и руку, умеет получать карты и сбрасывать руку.
 */
public class Participant {

    private final String displayName;
    private final Hand hand = new Hand();

    /**
     * Создаёт участника с отображаемым именем.
     *
     * @param displayName имя участника для вывода
     * @throws NullPointerException если {@code displayName} равен {@code null}
     */
    public Participant(String displayName) {
        this.displayName = Objects.requireNonNull(displayName);
    }

    /**
     * Возвращает имя участника.
     *
     * @return отображаемое имя
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Возвращает руку участника.
     *
     * @return рука с картами
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Очищает руку участника.
     */
    public void resetHand() {
        hand.clear();
    }

    /**
     * Получает карту и добавляет её в руку.
     *
     * @param card карта, которую нужно добавить
     */
    public void receiveCardFromShoe(Card card) {
        hand.add(card);
    }
}
