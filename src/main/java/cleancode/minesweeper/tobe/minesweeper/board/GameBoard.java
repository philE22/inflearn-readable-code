package cleancode.minesweeper.tobe.minesweeper.board;

import cleancode.minesweeper.tobe.minesweeper.board.cell.*;
import cleancode.minesweeper.tobe.minesweeper.board.position.CellPosition;
import cleancode.minesweeper.tobe.minesweeper.board.position.CellPositions;
import cleancode.minesweeper.tobe.minesweeper.board.position.RelativePosition;
import cleancode.minesweeper.tobe.minesweeper.gamelevel.GameLevel;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static cleancode.minesweeper.tobe.minesweeper.board.GameStatus.*;

public class GameBoard {

    private final Cell[][] board;
    private final int landMineCount;
    private GameStatus gameStatus;

    public GameBoard(GameLevel gameLevel) {
        int rowSize = gameLevel.getRowSize();
        int colSize = gameLevel.getColSize();
        board = new Cell[rowSize][colSize];

        this.landMineCount = gameLevel.getLandMineCount();
    }

    public void initializeGame() {
        initializeGameStatus();

        CellPositions cellPositions = CellPositions.from(board);

        initializeEmptyCells(cellPositions);

        List<CellPosition> landMinePositions = cellPositions.extractRandomPosition(landMineCount);
        initializeLandMineCells(landMinePositions);

        List<CellPosition> numberCellPositionCandidates = cellPositions.subtract(landMinePositions);
        initializeNumberCells(numberCellPositionCandidates);
    }

    public void flagAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        cell.flag();

        checkIfGameIsOver();
    }

    public void openAt(CellPosition cellPosition) {
        if (isLandMineCellAt(cellPosition)) {
            openOneCellAt(cellPosition);
            changeGameStatusToLose();
            return;
        }

//        openSurroundedCells(cellPosition);
        openSurroundedCells2(cellPosition);
        checkIfGameIsOver();
    }

    public boolean isInvalidCellPosition(CellPosition cellPosition) {
        int rowSize = getRowSize();
        int colSize = getColSize();

        return cellPosition.isRowIndexMoreThanOrEqual(rowSize)
                || cellPosition.isColIndexMoreThanOrEqual(colSize);
    }

    public boolean isInProgress() {
        return gameStatus == IN_PROGRESS;
    }

    public boolean isWinStatus() {
        return gameStatus == WIN;
    }

    public boolean isLoseStatus() {
        return gameStatus == LOSE;
    }

    public CellSnapshot getSnapshot(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        return cell.getSnapshot();
    }

    public int getRowSize() {
        return board.length;
    }

    public int getColSize() {
        return board[0].length;
    }

    private void initializeGameStatus() {
        gameStatus = IN_PROGRESS;
    }

    private void initializeEmptyCells(CellPositions cellPositions) {
        List<CellPosition> allPositions = cellPositions.getPositions();
        for (CellPosition position : allPositions) {
            updateCellAt(position, new EmptyCell());
        }
    }

    private void initializeLandMineCells(List<CellPosition> landMinePositions) {
        for (CellPosition position : landMinePositions) {
            updateCellAt(position, new LandMineCell());
        }
    }

    private void initializeNumberCells(List<CellPosition> numberCellPositionCandidates) {
        for (CellPosition candidatePosition : numberCellPositionCandidates) {
            int count = countNearbyLandMines(candidatePosition);
            if (count != 0) {
                updateCellAt(candidatePosition, new NumberCell(count));
            }
        }
    }

    private void openSurroundedCells(CellPosition cellPosition) {
        if (isOpenedCell(cellPosition)) {
            return;
        }
        if (isLandMineCellAt(cellPosition)) {
            return;
        }

        openOneCellAt(cellPosition);

        if (doesCellHasLandMineCounts(cellPosition)) {
            return;
        }

        calculateSurroundedPositions(cellPosition)
                .forEach(this::openSurroundedCells);
    }

    private void openSurroundedCells2(CellPosition cellPosition) {
        Deque<CellPosition> deque = new ArrayDeque<>();
        deque.push(cellPosition);

        while (!deque.isEmpty()) {
            openAndPushCellAt(deque);
        }
    }

    private void openAndPushCellAt(Deque<CellPosition> stack) {
        CellPosition cellPosition = stack.pop();

        if (isOpenedCell(cellPosition)) {
            return;
        }
        if (isLandMineCellAt(cellPosition)) {
            return;
        }

        openOneCellAt(cellPosition);

        if (doesCellHasLandMineCounts(cellPosition)) {
            return;
        }

        calculateSurroundedPositions(cellPosition)
                .forEach(stack::push);
    }

    private void openOneCellAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        cell.open();
    }

    private void updateCellAt(CellPosition position, Cell cell) {
        board[position.getRowIndex()][position.getColIndex()] = cell;
    }

    private boolean isOpenedCell(CellPosition cellPosition) {
        return findCell(cellPosition).isOpened();
    }

    private boolean isAllCellChecked() {
        Cells cells = Cells.from(board);
        return cells.isAllChecked();
    }

    private boolean isLandMineCellAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        return cell.isLandMine();
    }

    private void checkIfGameIsOver() {
        if (isAllCellChecked()) {
            changeGameStatusToWin();
        }
    }

    private boolean doesCellHasLandMineCounts(CellPosition cellPosition) {
        return findCell(cellPosition).hasNearbyLandMineCount();
    }

    private int countNearbyLandMines(CellPosition cellPosition) {
        long count = calculateSurroundedPositions(cellPosition).stream()
                .filter(this::isLandMineCellAt)
                .count();

        return (int) count;
    }

    private List<CellPosition> calculateSurroundedPositions(CellPosition cellPosition) {
        return RelativePosition.SURROUNDED_POSITIONS.stream()
                .filter(cellPosition::canCalculatePositionBy)
                .map(cellPosition::calculatePositionBy)
                .filter(position -> position.isRowIndexLessThan(getRowSize()))
                .filter(position -> position.isColIndexLessThan(getColSize()))
                .toList();
    }

    private void changeGameStatusToWin() {
        gameStatus = WIN;
    }

    private void changeGameStatusToLose() {
        gameStatus = GameStatus.LOSE;
    }

    private Cell findCell(CellPosition cellPosition) {
        return board[cellPosition.getRowIndex()][cellPosition.getColIndex()];
    }
}
