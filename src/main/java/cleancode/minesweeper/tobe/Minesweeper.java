package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.game.GameInitializable;
import cleancode.minesweeper.tobe.game.GameRunnable;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.io.InputHandler;
import cleancode.minesweeper.tobe.io.OutputHandler;
import cleancode.minesweeper.tobe.position.CellPosition;
import cleancode.minesweeper.tobe.user.UserAction;

public class Minesweeper implements GameInitializable, GameRunnable {

    private final GameBoard gameBoard;

    private final InputHandler inputHandler;
    private final OutputHandler outputHandler;
    private static int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

    public Minesweeper(GameLevel gameLevel, InputHandler inputHandler, OutputHandler outputHandler) {
        this.gameBoard = new GameBoard(gameLevel);
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
    }

    @Override
    public void initialize() {
        gameBoard.initializeGame();
    }

    @Override
    public void run() {
        outputHandler.showGameStartComments();

        while (true) {
            try {
                outputHandler.showBoard(gameBoard);

                if (doesUserWinTheGame()) {
                    outputHandler.printGameWinningComment();
                    break;
                }
                if (doesUserLoseTheGame()) {
                    outputHandler.printGameLosingComment();
                    break;
                }

                CellPosition cellInput = getCellInputFromUser();
                UserAction userAction = getUserActionInputFromUser();
                actOnCell(cellInput, userAction);
            } catch (GameException e) {
                outputHandler.printExceptionMessage(e);
            } catch (Exception e) {
                outputHandler.printSimpleMessage("프로그램에 문제가 생겼습니다");
                e.printStackTrace();
            }
        }
    }


    private void actOnCell(CellPosition cellPosition, UserAction userAction) {
        if (doesUserChoosePlantFlag(userAction)) {
            gameBoard.flagAt(cellPosition);
            checkIfGameIsOver();
            return;
        }

        if (doesUserChooseOpenCell(userAction)) {
            if (gameBoard.isLandMineCellAt(cellPosition)) {
                gameBoard.openAt(cellPosition);
                changeGameStatusToLose();
                return;
            }

            gameBoard.openSurroundedCells(cellPosition);
            checkIfGameIsOver();
            return;
        }

        System.out.println("잘못된 번호를 선택하셨습니다.");
    }

    private void changeGameStatusToLose() {
        gameStatus = -1;
    }

    private boolean doesUserChooseOpenCell(UserAction userAction) {
        return userAction == UserAction.OPEN;
    }

    private boolean doesUserChoosePlantFlag(UserAction userAction) {
        return userAction == UserAction.FLAG;
    }

    private CellPosition getCellInputFromUser() {
        outputHandler.printCommentForSelectingCell();

        CellPosition cellPosition = inputHandler.getCellPositionFromUser();
        if (gameBoard.isInvalidCellPosition(cellPosition)) {
            throw new IllegalArgumentException("잘못된 좌표를 선택하셨습니다.");
        }

        return cellPosition;
    }

    private UserAction getUserActionInputFromUser() {
        outputHandler.printCommentForUserAction();
        return inputHandler.getUserActionFromUser();
    }

    private boolean doesUserLoseTheGame() {
        return gameStatus == -1;
    }

    private boolean doesUserWinTheGame() {
        return gameStatus == 1;
    }

    private void checkIfGameIsOver() {
        if (gameBoard.isAllCellChecked()) {
            changeGameStatusToWin();
        }
    }

    private void changeGameStatusToWin() {
        gameStatus = 1;
    }

}
