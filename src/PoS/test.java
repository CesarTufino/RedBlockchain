package PoS;

import Blockchain.Block;
import Blockchain.Blockchain;
import MessageTypes.Transaction;
import Network.Network;
import Network.ValidatorNode;
import Network.ValidatorParaL;
import Network.Validator;
import Network.ValidatorMonoType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class test {

    public static void main(String[] args) {
        ///!\ DO NOT RUN MORE THAN ONE SIMULATION PER RUN OF THE CODE /!\
        //simulationPoS(1, 201,0.95); // Simulation T-Probabilities with 200 blocks (Last parameter = proba
        // IF PROBA IS 0 THAN ITS T-PROBA IF ITS ANYTHING MORE THAN 0 IT'S X-Proba

        simulationPoS(2, 201,0); // Simulation Parallel with 200 blocks
        //simulationPoS(3, 201,0); // Simulation Traditional with 200 blocks
        //simuBlk(10000); // Simulation Search Time with maximum 10 000 blocks
    }

    public static void simulationPoS(int type, int Block_NB, double proba) {

        String TYPE1 = "Bit1";
        String TYPE2 = "Bit2";

        Network net = new Network(TYPE1, TYPE2);
        net.mode = "POS";

        FullNode s1 = new FullNode("Spain Server", net);
        FullNode s2 = new FullNode("Alex Server", net);
        FullNode s3 = new FullNode("Paris Server", net);
        FullNode s4 = new FullNode("England Server", net);

        // Validator Wallet
        LightNode wvn1 = new LightNode("Validator wallet1", net);
        LightNode wvn2 = new LightNode("Validator wallet2", net);
        LightNode wvn3 = new LightNode("Validator wallet3", net);
        LightNode wvn4 = new LightNode("Validator wallet4", net);
        LightNode wvn5 = new LightNode("Validator wallet5", net);
        LightNode wvn6 = new LightNode("Validator wallet6", net);
        // Validator
        ValidatorNode vn1 = new ValidatorNode("Validator 1", net, wvn1);
        ValidatorNode vn2 = new ValidatorNode("Validator 2", net, wvn2);
        ValidatorNode vn3 = new ValidatorNode("Validator 3", net, wvn3);
        ValidatorNode vn4 = new ValidatorNode("Validator 4", net, wvn4);
        ValidatorNode vn5 = new ValidatorNode("Validator 5", net, wvn5);
        ValidatorNode vn6 = new ValidatorNode("Validator 6", net, wvn6);

        // Normal lightNode
        LightNode ln1 = new LightNode("Jack", net);
        LightNode ln2 = new LightNode("Alex", net);
        LightNode ln3 = new LightNode("Chen", net);
        LightNode ln4 = new LightNode("Kyle", net);
        LightNode ln5 = new LightNode("Jennifer", net);
        LightNode ln6 = new LightNode("Lise", net);

        ln1.stake(20, TYPE1);
        ln2.stake(25, TYPE1);
        ln3.stake(20, TYPE1);
        ln4.stake(50, TYPE1);
        ln5.stake(70, TYPE1);
        ln6.stake(30, TYPE1);

        ln1.stake(10, TYPE2);
        ln2.stake(25, TYPE2);
        ln3.stake(40, TYPE2);
        ln4.stake(5, TYPE2);
        ln5.stake(30, TYPE2);
        ln6.stake(60, TYPE2);

        vn1.addInvestorType(ln1.getNodeAddress(), ln1.getStakeAmount1(), TYPE1);
        vn2.addInvestorType(ln2.getNodeAddress(), ln2.getStakeAmount1(), TYPE1);
        vn3.addInvestorType(ln3.getNodeAddress(), ln3.getStakeAmount1(), TYPE1);
        vn4.addInvestorType(ln4.getNodeAddress(), ln4.getStakeAmount1(), TYPE1);
        vn5.addInvestorType(ln5.getNodeAddress(), ln5.getStakeAmount1(), TYPE1);
        vn6.addInvestorType(ln6.getNodeAddress(), ln6.getStakeAmount1(), TYPE1);

        vn1.addInvestorType(ln1.getNodeAddress(), ln1.getStakeAmount1(), TYPE2);
        vn2.addInvestorType(ln2.getNodeAddress(), ln2.getStakeAmount1(), TYPE2);
        vn3.addInvestorType(ln3.getNodeAddress(), ln3.getStakeAmount1(), TYPE2);
        vn4.addInvestorType(ln4.getNodeAddress(), ln4.getStakeAmount1(), TYPE2);
        vn5.addInvestorType(ln5.getNodeAddress(), ln5.getStakeAmount1(), TYPE2);
        vn6.addInvestorType(ln6.getNodeAddress(), ln6.getStakeAmount1(), TYPE2);

        Object validatorExec;
        if(type==1) {
            validatorExec = new Validator(net,proba);
            new Thread((Validator)validatorExec).start();
        }
        if(type==2) {
            validatorExec = new ValidatorParaL(net);
            new Thread((ValidatorParaL)validatorExec).start();
        }
        if(type==3) {
            validatorExec = new ValidatorMonoType(net);
            new Thread((ValidatorMonoType)validatorExec).start();
        }



        for (int i = 0; i < 10000; i++) {
            int a = (int)(((Math.random())*2)+1);
            double p_t1; //p_t1?
            if (a == 1)
                p_t1 = 0.85;
            else {
                p_t1 = 0.30;
            }

            for (int j = 0; j < 100*(Math.random()*3)+1; j++) {
                double b = Math.random() ;
                //System.out.println(a);
                if(b < p_t1)
                    ln1.sendMoneyTo(1.23, ln3.getNodeAddress(), TYPE1);
                else
                    ln3.sendMoneyTo(3.47, ln3.getNodeAddress(), TYPE2);

                try {
                    Thread.sleep(100);

                }catch(InterruptedException e){
                    System.out.println(e);
                }
            }
            net.printStats();
            //System.out.println("------------Nb of block 1 : " + Network.NB_OF_BLOCK_OF_TYPE1_CREATED+"\n"+"--------------Nb of block 2 : "+ Network.NB_OF_BLOCK_OF_TYPE2_CREATED);

            if (Network.NB_OF_BLOCK_OF_TYPE1_CREATED.get(Network.NB_OF_BLOCK_OF_TYPE1_CREATED.size()-1) + Network.NB_OF_BLOCK_OF_TYPE2_CREATED.get(Network.NB_OF_BLOCK_OF_TYPE2_CREATED.size()-1) > Block_NB){
                net.printStats();
                System.exit(1);
                return;
            }
        }
    }

    public static void simuBlk(int MAX_AMOUNT_BLOCK){
        List<Double> ST = new ArrayList<>();
        List<Integer> NB_BLOCK = new ArrayList<>();
        Network test = new Network("1","2");
        for (int i = 0; i < MAX_AMOUNT_BLOCK; i++) {
            Blockchain b = new Blockchain(test);
            for (int w = 0; w < i;w++) {
                List<Transaction> LT = new ArrayList<Transaction>();
                Block bb = new Block(b.getLatestBlock(), b.searchPrevBlockByID("1", b.getSize()-1), LT,"1");
                b.addBlock(bb);
            }
            long start = System.nanoTime();
            b.searchPrevBlockByID("2", b.getSize()-1);
            long end = System.nanoTime();
            ST.add((double)end-start);
            NB_BLOCK.add(i);
            System.out.println(ST);
            System.out.println(NB_BLOCK);
        }
    }
}
