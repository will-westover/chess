package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING(false, Direction.ALL),
        QUEEN(true, Direction.ALL),
        BISHOP(true, Direction.DIAGONAL),
        KNIGHT(false, Direction.KNIGHT_MOVES),
        ROOK(true, Direction.STRAIGHT),
        PAWN(false, Direction.PAWN_MOVES);

        public final boolean slider;
        public final Direction[] directions;

        PieceType(boolean slider, Direction[] directions) {
            this.slider = slider;
            this.directions = directions;
        }
    }

    public enum Direction {
        UP(1, 0),
        DOWN(-1, 0),
        LEFT(0, -1),
        RIGHT(0, 1),
        UP_RIGHT(1, 1),
        UP_LEFT(1, -1),
        DOWN_RIGHT(-1, 1),
        DOWN_LEFT(-1, -1),
        KNIGHT_1(1, 2),
        KNIGHT_2(2, 1),
        KNIGHT_3(-1, 2),
        KNIGHT_4(-2, 1),
        KNIGHT_5(-2, -1),
        KNIGHT_6(-1, -2),
        KNIGHT_7(1, -2),
        KNIGHT_8(2, -1),
        PAWN_DOUBLE_STEP(2, 0);


        public final int rowDelta;
        public final int colDelta;

        Direction(int rowDelta, int colDelta) {
            this.rowDelta = rowDelta;
            this.colDelta = colDelta;
        }

        public static final Direction[] STRAIGHT = {UP, DOWN, LEFT, RIGHT};
        public static final Direction[] ALL = {UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT};
        public static final Direction[] DIAGONAL = {UP_LEFT, UP_RIGHT, DOWN_RIGHT, DOWN_LEFT};
        public static final Direction[] KNIGHT_MOVES = {KNIGHT_1, KNIGHT_2, KNIGHT_3, KNIGHT_4, KNIGHT_5,
                KNIGHT_6, KNIGHT_7, KNIGHT_8};
        public static final Direction[] PAWN_MOVES = {UP, DOWN, LEFT, RIGHT, PAWN_DOUBLE_STEP,
                UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT};
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;

    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.PAWN){
            potentialMoves.addAll(pawnMoves(board,myPosition));
        } else {
            for (Direction d : piece.getPieceType().directions) {
                potentialMoves.addAll(availableMoves(board, myPosition, d));
            }
        }
        return potentialMoves;
    }

    private Collection<ChessMove> availableMoves(ChessBoard board, ChessPosition start, Direction direction) {
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        Boolean slider = board.getPiece(start).getPieceType().slider;
        int row = start.getRow();
        int col = start.getColumn();

        while (true) {
            row += direction.rowDelta;
            col += direction.colDelta;

            if (row > 8 || row <= 0 || col > 8 || col <= 0) {
                break;
            }
            //main part.
            ChessPosition next = new ChessPosition(row, col);
            ChessPiece available = board.getPiece(next);

            if (available == null) {
                potentialMoves.add(new ChessMove(start, next, null));
                if(!slider){break;}
            } else {
                if (available.getTeamColor() != board.getPiece(start).getTeamColor()) {
                    potentialMoves.add(new ChessMove(start, next, null));
                }
                break;
            }

        }
        return potentialMoves;
    }
    private Collection<ChessMove> pawnMoves (ChessBoard board, ChessPosition start) {
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(start).getTeamColor();
        int row = start.getRow();
        int col = start.getColumn();

        int forward = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promoRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;

        // this is just for a basic one step
        int oneStepRow = row + forward;
        if (oneStepRow >= 1 && oneStepRow <= 8) {
            ChessPosition onePawnStep = new ChessPosition(oneStepRow, col);
            if (board.getPiece(onePawnStep) == null) {
                addPawnMove(potentialMoves, start, onePawnStep, promoRow);
            }
        }
        // this would be for opening
        int twoStepRow = row + 2* forward;
        if (row == startRow){
            ChessPosition doublePawnStep = new ChessPosition(twoStepRow, col);
            ChessPosition onePawnStep = new ChessPosition(oneStepRow, col);
            if (board.getPiece(doublePawnStep) == null && board.getPiece(onePawnStep) == null){
                addPawnMove(potentialMoves, start, doublePawnStep, promoRow);
            }
        }
        // this would be for the ability to capture
        int diagonalCaptureRow = (row + forward);
        int diagonalRightCaptureCol = (col +1);
        int diagonalLeftCaptureCol = + (col - 1);

        if (diagonalCaptureRow > 0 && diagonalCaptureRow <=8
        && diagonalLeftCaptureCol > 0 && diagonalLeftCaptureCol <= 8)
        {
            ChessPosition leftDiagonalCapture = new ChessPosition(diagonalCaptureRow, diagonalLeftCaptureCol);
            ChessPiece leftSquare = board.getPiece(leftDiagonalCapture);
            if(leftSquare != null && leftSquare.getTeamColor() != color){
                addPawnMove(potentialMoves, start, leftDiagonalCapture,promoRow);
            }
        }

        if (diagonalCaptureRow > 0 && diagonalCaptureRow <=8
                && diagonalRightCaptureCol > 0 && diagonalRightCaptureCol <= 8)
        {
            ChessPosition rightDiagonalCapture = new ChessPosition(diagonalCaptureRow, diagonalRightCaptureCol);
            ChessPiece rightSquare = board.getPiece(rightDiagonalCapture);
            if(rightSquare != null && rightSquare.getTeamColor() != color){
                addPawnMove(potentialMoves, start, rightDiagonalCapture,promoRow);
            }
        }

        return potentialMoves;
    }

    private void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, int promoRow ){
        if (end.getRow() == promoRow){
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
        } else{
            moves.add(new ChessMove(start, end, null));
        }
    }

}











