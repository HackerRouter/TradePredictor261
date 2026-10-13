package trade;

import gamedata.GameData;

import java.util.List;

public class TradePoolRegistry {

    public static TradePool getPool(String profession, int level) {
        return GameData.get().getPool(profession, level);
    }

    public static List<String> getAllProfessions() {
        return GameData.get().getProfessions();
    }
}
