package trade;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import rng.Xoroshiro128PlusPlus;

import java.util.ArrayList;
import java.util.List;

public interface IntProvider {

    int getInt(Xoroshiro128PlusPlus rng);

    static IntProvider parse(JsonObject json) {
        String type = json.get("type").getAsString();
        switch (type) {
            case "constant": {
                int value = json.get("value").getAsInt();
                return rng -> value;
            }
            case "uniform": {
                int min = json.get("min").getAsInt();
                int max = json.get("max").getAsInt();
                return rng -> rng.nextInt(min, max);
            }
            case "binomial": {
                int n = json.get("n").getAsInt();
                float p = json.get("p").getAsFloat();
                return rng -> {
                    int result = 0;
                    for (int i = 0; i < n; i++) {
                        if (rng.nextFloat() < p) result++;
                    }
                    return result;
                };
            }
            case "sum": {
                List<IntProvider> values = new ArrayList<>();
                for (JsonElement value : json.getAsJsonArray("values")) {
                    values.add(parse(value.getAsJsonObject()));
                }
                return rng -> {
                    int total = 0;
                    for (IntProvider value : values) total += value.getInt(rng);
                    return total;
                };
            }
            default:
                throw new IllegalArgumentException("Unknown number provider: " + type);
        }
    }
}
