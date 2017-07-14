package agents.myAgents.NN.ConsideredFeatures;

import serialization.Types;

import java.util.List;

/**
 * Created by SteepMike on 10/07/2017.
 */
public class SpriteTypePresenceFeature {

    public List<boolean[][]> previous;
    public List<boolean[][]> result;
    public Types.ACTIONS action;
    public double previous_reward;
    public double result_reward;

    public SpriteTypePresenceFeature(List<boolean[][]> previous, List<boolean[][]> result,
                                     Types.ACTIONS action,
                                     double previous_reward,
                                     double result_reward)
    {
        this.previous = previous;
        this.result = result;
        this.action = action;
        this.previous_reward = previous_reward;
        this.result_reward = result_reward;
    }

}
