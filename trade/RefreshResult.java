package trade;

import java.util.ArrayList;
import java.util.List;

public class RefreshResult {
    public final int refreshIndex;       // 1-based refresh number
    public final List<TradeResult> trades;

    public RefreshResult(int refreshIndex) {
        this.refreshIndex = refreshIndex;
        this.trades = new ArrayList<>();
    }

    public void addTrade(TradeResult trade) {
        trades.add(trade);
    }

    public int getSuccessfulTradeCount() {
        int count = 0;
        for (TradeResult t : trades) {
            if (!t.discarded) count++;
        }
        return count;
    }

    public List<String> toOutputLines() {
        List<String> lines = new ArrayList<>();
        int slotIdx = 1;
        for (TradeResult trade : trades) {
            if (!trade.discarded) {
                lines.add(refreshIndex + "|" + slotIdx + "|" + trade.getFullDescription());
                slotIdx++;
            }
        }
        return lines;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Refresh #").append(refreshIndex).append(":\n");
        int slotIdx = 1;
        for (TradeResult trade : trades) {
            if (!trade.discarded) {
                sb.append("  Slot ").append(slotIdx).append(": ").append(trade.getFullDescription()).append("\n");
                slotIdx++;
            } else {
                sb.append("  [Discarded: ").append(trade.tradeName).append("]\n");
            }
        }
        return sb.toString();
    }
}
