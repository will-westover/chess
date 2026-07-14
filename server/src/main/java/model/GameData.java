package model;
import chess.ChessGame;

public record GameData (int GameID,String whiteUserName, String blackUserName, String gameName, ChessGame game ){};
