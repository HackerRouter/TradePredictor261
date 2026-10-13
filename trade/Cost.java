package trade;

import com.google.gson.JsonObject;

public class Cost {
    public final String item;
    public final int count;
    public final int maxStack;

    public Cost(String item, int count, int maxStack) {
        this.item = item;
        this.count = count;
        this.maxStack = maxStack;
    }

    public static Cost parse(JsonObject json) {
        return new Cost(json.get("item").getAsString(), json.get("count").getAsInt(),
            json.has("max_stack") ? json.get("max_stack").getAsInt() : 64);
    }

    public int withAdditional(int additional) {
        return Math.max(0, Math.min(maxStack, count + additional));
    }
}
