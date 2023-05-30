package Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Clase ValidatorMonoType.
 * 
 */
public class ValidatorMonoType implements Runnable {
    
    /**
     *
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
     * ValidatorNode asociado.
     */
    private ValidatorNode validator = null;

    /**
     * Constructor ValidatorMonoType.
     * 
     * @param network Red a la que pertenece.
     */
    public ValidatorMonoType(Network network) {
        this.network = network;
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
                    stakeAmount = ((ValidatorNode) node).getStakeAmount1(); 
                } else {
                    stakeAmount = ((ValidatorNode) node).getStakeAmount2(); 
                }
                double stakeTime = System.currentTimeMillis() - ((ValidatorNode) node).getStakeTime(); // Get LightNode's stakeTime (How long the node have been Staking)
                mapProba.put((ValidatorNode) node, stakeAmount * (stakeTime));
            }
        }
        double sum = mapProba.values().stream().mapToDouble(v -> v).sum();
        int number_of_slots = 0;
        for (Node node : listNode) {
            if (node instanceof ValidatorNode) {
                number_of_slots += (mapProba.get(node) / sum) * 10;
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
     * Siempre se utiliza el primer blockchain lógico.
     */
    public void validate() {
        String currentBlockType = "";
        boolean interrupt = false;
        while (!interrupt) {
            lock.lock();
            try {
                if (validator != null) {
                    validator.forgeBlock(currentBlockType);
                    validator = null;

                }
                long start = System.currentTimeMillis();
                while (true) {
                    long end = System.currentTimeMillis();
                    if (end - start > 10000) {
                        break;
                    }
                }
                currentBlockType = network.TYPE1;
                chooseValidator(network.TYPE1);
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
