package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessBoard board) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        Collection<ChessMove> allMoves = allBoardMoves(board);
        int count = allMoves.size();

        //code for what determines what a valid move would be
        //we need to check if each move puts the our king in check or not.
        //we should get all of the moves from the other team if we move each peice to every possible legal play
        //if one of the moves that we makes returns moves from the other team that could put our king in check
        //then we shouldn't allow the user to make that move and instead remove it from the vlaid moves list.
        for (ChessMove move : allMoves){
            //for each of the moves we want to generate a new board and return all of the moves after that.
            //Get origninal board
            //Get first move and apply it
            //return the moves of the other team
            // if any of the moves of the other team have the position of the king, then remove the move we're evaluating
            ChessBoard copy = new ChessBoard(board);
            ChessPiece piece = copy.getPiece(move.getStartPosition());

            if(move.getPromotionPiece() != null){
                piece = new ChessPiece(piece.getTeamColor(),piece.getPieceType());
            }
            copy.addPiece(move.getEndPosition(), piece);
            copy.addPiece(move.getStartPosition(), null);

        }




        return validMoves;
    }
    public Collection<ChessMove> allBoardMoves(ChessBoard board){
        Collection<ChessMove> legalBoardMoves = new ArrayList<>();

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition currentPosition = new ChessPosition(i,j);
                ChessPiece current = board.getPiece(currentPosition);

                if(current != null){
                    Collection<ChessMove> moves = current.pieceMoves(board,currentPosition);
                    legalBoardMoves.addAll(moves);
                }
            }
        }
        return legalBoardMoves;
    }

    public ChessPosition kingLocation(ChessBoard board, ChessGame.TeamColor color){
        for (int i = 1; i <=8; i++){
            for (int j = 1; j<=8; j++){
                ChessPosition evalPosition = new ChessPosition(i,j);
                ChessPiece evalPiece= board.getPiece(evalPosition);

                if(evalPiece != null && evalPiece.getPieceType() == ChessPiece.PieceType.KING
                        && evalPiece.getTeamColor() == color){
                    return new ChessPosition(i, j);
                }
            }
        }
        return null;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
