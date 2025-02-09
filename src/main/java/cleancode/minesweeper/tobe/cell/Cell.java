package cleancode.minesweeper.tobe.cell;

public interface Cell {

    boolean isLandMine();

    boolean hasNearbyLandMineCount();

    CellSnapshot getSnapshot();

    void flag();

    void open();

    boolean isChecked();

    boolean isOpened();
}
