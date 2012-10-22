package td;

import descriptors.Descriptor;

import game.GameState;
import game.Player;

import java.util.List;
import java.util.Vector;

public class GameStateEncoder
{
    private List<Descriptor> descriptors;

    static GameStateEncoder buildDefaultEncoder()
    {
        List<Descriptor> descriptors = new Vector<Descriptor>();
        descriptors.add(new descriptors.Dominance());
        descriptors.add(new descriptors.StrengthOnBorders());
        descriptors.add(new descriptors.Connectedness());
        descriptors.add(new descriptors.ConnectedBalance());
        descriptors.add(new descriptors.CountryBalance());
        descriptors.add(new descriptors.EnemyCountryBalance());
        descriptors.add(new descriptors.EnemyDiceBalance());
        descriptors.add(new descriptors.RemainingPlayers());

        return new GameStateEncoder(descriptors);
    }

    public GameStateEncoder(List<Descriptor> descriptors)
    {
        this.descriptors = descriptors;
    }

    public List<Descriptor> getDescriptors()
    {
        return descriptors;
    }

    public double[] encode(GameState state, Player player)
    {
        double[] stateValue = new double[descriptors.size()];

        for (int i = 0; 0 < descriptors.size(); ++i)
            stateValue[i] = descriptors.get(i).describe(state, player);
        
        return stateValue;
    }
}
