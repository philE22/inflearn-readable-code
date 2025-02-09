package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.cell.*;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.position.CellPosition;
import cleancode.minesweeper.tobe.position.CellPositions;
import cleancode.minesweeper.tobe.position.RelativePosition;

import java.util.List;

public class GameBoard {

    private final Cell[][] board;
    private final int landMineCount;

    public GameBoard(GameLevel gameLevel) {
        int rowSize = gameLevel.getRowSize();
        int colSize = gameLevel.getColSize();
        board = new Cell[rowSize][colSize];

        this.landMineCount = gameLevel.getLandMineCount();
    }

    public void openAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        cell.open();
    }

    public void flagAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        cell.flag();
    }

    public boolean isLandMineCellAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        return cell.isLandMine();
    }

    public boolean isAllCellChecked() {
        Cells cells = Cells.from(board);
        return cells.isAllChecked();
    }

    public boolean isInvalidCellPosition(CellPosition cellPosition) {
        int rowSize = getRowSize();
        int colSize = getColSize();

        return cellPosition.isRowIndexMoreThanOrEqual(rowSize)
                || cellPosition.isColIndexMoreThanOrEqual(colSize);
    }

    public CellSnapshot getSnapshot(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        return cell.getSnapshot();
    }

    public void initializeGame() {
        CellPositions cellPositions = CellPositions.from(board);

        initializeEmptyCells(cellPositions);

        List<CellPosition> landMinePositions = cellPositions.extractRandomPosition(landMineCount);
        initializeLandMineCells(landMinePositions);

        List<CellPosition> numberCellPositionCandidates = cellPositions.subtract(landMinePositions);
        initializeNumberCells(numberCellPositionCandidates);
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

    public int getRowSize() {
        return board.length;
    }

    public int getColSize() {
        return board[0].length;
    }

    public void openSurroundedCells(CellPosition cellPosition) {
        if (isOpenedCell(cellPosition)) {
            return;
        }
        if (isLandMineCellAt(cellPosition)) {
            return;
        }

        openAt(cellPosition);

        if (doesCellHasLandMineCounts(cellPosition)) {
            return;
        }

        calculateSurroundedPositions(cellPosition)
                .forEach(this::openSurroundedCells);
    }

    private Cell findCell(CellPosition cellPosition) {
        return board[cellPosition.getRowIndex()][cellPosition.getColIndex()];
    }

    private void updateCellAt(CellPosition position, Cell cell) {
        board[position.getRowIndex()][position.getColIndex()] = cell;
    }

    private boolean doesCellHasLandMineCounts(CellPosition cellPosition) {
        return findCell(cellPosition).hasNearbyLandMineCount();
    }

    private boolean isOpenedCell(CellPosition cellPosition) {
        return findCell(cellPosition).isOpened();
    }

    private int countNearbyLandMines(CellPosition cellPosition) {
        long count = calculateSurroundedPositions(cellPosition).stream()
                .filter(this::isLandMineCellAt)
                .count();

        return (int)count;
    }

    private List<CellPosition> calculateSurroundedPositions(CellPosition cellPosition) {
        return RelativePosition.SURROUNDED_POSITIONS.stream()
                .filter(cellPosition::canCalculatePositionBy)
                .map(cellPosition::calculatePositionBy)
                .filter(position -> position.isRowIndexLessThan(getRowSize()))
                .filter(position -> position.isColIndexLessThan(getColSize()))
                .toList();
    }
}
