package backend.board;

public class Board {
    public static final int BOARD_SIZE = 8;
    private final Tile[][] board;
    public Board() {
        board = new Tile[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }
    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = new Tile(new Position(row, col));
            }
        }
    }
    public Tile getTile(Position position) {
        if (position == null || !position.isValid()) {
            return null;
        }

        return board[position.getRow()][position.getCol()];
    }

    public boolean isPositionValid(Position position) {
        return position != null && position.isValid();
    }

    public void clearBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col].clearTile();
            }
        }
    }

    public Tile getTileByCoordinate(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE ||
                col < 0 || col >= BOARD_SIZE) {
            return null;
        }
        return board[row][col];
    }
}
