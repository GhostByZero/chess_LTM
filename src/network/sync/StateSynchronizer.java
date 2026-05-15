package network.sync;

import network.common.Message;

public class StateSynchronizer {

    /**
     * Hàm này được gọi ở Client khi nhận được MessageType.SYNC_STATE
     */
    public void handleSyncState(Message msg) {
        // 1. Bóc tách dữ liệu từ Message
        String boardData   = msg.get("boardData");
        String currentTurn = msg.get("currentTurn");
        boolean isCheck    = msg.getBoolean("isCheck");
        boolean isGameOver = msg.getBoolean("isGameOver");

        // TODO: Xóa trắng bàn cờ cũ trên giao diện Client (Clear Board UI)
        // ClientGameController.getInstance().clearBoardUI();

        // 2. Phân tích chuỗi dữ liệu bàn cờ
        if (boardData != null && !boardData.isEmpty()) {
            // Tách từng quân cờ theo dấu chấm phẩy ";"
            String[] piecesData = boardData.split(";");

            for (String pData : piecesData) {
                if (pData.isEmpty()) continue;

                // Tách thông tin chi tiết bằng dấu phẩy ","
                String[] parts = pData.split(",");
                int r         = Integer.parseInt(parts[0]);
                int c         = Integer.parseInt(parts[1]);
                String symbol = parts[2];
                String color  = parts[3];

                // TODO: Tạo quân cờ và vẽ lại nó lên ô (r, c) trên giao diện Client
                // ClientGameController.getInstance().placePieceUI(r, c, symbol, color);
            }
        }

        // 3. Cập nhật lại các thông số trạng thái lên UI
        // TODO: Cập nhật text hiển thị lượt đi hiện tại
        // ClientGameController.getInstance().updateTurnUI(currentTurn);

        System.out.println("[Client Sync] Đồng bộ hoàn tất. Lượt hiện tại: " + currentTurn);
    }
}