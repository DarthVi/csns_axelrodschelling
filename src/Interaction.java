import peersim.cdsim.CDProtocol;
import peersim.core.Node;

/**
 * This protocol has only two static fields to log whether after an {@link AxelrodSchelling} cycle
 * any activity (cultural changes or moving Sites) happened. It will be useful to the
 * controller responsible for ending the simulation
 */
public class Interaction implements CDProtocol
{
    //FIELDS
    private static boolean moveActivity = false;
    private static boolean culturalChanges = false;

    public Interaction(String prefix)
    {
        //do nothing
    }

    public Object clone()
    {
        Interaction interaction = null;

        try
        {
            interaction = (Interaction) super.clone();
        }catch (CloneNotSupportedException e) {} //never happens

        return interaction;
    }

    @Override
    public void nextCycle(Node node, int i)
    {
        Interaction.moveActivity = false;
        Interaction.culturalChanges = false;
    }

    public static void setMoveActivity(boolean value)
    {
        Interaction.moveActivity = value;
    }

    public static boolean getMoveActivity()
    {
        return Interaction.moveActivity;
    }

    public static void setCulturalChanges(boolean value)
    {
        Interaction.culturalChanges = value;
    }

    public static boolean getCulturalChanges()
    {
        return Interaction.culturalChanges;
    }
}
