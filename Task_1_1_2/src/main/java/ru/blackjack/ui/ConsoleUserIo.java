package ru.blackjack.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import ru.blackjack.cards.Card;
import ru.blackjack.game.Dealer;
import ru.blackjack.game.Participant;

/**
 * Консольный ввод/вывод для игры Блэкджек.
 * Отвечает за все фразы, вопросы к пользователю и форматирование рук/сумм.
 */
public class ConsoleUserIo {

    private final Scanner scanner;

    /**
     * Создаёт консольный интерфейс с указанным сканером ввода.
     *
     * @param scanner источник строк ввода пользователя
     */
    public ConsoleUserIo(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Печатает приветствие.
     */
    public void printToUserConsoleWelcomeMessage() {
        println(RussianPhrases.WELCOME);
        println("");
    }

    /**
     * Печатает заголовок раунда.
     *
     * @param roundNumber номер раунда
     */
    public void printToUserConsoleRoundHeader(int roundNumber) {
        println(RussianPhrases.ROUND + roundNumber);
    }

    /**
     * Сообщает о раздаче карт дилером.
     */
    public void printToUserConsoleDealerDealsCards() {
        println(RussianPhrases.DEALT);
    }

    /**
     * Печатает руки после стартовой раздачи (у дилера одна карта скрыта).
     *
     * @param player игрок
     * @param dealer дилер
     */
    public void printToUserConsoleHandsAfterDeal(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithHiddenHole(dealer));
        println("");
    }

    /**
     * Печатает заголовок хода игрока.
     */
    public void printToUserConsolePlayerTurnHeader() {
        println(RussianPhrases.PLAYER_TURN_HEADER);
    }

    /**
     * Спрашивает у пользователя «взять карту» или «остановиться».
     *
     * @return {@code 1}, если «взять»; {@code 0}, если «стоп»
     */
    public int askUserHitOrStand() {
        while (true) {
            print(RussianPhrases.ASK_HIT_OR_STAND);
            String in = scanner.nextLine().trim();
            if ("1".equals(in)) {
                return 1;
            }
            if ("0".equals(in)) {
                return 0;
            }
        }
    }

    /**
     * Сообщает, какую карту открыл игрок.
     *
     * @param card только что полученная карта
     */
    public void printToUserConsolePlayerDrewCard(Card card) {
        println(RussianPhrases.PLAYER_DREW + buildSingleCardWithValueUnknown(card));
    }

