package backend.engine;

import backend.board.Board;
import backend.board.Tile;
import backend.controller.GameController;
import backend.move.Move;
import backend.move.MoveGenerator;
import backend.move.MoveType;
import backend.pieces.Piece;
import backend.pieces.PieceColor;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChessBot {
    private final GameManager gameManager;
    private final GameController gameController;
    private final PieceColor botColor;
    private final boolean isHardMode;
    private final Random random = new Random();

    // Callback để cập nhật giao diện sau khi Bot đi xong
    private Runnable refreshUI;
    private Runnable onMoveExecutedCallback;

    public ChessBot(GameManager gameManager, GameController gameController, PieceColor botColor, boolean isHardMode) {
        this.gameManager = gameManager;
        this.gameController = gameController;
        this.botColor = botColor;
        this.isHardMode = isHardMode;
    }

    public void setCallbacks(Runnable refreshUI, Runnable onMoveExecutedCallback) {
        this.refreshUI = refreshUI;
        this.onMoveExecutedCallback = onMoveExecutedCallback;
    }

    public void triggerBotMove() {
        // Chạy trên một luồng (Thread) riêng để máy "suy nghĩ" mà không làm đơ giao diện game
        new Thread(() -> {
            try {
                // Giả lập thời gian máy suy nghĩ (0.5 giây đến 1.5 giây)
                Thread.sleep(500 + random.nextInt(1000));

                List<Move> allLegalMoves = getAllLegalMoves();

                if (allLegalMoves.isEmpty()) return; // Hết cờ (Chiếu bí hoặc Hòa)

                Move selectedMove;
                if (isHardMode) {
                    selectedMove = findBestMove(allLegalMoves);
                } else {
                    selectedMove = allLegalMoves.get(random.nextInt(allLegalMoves.size())); // Chế độ dễ: Đi bừa
                }

                // Đi cờ (Phải gọi trên luồng UI của Swing)
                SwingUtilities.invokeLater(() -> {
                    // Nếu là nước phong cấp, mặc định Bot chọn Hậu
                    if (selectedMove.getMoveType() == MoveType.PROMOTION) {
                        selectedMove.setPromotionChoice("QUEEN");
                    }

                    gameController.handleMove(selectedMove);

                    // Cập nhật lại giao diện
                    if (refreshUI != null) refreshUI.run();
                    if (onMoveExecutedCallback != null) onMoveExecutedCallback.run();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Lấy TẤT CẢ các nước đi hợp lệ của Bot trên bàn cờ
    private List<Move> getAllLegalMoves() {
        List<Move> moves = new ArrayList<>();
        Board board = gameManager.getBoard();
        MoveGenerator generator = gameManager.getChessEngine().getMoveGenerator();

        for (int r = 0; r < Board.BOARD_SIZE; r++) {
            for (int c = 0; c < Board.BOARD_SIZE; c++) {
                Tile tile = board.getTileByCoordinate(r, c);
                if (tile != null && tile.isOccupied() && tile.getPiece().getColor() == botColor) {
                    moves.addAll(generator.generateMoves(tile.getPiece()));
                }
            }
        }
        return moves;
    }

    // Logic Chế độ Khó: Tìm nước đi "Thơm" nhất (Ưu tiên ăn quân có giá trị)
    private Move findBestMove(List<Move> moves) {
        Move bestMove = moves.get(random.nextInt(moves.size())); // Mặc định random
        int maxScore = -1;

        for (Move move : moves) {
            int score = 0;

            // Nếu nước đi có thể ăn quân địch
            if (move.getCapturedPiece() != null) {
                score = getPieceValue(move.getCapturedPiece());
            }

            // Nếu nước đi có thể phong cấp
            if (move.getMoveType() == MoveType.PROMOTION) {
                score += 8; // Điểm gần bằng con Hậu
            }

            // Chọn nước có điểm cao nhất (Nếu bằng điểm thì nó giữ nguyên nước tìm thấy đầu tiên)
            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    // Đánh giá giá trị các quân cờ
    private int getPieceValue(Piece piece) {
        String symbol = piece.getSymbol().toUpperCase();
        return switch (symbol) {
            case "Q" -> 9;
            case "R" -> 5;
            case "B", "N" -> 3;
            case "P" -> 1;
            default -> 0;
        };
    }
}