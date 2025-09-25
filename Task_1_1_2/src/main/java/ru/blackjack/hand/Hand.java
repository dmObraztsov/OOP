package ru.blackjack.hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.blackjack.cards.Card;
import ru.blackjack.cards.Rank;

/**
 * Рука игрока/дилера в Блэкджеке.
 * Хранит карты и корректно считает сумму с учётом тузов.
 */
public class Hand {

    private final List<Card> cards = new ArrayList<>();

    public void add(Card card) {
        cards.add(card);
    }

    public void clear() {
        cards.clear();
    }

    public List<Card> viewCards() {
        return Collections.unmodifiableList(cards);
    }

    /**
     * Вычисляет лучшую сумму очков руки с учётом тузов.
     * Сначала все тузы считаются как 11,
     * затем по одному «схлопываются» до 1, если сумма превышает 21.
     *
     * @return наилучшая сумма очков
     */
    public int bestValue() {
        int sum = 0;
        int aces = 0;
        for (Card c : cards) {
            if (c.getRank().isAce()) {
                aces++;
                sum += 11;
            } else {
                sum += c.getRank().getBaseValue();
            }
        }
        while (sum > 21 && aces > 0) {
            sum -= 10; // один туз становится 1 вместо 11
            aces--;
        }
        return sum;
    }

    /**
     * Проверяет, является ли рука блэкджеком.
     *
     * @return {@code true}, если блэкджек; иначе {@code false}
     */
    public boolean hasBlackjack() {
        return cards.size() == 2 && bestValue() == 21;
    }

    public boolean isBusted() {
        return bestValue() > 21;
    }

    /**
     * Возвращает список текущих значений для каждой карты в руке.
     * Для не тузов используется их базовое значение. Для тузов часть считается как 11,
     * а оставшиеся — как 1, исходя из текущей оптимальной суммы руки.
     *
     * @return список значений карт в текущем подсчёте
     */
    public List<Integer> computePerCardValuesAsCurrentlyCounted() {
        int sum = 0;
        int totalAces = 0;
        for (Card c : cards) {
            if (c.getRank().isAce()) {
                totalAces++;
                sum += 11;
            } else {
                sum += c.getRank().getBaseValue();
            }
        }
        int acesCountedAsEleven = totalAces;
        while (sum > 21 && acesCountedAsEleven > 0) {
            sum -= 10;
            acesCountedAsEleven--;
        }

        List<Integer> values = new ArrayList<>(cards.size());
        int remainingAcesAs11 = acesCountedAsEleven;
        for (Card c : cards) {
            if (c.getRank().isAce()) {
                if (remainingAcesAs11 > 0) {
                    values.add(11);
                    remainingAcesAs11--;
                } else {
                    values.add(1);
                }
            } else {
                values.add(c.getRank().getBaseValue());
            }
        }
        return values;
    }
}
