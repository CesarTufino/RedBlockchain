package pos.blockchainTradicional.nodo;

public class Temporizador extends Thread {

    private Red red = null;
    private Nodo miNodo = null;
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_RESET = "\u001B[0m";

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
                Validador hiloValidador = new Validador(miNodo.getRed(), miNodo);
                hiloValidador.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
