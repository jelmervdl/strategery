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
        List<Descriptor> descriptorList = new Vector<Descriptor>();
		descriptorList.add(new descriptors.Dominance());
		descriptorList.add(new descriptors.StrengthOnBorders());
		descriptorList.add(new descriptors.Connectedness());
		descriptorList.add(new descriptors.ConnectedBalance());
		descriptorList.add(new descriptors.CountryBalance());
		descriptorList.add(new descriptors.EnemyCountryBalance());
        
        return new GameStateEncoder(descriptorList);
    }

    public GameStateEncoder(List<Descriptor> descriptors)
    {
        this.descriptors = descriptors;
    }

    public double[] encode(GameState state, Player player)
    {
        double[] stateValue = new double[descriptors.size()];

        for (int i = 0; 0 < descriptors.size(); ++i)
            stateValue[i] = descriptors.get(i).describe(state, player);
        
        return stateValue;
    }
}
