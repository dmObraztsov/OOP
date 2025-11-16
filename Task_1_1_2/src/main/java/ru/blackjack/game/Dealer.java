package ru.blackjack.game;

import ru.blackjack.cards.Card;
import ru.blackjack.cards.Shoe;

/**
 * Дилер в игре Блэкджек. Хранит скрытую карту и следует правилу добора до 17.
 */
public class Dealer extends Participant {

    private Card holeCard; // закрытая карта
    private boolean holeCardHidden; // true, пока карта скрыта

    /**
     * Создаёт дилера с отображаемым именем.
     *
     * @param displayName имя дилера для вывода
     */
    public Dealer(String displayName) {
        super(displayName);
        this.holeCardHidden = false;
    }

    /**
     * Сбрасывает руку дилера и скрытую карту.
     */
    public void resetHandAndHideHoleCard() {
        getHand().clear();
        this.holeCard = null;
        this.holeCardHidden = false;
    }

    /**
     * Получает открытую карту из вне и добавляет её в руку.
     *
     * @param card открытая карта
     */
    public void receiveOpenCardFromShoe(Card card) {
        getHand().add(card);
    }

    /**
     * Получает скрытую карту (hole) и помечает её как скрытую.
     *
     * @param card скрытая карта
     */
    public void receiveHoleCardFromShoe(Card card) {
        this.holeCard = card;
        this.holeCardHidden = true;
        // пока не добавляем в руку — добавим в момент раскрытия
    }

    /**
     * Показывает, скрыта ли hole-карта.
     *
     * @return {@code true}, если скрытая карта ещё не раскрыта; иначе {@code false}
     */
    public boolean isHoleCardHidden() {
        return holeCardHidden;
    }

    /**
     * Раскрывает скрытую карту и добавляет её в руку.
     */
    public void revealHoleCard() {
        if (holeCardHidden) {
            getHand().add(holeCard);
            holeCardHidden = false;
        }
    }

    /**
     * Возвращает необходимость добора по правилу дилера.
     * Дилер берёт карты, пока сумма строго меньше 17.
     *
     * @return {@code true}, если нужно добирать; иначе {@code false}
     */
    public boolean mustDrawAccordingToRules() {
        return getHand().bestValue() < 17;
    }

    /**
     * Возвращает скрытую карту, если она была выдана.
     *
     * @return скрытая карта или {@code null}, если её нет
     */
    public Card getHoleCardIfAny() {
        return holeCard;
    }
}
