package trade;

import util.I18n;

import java.util.ArrayList;
import java.util.List;

public class RefreshResult {
    public final int refreshIndex;
    public final List<TradeResult> trades;

    public RefreshResult(int refreshIndex) {
        this.refreshIndex = refreshIndex;
        this.trades = new ArrayList<>();
    }

    public void addTrade(TradeResult trade) {
        trades.add(trade);
    }

    public List<TradeResult> getOffers() {
        List<TradeResult> offers = new ArrayList<>();
        for (TradeResult trade : trades) {
            if (!trade.discarded) offers.add(trade);
        }
        return offers;
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
        sb.append(I18n.get("result.refresh", refreshIndex)).append("\n");
        int slotIdx = 1;
        for (TradeResult trade : trades) {
            if (!trade.discarded) {
                sb.append("  ").append(I18n.get("result.slot", slotIdx)).append(trade.getFullDescription()).append("\n");
                slotIdx++;
            } else {
                sb.append("  ").append(trade.getFullDescription()).append("\n");
            }
        }
        return sb.toString();
    }
}
