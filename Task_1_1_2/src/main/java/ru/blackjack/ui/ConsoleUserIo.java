package ru.blackjack.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import ru.blackjack.cards.Card;
import ru.blackjack.game.Dealer;
import ru.blackjack.game.Participant;

public class ConsoleUserIo {

    private final Scanner scanner;
    private final Phrases phrases;

    public ConsoleUserIo(Scanner scanner, Phrases phrases) {
        this.scanner = scanner;
        this.phrases = phrases;
    }

    public void printToUserConsoleWelcomeMessage() {
        println(phrases.welcome());
        println("");
    }

    public void printToUserConsoleRoundHeader(int roundNumber) {
        println(phrases.roundPrefix() + roundNumber);
    }

    public void printToUserConsoleDealerDealsCards() {
        println(phrases.dealt());
    }

    public void printToUserConsoleHandsAfterDeal(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithHiddenHole(dealer));
        println("");
    }

    public void printToUserConsolePlayerTurnHeader() {
        println(phrases.playerTurnHeader());
    }

    public int askUserHitOrStand() {
        while (true) {
            print(phrases.askHitOrStand());
            String in = scanner.nextLine().trim();
            if ("1".equals(in)) return 1;
            if ("0".equals(in)) return 0;
        }
    }

    public void printToUserConsolePlayerDrewCard(Card card) {
        println(phrases.playerDrewPrefix() + buildSingleCardWithValueUnknown(card));
    }

    public void printToUserConsoleHandsWithHiddenDealerHole(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithHiddenHole(dealer));
    }

    public void printToUserConsolePlayerBusted() {
        println(phrases.playerBusted());
    }

    public void printToUserConsoleDealerTurnHeader() {
        println(phrases.dealerTurnHeader());
    }

    public void printToUserConsoleRevealHoleCard(Dealer dealer) {
        if (dealer.isHoleCardHidden() && dealer.getHoleCardIfAny() != null) {
            println(phrases.dealerRevealsPrefix()
                    + buildSingleCardWithDynamicValueForDealer(dealer.getHoleCardIfAny(), dealer));
        }
    }

    public void printToUserConsoleHandsWithSums(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithSum(dealer));
    }

    public void printToUserConsoleDealerDrewCard(Card card) {
        println(phrases.dealerDrewPrefix() + buildSingleCardWithValueUnknown(card));
    }

    public void printToUserConsoleDealerBusted() {
        println(phrases.dealerBusted());
    }

    public boolean askUserWhetherPlayAnotherRound() {
        while (true) {
            print(phrases.playAgain());
            String in = scanner.nextLine().trim();
            if ("1".equals(in)) return true;
            if ("0".equals(in)) return false;
        }
    }

    public void printToUserConsoleGoodbyeWithFinalScore(int playerWins, int dealerWins) {
        println(phrases.goodbye(playerWins, dealerWins));
    }

    public void printToUserConsoleEmptyLine() {
        println("");
    }

    public void printToUserConsoleShoeShuffleHappened() {
        println(phrases.shuffleNotice());
        println("");
    }

    public void printToUserConsoleRoundWinDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(phrases.roundPlayerWinPrefix(), playerWins, dealerWins));
    }

    public void printToUserConsoleRoundLoseDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(phrases.roundPlayerLosePrefix(), playerWins, dealerWins));
    }

    public void printToUserConsoleRoundPushDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(phrases.roundPushPrefix(), playerWins, dealerWins));
    }

    private String buildPlayerHandLineWithSum(Participant player) {
        String list = buildCardsListWithPerCardValues(player);
        int sum = player.getHand().bestValue();
        return phrases.yourCardsPrefix() + list + phrases.greaterSignWithSpaces() + sum;
    }

    private String buildDealerHandLineWithHiddenHole(Dealer dealer) {
        String left = dealer.getHand().viewCards().isEmpty() ? "" :
                buildSingleCardWithDynamicValueForDealer(dealer.getHand().viewCards().get(0), dealer);
        String list = "[" + left + (left.isEmpty() ? "" : ", ") + phrases.closedCardToken() + "]";
        return phrases.dealerCardsPrefix() + list;
    }

    private String buildDealerHandLineWithSum(Dealer dealer) {
        String list = buildCardsListWithPerCardValues(dealer);
        int sum = dealer.getHand().bestValue();
        return phrases.dealerCardsPrefix() + list + phrases.greaterSignWithSpaces() + sum;
    }

    private String buildCardsListWithPerCardValues(Participant participant) {
        var cards = participant.getHand().viewCards();
        var values = participant.getHand().computePerCardValuesAsCurrentlyCounted();
        String joined = cards.isEmpty() ? "" : joinCardsWithValues(cards, values);
        return "[" + joined + "]";
    }

    private String joinCardsWithValues(List<ru.blackjack.cards.Card> cards, List<Integer> values) {
        return cards.stream()
                .map(c -> c.buildDisplayNameWithoutValue() + " (" +
                        values.get(cards.indexOf(c)) + ")")
                .collect(Collectors.joining(", "));
    }

    private String buildSingleCardWithValueUnknown(ru.blackjack.cards.Card card) {
        return card.buildDisplayNameWithoutValue()
                + " (" + (card.getRank().isAce() ? "11" : card.getRank().getBaseValue()) + ")";
    }

    private String buildSingleCardWithDynamicValueForDealer(ru.blackjack.cards.Card card, Dealer dealer) {
        var cards = dealer.getHand().viewCards();
        var values = dealer.getHand().computePerCardValuesAsCurrentlyCounted();
        int idx = cards.indexOf(card);
        int v = (idx >= 0) ? values.get(idx)
                : (card.getRank().isAce() ? 11 : card.getRank().getBaseValue());
        return card.buildDisplayNameWithoutValue() + " (" + v + ")";
    }

    private String buildScoreLineWithFavor(String prefix, int playerWins, int dealerWins) {
        String suffix;
        if (playerWins > dealerWins) {
            suffix = phrases.scoreSuffixInYourFavor(playerWins, dealerWins);
        } else if (dealerWins > playerWins) {
            suffix = phrases.scoreSuffixInDealerFavor(playerWins, dealerWins);
        } else {
            suffix = phrases.scoreSuffixTied(playerWins, dealerWins);
        }
        return prefix + suffix;
    }

    private void println(String s) { System.out.println(s); }
    private void print(String s) { System.out.print(s); }
}
