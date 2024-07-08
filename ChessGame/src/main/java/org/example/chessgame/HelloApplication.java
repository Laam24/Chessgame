package org.example.chessgame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelloApplication extends Application {
    private static final int BOARD_SIZE = 8;
    private static final int CELL_SIZE = 50;

    private char[][] board;
    private Button[][] boardButtons;
    private Label statusLabel;
    private int[] selectedPiece;
    private String currentTurn;
    private String gameStatus;
    private List<int[]> availableMoves;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Two-Player Chess Game");

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Two-Player Chess Game");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button resetButton = new Button("Reset Game");
        resetButton.setOnAction(e -> resetGame());

        statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        GridPane boardPane = new GridPane();
        boardPane.setHgap(1);
        boardPane.setVgap(1);

        boardButtons = new Button[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Button cell = createCell(row, col);
                boardButtons[row][col] = cell;
                boardPane.add(cell, col, row);
            }
        }

        root.getChildren().addAll(titleLabel, resetButton, statusLabel, boardPane);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        resetGame();
    }

    private Button createCell(int row, int col) {
        Button cell = new Button();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        cell.setOnAction(e -> handleCellClick(row, col));
        return cell;
    }

    private void resetGame() {
        board = new char[][]{
                {'r', 'n', 'b', 'k', 'q', 'b', 'n', 'r'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}
        };
        selectedPiece = null;
        currentTurn = "white";
        gameStatus = "playing";
        availableMoves = new ArrayList<>();
        updateBoard();
        updateStatus();
    }

    private void updateBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Button cell = boardButtons[row][col];
                cell.setText(String.valueOf(board[row][col]));
                cell.setStyle(getDefaultCellStyle(row, col));
            }
        }
    }

    private String getDefaultCellStyle(int row, int col) {
        String baseStyle = "-fx-font-weight: bold; -fx-font-size: 20;";
        if ((row + col) % 2 == 0) {
            return baseStyle + "-fx-background-color: white;";
        } else {
            return baseStyle + "-fx-background-color: lightgray;";
        }
    }

    private void updateStatus() {
        statusLabel.setText("Current Turn: " + currentTurn + " | Status: " + gameStatus);
    }

    private void handleCellClick(int row, int col) {
        if (gameStatus.equals("checkmate")) return;

        if (selectedPiece != null) {
            if (isValidMove(row, col)) {
                handleMove(selectedPiece[0], selectedPiece[1], row, col);
            } else {
                clearSelection();
            }
        } else if (board[row][col] != ' ' &&
                (currentTurn.equals("white") ? Character.isUpperCase(board[row][col]) : Character.isLowerCase(board[row][col]))) {
            selectPiece(row, col);
        }
    }

    private void selectPiece(int row, int col) {
        selectedPiece = new int[]{row, col};
        availableMoves = getLegalMoves(row, col);
        highlightSelectedPiece();
        highlightAvailableMoves();
    }

    private void clearSelection() {
        selectedPiece = null;
        availableMoves.clear();
        updateBoard();
    }

    private void highlightSelectedPiece() {
        Button cell = boardButtons[selectedPiece[0]][selectedPiece[1]];
        cell.setStyle(cell.getStyle() + "-fx-background-color: yellow;");
    }

    private void highlightAvailableMoves() {
        for (int[] move : availableMoves) {
            Button cell = boardButtons[move[0]][move[1]];
            cell.setStyle(cell.getStyle() + "-fx-background-color: lightgreen;");
        }
    }

    private boolean isValidMove(int row, int col) {
        for (int[] move : availableMoves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }

    private void handleMove(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = ' ';

        currentTurn = currentTurn.equals("white") ? "black" : "white";

        if (isKingInCheck(currentTurn)) {
            if (isCheckmate(currentTurn)) {
                gameStatus = "checkmate";
            } else {
                gameStatus = "check";
            }
        } else {
            gameStatus = "playing";
        }

        clearSelection();
        updateBoard();
        updateStatus();
    }

    private List<int[]> getAvailableMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        char piece = board[row][col];

        switch (Character.toLowerCase(piece)) {
            case 'p':
                moves.addAll(getPawnMoves(row, col));
                break;
            case 'r':
                moves.addAll(getRookMoves(row, col));
                break;
            case 'n':
                moves.addAll(getKnightMoves(row, col));
                break;
            case 'b':
                moves.addAll(getBishopMoves(row, col));
                break;
            case 'q':
                moves.addAll(getQueenMoves(row, col));
                break;
            case 'k':
                moves.addAll(getKingMoves(row, col));
                break;
        }

        return moves;
    }

    private List<int[]> getPawnMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int direction = Character.isUpperCase(board[row][col]) ? -1 : 1;
        int startRow = Character.isUpperCase(board[row][col]) ? 6 : 1;

        if (isValidPosition(row + direction, col) && board[row + direction][col] == ' ') {
            moves.add(new int[]{row + direction, col});
            if (row == startRow && board[row + 2 * direction][col] == ' ') {
                moves.add(new int[]{row + 2 * direction, col});
            }
        }

        if (isValidPosition(row + direction, col - 1) && isEnemyPiece(row, col, row + direction, col - 1)) {
            moves.add(new int[]{row + direction, col - 1});
        }
        if (isValidPosition(row + direction, col + 1) && isEnemyPiece(row, col, row + direction, col + 1)) {
            moves.add(new int[]{row + direction, col + 1});
        }

        return moves;
    }

    private List<int[]> getRookMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            moves.addAll(getMovesInDirection(row, col, dir[0], dir[1]));
        }

        return moves;
    }

    private List<int[]> getKnightMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidPosition(newRow, newCol) && (board[newRow][newCol] == ' ' || isEnemyPiece(row, col, newRow, newCol))) {
                moves.add(new int[]{newRow, newCol});
            }
        }

        return moves;
    }

    private List<int[]> getBishopMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] dir : directions) {
            moves.addAll(getMovesInDirection(row, col, dir[0], dir[1]));
        }

        return moves;
    }

    private List<int[]> getQueenMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        moves.addAll(getRookMoves(row, col));
        moves.addAll(getBishopMoves(row, col));
        return moves;
    }

    private List<int[]> getKingMoves(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidPosition(newRow, newCol) && (board[newRow][newCol] == ' ' || isEnemyPiece(row, col, newRow, newCol))) {
                moves.add(new int[]{newRow, newCol});
            }
        }

        return moves;
    }

    private List<int[]> getMovesInDirection(int row, int col, int rowStep, int colStep) {
        List<int[]> moves = new ArrayList<>();
        int newRow = row + rowStep;
        int newCol = col + colStep;

        while (isValidPosition(newRow, newCol)) {
            if (board[newRow][newCol] == ' ') {
                moves.add(new int[]{newRow, newCol});
            } else if (isEnemyPiece(row, col, newRow, newCol)) {
                moves.add(new int[]{newRow, newCol});
                break;
            } else {
                break;
            }
            newRow += rowStep;
            newCol += colStep;
        }

        return moves;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private boolean isEnemyPiece(int fromRow, int fromCol, int toRow, int toCol) {
        return board[toRow][toCol] != ' ' && Character.isUpperCase(board[fromRow][fromCol]) != Character.isUpperCase(board[toRow][toCol]);
    }

    private boolean isKingInCheck(String color) {
        char kingPiece = color.equals("white") ? 'K' : 'k';
        int[] kingPosition = findKing(kingPiece);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != ' ' && (color.equals("white") ? Character.isLowerCase(board[row][col]) : Character.isUpperCase(board[row][col]))) {
                    List<int[]> moves = getAvailableMoves(row, col);
                    for (int[] move : moves) {
                        if (move[0] == kingPosition[0] && move[1] == kingPosition[1]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private int[] findKing(char kingPiece) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == kingPiece) {
                    return new int[]{row, col};
                }
            }
        }
        return null; // This should never happen in a valid chess game
    }

    private List<int[]> getLegalMoves(int row, int col) {
        List<int[]> potentialMoves = getAvailableMoves(row, col);
        List<int[]> legalMoves = new ArrayList<>();

        for (int[] move : potentialMoves) {
            if (!moveResultsInCheck(row, col, move[0], move[1])) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    private boolean moveResultsInCheck(int fromRow, int fromCol, int toRow, int toCol) {
        char[][] tempBoard = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            tempBoard[i] = Arrays.copyOf(board[i], BOARD_SIZE);
        }

        tempBoard[toRow][toCol] = tempBoard[fromRow][fromCol];
        tempBoard[fromRow][fromCol] = ' ';

        String color = Character.isUpperCase(tempBoard[toRow][toCol]) ? "white" : "black";
        char kingPiece = color.equals("white") ? 'K' : 'k';
        int[] kingPosition = findKingOnBoard(kingPiece, tempBoard);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (tempBoard[row][col] != ' ' && (color.equals("white") ? Character.isLowerCase(tempBoard[row][col]) : Character.isUpperCase(tempBoard[row][col]))) {
                    List<int[]> moves = getAvailableMovesOnBoard(row, col, tempBoard);
                    for (int[] move : moves) {
                        if (move[0] == kingPosition[0] && move[1] == kingPosition[1]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private int[] findKingOnBoard(char kingPiece, char[][] board) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == kingPiece) {
                    return new int[]{row, col};
                }
            }
        }
        return null; // This should never happen in a valid chess game
    }

    private List<int[]> getAvailableMovesOnBoard(int row, int col, char[][] board) {
        List<int[]> moves = new ArrayList<>();
        char piece = board[row][col];

        switch (Character.toLowerCase(piece)) {
            case 'p':
                moves.addAll(getPawnMovesOnBoard(row, col, board));
                break;
            case 'r':
                moves.addAll(getRookMovesOnBoard(row, col, board));
                break;
            case 'n':
                moves.addAll(getKnightMovesOnBoard(row, col, board));
                break;
            case 'b':
                moves.addAll(getBishopMovesOnBoard(row, col, board));
                break;
            case 'q':
                moves.addAll(getQueenMovesOnBoard(row, col, board));
                break;
            case 'k':
                moves.addAll(getKingMovesOnBoard(row, col, board));
                break;
        }

        return moves;
    }

    private List<int[]> getPawnMovesOnBoard(int row, int col, char[][] board) {
        List<int[]> moves = new ArrayList<>();
        int direction = Character.isUpperCase(board[row][col]) ? -1 : 1;
        int startRow = Character.isUpperCase(board[row][col]) ? 6 : 1;

        if (isValidPosition(row + direction, col) && board[row + direction][col] == ' ') {
            moves.add(new int[]{row + direction, col});
            if (row == startRow && board[row + 2 * direction][col] == ' ') {
                moves.add(new int[]{row + 2 * direction, col});
            }
        }

        if (isValidPosition(row + direction, col - 1) && isEnemyPieceOnBoard(row, col, row + direction, col - 1, board)) {
            moves.add(new int[]{row + direction, col - 1});
        }
        if (isValidPosition(row + direction, col + 1) && isEnemyPieceOnBoard(row, col, row + direction, col + 1, board)) {
            moves.add(new int[]{row + direction, col + 1});
        }

        return moves;
    }

    private List<int[]> getRookMovesOnBoard(int row, int col, char[][] board) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            moves.addAll(getMovesInDirectionOnBoard(row, col, dir[0], dir[1], board));
        }

        return moves;
    }

    private List<int[]> getKnightMovesOnBoard(int row, int col, char[][] board) {
        List<int[]> moves = new ArrayList<>();
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            if (isValidPosition(newRow, newCol) && (board[newRow][newCol] == ' ' || isEnemyPieceOnBoard(row, col, newRow, newCol, board))) {
                moves.add(new int[]{newRow, newCol});
            }
        }

        return moves;
    }

    private List<int[]> getBishopMovesOnBoard(int row, int col, char[][] board) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] dir : directions) {
            moves.addAll(getMovesInDirectionOnBoard(row, col, dir[0], dir[1], board));
        }

        return moves;
    }

    private List<int[]> getQueenMovesOnBoard(int row, int col, char[][] board) {
        List<int[]> moves = new ArrayList<>();
        moves.addAll(getRookMovesOnBoard(row, col, board));
        moves.addAll(getBishopMovesOnBoard(row, col, board));
        return moves;
    }

    private List<int[]> getKingMovesOnBoard(int row, int col, char[][] board) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidPosition(newRow, newCol) && (board[newRow][newCol] == ' ' || isEnemyPieceOnBoard(row, col, newRow, newCol, board))) {
                moves.add(new int[]{newRow, newCol});
            }
        }

        return moves;
    }

    private List<int[]> getMovesInDirectionOnBoard(int row, int col, int rowStep, int colStep, char[][] board) {
        List<int[]> moves = new ArrayList<>();
        int newRow = row + rowStep;
        int newCol = col + colStep;

        while (isValidPosition(newRow, newCol)) {
            if (board[newRow][newCol] == ' ') {
                moves.add(new int[]{newRow, newCol});
            } else if (isEnemyPieceOnBoard(row, col, newRow, newCol, board)) {
                moves.add(new int[]{newRow, newCol});
                break;
            } else {
                break;
            }
            newRow += rowStep;
            newCol += colStep;
        }

        return moves;
    }

    private boolean isEnemyPieceOnBoard(int fromRow, int fromCol, int toRow, int toCol, char[][] board) {
        return board[toRow][toCol] != ' ' && Character.isUpperCase(board[fromRow][fromCol]) != Character.isUpperCase(board[toRow][toCol]);
    }

    private boolean isCheckmate(String color) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] != ' ' && (color.equals("white") ? Character.isUpperCase(board[row][col]) : Character.isLowerCase(board[row][col]))) {
                    List<int[]> legalMoves = getLegalMoves(row, col);
                    if (!legalMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}