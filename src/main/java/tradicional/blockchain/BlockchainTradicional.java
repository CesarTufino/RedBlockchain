package tradicional.blockchain;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase BlockchainTradicional
 */
public class BlockchainTradicional implements Serializable {

    private List<BloqueTradicional> bloques = new CopyOnWriteArrayList<>();
    private List<Double> tiempoEntreCreacionDeBloques = new CopyOnWriteArrayList<>();

    public BlockchainTradicional() {
        bloques.addAll(crearPrimerBloque());
    }

    public List<Double> getTiempoEntreCreacionDeBloques() {
        return tiempoEntreCreacionDeBloques;
    }

    /**
     * Método que crea el primer bloque del general.blockchain.
     *
     * @return Primer bloque.
     */
    public List<BloqueTradicional> crearPrimerBloque() {
        BloqueTradicional primerBloqueTradicional = new BloqueTradicional();
        return List.of(primerBloqueTradicional);
    }

    /**
     * Método que devuelve el último bloque del general.blockchain.
     *
     * @return Último bloque del general.blockchain.
     */
    public BloqueTradicional obtenerUltimoBloque() {
        return bloques.get(bloques.size() - 1);
    }

    /**
     * Método que agrega un bloque al general.blockchain físico y
     * guarda la diferencia de tiempo de creación entre el bloque actual y el
     * anterior.
     *
     * @param bloqueTradicional Bloque que se va a añadir.
     */
    public void agregarBloque(BloqueTradicional bloqueTradicional) {
        BloqueTradicional prevBlock = obtenerUltimoBloque();
        tiempoEntreCreacionDeBloques.add((double) (bloqueTradicional.getHeader().getMarcaDeTiempoDeCreacion() - prevBlock.getHeader().getMarcaDeTiempoDeCreacion()) / 1000);
        bloques.add(bloqueTradicional);
    }

    public int obtenerCantidadDeBloques(){
        return bloques.size();
    }
}
