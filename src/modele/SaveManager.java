package modele;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import modele.ShopManager;

public class SaveManager {

    public static final int SLOT_COUNT = 4;
    private static final String SAVE_DIR      = System.getProperty("user.dir") + File.separator + "saves";
    private static final String SAVE_FILE     = SAVE_DIR + File.separator + "save.txt";
    private static final String SCORE_FILE    = SAVE_DIR + File.separator + "scoreboard.txt";
    private static final String PURCHASE_FILE = SAVE_DIR + File.separator + "purchases.txt";

    // Init 

    public static void init() {
        new File(SAVE_DIR).mkdirs();
        if (!new File(SAVE_FILE).exists()) {
            createEmptyFile();
        }
    }

    private static void createEmptyFile() {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= SLOT_COUNT; i++) {
                sb.append("=== SLOT ").append(i).append(" ===\n");
                sb.append("EMPTY\n");
                sb.append("=== END SLOT ").append(i).append(" ===\n");
            }
            Files.writeString(Path.of(SAVE_FILE), sb.toString());
        } catch (IOException e) {
            System.err.println("[SaveManager] Cannot create save file: " + e.getMessage());
        }
    }

    // Slot data

    public static class SlotData {
        public String villageName = "";
        public int    turn = 0;
        public String date = "";
        public Map<String, Integer> resources = new LinkedHashMap<>();
        public List<String> crises = new ArrayList<>();
        public List<String> events = new ArrayList<>();

        public String toSlotLabel(int slotNumber) {
            return "Slot " + slotNumber
                + "   Tour " + turn + "/12"
                + "  Village " + villageName
                + "   " + date;
        }
    }

    public static SlotData[] loadAllSlots() {
        init();
        SlotData[] slots = new SlotData[SLOT_COUNT];
        try {
            String content = Files.readString(Path.of(SAVE_FILE));
            for (int i = 1; i <= SLOT_COUNT; i++) {
                slots[i - 1] = parseSlot(content, i);
            }
        } catch (IOException e) {
            System.err.println("[SaveManager] Read error: " + e.getMessage());
        }
        return slots;
    }

    public static SlotData loadSlot(int slot) {
        return loadAllSlots()[slot - 1];
    }

    public static void save(int slot, SmurfVillage village,
                            String villageName, int turn) {
        init();
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        StringBuilder sb = new StringBuilder();
        sb.append("village:").append(villageName).append("\n");
        sb.append("turn:").append(turn).append("\n");
        sb.append("date:").append(date).append("\n");

        StringJoiner resJoiner = new StringJoiner(",");
        for (Resource r : Resource.values()) {
            resJoiner.add(r.name() + "=" + village.getQuantity(r));
        }
        sb.append("resources:").append(resJoiner).append("\n");

        StringJoiner crisisJoiner = new StringJoiner(",");
        for (CrisisType c : village.getCurrentCrises()) {
            crisisJoiner.add(c.name());
        }
        sb.append("crises:").append(crisisJoiner).append("\n");

        StringJoiner eventJoiner = new StringJoiner("|");
        for (Event e : village.getEventHistory()) {
            eventJoiner.add(e.getDescription());
        }
        sb.append("events:").append(eventJoiner).append("\n");

        replaceSlotInFile(slot, sb.toString());
    }

    public static void deleteSlot(int slot) {
        replaceSlotInFile(slot, "EMPTY\n");
    }

    // Scoreboard

    public record ScoreEntry(String villageName, int score, int turns, String date) {
        @Override public String toString() {
            return villageName + "|" + score + "|" + turns + "|" + date;
        }
        public static ScoreEntry parse(String line) {
            String[] p = line.split("\\|", 4);
            if (p.length < 4) return null;
            try { return new ScoreEntry(p[0], Integer.parseInt(p[1]),
                                        Integer.parseInt(p[2]), p[3]); }
            catch (NumberFormatException e) { return null; }
        }
    }

    public static void addScore(String villageName, int score, int turns) {
        init();
        List<ScoreEntry> entries = loadScoreboard();
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        entries.add(new ScoreEntry(villageName, score, turns, date));
        entries.sort((a, b) -> Integer.compare(b.score(), a.score()));
        if (entries.size() > 10) entries = entries.subList(0, 10);
        try {
            StringBuilder sb = new StringBuilder();
            entries.forEach(e -> sb.append(e).append("\n"));
            Files.writeString(Path.of(SCORE_FILE), sb.toString());
        } catch (IOException e) {
            System.err.println("[SaveManager] Scoreboard write error: " + e.getMessage());
        }
    }

    public static List<ScoreEntry> loadScoreboard() {
        List<ScoreEntry> list = new ArrayList<>();
        File f = new File(SCORE_FILE);
        if (!f.exists()) return list;
        try {
            for (String line : Files.readAllLines(f.toPath())) {
                if (line.isBlank()) continue;
                ScoreEntry e = ScoreEntry.parse(line);
                if (e != null) list.add(e);
            }
        } catch (IOException e) {
            System.err.println("[SaveManager] Scoreboard read error: " + e.getMessage());
        }
        return list;
    }

    public static void clearScoreboard() {
        new File(SCORE_FILE).delete();
    }

    // Achats (Sauvegarde les IDs des Schtroumpfs achetés.)

    public static void savePurchases(List<String> purchasedIds) {
        init();
        try {
            StringBuilder sb = new StringBuilder();
            for (String id : purchasedIds) sb.append(id).append("\n");
            Files.writeString(Path.of(PURCHASE_FILE), sb.toString());
        } catch (IOException e) {
            System.err.println("[SaveManager] Purchase write error: " + e.getMessage());
        }
    }

    public static void loadPurchases() {
        File f = new File(PURCHASE_FILE);
        if (!f.exists()) return;
        try {
            List<String> ids = new ArrayList<>();
            for (String line : Files.readAllLines(f.toPath())) {
                if (!line.isBlank()) ids.add(line.trim());
            }
            ShopManager.loadPurchases(ids);
        } catch (IOException e) {
            System.err.println("[SaveManager] Purchase read error: " + e.getMessage());
        }
    }

    private static SlotData parseSlot(String fileContent, int slot) {
        String startTag = "=== SLOT " + slot + " ===";
        String endTag   = "=== END SLOT " + slot + " ===";
        int start = fileContent.indexOf(startTag);
        int end   = fileContent.indexOf(endTag);
        if (start < 0 || end < 0) return null;

        String block = fileContent.substring(start + startTag.length(), end).trim();
        if (block.equals("EMPTY")) return null;

        SlotData data = new SlotData();
        for (String line : block.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;
            int colon = line.indexOf(':');
            if (colon < 0) continue;
            String key   = line.substring(0, colon).trim();
            String value = line.substring(colon + 1).trim();
            switch (key) {
                case "village"   -> data.villageName = value;
                case "turn"      -> { try { data.turn = Integer.parseInt(value); }
                                      catch (NumberFormatException ignored) {} }
                case "date"      -> data.date = value;
                case "resources" -> {
                    for (String pair : value.split(",")) {
                        String[] kv = pair.split("=");
                        if (kv.length == 2) {
                            try { data.resources.put(kv[0].trim(),
                                                     Integer.parseInt(kv[1].trim())); }
                            catch (NumberFormatException ignored) {}
                        }
                    }
                }
                case "crises" -> {
                    if (!value.isBlank())
                        data.crises.addAll(Arrays.asList(value.split(",")));
                }
                case "events" -> {
                    if (!value.isBlank())
                        data.events.addAll(Arrays.asList(value.split("\\|")));
                }
            }
        }
        return data;
    }

    private static void replaceSlotInFile(int slot, String newContent) {
        init();
        try {
            String content = Files.readString(Path.of(SAVE_FILE));
            String startTag = "=== SLOT " + slot + " ===";
            String endTag   = "=== END SLOT " + slot + " ===";
            int start = content.indexOf(startTag);
            int end   = content.indexOf(endTag);
            if (start < 0 || end < 0) return;

            String updated = content.substring(0, start)
                    + startTag + "\n"
                    + newContent
                    + endTag + "\n"
                    + content.substring(end + endTag.length());

            Files.writeString(Path.of(SAVE_FILE), updated);
        } catch (IOException e) {
            System.err.println("[SaveManager] Write error: " + e.getMessage());
        }
    }
}
