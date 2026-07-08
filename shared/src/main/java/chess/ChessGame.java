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
    private ChessBoard board;
    private TeamColor turn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
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

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = getBoard().getPiece(startPosition);
        if (piece == null) {return null;}

        TeamColor color = piece.getTeamColor();
        TeamColor enemy = (color == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);

        for (ChessMove move : allMoves){
            ChessBoard copy = new ChessBoard(board);
            ChessPiece moving = copy.getPiece(move.getStartPosition());

            if(move.getPromotionPiece() != null){
                moving = new ChessPiece(moving.getTeamColor(),move.getPromotionPiece());
            }
            copy.addPiece(move.getEndPosition(), moving);
            copy.addPiece(move.getStartPosition(), null);

            Collection<ChessMove> copyMoves = allBoardMoves(copy, enemy);
            ChessPosition kingPosition = kingLocation(copy,color);
            boolean kingCaptured = false;
            for (ChessMove capture: copyMoves){
                if(capture.getEndPosition().equals(kingPosition)){
                    kingCaptured = true;
                    break;
                }
            }
            if(!kingCaptured){
                validMoves.add(move);
            }

        }
        return validMoves;
    }


    public Collection<ChessMove> allBoardMoves(ChessBoard board, TeamColor color){
        Collection<ChessMove> legalBoardMoves = new ArrayList<>();

        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition currentPosition = new ChessPosition(i,j);
                ChessPiece current = board.getPiece(currentPosition);

                if(current != null){
                    if (current.getTeamColor() == color) {
                        Collection<ChessMove> moves = current.pieceMoves(board, currentPosition);
                        legalBoardMoves.addAll(moves);
                    }
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
        ChessPosition moveStartPosition = move.getStartPosition();
        ChessPosition moveEndPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(moveStartPosition);

        Collection<ChessMove> legal = validMoves(moveStartPosition);

        if(piece == null || piece.getTeamColor() != turn){
            throw new InvalidMoveException("Illegal move");
        }
        if(!legal.contains(move)){
            throw new InvalidMoveException("Illegal move");
        }
        if(move.getPromotionPiece() != null){
            piece = new ChessPiece(turn, move.getPromotionPiece());
        }
        board.addPiece(moveEndPosition, piece);
        board.addPiece(moveStartPosition, null);
        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //return whether the current set up of the board is in check or not
        //if the current moves of the other team are the location of your king then youre semi-cooked
        ChessPosition king = kingLocation(board, teamColor);
        TeamColor enemy = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        //we want the end position of each move
        for (ChessMove move: allBoardMoves(board,enemy)){
            if(move.getEndPosition().equals(king)){
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //get all of the moves that the king could make
        //itterate through all of the moves that the other team could make
        //store those moves
        //if all of the moves a king could make are in that list AND its in check, then he cooked
        return true;
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
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
