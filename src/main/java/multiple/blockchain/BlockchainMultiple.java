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

    private List<Bloque> bloques = new CopyOnWriteArrayList<>();
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
    public List<Bloque> crearPrimerBloque() {
        Bloque primerBloque = new Bloque(Tipo.LOGICO1);
        Bloque segundoBloque = new Bloque(primerBloque, Tipo.LOGICO2);
        return Arrays.asList(primerBloque, segundoBloque);
    }

    /**
     * Método que devuelve el último bloque del blockchain físico.
     *
     * @return Último bloque del blockchain físico.
     */
    public Bloque obtenerUltimoBloque() {
        return bloques.get(bloques.size() - 1);
    }

    /**
     * Método que devuelve el último bloque de un blockchain lógico.
     *
     * @param tipo Identificador del blockchain lógico.
     * @param i    Posición desde la que se comienza a buscar.
     * @return Último bloque del blockchain lógico.
     */
    public Bloque buscarBloquePrevioLogico(Tipo tipo, int i) {
        if (i < 0) {
            return null;
        }
        Bloque bloque = this.bloques.get(i);
        if (tipo.equals(bloque.getTipo()))
            return bloque;
        else {
            return buscarBloquePrevioLogico(tipo, --i);
        }
    }

    /**
     * Método que agrega un bloque al blockchain físico y
     * guarda la diferencia de tiempo de creación entre el bloque actual y el
     * anterior.
     *
     * @param bloque Bloque que se va a añadir.
     */
    public void agregarBloque(Bloque bloque) {
        Tipo tipo = bloque.getTipo();
        Bloque bloquePrevioLogico = buscarBloquePrevioLogico(tipo, this.bloques.size() - 1);

        bloques.add(bloque);

        tiempoEntreCreacionDeBloques.get(tipo).add((double) (bloque.getHeader().getMarcaDeTiempoDeCreacion() - bloquePrevioLogico.getHeader().getMarcaDeTiempoDeCreacion()) / 1000);

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
