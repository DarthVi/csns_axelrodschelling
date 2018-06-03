import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ASObserver implements Control
{

    //CONSTANTS
    private static final String PAR_ITERINTERVAL = "logInterval";
    private static final String PAR_PROT = "protocol";

    private static final String NODEFILENAME = "nodes";
    private static final String EDGEFILENAME = "edges";
    private static final String[] EDGE_HEADERS = {"source", "target"};

    //FIELDS
    private int iterations;
    private int interval;
    private int pid;

    public ASObserver(String prefix)
    {
        interval = Configuration.getInt(prefix + "." + PAR_ITERINTERVAL);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        iterations = 0;
    }

    @Override
    public boolean execute()
    {
        //serialize a snapshot of the network current state
        iterations++;
        if(iterations % interval == 0)
            serializeNetworkSnapshot(iterations);

        //if no imitation or movements occur, stop the simulation
        return (!Interaction.getCulturalChanges() && !Interaction.getMoveActivity());
    }

    /**
     * Saves a .csv snapshot of the network compatible with gephi (nodes.csv and edges.csv).
     * edges.csv contains merely source id and target id, while nodes.csv contains the id and the important attributes
     * for each site.
     *
     * @param iterAppend    used as an index to append to the filenames
     */
    private void serializeNetworkSnapshot(int iterAppend)
    {
        String[] NODE_HEADERS = null;

        for(int i=0; i < Network.size(); i++)
        {
            Site site = (Site) Network.get(i);
            Linkable linkable = (Linkable) site.getProtocol(FastConfig.getLinkable(pid));

            //collect site data
            long siteID = site.getID();
            boolean isEmpty = site.isEmpty();
            int[] culturalCode = site.getWholeSigma();

            //collecte neighbors IDs
            List<Long> neighborsID = new ArrayList<Long>();

            for(int j = 0; j < linkable.degree(); j++)
            {
                neighborsID.add(linkable.getNeighbor(j).getID());
            }

            //node headers depends on cultural code size, cannot be defined as a static final field
            NODE_HEADERS = new String[2 + culturalCode.length];
            NODE_HEADERS[0] = "id";
            NODE_HEADERS[1] = "empty";

            for(int j = 2; j < NODE_HEADERS.length; j++)
            {
                NODE_HEADERS[j] = "code" + (j-2);
            }

            try
            {
                FileWriter outNodes = new FileWriter("./snapshots/" + NODEFILENAME + iterAppend + ".csv");
                FileWriter outEdges = new FileWriter("./snapshots/" + EDGEFILENAME + iterAppend + ".csv");

                CSVPrinter nodePrinter = new CSVPrinter(outNodes, CSVFormat.DEFAULT.withHeader(NODE_HEADERS));

                nodePrinter.printRecord(siteID, isEmpty, Arrays.toString(culturalCode));

                CSVPrinter edgePrinter = new CSVPrinter(outEdges, CSVFormat.DEFAULT.withHeader(EDGE_HEADERS));

                for(long id : neighborsID)
                {
                    edgePrinter.printRecord(siteID, id);
                }

                outNodes.close();
                outEdges.close();

            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

    }
}
