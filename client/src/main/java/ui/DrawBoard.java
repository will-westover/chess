package ui;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private static final String[][] PIECES = new String[8][8];

    static {
        String[] whitePieces = {
                WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
                WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK
        };
        String[] blackPieces = {
                BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK
        };

        for (int i = 0; i < 8; i++) {
            PIECES[0][i] = whitePieces[i];
            PIECES[1][i] = WHITE_PAWN;
            PIECES[6][i] = BLACK_PAWN;
            PIECES[7][i] = blackPieces[i];
            for (int j = 2; j <= 5; j++) {
                PIECES[j][i] = EMPTY;
            }
        }
    }

    public static void design(boolean whiteSide) {
        int rankStart = whiteSide ? 8 : 1;
        int rankStep = whiteSide ? -1 : 1;
        int fileStart = whiteSide ? 1 : 8;
        int fileStep = whiteSide ? 1 : -1;

        for (int i = 0; i < 8; i++) {
            int rank = rankStart + i * rankStep;
            System.out.print(" " + rank + " ");
            for (int j = 0; j < 8; j++) {
                int file = fileStart + j * fileStep;
                boolean light = (rank + file) % 2 == 1;
                String background = light ? SET_BG_COLOR_WHITE : SET_BG_COLOR_DARK_GREY;
                System.out.print(background + PIECES[rank - 1][file - 1] + RESET_BG_COLOR);
            }
            System.out.println();
        }
    }
}
