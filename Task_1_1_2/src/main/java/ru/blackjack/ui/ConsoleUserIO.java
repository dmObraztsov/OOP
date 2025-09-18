package ru.blackjack.ui;

import ru.blackjack.cards.Card;
import ru.blackjack.game.Dealer;
import ru.blackjack.game.Participant;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class ConsoleUserIO {

    private final Scanner scanner;

    public ConsoleUserIO(Scanner scanner) {
        this.scanner = scanner;
    }

    public void printToUserConsoleWelcomeMessage() {
        println(RussianPhrases.WELCOME);
        println("");
    }

    public void printToUserConsoleRoundHeader(int roundNumber) {
        println(RussianPhrases.ROUND + roundNumber);
    }

    public void printToUserConsoleDealerDealsCards() {
        println(RussianPhrases.DEALT);
    }

    public void printToUserConsoleHandsAfterDeal(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithHiddenHole(dealer));
        println("");
    }

    public void printToUserConsolePlayerTurnHeader() {
        println(RussianPhrases.PLAYER_TURN_HEADER);
    }

    public int askUserHitOrStand() {
        while (true) {
            print(RussianPhrases.ASK_HIT_OR_STAND);
            String in = scanner.nextLine().trim();
            if ("1".equals(in)) return 1;
            if ("0".equals(in)) return 0;
        }
    }

    public void printToUserConsolePlayerDrewCard(Card card) {
        println(RussianPhrases.PLAYER_DREW + buildSingleCardWithValueUnknown(card));
    }

    public void printToUserConsoleHandsWithHiddenDealerHole(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithHiddenHole(dealer));
    }

    public void printToUserConsolePlayerBusted() {
        println(RussianPhrases.PLAYER_BUSTED);
    }

    public void printToUserConsoleDealerTurnHeader() {
        println(RussianPhrases.DEALER_TURN_HEADER);
    }

    public void printToUserConsoleRevealHoleCard(Dealer dealer) {
        if (dealer.isHoleCardHidden() && dealer.getHoleCardIfAny() != null) {
            println(RussianPhrases.DEALER_REVEALS + buildSingleCardWithDynamicValueForDealer(dealer.getHoleCardIfAny(), dealer));
        }
    }

    public void printToUserConsoleHandsWithSums(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithSum(dealer));
    }

    public void printToUserConsoleDealerDrewCard(Card card) {
        println(RussianPhrases.DEALER_DREW + buildSingleCardWithValueUnknown(card));
    }

    public void printToUserConsoleDealerBusted() {
        println(RussianPhrases.DEALER_BUSTED);
    }

    public boolean askUserWhetherPlayAnotherRound() {
        while (true) {
            print(RussianPhrases.PLAY_AGAIN);
            String in = scanner.nextLine().trim();
            if ("1".equals(in)) return true;
            if ("0".equals(in)) return false;
        }
    }

    public void printToUserConsoleGoodbyeWithFinalScore(int playerWins, int dealerWins) {
        println(String.format(RussianPhrases.GOODBYE, playerWins, dealerWins));
    }

    public void printToUserConsoleEmptyLine() {
        println("");
    }

    public void printToUserConsoleShoeShuffleHappened() {
        println(RussianPhrases.SHUFFLE);
        println("");
    }

    public void printToUserConsoleRoundWinDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(
                RussianPhrases.ROUND_PLAYER_WIN_PREFIX, playerWins, dealerWins));
    }

    public void printToUserConsoleRoundLoseDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(
                RussianPhrases.ROUND_PLAYER_LOSE_PREFIX, playerWins, dealerWins));
    }

    public void printToUserConsoleRoundPushDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(
                RussianPhrases.ROUND_PUSH_PREFIX, playerWins, dealerWins));
    }

    private String buildPlayerHandLineWithSum(Participant player) {
        String list = buildCardsListWithPerCardValues(player);
        int sum = player.getHand().bestValue();
        return RussianPhrases.YOUR_CARDS + list + RussianPhrases.GREATER_SIGN + sum;
    }

    private String buildDealerHandLineWithHiddenHole(Dealer dealer) {
        // Первая открытая карта дилера + <закрытая карта>
        String left = dealer.getHand().viewCards().isEmpty() ? "" :
                buildSingleCardWithDynamicValueForDealer(dealer.getHand().viewCards().get(0), dealer);
        String list = "[" + left + (left.isEmpty() ? "" : ", ") + RussianPhrases.CLOSED_CARD + "]";
        return RussianPhrases.DEALER_CARDS + list;
    }

    private String buildDealerHandLineWithSum(Dealer dealer) {
        String list = buildCardsListWithPerCardValues(dealer);
        int sum = dealer.getHand().bestValue();
        return RussianPhrases.DEALER_CARDS + list + RussianPhrases.GREATER_SIGN + sum;
    }

    private String buildCardsListWithPerCardValues(Participant participant) {
        List<Card> cards = participant.getHand().viewCards();
        List<Integer> values = participant.getHand().computePerCardValuesAsCurrentlyCounted();

        String joined = "";
        if (!cards.isEmpty()) {
            joined = joinCardsWithValues(cards, values);
        }
        return "[" + joined + "]";
    }

    private String joinCardsWithValues(List<Card> cards, List<Integer> values) {
        return cards.stream()
                .map(c -> c.buildDisplayNameWithoutValue() + " (" + values.get(cards.indexOf(c)) + ")")
                .collect(Collectors.joining(", "));
    }

    private String buildSingleCardWithValueUnknown(Card card) {
        return card.buildDisplayNameWithoutValue()
                + " (" + (card.getRank().isAce() ? "11" : card.getRank().getBaseValue()) + ")";
    }

    private String buildSingleCardWithDynamicValueForDealer(Card card, Dealer dealer) {
        // Чтобы корректно показать туз как 1/11 именно в КОНТЕКСТЕ текущей руки дилера:
        List<Card> cards = dealer.getHand().viewCards();
        List<Integer> values = dealer.getHand().computePerCardValuesAsCurrentlyCounted();
        int idx = cards.indexOf(card);
        int v;
        if (idx >= 0) {
            v = values.get(idx);
        } else {
            // Если карта ещё не в руке (например, hole, до reveal), покажем "сырой" номинал
            v = card.getRank().isAce() ? 11 : card.getRank().getBaseValue();
        }
        return card.buildDisplayNameWithoutValue() + " (" + v + ")";
    }

    private String buildScoreLineWithFavor(String prefix, int playerWins, int dealerWins) {
        String suffix;
        if (playerWins > dealerWins) {
            suffix = String.format(RussianPhrases.SCORE_SUFFIX_IN_YOUR_FAVOR, playerWins, dealerWins);
        } else if (dealerWins > playerWins) {
            suffix = String.format(RussianPhrases.SCORE_SUFFIX_IN_DEALER_FAVOR, playerWins, dealerWins);
        } else {
            suffix = String.format(RussianPhrases.SCORE_SUFFIX_TIED, playerWins, dealerWins);
        }
        return prefix + suffix;
    }

    private void println(String s) {
        System.out.println(s);
    }

    private void print(String s) {
        System.out.print(s);
    }
}
