package multiple.blockchain;

import constantes.Tipo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase BlockchainMultiple
 */
public class BlockchainMultiple implements Serializable {

    private List<BloqueMultiple> bloques = new CopyOnWriteArrayList<>();
    private HashMap<Tipo,List<Double>> tiempoEntreCreacionDeBloques = new HashMap<>();

    public BlockchainMultiple() {
        tiempoEntreCreacionDeBloques.put(Tipo.LOGICO1, new CopyOnWriteArrayList<>());
        tiempoEntreCreacionDeBloques.put(Tipo.LOGICO2, new CopyOnWriteArrayList<>());
        bloques.addAll(crearPrimerBloque());
    }

    public HashMap<Tipo, List<Double>> getTiempoEntreCreacionDeBloques() {
        return tiempoEntreCreacionDeBloques;
    }

    /**
     * Método que crea el primer bloque de cada blockchain lógico.
     *
     * @return Primeros bloques.
     */
    public List<BloqueMultiple> crearPrimerBloque() {
        BloqueMultiple primerBloqueMultiple = new BloqueMultiple(Tipo.LOGICO1);
        BloqueMultiple segundoBloqueMultiple = new BloqueMultiple(primerBloqueMultiple, Tipo.LOGICO2);
        return Arrays.asList(primerBloqueMultiple, segundoBloqueMultiple);
    }

    /**
     * Método que devuelve el último bloque del blockchain físico.
     *
     * @return Último bloque del blockchain físico.
     */
    public BloqueMultiple obtenerUltimoBloque() {
        return bloques.get(bloques.size() - 1);
    }

    /**
     * Método que devuelve el último bloque de un blockchain lógico.
     *
     * @param tipo Identificador del blockchain lógico.
     * @param i    Posición desde la que se comienza a buscar.
     * @return Último bloque del blockchain lógico.
     */
    public BloqueMultiple buscarBloquePrevioLogico(Tipo tipo, int i) {
        if (i < 0) {
            return null;
        }
        BloqueMultiple bloqueMultiple = this.bloques.get(i);
        if (tipo.equals(bloqueMultiple.getTipo()))
            return bloqueMultiple;
        else {
            return buscarBloquePrevioLogico(tipo, --i);
        }
    }

    /**
     * Método que agrega un bloque al blockchain físico y
     * guarda la diferencia de tiempo de creación entre el bloque actual y el
     * anterior.
     *
     * @param bloqueMultiple Bloque que se va a añadir.
     */
    public void agregarBloque(BloqueMultiple bloqueMultiple) {
        Tipo tipo = bloqueMultiple.getTipo();
        BloqueMultiple bloqueMultiplePrevioLogico = buscarBloquePrevioLogico(tipo, this.bloques.size() - 1);

        bloques.add(bloqueMultiple);

        tiempoEntreCreacionDeBloques.get(tipo).add((double) (bloqueMultiple.getHeader().getMarcaDeTiempoDeCreacion() - bloqueMultiplePrevioLogico.getHeader().getMarcaDeTiempoDeCreacion()) / 1000);

        if (tipo.equals(Tipo.LOGICO1)) {
            tiempoEntreCreacionDeBloques.get(Tipo.LOGICO2).add((double) 0);
        } else {
            tiempoEntreCreacionDeBloques.get(Tipo.LOGICO1).add((double) 0);
        }
    }

    /**
     * Método que devuelve el tamaño del blockchain físico.
     *
     * @return Tamaño del blockchain físico.
     */
    public int obtenerCantidadDeBloques() {
        return bloques.size();
    }
    
}
