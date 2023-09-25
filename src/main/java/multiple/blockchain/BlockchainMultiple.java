package multiple.blockchain;

import general.constantes.Tipo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La clase BlockchainMultiple representa la cadena de bloques de un blockchain (físico) compuesto por dos cadenas de
 * bloques (lógico).
 */
public class BlockchainMultiple implements Serializable {
    private List<BloqueMultiple> bloques = new CopyOnWriteArrayList<>();
    private HashMap<Tipo,List<Double>> tiempoEntreCreacionDeBloques = new HashMap<>();

    public BlockchainMultiple() {
        tiempoEntreCreacionDeBloques.put(Tipo.LOGICO1, new CopyOnWriteArrayList<>());
        tiempoEntreCreacionDeBloques.put(Tipo.LOGICO2, new CopyOnWriteArrayList<>());
        bloques.addAll(crearPrimerBloque());
    }

    /**
     * Obtiene la tabla de mapeo de los tiempos entre creación de bloques de cada tipo de blockchain lógico.
     * @return tabla de mapeo de los tiempos entre creación de bloques.
     */
    public HashMap<Tipo, List<Double>> getTiempoEntreCreacionDeBloques() {
        return tiempoEntreCreacionDeBloques;
    }

    /**
     * Crea el primer bloque de cada blockchain lógico.
     * @return lista de los dos primeros bloques del blockchain.
     */
    public List<BloqueMultiple> crearPrimerBloque() {
        BloqueMultiple primerBloqueMultiple = new BloqueMultiple(Tipo.LOGICO1);
        BloqueMultiple segundoBloqueMultiple = new BloqueMultiple(primerBloqueMultiple, Tipo.LOGICO2);
        return Arrays.asList(primerBloqueMultiple, segundoBloqueMultiple);
    }

    /**
     * Obtiene el último bloque del blockchain físico.
     * @return último bloque del blockchain físico.
     */
    public BloqueMultiple obtenerUltimoBloque() {
        return bloques.get(bloques.size() - 1);
    }

    /**
     * Obtiene el último bloque de un blockchain lógico.
     * @param tipo identificador del blockchain lógico.
     * @param i    posición desde la que se comienza a buscar.
     * @return último bloque de un blockchain lógico.
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
     * Agrega un bloque al blockchain físico y guarda la diferencia de tiempo de creación entre el bloque actual y el
     * anterior.
     * @param bloqueMultiple Bloque que se va a agregar.
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
     * Obtiene el tamaño del blockchain físico.
     * @return tamaño del blockchain físico.
     */
    public int obtenerCantidadDeBloques() {
        return bloques.size();
    }
    
}
