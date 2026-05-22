package Valdris.model.items;

import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;

/**
 * Item de progresión narrativa guardado en una sección propia del inventario.
 *
 * <p>Estos objetos representan llaves, fragmentos, semillas o documentos que
 * abren rutas y diálogos. No se equipan, no se consumen y no aportan estadísticas
 * directas de combate.</p>
 */
public class NarrativeItem extends Item {

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un item narrativo.
     *
     * @param id identificador único
     * @param nombre nombre visible
     * @param descripcion descripción narrativa o funcional
     */
    public NarrativeItem(String id, String nombre, String descripcion) {
        super(id, nombre, ItemType.NARRATIVE, descripcion);
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * No equipa ni consume el item.
     *
     * <p>La utilidad real de estos objetos se consulta por id desde la lógica de
     * mapa, puertas, salas secretas o diálogos.</p>
     *
     * @param player jugador que intenta usar el item
     */
    @Override
    public void use(Player player) {
        // Los items narrativos se conservan como progreso permanente.
    }
}
