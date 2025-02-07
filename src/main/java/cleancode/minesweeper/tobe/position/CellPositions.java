package cleancode.minesweeper.tobe.position;

import cleancode.minesweeper.tobe.cell.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CellPositions {

    private final List<CellPosition> cellPositions;

    private CellPositions(List<CellPosition> cellPositions) {
        this.cellPositions = cellPositions;
    }

    public static CellPositions of(List<CellPosition> cellPositions) {
        return new CellPositions(cellPositions);
    }


    public static CellPositions from(Cell[][] board) {
        ArrayList<CellPosition> cellPositions = new ArrayList<>();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                CellPosition cellPosition = CellPosition.of(row, col);
                cellPositions.add(cellPosition);
            }
        }

        return of(cellPositions);
    }

    public List<CellPosition> extractRandomPosition(int count) {
        ArrayList<CellPosition> cellPositions = new ArrayList<>(this.cellPositions);

        Collections.shuffle(cellPositions);
        return cellPositions.subList(0, count);
    }

    public List<CellPosition> subtract(List<CellPosition> cellPositionsForSubtract) {
        CellPositions positionsForSubtract = CellPositions.of(cellPositionsForSubtract);

        return cellPositions.stream()
                .filter(positionsForSubtract::doesNotContain)
                .toList();
    }

    private boolean doesNotContain(CellPosition cellPosition) {
        return !cellPositions.contains(cellPosition);
    }

    public List<CellPosition> getPositions() {
        return new ArrayList<>(cellPositions);
    }
}
