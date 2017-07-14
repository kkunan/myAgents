package agents.myAgents.NN;

/**
 * Created by Mike on 10.07.2017.
 */

import agents.myAgents.NN.ConsideredFeatures.SpriteTypePresenceFeature;
import org.neuroph.core.NeuralNetwork;
import serialization.Observation;
import serialization.SerializableStateObservation;
import serialization.Types;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class has been built with a simple design in mind.
 * It is to be used to store player agent information,
 * to be later used by the client to send and receive information
 * to and from the server.
 */
public class Agent extends utils.AbstractPlayer {


    /**
     * Public method to be called at the start of the communication. No game has been initialized yet.
     * Perform one-time setup here.
     */
    public Agent(){}

    boolean veryFirstTime = true;
    List<boolean[][]> previousScreen;
    double previousReward;
    List<SpriteTypePresenceFeature> spriteTypePresenceFeatures;

    NeuralNetwork screenPredictor;
    NeuralNetwork rewardPredictor;

    int counterLevelFirstTime = 0;
    /**
     * Public method to be called at the start of every level of a game.
     * Perform any level-entry initialization here.
     * @param sso Phase Observation of the current game.
     * @param elapsedTimer Timer (1s)
     */
    @Override
    public void init(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer){
        if(veryFirstTime && counterLevelFirstTime == 0)
        {
            // TODO: 05/07/2017 do whatever initialize the things
            
            spriteTypePresenceFeatures = new ArrayList<>();

            // TODO: 10/07/2017 they don't give us the num of sprites, fine.... 

        }

    }

    /**
     * Method used to determine the next move to be performed by the agent.
     * This method can be used to identify the current state of the game and all
     * relevant details, then to choose the desired course of action.
     *
     * @param sso Observation of the current state of the game to be used in deciding
     *            the next action to be taken by the agent.
     * @param elapsedTimer Timer (40ms)
     * @return The action to be performed by the agent.
     */
   
    @Override
    public Types.ACTIONS act(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer){
        // TODO: 10/07/2017 everything basically
        List<boolean[][]> currentScreen = createFeature(sso);

        //2nd time onwards
        if(previousScreen != null) {
            double currentReward = sso.gameScore;
            SpriteTypePresenceFeature spriteTypePresenceFeature = new SpriteTypePresenceFeature(previousScreen, currentScreen,sso.avatarLastAction, previousReward, currentReward);
            spriteTypePresenceFeatures.add(spriteTypePresenceFeature);
        }

        previousScreen = new ArrayList<>();
        previousScreen.addAll(currentScreen);
        previousReward = sso.gameScore;

        return randomAct(sso.availableActions);

//        for(double d: flat)
//            System.out.print(d+" ");
//        System.out.println();


    }

    private Types.ACTIONS randomAct(ArrayList<Types.ACTIONS> actions)
    {
        int index = new Random().nextInt(actions.size());
        return actions.get(index);
    }

    public double[] flattenFeature(List<boolean[][]> features)
    {
        if(features == null)
            return null;

        if(features.size()>0)
        {
            try {
                double[] flatFeatures = new double[features.size() * features.get(0).length * features.get(0)[0].length];
                int counter = 0;

                for(boolean[][] list : features)
                {
                    for(boolean[] row : list)
                    {
                        for(boolean value: row)
                        {
                            flatFeatures[counter++] = value?1:0;
                        }
                    }
                }

                return flatFeatures;
            }catch (Exception e){
                System.out.println("clearly there's something wrong here in flattenFeature");
            }
        }
        return null;
    }

    public List<boolean[][]> createFeature(SerializableStateObservation sso)
    {
        Observation[][][] observationGrid = sso.observationGrid;
        if(observationGrid == null)
            return null;
        int typeCounter = 0;
        List<boolean[][]> featureList = new ArrayList<>();

        for(int i=0;i<observationGrid.length;i++)
        {
            for(int j=0;j<observationGrid[0].length;j++)
            {
                try {
                    int spTypeAtThisPos = observationGrid[i][j][0].itype;
                        while(spTypeAtThisPos >= typeCounter) {
                            boolean[][] newType = new boolean[observationGrid.length][observationGrid[i].length];
                            typeCounter++;
                            featureList.add(newType);

                            if(spTypeAtThisPos==typeCounter)
                                newType[i][j] = true;
                        }
                        boolean[][] storedType = featureList.get(spTypeAtThisPos);
                        storedType[i][j] = true;
                }catch (Exception e)
                {
                }
            }
        }
        return featureList;
    }

    public void printFeature(List<boolean[][]> feature)
    {
        int i=0;
        for(boolean[][] each : feature)
        {
            System.out.println("type "+i++);
            printList(each);
            System.out.println("=========================================");
        }
    }

    public void printList(boolean[][] list)
    {
        for(int i=0;i<list[0].length;i++)
        {
            for(int j=0;j<list.length;j++)
            {
                System.out.print((list[j][i]?'x':' ')+"\t|");
            }
            System.out.println();
        }
    }
    /**
     * Method used to perform actions in case of a game end.
     * This is the last thing called when a level is played (the game is already in a terminal state).
     * Use this for actions such as teardown or process data.
     *
     * @param sso The current state observation of the game.
     * @param elapsedTimer Timer (up to CompetitionParameters.TOTAL_LEARNING_TIME
     * or CompetitionParameters.EXTRA_LEARNING_TIME if current global time is beyond TOTAL_LEARNING_TIME)
     * @return The next level of the current game to be played.
     * The level is bound in the range of [0,2]. If the input is any different, then the level
     * chosen will be ignored, and the game will play a random one instead.
     */
    @Override
    public int result(SerializableStateObservation sso, ElapsedCpuTimer elapsedTimer){

        if(counterLevelFirstTime == 2 && veryFirstTime) {
            // TODO: 10/07/2017 now we able to create the NN
//            for(SpriteTypePresenceFeature trainingSet : spriteTypePresenceFeatures){
//                System.out.println(trainingSet.previous.size()+" "+trainingSet.result.size());
//            }

            int countSpriteType = spriteTypePresenceFeatures.get(0).previous.size();


            // create new perceptron network
//            NeuralNetwork neuralNetwork = new Perceptron(2, 1);
//// create training set
//            DataSet trainingSet =
//                    new DataSet(2, 1);
//// add training data to training set (logical OR function)
//            trainingSet. addRow (new DataSetRow(new double[]{0, 0},
//                    new double[]{0}));
//            trainingSet. addRow (new DataSetRow (new double[]{0, 1},
//                    new double[]{1}));
//            trainingSet. addRow (new DataSetRow (new double[]{1, 0},
//                    new double[]{1}));
//            trainingSet. addRow (new DataSetRow (new double[]{1, 1},
//                    new double[]{1}));
//// learn the training set
//            neuralNetwork.learn(trainingSet);
//// save the trained network into file
////            neuralNetwork.save(“or_perceptron.nnet”);
//
//            // set network input
//            neuralNetwork.setInput(1, 1);
//// calculate network
//            neuralNetwork.calculate();
//// get network output
//            double[] networkOutput = neuralNetwork.getOutput();
//
//            for(double d : networkOutput)
//                System.out.println(d);
//            veryFirstTime = false;


            veryFirstTime = false;
        }

//        System.out.println(spriteTypePresenceFeatures.size()+" "+sso.gameTick);
        Integer level;
        if(veryFirstTime)
        {
            level = counterLevelFirstTime++;
        }
        else {
            Random r = new Random();
            level = r.nextInt(3);
        }
        previousScreen = null;
//        System.out.println(level);
        return level;
    }

}
