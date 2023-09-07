package pos.blockchainTradicional.nodo;

public class Temporizador extends Thread {

    private Nodo miNodo;

    public Temporizador(Nodo miNodo) {
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
