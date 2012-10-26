package td;

import descriptors.Descriptor;

import game.GameState;
import game.Player;

import java.util.List;
import java.util.Vector;

public class GameStateEncoder
{
    private List<Descriptor> descriptors;

    static public GameStateEncoder buildDefaultEncoder()
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
        descriptors.add(new descriptors.ConnectednessAverage());
        descriptors.add(new descriptors.DiceDominance());

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

        for (int i = 0; i < descriptors.size(); ++i)
        {
            stateValue[i] = descriptors.get(i).describe(state, player);

            if (stateValue[i] < -1.0 || stateValue[i] > 1.0)
                throw new RuntimeException("Descriptor " + descriptors.get(i) + " returns a value outside of the range <-1,1>.");
        }
        
        return stateValue;
    }
}
