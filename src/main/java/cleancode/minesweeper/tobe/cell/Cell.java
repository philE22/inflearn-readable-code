package cleancode.minesweeper.tobe.cell;

public interface Cell {

    String FLAG_SIGN = "⚑";
    String UNCHECKED_CELL_SIGN = "□";

    boolean isLandMine();

    boolean hasNearbyLandMineCount();

    String getSign();

    void flag();

    void open();

    boolean isChecked();

    boolean isOpened();
}
