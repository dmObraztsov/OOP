package ru.blackjack.game;

import ru.blackjack.cards.Card;
import ru.blackjack.cards.Shoe;
import ru.blackjack.ui.ConsoleUserIO;

import java.util.Objects;

public class BlackJackGame {

    private final ConsoleUserIO userIO;
    private final Shoe shoe;
    private final Dealer dealer;
    private final Participant player;

    private int roundNumber = 0;
    private int playerWins = 0;
    private int dealerWins = 0;

    public BlackJackGame(ConsoleUserIO userIO) {
        this.userIO = Objects.requireNonNull(userIO);
        this.shoe = new Shoe();            // всегда одна колода
        this.dealer = new Dealer("Дилер");
        this.player = new Participant("Игрок");
    }

    public void startGameLoopUntilUserStops() {
        userIO.printToUserConsoleWelcomeMessage();
        boolean keepPlaying = true;
        while (keepPlaying) {
            roundNumber++;
            RoundResult result = runSingleRoundAndReturnResult();
            updateScoreAndInformUser(result);
            keepPlaying = userIO.askUserWhetherPlayAnotherRound();
            userIO.printToUserConsoleEmptyLine();
        }

        userIO.printToUserConsoleGoodbyeWithFinalScore(playerWins, dealerWins);
    }

    private RoundResult runSingleRoundAndReturnResult() {
        ensureShoeHasEnoughCardsOrReshuffle();

        player.resetHand();
        dealer.resetHandAndHideHoleCard();

        userIO.printToUserConsoleRoundHeader(roundNumber);
        userIO.printToUserConsoleDealerDealsCards();

        player.receiveCardFromShoe(shoe.drawTopCard());
        dealer.receiveOpenCardFromShoe(shoe.drawTopCard());
        player.receiveCardFromShoe(shoe.drawTopCard());
        dealer.receiveHoleCardFromShoe(shoe.drawTopCard());

        userIO.printToUserConsoleHandsAfterDeal(player, dealer);

        boolean playerBJ = player.getHand().hasBlackjack();
        boolean dealerBJ = dealer.getHand().hasBlackjack();

        if (playerBJ || dealerBJ) {
            dealer.revealHoleCard();
            userIO.printToUserConsoleRevealHoleCard(dealer);
            userIO.printToUserConsoleHandsWithSums(player, dealer);

            if (playerBJ && dealerBJ) return RoundResult.PUSH;
            if (playerBJ) return RoundResult.PLAYER_WINS;
            return RoundResult.DEALER_WINS;
        }

        userIO.printToUserConsolePlayerTurnHeader();
        boolean playerStands = false;
        while (!playerStands) {
            int decision = userIO.askUserHitOrStand();
            if (decision == 1) {
                Card newCard = shoe.drawTopCard();
                player.receiveCardFromShoe(newCard);
                userIO.printToUserConsolePlayerDrewCard(newCard);
                userIO.printToUserConsoleHandsWithHiddenDealerHole(player, dealer);
                if (player.getHand().isBusted()) {
                    userIO.printToUserConsolePlayerBusted();
                    dealer.revealHoleCard();
                    userIO.printToUserConsoleRevealHoleCard(dealer);
                    userIO.printToUserConsoleHandsWithSums(player, dealer);
                    return RoundResult.DEALER_WINS;
                }
            } else {
                playerStands = true;
            }
        }

        userIO.printToUserConsoleDealerTurnHeader();
        dealer.revealHoleCard();
        userIO.printToUserConsoleRevealHoleCard(dealer);
        userIO.printToUserConsoleHandsWithSums(player, dealer);

        while (dealer.mustDrawAccordingToRules()) {
            Card card = shoe.drawTopCard();
            dealer.receiveOpenCardFromShoe(card);
            userIO.printToUserConsoleDealerDrewCard(card);
            userIO.printToUserConsoleHandsWithSums(player, dealer);
            if (dealer.getHand().isBusted()) {
                userIO.printToUserConsoleDealerBusted();
                return RoundResult.PLAYER_WINS;
            }
        }

        int playerSum = player.getHand().bestValue();
        int dealerSum = dealer.getHand().bestValue();
        if (playerSum > dealerSum) return RoundResult.PLAYER_WINS;
        if (dealerSum > playerSum) return RoundResult.DEALER_WINS;
        return RoundResult.PUSH;
    }

    private void updateScoreAndInformUser(RoundResult result) {
        switch (result) {
            case PLAYER_WINS -> {
                playerWins++;
                userIO.printToUserConsoleRoundWinDynamicFavor(playerWins, dealerWins);
            }
            case DEALER_WINS -> {
                dealerWins++;
                userIO.printToUserConsoleRoundLoseDynamicFavor(playerWins, dealerWins);
            }
            default -> {
                userIO.printToUserConsoleRoundPushDynamicFavor(playerWins, dealerWins);
            }
        }
    }

    private void ensureShoeHasEnoughCardsOrReshuffle() {
        if (shoe.shouldShuffleBecauseLowOnCards()) {
            shoe.shuffleAllDecksBack();
            userIO.printToUserConsoleShoeShuffleHappened();
        }
    }
}
