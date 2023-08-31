package gatewayVersion.blockchainTradicional.blockchain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Blockchain implements Serializable {

    private List<Bloque> bloques = new CopyOnWriteArrayList<>();
    /**
     * Diferencia de tiempo de creación entre los bloques
     */
    public List<Double> WTT1 = new CopyOnWriteArrayList<>();

    public Blockchain() {
        bloques.addAll(crearPrimerBloque());
    }

    public List<Double> getWTT1() {
        return WTT1;
    }

    /**
     * Método que crea el primer bloque del blockchain.
     *
     * @return Primer bloque.
     */
    public List<Bloque> crearPrimerBloque() {
        Bloque primerBloque = new Bloque();
        return Arrays.asList(primerBloque);
    }

    /**
     * Método que devuelve el último bloque del blockchain.
     *
     * @return Último bloque del blockchain.
     */
    public Bloque obtenerUltimoBloque() {
        return bloques.get(bloques.size() - 1);
    }

    /**
     * Método que agrega un bloque al blockchain físico y
     * guarda la diferencia de tiempo de creación entre el bloque actual y el
     * anterior.
     *
     * @param bloque Bloque que se va a añadir.
     */
    public void agregarBloque(Bloque bloque) {
        Bloque prevBlock = obtenerUltimoBloque();
        WTT1.add((double) (bloque.getHeader().getMarcaDeTiempo() - prevBlock.getHeader().getMarcaDeTiempo()) / 1000);
        bloques.add(bloque);
    }
    
}
