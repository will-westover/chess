package dataaccess;
import model.GameData;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    int nextId = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

}
