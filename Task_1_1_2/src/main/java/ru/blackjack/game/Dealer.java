package ru.blackjack.game;

import ru.blackjack.cards.Card;
import ru.blackjack.cards.Shoe;


public class Dealer extends Participant {

    private Card holeCard; // закрытая карта
    private boolean holeCardHidden; // true пока карта скрыта

    public Dealer(String displayName) {
        super(displayName);
        this.holeCardHidden = false;
    }

    public void resetHandAndHideHoleCard() {
        getHand().clear();
        this.holeCard = null;
        this.holeCardHidden = false;
    }

    public void receiveOpenCardFromShoe(Card card) {
        getHand().add(card);
    }

    public void receiveOpenCardFromShoe(Shoe shoe) {
        getHand().add(shoe.drawTopCard());
    }

    public void receiveHoleCardFromShoe(Card card) {
        this.holeCard = card;
        this.holeCardHidden = true;
        // пока не добавляем в руку — добавим в момент раскрытия
    }

    public boolean isHoleCardHidden() {
        return holeCardHidden;
    }

    public void revealHoleCard() {
        if (holeCardHidden) {
            getHand().add(holeCard);
            holeCardHidden = false;
        }
    }

    /**
     * Дилер берёт карты, пока сумма < 17
     */
    public boolean mustDrawAccordingToRules() {
        return getHand().bestValue() < 17;
    }

    public Card getHoleCardIfAny() {
        return holeCard;
    }
}
