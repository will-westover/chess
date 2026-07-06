package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
    private final static int [][] STRAIGHT = {{1,0},{0,1},{-1,0},{0,-1}};
    private final static int [][] DIAGONAL = {{1,1},{1,-1},{-1,1},{-1,-1}};
    private final static int [][] ALL = {{1,0},{0,1},{-1,0},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
    private final static int [][] KNIGHT_MOVES = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};

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
    public ChessGame.TeamColor getTeamColor() {return pieceColor;}

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {return type;}

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch(type){
            case KING -> step(board, myPosition, moves, ALL);
            case KNIGHT -> step(board, myPosition, moves, KNIGHT_MOVES);
            case QUEEN -> slide(board, myPosition, moves, ALL);
            case ROOK -> slide(board, myPosition, moves, STRAIGHT);
            case BISHOP -> slide(board, myPosition, moves, DIAGONAL);
            case PAWN-> pawnMoves(board, myPosition, moves);
        }
        return moves;
    }

    private void step(ChessBoard board, ChessPosition from, Collection<ChessMove>moves, int dir[][]){
        for(int d[]: dir){
            int row = from.getRow()+d[0];
            int col = from.getColumn()+d[1];
            if(!inBound(row,col)) continue;

            ChessPosition to = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(to);
            if(target == null || target.getTeamColor() != pieceColor){
                moves.add(new ChessMove(from,to, null));
            }
        }
    }
    private void slide(ChessBoard board, ChessPosition from, Collection<ChessMove>moves, int dir[][]){
        for(int d[]: dir){
            int row = from.getRow();
            int col = from.getColumn();

            while(true){
                row += d[0];
                col += d[1];
                if(!inBound(row,col)) break;

                ChessPosition to = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(to);

                if(target == null){
                    moves.add(new ChessMove(from,to, null));
                } else{
                    if(target.getTeamColor() != pieceColor){
                        moves.add(new ChessMove(from,to, null));
                    }
                    break;
                }

            }
        }

    }
    private void pawnMoves(ChessBoard board, ChessPosition from, Collection<ChessMove>moves){
        int dir = (pieceColor == ChessGame.TeamColor.WHITE) ? 1:-1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2:7;
        int promoRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 8:1;
        int row = from.getRow();
        int col = from.getColumn();

        ChessPosition oneAhead = new ChessPosition(row + dir,col);
        ChessPiece target1 = board.getPiece(oneAhead);
        if(target1 == null && inBound(row+dir,col)){
            addPawnMoves(moves,from,oneAhead,promoRow);

            ChessPosition twoAhead = new ChessPosition(row + 2*dir, col);
            if(startRow == row){
                ChessPiece target2 = board.getPiece(twoAhead);
                if(target2 == null){
                    addPawnMoves(moves,from,twoAhead,promoRow);
                }
            }
        }

        for(int dc: new int[]{-1,1}) {
            int newRow = row + dir;
            int newCol = col + dc;
            if(!inBound(newRow,newCol)) continue;

            ChessPosition diag = new ChessPosition(newRow,newCol);
            ChessPiece target = board.getPiece(diag);
            if(target != null && target.getTeamColor() != pieceColor){
                addPawnMoves(moves, from, diag, promoRow);
            }

        }

    }
    private void addPawnMoves(Collection<ChessMove>moves, ChessPosition from, ChessPosition to, int promo){
        if (to.getRow() == promo){
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
        }else{
            moves.add(new ChessMove(from, to, null));
        }
    }
    private boolean inBound(int row, int col){
        return row >= 1 && row <=8 && col >= 1 && col <=8;
    }
}
