package gatewayVersion.blockchainMultipleAletorio.nodo;

public class Temporizador extends Thread {

    private Gateway gateway;

    public Temporizador(Gateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void run() {
        try {
            while (true) {
                long tiempoParaIniciar = 10000 - (System.currentTimeMillis() % 10000);
                Thread.sleep(tiempoParaIniciar);
                Seleccionador hiloSeleccionador = new Seleccionador(gateway);
                hiloSeleccionador.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
