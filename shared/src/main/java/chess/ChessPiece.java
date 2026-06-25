package chess;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
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
        switch (piece.getPieceType()){
            case ROOK -> {

            }
            case BISHOP -> {

            }
            case QUEEN -> {

            }
            case KNIGHT -> {

            }
            case PAWN -> {

            }
            case KING -> {

            }
            case null -> {}

        }

        return potentialMoves;
    }

    private Collection<ChessMove> horizontalShort(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        ChessPosition rightPosition =  new ChessPosition(myPosition.getRow(),myPosition.getColumn() + 1);
        ChessPosition leftPosition =  new ChessPosition(myPosition.getRow(),myPosition.getColumn()-1);

        if (
                rightPosition.getRow() <= 8 && rightPosition.getRow() > 0
                && rightPosition.getColumn() <= 8 && rightPosition.getColumn() > 0
        ){
            if (board.getPiece(rightPosition) == null) {
                potentialMoves.add(new ChessMove(myPosition,rightPosition,null));
            }
            if (board.getPiece(leftPosition) == null) {
                potentialMoves.add(new ChessMove(myPosition,leftPosition,null));
            }
        }
        return potentialMoves;
    }
}
