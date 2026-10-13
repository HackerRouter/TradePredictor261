# TradePredictor261

Villager trade predictor for Minecraft Java Edition 26.1 and later.

Since 26.1, villager trades are rolled from a random sequence derived from the world seed and the trade set id
(e.g. `minecraft:trade_set/librarian/level_1`). The sequence is shared by every villager of the same profession
and level in the world. Given the world seed, this tool replays that sequence to show which trades each refresh
will produce.

Supported game versions (releases only): 26.1, 26.1.1, 26.1.2, 26.2, 26.3.

## Running

Requires Java 17 or newer.

Download the `TradePredictor261` artifact from the latest successful **Build Fat JAR** run in GitHub Actions,
unzip it, then:

```
java -jar TradePredictor261.jar
```

`config.json` (seeds, language, game version, tracked positions) and generated files are written to the current
directory.

## First start

1. Select your game version.
2. Enter the world seed. Numbers and text are both accepted and parsed the same way as in the game.

Both can be changed later under **Show config**.

## Modes

Trades, enchantments, potions, stew effects and dye colors are referred to by index. Each mode prints the index
tables for the selected pool.

- **Generate trade sequence**: writes the trades of the first N refreshes of a pool to `data/<seed>/`.
- **Match observed trades**: enter the trades of a few consecutive refreshes you saw in game, one line per slot,
  `done` to finish. If the match is unique, the position in the sequence is saved and used by the other modes.
  - Plain trade: `trade_index`
  - Enchanted book: `trade_index,enchant_idx,level,cost`
  - Enchanted equipment: `trade_index,enchant_idx,level[,enchant_idx,level,...],total_cost` (any order)
- **Predict future trades**: finds the next refreshes that contain the trades you want
  (`trade_index` or `trade_index,enchant_idx,level,max_cost`).
- **Optimal librarian route**: given a list of wanted enchanted books (`enchant_idx,level,max_cost`), finds the
  fewest refreshes across librarian levels 1-4, with one villager (mode a) or several villagers (mode b).

In all inputs, `0` for a level or cost means "any".

## Notes

- Costs are the original price of the trade (base + additional). The price in the trade GUI can be lower because
  of reputation or Hero of the Village, or higher because of demand. Enter `0` if unsure.
- Every refresh of any villager of the same profession and level advances the shared sequence, in any dimension.
- Some pools need extra information: the villager type (cartographer levels 2 and 4, fisherman level 5) and which
  exploration maps cannot be found in your world (cartographer).
- Predictions are wrong if the world's random sequences were changed with `/randomsequence`.

## Building from source

```
javac -encoding UTF-8 -cp "lib/*" -d out *.java rng/*.java trade/*.java enchant/*.java functions/*.java gamedata/*.java mode/*.java io/*.java util/*.java
java -cp "out:lib/*" Main
```

Run it from the repository root so `lang/` and `versions/` are found (use `;` instead of `:` on Windows).
See `.github/workflows/build.yml` for the fat JAR build.

## Adding a game version

Trade data is generated from the game's data files (`data/minecraft/...`):

```
java -cp lib/gson-2.13.1.jar tools/ExtractTradeData.java <resources-dir> <version> versions/<version>.json
```

Then add the version to `versions/index.json`. Versions whose generated data is identical can share one file.
