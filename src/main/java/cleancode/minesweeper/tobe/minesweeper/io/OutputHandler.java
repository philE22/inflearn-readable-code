package cleancode.minesweeper.tobe.minesweeper.io;

import cleancode.minesweeper.tobe.minesweeper.board.GameBoard;
import cleancode.minesweeper.tobe.minesweeper.exception.GameException;

public interface OutputHandler {
    void showGameStartComments();

    void showBoard(GameBoard board);

    void printGameWinningComment();

    void printGameLosingComment();

    void printCommentForSelectingCell();

    void printCommentForUserAction();

    void printExceptionMessage(GameException e);

    void printSimpleMessage(String message);
}
