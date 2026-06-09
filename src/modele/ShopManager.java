package modele;

import java.util.ArrayList;
import java.util.List;

public class ShopManager {

    public static class ShopItem {
        public final String id;
        public final String name;
        public final String description;
        public final double price;
        private boolean purchased;

        public ShopItem(String id, String name, String description, double price) {
            this.id          = id;
            this.name        = name;
            this.description = description;
            this.price       = price;
            this.purchased   = false;
        }

        public boolean isPurchased() { return purchased; }
        public void setPurchased(boolean purchased) { this.purchased = purchased; }
    }

    public static class Bundle {
        public final String id;
        public final String name;
        public final String description;
        public final double price;
        public final List<String> itemIds;
        private boolean purchased;

        public Bundle(String id, String name, String description, double price, List<String> itemIds) {
            this.id          = id;
            this.name        = name;
            this.description = description;
            this.price       = price;
            this.itemIds     = itemIds;
            this.purchased   = false;
        }

        public boolean isPurchased() { return purchased; }
        public void setPurchased(boolean purchased) { this.purchased = purchased; }
    }

    private static final List<ShopItem> items   = new ArrayList<>();
    private static final List<Bundle>   bundles = new ArrayList<>();
    private static final List<String>   purchasedIds = new ArrayList<>();

    static {
        items.add(new ShopItem(
            "BRAINY",
            "Schtroumpf à Lunettes",
            "Augmente le savoir du village.\nDonne un cours, lit un livre ou fait un discours.",
            4.99
        ));
        items.add(new ShopItem(
            "MINER",
            "Schtroumpf Mineur",
            "Explore les tunnels pour récupérer des ressources.\nMine de l'or, renforce les outils ou sécurise un tunnel.",
            4.99
        ));
        items.add(new ShopItem(
            "MAGICIAN",
            "Schtroumpf Magicien",
            "Transmute les ressources entre elles.\nL'outil parfait pour rééquilibrer le village.",
            7.99
        ));

        bundles.add(new Bundle(
            "BUNDLE_ALL",
            "Pack Complet",
            "Obtenez les 3 Schtroumpfs premium en une seule fois !\nÉconomisez " + String.format("%.2f", (4.99 + 4.99 + 7.99) - 13.99) + "€ par rapport à l'achat séparé.",
            13.99,
            List.of("BRAINY", "MINER", "MAGICIAN")
        ));
    }

    public static List<ShopItem> getItems()   { return items; }
    public static List<Bundle>   getBundles() { return bundles; }

    public static ShopItem getItemById(String id) {
        for (ShopItem item : items)
            if (item.id.equals(id)) return item;
        return null;
    }

    public static void purchase(String id) {
        ShopItem item = getItemById(id);
        if (item != null && !item.isPurchased()) {
            item.setPurchased(true);
            if (!purchasedIds.contains(id)) purchasedIds.add(id);
        }
    }

    public static void purchaseBundle(String bundleId) {
        for (Bundle b : bundles) {
            if (b.id.equals(bundleId) && !b.isPurchased()) {
                b.setPurchased(true);
                for (String id : b.itemIds) purchase(id);
            }
        }
    }

    public static boolean isPurchased(String id) {
        ShopItem item = getItemById(id);
        return item != null && item.isPurchased();
    }

    public static boolean isBundlePurchased(String bundleId) {
        for (Bundle b : bundles)
            if (b.id.equals(bundleId)) return b.isPurchased();
        return false;
    }

    public static List<String> getPurchasedIds() { return purchasedIds; }

    public static void loadPurchases(List<String> ids) {
        purchasedIds.clear();
        for (String id : ids) {
            purchasedIds.add(id);
            ShopItem item = getItemById(id);
            if (item != null) item.setPurchased(true);
        }
        for (Bundle b : bundles) {
            if (purchasedIds.containsAll(b.itemIds)) b.setPurchased(true);
        }
    }

    public static double getTotalSeparate() {
        double total = 0;
        for (ShopItem item : items) total += item.price;
        return total;
    }
}