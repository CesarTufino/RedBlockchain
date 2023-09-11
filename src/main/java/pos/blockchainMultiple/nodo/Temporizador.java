package pos.blockchainMultiple.nodo;

public class Temporizador extends Thread {

    private Red red = null;
    private Nodo miNodo = null;
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_RESET = "\u001B[0m";
    private final String type1 = "Type1";
    private final String type2 = "Type2";

    public Temporizador(Red infoRed, Nodo miNodo) {
        this.red = infoRed;
        this.miNodo = miNodo;
    }

    @Override
    public void run() {
        try {
            while (true) {
                long tiempoParaIniciar = 10000 - (System.currentTimeMillis() % 10000);
                Thread.sleep(tiempoParaIniciar);
                Seleccionador hiloSeleccionador = new Seleccionador(miNodo.getRed(), miNodo);
                hiloSeleccionador.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
