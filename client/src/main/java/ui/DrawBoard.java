package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private static final String LIGHT_SQUARE = "\u001b[48;5;187m";
    private static final String DARK_SQUARE = "\u001b[48;5;107m";

    public static void design(ChessGame game, boolean whiteSide) {
        var board = game.getBoard();
        int rankStart = whiteSide ? 8 : 1;
        int rankStep = whiteSide ? -1 : 1;
        int fileStart = whiteSide ? 1 : 8;
        int fileStep = whiteSide ? 1 : -1;

        printFiles(fileStart, fileStep);
        for (int i = 0; i < 8; i++) {
            int rank = rankStart + i * rankStep;
            System.out.print(" " + rank + " ");
            for (int j = 0; j < 8; j++) {
                int file = fileStart + j * fileStep;
                boolean light = (rank + file) % 2 == 1;
                String background = light ? LIGHT_SQUARE : DARK_SQUARE;
                var piece = board.getPiece(new ChessPosition(rank, file));
                String foreground = piece == null ? "" : (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK);
                System.out.print(background + foreground + glyph(piece) + RESET_TEXT_COLOR + RESET_BG_COLOR);
            }
            System.out.println(" " + rank + " ");
        }
        printFiles(fileStart, fileStep);
    }

    private static void printFiles(int fileStart, int fileStep) {
        System.out.print("   ");
        for (int i = 0; i < 8; i++) {
            int file = fileStart + i * fileStep;
            char letter = (char) ('a' + file - 1);
            System.out.print(" " + letter + " ");
        }
        System.out.println();
    }

    private static String glyph(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> white ? WHITE_KING : BLACK_KING;
            case QUEEN -> white ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> white ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> white ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> white ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> white ? WHITE_PAWN : BLACK_PAWN;
        };
    }

    public static void highlight(ChessGame game, boolean whiteSide, ChessPosition source) {
        var board = game.getBoard();
        var moves = game.validMoves(source);
        java.util.Set<ChessPosition> targets = new java.util.HashSet<>();

        if (moves != null) {
            for (var m : moves) {
                targets.add(m.getEndPosition());
            }
        }

        int rankStart = whiteSide ? 8 : 1;
        int rankStep = whiteSide ? -1 : 1;
        int fileStart = whiteSide ? 1 : 8;
        int fileStep = whiteSide ? 1 : -1;

        printFiles(fileStart, fileStep);
        for (int i = 0; i < 8; i++) {
            int rank = rankStart + i * rankStep;
            System.out.print(" " + rank + " ");
            for (int j = 0; j < 8; j++) {
                int file = fileStart + j * fileStep;
                var pos = new ChessPosition(rank, file);
                boolean light = (rank + file) % 2 == 1;
                String background;
                if (pos.equals(source)) {
                    background = SET_BG_COLOR_YELLOW;
                } else if (targets.contains(pos)) {
                    background = SET_BG_COLOR_GREEN;
                } else {
                    background = light ? LIGHT_SQUARE : DARK_SQUARE;
                }
                var piece = board.getPiece(pos);
                String foreground = piece == null ? "" : (piece.getTeamColor() ==
                        ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK);
                System.out.print(background + foreground + glyph(piece) + RESET_TEXT_COLOR + RESET_BG_COLOR);
            }
            System.out.println(" " + rank + " ");
        }
        printFiles(fileStart, fileStep);
    }
}
