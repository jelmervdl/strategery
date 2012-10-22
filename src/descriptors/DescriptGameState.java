import java.util.*;
import java.io.File;
import java.io.IOException;
import descriptors.Descriptor;
import game.*;
import map.MapReader;

class DescriptGameState
{
    static public List<Descriptor> descriptorList()
    {
        List<Descriptor> descriptorList = new Vector<Descriptor>();
		descriptorList.add(new descriptors.Dominance());
		descriptorList.add(new descriptors.StrengthOnBorders());
		descriptorList.add(new descriptors.Connectedness());
		descriptorList.add(new descriptors.ConnectedBalance());
		descriptorList.add(new descriptors.CountryBalance());
		descriptorList.add(new descriptors.EnemyCountryBalance());
        
        return descriptorList;
    }

    public double[] assignValue(GameState state, Player player)
    {
        List<Descriptor> descriptorList = descriptorList();
        double[] stateValue = new double[descriptorList.size()];
        for(int i = 1; 1<= descriptorList.size();i++)
        {
            stateValue[i] = descriptorList.get(i).describe(state, player);
        }
        return stateValue;
    }
}
