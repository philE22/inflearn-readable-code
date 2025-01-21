package cleancode.minesweeper.tobe.cell;

public class NumberCell implements Cell {

    private final int nearbyLandMineCount;
    private final CellState cellState = CellState.initialize();

    public NumberCell(int nearbyLandMineCount) {
        this.nearbyLandMineCount = nearbyLandMineCount;
    }

    @Override
    public boolean isLandMine() {
        return false;
    }

    @Override
    public boolean hasNearbyLandMineCount() {
        return true;
    }

    @Override
    public String getSign() {
        if (cellState.isOpened()) {
            return String.valueOf(this.nearbyLandMineCount);
        }

        if (cellState.isFlagged()) {
            return FLAG_SIGN;
        }

        return UNCHECKED_CELL_SIGN;
    }

    @Override
    public void flag() {
        cellState.flag();
    }

    @Override
    public void open() {
        cellState.open();
    }

    @Override
    public boolean isChecked() {
        return cellState.isChecked();
    }

    @Override
    public boolean isOpened() {
        return cellState.isOpened();
    }
}
