package tradicional.blockchain;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * La clase BlockchainTradicional representa la cadena de bloques de un blockchain.
 */
public class BlockchainTradicional implements Serializable {
    private List<BloqueTradicional> bloques = new CopyOnWriteArrayList<>();
    private List<Double> tiempoEntreCreacionDeBloques = new CopyOnWriteArrayList<>();

    public BlockchainTradicional() {
        bloques.addAll(crearPrimerBloque());
    }

    /**
     * Obtiene la lista de los tiempos entre creación de bloques.
     * @return lista de los tiempos entre creación de bloques.
     */
    public List<Double> getTiempoEntreCreacionDeBloques() {
        return tiempoEntreCreacionDeBloques;
    }

    /**
     * Crea el primer bloque del blockchain.
     * @return primer bloque del blockchain.
     */
    public List<BloqueTradicional> crearPrimerBloque() {
        BloqueTradicional primerBloqueTradicional = new BloqueTradicional();
        return List.of(primerBloqueTradicional);
    }

    /**
     * Obtiene el último bloque del blockchain.
     * @return último bloque del blockchain.
     */
    public BloqueTradicional obtenerUltimoBloque() {
        return bloques.get(bloques.size() - 1);
    }

    /**
     * Agrega un bloque al blockchain y guarda la diferencia de tiempo de creación entre el bloque actual y el
     * anterior.
     * @param bloqueTradicional Bloque que se va a agregar.
     */
    public void agregarBloque(BloqueTradicional bloqueTradicional) {
        BloqueTradicional prevBlock = obtenerUltimoBloque();
        tiempoEntreCreacionDeBloques.add((double) (bloqueTradicional.getHeader().getMarcaDeTiempoDeCreacion() -
                prevBlock.getHeader().getMarcaDeTiempoDeCreacion()) / 1000);
        bloques.add(bloqueTradicional);
    }

    /**
     * Obtiene el tamaño del blockchain.
     * @return tamaño del blockchain.
     */
    public int obtenerCantidadDeBloques(){
        return bloques.size();
    }

}
