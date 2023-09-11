package gatewayVersion.blockchainMultipleAletorio.blockchain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Blockchain implements Serializable {

    private List<Bloque> bloques = new CopyOnWriteArrayList<>();
    /**
     * Diferencia de tiempo de creación entre los bloques del primer blockchain
     * lógico.
     */
    public List<Double> WTT1 = new CopyOnWriteArrayList<>();
    /**
     * Diferencia de tiempo de creación entre los bloques del segundo blockchain
     * lógico.
     */
    public List<Double> WTT2 = new CopyOnWriteArrayList<>();


    public Blockchain() {
        bloques.addAll(crearPrimerBloque());
    }

    public List<Double> getWTT1() {
        return WTT1;
    }
    public List<Double> getWTT2() {
        return WTT2;
    }

    /**
     * Método que crea el primer bloque de cada blockchain lógico.
     *
     * @return Primeros bloques.
     */
    public List<Bloque> crearPrimerBloque() {
        Bloque primerBloque = new Bloque("Type1");
        Bloque segundoBloque = new Bloque(primerBloque, "Type2");
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
    public Bloque buscarBloquePrevioLogico(String tipo, int i) {
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
        String tipo = bloque.getTipo();
        Bloque bloquePrevioLogico = buscarBloquePrevioLogico(tipo, this.bloques.size() - 1);
        if (tipo.equals("Type1")) {
            WTT1.add((double) (bloque.getHeader().getMarcaDeTiempo() - bloquePrevioLogico.getHeader().getMarcaDeTiempo()) / 1000);
            WTT2.add((double) 0);
        } else {
            WTT1.add((double) 0);
            WTT2.add((double) (bloque.getHeader().getMarcaDeTiempo() - bloquePrevioLogico.getHeader().getMarcaDeTiempo()) / 1000);
        }
        bloques.add(bloque);
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
