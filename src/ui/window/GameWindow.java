package ui.window;

import backend.controller.GameController;
import backend.engine.GameManager;

import ui.board.BoardPanel;

import ui.dialog.DisconnectDialog;
import ui.dialog.GameOverDialog;
import ui.dialog.LeaveRoomDialog;
import ui.dialog.SurrenderDialog;

import ui.input.BoardMouseListener;
import ui.input.InputHandler;

import ui.menu.MainMenu;
import ui.menu.PauseMenu;

import ui.panel.ChatPanel;
import ui.panel.MoveHistoryPanel;
import ui.panel.PlayerInfoPanel;
import ui.panel.StatusPanel;
import ui.panel.TimerPanel;

import ui.theme.ThemeManager;

import util.constants.GameConstants;
import util.timer.GameTimer;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    private static final int WINDOW_WIDTH  = 1400;
    private static final int WINDOW_HEIGHT = 900;

    /*
     * =========================
     * Backend
     * =========================
     */

    private GameManager    gameManager;
    private GameController gameController;

    /*
     * =========================
     * Reference về MainWindow
     * Dùng để show lại khi người chơi Exit Match,
     * thay vì tạo MainWindow mới (tránh memory leak).
     * =========================
     */

    private final MainWindow mainWindow;

    /*
     * =========================
     * Layout
     * =========================
     */

    private JPanel     contentPanel;
    private CardLayout cardLayout;

    /*
     * =========================
     * Screens
     * =========================
     */

    private JPanel gameScreen;

    /*
     * =========================
     * Board
     * =========================
     */

    private BoardPanel boardPanel;

    /*
     * =========================
     * Menus
     * =========================
     */

    private MainMenu     mainMenu;
    private PauseMenu    pauseMenu;

    /*
     * =========================
     * Panels
     * =========================
     */

    private ChatPanel        chatPanel;
    private MoveHistoryPanel moveHistoryPanel;
    private PlayerInfoPanel  playerInfoPanel;
    private StatusPanel      statusPanel;
    private TimerPanel       timerPanel;

    /*
     * =========================
     * Timer
     * =========================
     */

    private GameTimer gameTimer;

    /*
     * =========================
     * Input
     * =========================
     */

    private InputHandler       inputHandler;
    private BoardMouseListener boardMouseListener;

    /*
     * =========================
     * Dialogs
     * =========================
     */

    private DisconnectDialog disconnectDialog;
    private GameOverDialog   gameOverDialog;
    private LeaveRoomDialog  leaveRoomDialog;
    private SurrenderDialog  surrenderDialog;

    /*
     * =========================
     * Pause overlay (GlassPane)
     * =========================
     */

    /*
     * [FIX #3] GlassPane thay the JLayeredPane + reparenting.
     * GlassPane la layer san co cua JFrame, nam tren moi component,
     * fill full window, khong can reparent gameScreen.
     * Chi can setVisible(true/false) de show/hide overlay.
     */
    private JPanel glassPanePause;

    /*
     * =========================
     * Constructor
     * =========================
     */

    public GameWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initializeWindow();
        initializeBackend();
        initializeUI();
        initializePanels();
        initializeDialogs();
        initializeInput();
        initializeGlassPane();
        ThemeManager.applyWindowTheme(this);

        // Gắn tất cả listener thông qua controller
        new GameWindowController(this);

        setVisible(true);
    }

    /*
     * =========================
     * Window Initialization
     * =========================
     */

    private void initializeWindow() {
        setTitle("Multiplayer Chess");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    /*
     * =========================
     * Backend Initialization
     * =========================
     */

    private void initializeBackend() {
        gameManager    = new GameManager();
        gameController = new GameController(gameManager);
    }

    /*
     * =========================
     * UI Initialization
     * =========================
     */

    private void initializeUI() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        mainMenu  = new MainMenu();
        pauseMenu = new PauseMenu();

        /*
         * [FIX #3] Bỏ settingsMenu khỏi CardLayout.
         * SettingsMenu (JPanel trong CardLayout) không bao giờ có listener
         * và không có đường navigate tới nó. SettingsWindow (JDialog, modal)
         * đã được dùng nhất quán ở mọi nơi khác -- tiếp tục dùng nó.
         *
         * Đồng thời wire listener cho MainMenu ngay tại đây để tránh
         * MainMenu bị bỏ lơ như SettingsMenu trước đây.
         */
        wireMaintMenuListeners();

        initializeGameScreen();

        contentPanel.add(mainMenu,   "MAIN_MENU");
        contentPanel.add(gameScreen, "GAME");

        add(contentPanel);

        showMainMenu();
    }

    private void wireMaintMenuListeners() {
        mainMenu.getPlayButton().addActionListener(e -> showGameScreen());

        mainMenu.getSettingsButton().addActionListener(e -> new SettingsWindow(this));

        mainMenu.getExitButton().addActionListener(e -> {
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to quit?",
                    "Confirm Exit",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    /*
     * =========================
     * Game Screen
     * =========================
     */

    private void initializeGameScreen() {
        gameScreen = new JPanel(new BorderLayout());

        boardPanel = new BoardPanel(gameManager.getBoard());

        JPanel topPanel    = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel leftPanel   = new JPanel(new BorderLayout());
        JPanel rightPanel  = new JPanel(new BorderLayout());

        gameScreen.add(topPanel,    BorderLayout.NORTH);
        gameScreen.add(bottomPanel, BorderLayout.SOUTH);
        gameScreen.add(leftPanel,   BorderLayout.WEST);
        gameScreen.add(rightPanel,  BorderLayout.EAST);
        gameScreen.add(boardPanel,  BorderLayout.CENTER);
    }

    /*
     * =========================
     * Panels Initialization
     * =========================
     */

    private void initializePanels() {
        chatPanel        = new ChatPanel();
        moveHistoryPanel = new MoveHistoryPanel();
        playerInfoPanel  = new PlayerInfoPanel();
        statusPanel      = new StatusPanel();
        timerPanel       = new TimerPanel();

        gameTimer = new GameTimer(GameConstants.DEFAULT_MATCH_TIME);

        attachPanelsToGameScreen();
    }

    private void attachPanelsToGameScreen() {
        BorderLayout layout = (BorderLayout) gameScreen.getLayout();

        JPanel topPanel    = (JPanel) layout.getLayoutComponent(BorderLayout.NORTH);
        JPanel bottomPanel = (JPanel) layout.getLayoutComponent(BorderLayout.SOUTH);
        JPanel leftPanel   = (JPanel) layout.getLayoutComponent(BorderLayout.WEST);
        JPanel rightPanel  = (JPanel) layout.getLayoutComponent(BorderLayout.EAST);

        topPanel   .add(statusPanel,       BorderLayout.CENTER);
        bottomPanel.add(timerPanel,        BorderLayout.CENTER);
        leftPanel  .add(playerInfoPanel,   BorderLayout.NORTH);
        leftPanel  .add(moveHistoryPanel,  BorderLayout.CENTER);
        rightPanel .add(chatPanel,         BorderLayout.CENTER);
    }

    /*
     * =========================
     * Dialogs Initialization
     * =========================
     */

    private void initializeDialogs() {
        disconnectDialog = new DisconnectDialog(this);
        leaveRoomDialog  = new LeaveRoomDialog(this);
        surrenderDialog  = new SurrenderDialog(this);
    }

    /*
     * =========================
     * Input Initialization
     * =========================
     */

    private void initializeInput() {
        inputHandler       = new InputHandler(boardPanel, gameController);
        boardMouseListener = new BoardMouseListener(boardPanel, inputHandler);
        // onMoveExecuted callback duoc gan boi GameWindowController
    }

    /*
     * =========================
     * GlassPane Initialization
     * [FIX #3]
     * =========================
     */

    private void initializeGlassPane() {
        glassPanePause = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                /*
                 * Ve nen mo (semi-transparent) bang Graphics2D.
                 * JPanel thuong khong ho tro alpha qua setBackground(),
                 * nen phai override paintComponent.
                 */
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        glassPanePause.setOpaque(false);
        glassPanePause.add(pauseMenu);
        glassPanePause.setVisible(false);

        /*
         * setGlassPane() thay the GlassPane mac dinh cua JFrame.
         * GlassPane nam tren contentPane va toan bo UI -- khong reparent
         * bat ky component nao trong CardLayout.
         */
        setGlassPane(glassPanePause);
    }

    /*
     * =========================
     * Screen Switching
     * =========================
     */

    public void showMainMenu()   { cardLayout.show(contentPanel, "MAIN_MENU"); }
    public void showGameScreen() { cardLayout.show(contentPanel, "GAME");      }

    /*
     * =========================
     * Pause Menu Overlay
     * [FIX #3]
     * =========================
     */

    public void showPauseMenu() {
        /*
         * GlassPane co san, chi can setVisible(true).
         * Khong reparent, khong dong cham CardLayout, gameScreen giu nguyen.
         */
        glassPanePause.setVisible(true);
    }

    public void hidePauseMenu() {
        glassPanePause.setVisible(false);
    }

    /*
     * =========================
     * Dialog Display
     * =========================
     */

    public void showDisconnectDialog() {
        disconnectDialog.setVisible(true);
    }

    public void showGameOverDialog(String result) {
        gameOverDialog = new GameOverDialog(this, result);
        gameOverDialog.setVisible(true);
    }

    public boolean showLeaveRoomDialog() {
        leaveRoomDialog.setVisible(true);
        return leaveRoomDialog.isConfirmed();
    }

    public boolean showSurrenderDialog() {
        surrenderDialog.setVisible(true);
        return surrenderDialog.isSurrendered();
    }

    /*
     * =========================
     * Refresh
     * =========================
     */

    public void refreshBoard() {
        boardPanel.drawBoard();
        repaint();
    }

    /*
     * =========================
     * Getters
     * =========================
     */

    public GameController   getGameController()    { return gameController;    }
    public GameManager      getGameManager()        { return gameManager;       }
    public BoardPanel       getBoardPanel()         { return boardPanel;        }
    public ChatPanel        getChatPanel()          { return chatPanel;         }
    public MoveHistoryPanel getMoveHistoryPanel()   { return moveHistoryPanel;  }
    public PlayerInfoPanel  getPlayerInfoPanel()    { return playerInfoPanel;   }
    public StatusPanel      getStatusPanel()        { return statusPanel;       }
    public TimerPanel       getTimerPanel()         { return timerPanel;        }
    public GameTimer        getGameTimer()          { return gameTimer;         }
    public PauseMenu        getPauseMenu()          { return pauseMenu;         }
    public InputHandler     getInputHandler()       { return inputHandler;      }
    public MainWindow       getMainWindow()         { return mainWindow;        }
}
