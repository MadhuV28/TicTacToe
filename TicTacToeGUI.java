import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// Main class extending JFrame to create a GUI window
public class TicTacToeGUI extends JFrame {
    // 3x3 grid of buttons for the board
    private JButton[][] buttons = new JButton[3][3];
    // Symbols for user and CPU, and the current player
    private char userSymbol, cpuSymbol, currentPlayer;
    // Tracks if it's the user's turn
    private boolean userTurn;
    // User's money and wager for each game
    private double userMoney = 100.0;
    private double wager = 0.0;
    // Constructor: sets up the GUI and starts the first game
    public TicTacToeGUI() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 450);
        setLayout(new BorderLayout());
        // Create the board panel with a 3x3 grid
        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        Font font = new Font("Arial", Font.BOLD, 60);
        // Initialize each button, set font, and add click listeners
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(font);
                final int row = i, col = j;
                // When a button is clicked, handle the move
                buttons[i][j].addActionListener(e -> handleMove(row, col));
                boardPanel.add(buttons[i][j]);
            }
        add(boardPanel, BorderLayout.CENTER);
        // Add a reset button to start a new game
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> startNewGame());
        add(resetButton, BorderLayout.SOUTH);
        // Start the first game
        startNewGame();
    }
    // Starts a new game: clears board, gets symbol, wager, and who goes first
    private void startNewGame() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText("");
        chooseSymbol();
        getWager();
        randomizeFirstTurn();
        if (!userTurn) cpuMove(); // If CPU goes first, make its move
    }
    // Lets the user choose X or O using a dialog
    private void chooseSymbol() {
        String[] options = {"X", "O"};
        int choice = JOptionPane.showOptionDialog(this, "Choose your symbol:", "Symbol Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 1) {
            userSymbol = 'O';
            cpuSymbol = 'X';
        } else {
            userSymbol = 'X';
            cpuSymbol = 'O';
        }
    }
    // Prompts the user to enter a wager for the game
    private void getWager() {
        while (true) {
            String input = JOptionPane.showInputDialog(this, "You have $" + userMoney + "\nEnter wager amount:");
            if (input == null) System.exit(0); // Exit if user cancels
            try {
                wager = Double.parseDouble(input);
                if (wager > 0 && wager <= userMoney) break;
            } catch (Exception ignored) {}
            JOptionPane.showMessageDialog(this, "Invalid wager.");
        }
    }
    // Randomly decides who goes first and notifies the user
    private void randomizeFirstTurn() {
        userTurn = new Random().nextBoolean();
        currentPlayer = userTurn ? userSymbol : cpuSymbol;
        JOptionPane.showMessageDialog(this, (userTurn ? "You" : "CPU") + " go first.");
    }
    // Handles a user's move when a button is clicked
    private void handleMove(int row, int col) {
        if (!userTurn || !buttons[row][col].getText().equals("")) return; // Ignore if not user's turn or cell not empty
        buttons[row][col].setText(String.valueOf(userSymbol));
        if (checkWinner(userSymbol)) {
            userMoney += wager;
            showEndDialog("You win! You gain $" + wager);
        } else if (isBoardFull()) {
            showEndDialog("It's a draw! Your money stays the same.");
        } else {
            userTurn = false;
            cpuMove();
        }
    }
    // CPU makes its move: tries to win, block, or picks random
    private void cpuMove() {
        // Try to win
        int[] move = findBestMove(cpuSymbol);
        // Try to block user
        if (move == null) move = findBestMove(userSymbol);
        // Otherwise, pick random
        if (move == null) move = getRandomMove();
        buttons[move[0]][move[1]].setText(String.valueOf(cpuSymbol));
        if (checkWinner(cpuSymbol)) {
            userMoney -= wager;
            showEndDialog("CPU wins! You lose $" + wager);
        } else if (isBoardFull()) {
            showEndDialog("It's a draw! Your money stays the same.");
        } else {
            userTurn = true;
        }
    }
    // Finds a move that would win for the given symbol, or null if none
    private int[] findBestMove(char symbol) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (buttons[i][j].getText().equals("")) {
                    buttons[i][j].setText(String.valueOf(symbol));
                    boolean win = checkWinner(symbol);
                    buttons[i][j].setText("");
                    if (win) return new int[]{i, j};
                }
        return null;
    }
    // Returns a random available move
    private int[] getRandomMove() {
        java.util.List<int[]> moves = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (buttons[i][j].getText().equals(""))
                    moves.add(new int[]{i, j});
        return moves.get(new Random().nextInt(moves.size()));
    }
    // Checks if the given symbol has won the game
    private boolean checkWinner(char symbol) {
        // Check rows
        for (int i = 0; i < 3; i++)
            if (buttons[i][0].getText().equals(String.valueOf(symbol)) &&
                buttons[i][1].getText().equals(String.valueOf(symbol)) &&
                buttons[i][2].getText().equals(String.valueOf(symbol)))
                return true;
        // Check columns
        for (int i = 0; i < 3; i++)
            if (buttons[0][i].getText().equals(String.valueOf(symbol)) &&
                buttons[1][i].getText().equals(String.valueOf(symbol)) &&
                buttons[2][i].getText().equals(String.valueOf(symbol)))
                return true;
        // Check diagonals
        if (buttons[0][0].getText().equals(String.valueOf(symbol)) &&
            buttons[1][1].getText().equals(String.valueOf(symbol)) &&
            buttons[2][2].getText().equals(String.valueOf(symbol)))
            return true;
        if (buttons[0][2].getText().equals(String.valueOf(symbol)) &&
            buttons[1][1].getText().equals(String.valueOf(symbol)) &&
            buttons[2][0].getText().equals(String.valueOf(symbol)))
            return true;
        return false;
    }
    // Checks if the board is full (draw)
    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (buttons[i][j].getText().equals(""))
                    return false;
        return true;
    }
    // Shows a dialog with the result and starts a new game or exits if out of money
    private void showEndDialog(String message) {
        JOptionPane.showMessageDialog(this, message + "\nYou now have $" + userMoney);
        if (userMoney <= 0) {
            JOptionPane.showMessageDialog(this, "You're out of money! Game over.");
            System.exit(0);
        }
        startNewGame();
    }
    // Main method: launches the GUI on the event dispatch thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TicTacToeGUI().setVisible(true);
        });
    }
}
