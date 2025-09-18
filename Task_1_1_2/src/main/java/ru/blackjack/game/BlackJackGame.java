package ru.blackjack.game;

import java.util.Objects;

import ru.blackjack.cards.Card;
import ru.blackjack.cards.Shoe;
import ru.blackjack.ui.ConsoleUserIo;


public class BlackJackGame {

    private final ConsoleUserIo userIo;
    private final Shoe shoe;
    private final Dealer dealer;
    private final Participant player;

    private int roundNumber = 0;
    private int playerWins = 0;
    private int dealerWins = 0;

    public BlackJackGame(ConsoleUserIo userIo) {
        this.userIo = Objects.requireNonNull(userIo);
        this.shoe = new Shoe();            // всегда одна колода
        this.dealer = new Dealer("Дилер");
        this.player = new Participant("Игрок");
    }

    public void startGameLoopUntilUserStops() {
        userIo.printToUserConsoleWelcomeMessage();
        boolean keepPlaying = true;
        while (keepPlaying) {
            roundNumber++;
            RoundResult result = runSingleRoundAndReturnResult();
            updateScoreAndInformUser(result);
            keepPlaying = userIo.askUserWhetherPlayAnotherRound();
            userIo.printToUserConsoleEmptyLine();
        }

        userIo.printToUserConsoleGoodbyeWithFinalScore(playerWins, dealerWins);
    }

    private RoundResult runSingleRoundAndReturnResult() {
        ensureShoeHasEnoughCardsOrReshuffle();

        player.resetHand();
        dealer.resetHandAndHideHoleCard();

        userIo.printToUserConsoleRoundHeader(roundNumber);
        userIo.printToUserConsoleDealerDealsCards();

        player.receiveCardFromShoe(shoe.drawTopCard());
        dealer.receiveOpenCardFromShoe(shoe.drawTopCard());
        player.receiveCardFromShoe(shoe.drawTopCard());
        dealer.receiveHoleCardFromShoe(shoe.drawTopCard());

        userIo.printToUserConsoleHandsAfterDeal(player, dealer);

        boolean playerBj = player.getHand().hasBlackjack();
        boolean dealerBj = dealer.getHand().hasBlackjack();

        if (playerBj || dealerBj) {
            dealer.revealHoleCard();
            userIo.printToUserConsoleRevealHoleCard(dealer);
            userIo.printToUserConsoleHandsWithSums(player, dealer);

            if (playerBj && dealerBj) return RoundResult.PUSH;
            if (playerBj) return RoundResult.PLAYER_WINS;
            return RoundResult.DEALER_WINS;
        }

        userIo.printToUserConsolePlayerTurnHeader();
        boolean playerStands = false;
        while (!playerStands) {
            int decision = userIo.askUserHitOrStand();
            if (decision == 1) {
                Card newCard = shoe.drawTopCard();
                player.receiveCardFromShoe(newCard);
                userIo.printToUserConsolePlayerDrewCard(newCard);
                userIo.printToUserConsoleHandsWithHiddenDealerHole(player, dealer);
                if (player.getHand().isBusted()) {
                    userIo.printToUserConsolePlayerBusted();
                    dealer.revealHoleCard();
                    userIo.printToUserConsoleRevealHoleCard(dealer);
                    userIo.printToUserConsoleHandsWithSums(player, dealer);
                    return RoundResult.DEALER_WINS;
                }
            } else {
                playerStands = true;
            }
        }

        userIo.printToUserConsoleDealerTurnHeader();
        dealer.revealHoleCard();
        userIo.printToUserConsoleRevealHoleCard(dealer);
        userIo.printToUserConsoleHandsWithSums(player, dealer);

        while (dealer.mustDrawAccordingToRules()) {
            Card card = shoe.drawTopCard();
            dealer.receiveOpenCardFromShoe(card);
            userIo.printToUserConsoleDealerDrewCard(card);
            userIo.printToUserConsoleHandsWithSums(player, dealer);
            if (dealer.getHand().isBusted()) {
                userIo.printToUserConsoleDealerBusted();
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
                userIo.printToUserConsoleRoundWinDynamicFavor(playerWins, dealerWins);
            }
            case DEALER_WINS -> {
                dealerWins++;
                userIo.printToUserConsoleRoundLoseDynamicFavor(playerWins, dealerWins);
            }
            default -> {
                userIo.printToUserConsoleRoundPushDynamicFavor(playerWins, dealerWins);
            }
        }
    }

    private void ensureShoeHasEnoughCardsOrReshuffle() {
        if (shoe.shouldShuffleBecauseLowOnCards()) {
            shoe.shuffleAllDecksBack();
            userIo.printToUserConsoleShoeShuffleHappened();
        }
    }
}
