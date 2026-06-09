package modele;

import java.util.Objects;

public class Event {

    // Type d'événement (attaque, festival, tempête...)
    private final EventType type;

    public Event(EventType type) {
        this.type = Objects.requireNonNull(type, "Event type cannot be null");
    }

    public EventType getType() {
        return type;
    }

    // Description affichée au joueur
    public String getDescription() {
        switch (type) {

            case GARGAMEL_ATTACK:
                return "Gargamel attaque le village !";

            case MAGIC_BERRIES_DISCOVERY:
                return "Les Schtroumpfs découvrent des baies magiques.";

            case FRIENDLY_VILLAGE_VISIT:
                return "Un village ami vient rendre visite aux Schtroumpfs.";

            case SARSAPARILLA_STORM:
                return "Une tempête de salsepareille frappe le village.";

            case SMURF_FESTIVAL:
                return "Les Schtroumpfs organisent une grande fête.";

            case FOREST_CURSE:
                return "Une mystérieuse malédiction touche la forêt.";

            default:
                return "Événement inconnu.";
        }
    }

    // Effets appliqués au village
    public void applyEvent(SmurfVillage village) {

        Objects.requireNonNull(village, "Village cannot be null");

        switch (type) {

            case GARGAMEL_ATTACK:
                village.modifyResource(Resource.DEFENSE, -2);
                village.modifyResource(Resource.MORALE, -1);
                break;

            case MAGIC_BERRIES_DISCOVERY:
                village.modifyResource(Resource.BERRIES, 2);
                village.modifyResource(Resource.SARSAPARILLA, 1);
                break;

            case FRIENDLY_VILLAGE_VISIT:
                village.modifyResource(Resource.GOLD, 2);
                break;

            case SARSAPARILLA_STORM:
                village.modifyResource(Resource.TOOLS, -1);
                break;

            case SMURF_FESTIVAL:
                village.modifyResource(Resource.MORALE, 2);
                village.modifyResource(Resource.BERRIES, -1);
                break;

            case FOREST_CURSE:
                village.modifyResource(Resource.KNOWLEDGE, -2);
                break;
        }
    }

    // Texte détaillé des effets
    public String getEffectDescription() {
        switch (type) {

            case GARGAMEL_ATTACK:
                return "Effets :\n- Défense : -2\n- Moral : -1";

            case MAGIC_BERRIES_DISCOVERY:
                return "Effets :\n+ Baies : +2\n+ Salsepareille : +1";

            case FRIENDLY_VILLAGE_VISIT:
                return "Effets :\n+ Or : +2\nBonus possible : +Moral si Schtroumpfette agit.";

            case SARSAPARILLA_STORM:
                return "Effets :\n- Outils : -1\nBonus possible : +Savoir si l’événement est étudié.";

            case SMURF_FESTIVAL:
                return "Effets :\n+ Moral : +2\n- Baies : -1";

            case FOREST_CURSE:
                return "Effets :\n- Savoir : -2\nLa malédiction peut fragiliser le village sur les prochains tours.";

            default:
                return "Aucun effet.";
        }
    }
}
