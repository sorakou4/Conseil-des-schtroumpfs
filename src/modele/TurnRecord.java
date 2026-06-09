package modele;

import java.util.List;
import java.util.Map;

public record TurnRecord(
    int turn,
    String eventDescription,
    String firstCharacterName,
    String firstActionName,
    String secondCharacterName,
    String secondActionName,
    List<String> activeCrises,
    Map<String, Integer> resources,
    int scoreAtEndOfTurn
) {
    public String toDisplayText() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══ MOIS ").append(turn).append(" ═══\n");
        sb.append("📋 Événement : ").append(eventDescription).append("\n");
        sb.append("👤 ").append(firstCharacterName).append(" → ").append(firstActionName).append("\n");
        sb.append("👤 ").append(secondCharacterName).append(" → ").append(secondActionName).append("\n");
        sb.append("📦 Ressources :\n");
        resources.forEach((k, v) -> sb.append("  ").append(k).append(" : ").append(v).append("\n"));
        if (activeCrises.isEmpty()) {
            sb.append("✅ Aucune crise\n");
        } else {
            sb.append("⚠️ Crises : ").append(String.join(", ", activeCrises)).append("\n");
        }
        return sb.toString();
    }
}