package Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Clase Validator.
 */
public class Validator implements Runnable {

    /**
     * Probabilidad de escoger un blockchain lógico y otro.
     */
    double probaV = 0;
    /**
     * Variable sin uso.
     */
    private final static int SLOTS_MAX = 10;
    /**
     * concurrent.locks.Lock, para implementar la concurrencia.
     */
    private final Lock lock = new ReentrantLock();
    /**
     * Red a la que pertenece.
     */
    private final Network network;
    /**
     * Validador heredado de un nodo full.
     */
    private ValidatorNode validator = null;
    /**
     * Variable sin uso.
     */
    private int incremBlock = 0;
    /**
     * Variable sin uso.
     */
    private int testIncrement = 0;

    /**
     * Constructor Validator.
     * 
     * @param network Red a la que pertenece.
     * @param proba Probabilidad de .
     */
    public Validator(Network network, double proba) {
        this.network = network;
        this.probaV = proba;
    }

    /**
     * Método que escoge un ValidatorNode al azar entre un grupo de candidatos para construir un nuevo bloque.
     * 
     * @param ID Identificador del blockchain lógico.
     */
    public void chooseValidator(String ID) {
        System.out.println("Choosing a validator for a block of type " + ID);
        List<Node> listNode = network.getNetwork(); // List of nodes in the network
        Map<ValidatorNode, Double> mapProba = new HashMap<>();
        for (Node node : listNode) { // For each node in the network
            if (node instanceof ValidatorNode) { // If found node is an ValidatorNode
                double stakeAmount;
                if (ID.equals(network.TYPE1)) {
                    stakeAmount = ((ValidatorNode) node).getStakeAmount1(); // Monto de apuesta del ValidatorNode 
                } else {
                    stakeAmount = ((ValidatorNode) node).getStakeAmount2();
                }
                double stakeTime = System.currentTimeMillis() - ((ValidatorNode) node).getStakeTime(); // StakeTime de ValidatorNode (cuánto tiempo ha estado apostando el nodo)
                mapProba.put((ValidatorNode) node, stakeAmount * (stakeTime));
            }
        }
        double sum = mapProba.values().stream().mapToDouble(v -> v).sum();
        int number_of_slots = 0;
        for (Node node : listNode) {
            if (node instanceof ValidatorNode) {
                number_of_slots += (mapProba.get(node) / sum) * 10; //10?
            }
        }
        System.out.println("Slots : " + number_of_slots);
        int node_slots;
        List<ValidatorNode> validatorNodesSlots = new ArrayList<>(number_of_slots); 
        for (int j = 0; j < number_of_slots; j++)
            validatorNodesSlots.add(null);
        for (Node node : listNode) {
            if (node instanceof ValidatorNode) {
                node_slots = (int) ((mapProba.get(node) / sum) * 10);
                int slot_index;
                for (int i = 0; i < node_slots; i++) {
                    do {
                        slot_index = (int) (Math.random() * number_of_slots);
                    }
                    while (validatorNodesSlots.get(slot_index) != null);
                    validatorNodesSlots.set(slot_index, (ValidatorNode) node);
                }
            }
        }
        System.out.print("[");
        for (ValidatorNode ln : validatorNodesSlots) {
            System.out.print(ln.name + " ");
        }
        System.out.print("]\n");

        if (sum == 0) // if anyone didn't deposit bitcoin as stake
            return;

        int chosen_node_index = (int) (Math.random() * number_of_slots);
        validator = validatorNodesSlots.get(chosen_node_index);
        System.out.println(validator.name + " is chosen");
    }

    /**
     * Método que manda al ValidatorNode a crear un nuevo bloque y escoge el siguiente ValidatorNode.
     */
    public void validate() {
        int currentIDChosen = 0;
        boolean interrupt = false;
        while (!interrupt) {
            lock.lock();
            try {
                if (validator != null) { // Se manda a crear el bloque
                    if (currentIDChosen == 1)
                        validator.forgeBlock(this.network.TYPE1);
                    if (currentIDChosen == 2)
                        validator.forgeBlock(this.network.TYPE2);
                    validator = null;

                }
                long start = System.currentTimeMillis();
                while (true) {
                    long end = System.currentTimeMillis();
                    if (end - start > 10000) {
                        break;
                    }
                }

                HashMap<String, Integer> nbTransParType = (HashMap<String, Integer>) network.getNbTransParType();
                int nbSum = (int) nbTransParType.values().stream().collect(Collectors.summarizingInt(Integer::intValue)).getSum(); //?
                double proba;
                if (probaV!=0){
                    proba = probaV; //?
                }else{
                    proba = (double) nbTransParType.get(network.TYPE1) / nbSum; // Porbabilidad basada en el número de transacciones de cada blockchain lógico.
                }
                network.T1.add(nbTransParType.get(network.TYPE1)); // Se actualizan las transacciones en la lista por blockchain lógico.
                network.T2.add(nbTransParType.get(network.TYPE2));
                System.out.println("Transactions : " +  "["+nbTransParType.get(network.TYPE1)+","+nbTransParType.get(network.TYPE2)+"]");
                if (proba > 0.95) proba = 0.95;
                if (proba < 0.05) proba = 0.05;
                currentIDChosen = (Math.random() < proba ? 1 : 2); // Proba is between 0.95 and 0.05
                network.PT1.add(proba*100);
                network.PT2.add(100-proba*100); // Parentesis?

                System.out.println("T1-Probability is  " + proba*100 + " %");
                System.out.println("T2-Probability is " + (100-proba*100) + " %");
                //MOD

                // Se escoge el nodo que va a crear el bloque.
                if (currentIDChosen == 1) {
                    network.ELECTED.add(1);
                    chooseValidator(network.TYPE1);
                } else {
                    network.ELECTED.add(2);
                    chooseValidator(network.TYPE2);
                }
            } catch (Exception e) {
                e.printStackTrace();
                interrupt = true;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Método para ejecutar la validate() en los hilos.
     */
    @Override
    public void run() {
        validate();
    }
}