    /**
     * Печатает руки при скрытой карте дилера (и сумму игрока).
     *
     * @param player игрок
     * @param dealer дилер
     */
    public void printToUserConsoleHandsWithHiddenDealerHole(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithHiddenHole(dealer));
    }

    /**
     * Сообщает о переборе у игрока.
     */
    public void printToUserConsolePlayerBusted() {
        println(RussianPhrases.PLAYER_BUSTED);
    }

    /**
     * Печатает заголовок хода дилера.
     */
    public void printToUserConsoleDealerTurnHeader() {
        println(RussianPhrases.DEALER_TURN_HEADER);
    }

    /**
     * Сообщает о раскрытии скрытой карты дилера.
     *
     * @param dealer дилер
     */
    public void printToUserConsoleRevealHoleCard(Dealer dealer) {
        if (dealer.isHoleCardHidden() && dealer.getHoleCardIfAny() != null) {
            println(RussianPhrases.DEALER_REVEALS
                    + buildSingleCardWithDynamicValueForDealer(dealer.getHoleCardIfAny(), dealer));
        }
    }

    /**
     * Печатает обе руки с суммами (у дилера все карты открыты).
     *
     * @param player игрок
     * @param dealer дилер
     */
    public void printToUserConsoleHandsWithSums(Participant player, Dealer dealer) {
        println(buildPlayerHandLineWithSum(player));
        println(buildDealerHandLineWithSum(dealer));
    }

    /**
     * Сообщает, какую карту открыл дилер.
     *
     * @param card только что полученная карта
     */
    public void printToUserConsoleDealerDrewCard(Card card) {
        println(RussianPhrases.DEALER_DREW + buildSingleCardWithValueUnknown(card));
    }

    /**
     * Сообщает о переборе у дилера.
     */
    public void printToUserConsoleDealerBusted() {
        println(RussianPhrases.DEALER_BUSTED);
    }

    /**
     * Спрашивает, сыграть ли ещё один раунд.
     *
     * @return {@code true}, если пользователь хочет продолжить; иначе {@code false}
     */
    public boolean askUserWhetherPlayAnotherRound() {
        while (true) {
            print(RussianPhrases.PLAY_AGAIN);
            String in = scanner.nextLine().trim();
            if ("1".equals(in)) {
                return true;
            }
            if ("0".equals(in)) {
                return false;
            }
        }
    }

    /**
     * Печатает прощание и финальный счёт.
     *
     * @param playerWins победы игрока
     * @param dealerWins победы дилера
     */
    public void printToUserConsoleGoodbyeWithFinalScore(int playerWins, int dealerWins) {
        println(String.format(RussianPhrases.GOODBYE, playerWins, dealerWins));
    }

    /**
     * Печатает пустую строку.
     */
    public void printToUserConsoleEmptyLine() {
        println("");
    }

    /**
     * Сообщает о перетасовке колоды.
     */
    public void printToUserConsoleShoeShuffleHappened() {
        println(RussianPhrases.SHUFFLE);
        println("");
    }

    /**
     * Печатает исход раунда (победа игрока) и динамический суффикс со счётом.
     *
     * @param playerWins победы игрока
     * @param dealerWins победы дилера
     */
    public void printToUserConsoleRoundWinDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(
                RussianPhrases.ROUND_PLAYER_WIN_PREFIX, playerWins, dealerWins));
    }

    /**
     * Печатает исход раунда (поражение игрока) и динамический суффикс со счётом.
     *
     * @param playerWins победы игрока
     * @param dealerWins победы дилера
     */
    public void printToUserConsoleRoundLoseDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(
                RussianPhrases.ROUND_PLAYER_LOSE_PREFIX, playerWins, dealerWins));
    }

    /**
     * Печатает исход раунда (ничья) и динамический суффикс со счётом.
     *
     * @param playerWins победы игрока
     * @param dealerWins победы дилера
     */
    public void printToUserConsoleRoundPushDynamicFavor(int playerWins, int dealerWins) {
        println(buildScoreLineWithFavor(
                RussianPhrases.ROUND_PUSH_PREFIX, playerWins, dealerWins));
    }

    // -------------------- private helpers --------------------

    private String buildPlayerHandLineWithSum(Participant player) {
        String list = buildCardsListWithPerCardValues(player);
        int sum = player.getHand().bestValue();
        return RussianPhrases.YOUR_CARDS + list + RussianPhrases.GREATER_SIGN + sum;
    }

    private String buildDealerHandLineWithHiddenHole(Dealer dealer) {
        // Первая открытая карта дилера + <закрытая карта>
        String left = dealer.getHand().viewCards().isEmpty() ? "" :
                buildSingleCardWithDynamicValueForDealer(dealer.getHand().viewCards().get(0),
                        dealer);
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
                .map(c -> c.buildDisplayNameWithoutValue() + " (" +
                        values.get(cards.indexOf(c)) + ")")
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
            // Если карта ещё не в руке (например, hole, до reveal), покажем «сырой» номинал.
            v = card.getRank().isAce() ? 11 : card.getRank().getBaseValue();
        }
        return card.buildDisplayNameWithoutValue() + " (" + v + ")";
    }

    private String buildScoreLineWithFavor(String prefix, int playerWins, int dealerWins) {
        String suffix;
        if (playerWins > dealerWins) {
            suffix = String.format(RussianPhrases.SCORE_SUFFIX_IN_YOUR_FAVOR,
                    playerWins, dealerWins);
        } else if (dealerWins > playerWins) {
            suffix = String.format(RussianPhrases.SCORE_SUFFIX_IN_DEALER_FAVOR,
                    playerWins, dealerWins);
        } else {
            suffix = String.format(RussianPhrases.SCORE_SUFFIX_TIED,
                    playerWins, dealerWins);
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
