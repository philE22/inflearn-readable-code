package cleancode.minesweeper.tobe.minesweeper.io.sign;

import cleancode.minesweeper.tobe.minesweeper.board.cell.CellSnapshot;

import java.util.List;

public class CellSignFinder {

    private static final List<CellSignProvidable> CELL_SIGN_PROVIDERS = List.of(
            new EmptyCellSignProvider(),
            new FlagCellSignProvider(),
            new LandMineCellSignProvider(),
            new NumberCellSignProvider(),
            new UncheckedCellSignProvider()
    );

    public String findCellFrom(CellSnapshot cellSnapshot) {
        return CELL_SIGN_PROVIDERS.stream()
                .filter(provider -> provider.supports(cellSnapshot))
                .findFirst()
                .map(provider -> provider.provide(cellSnapshot))
                .orElseThrow(() -> new IllegalArgumentException("확인할 수 없는 셀입니다"));
    }
}
