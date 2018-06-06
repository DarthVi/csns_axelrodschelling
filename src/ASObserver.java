import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This observer checks if the simulation must be stopped (no moves or imitations occured) or not.
 * Moreover it saves the network evolution in a CSV format compatible with Gephi.
 * Two csv: one for the edges and one for nodes.
 *
 * The csv for nodes saves the network evolution following the interval attribute syntax of Gephi.
 * In the following line there's an example of one line with its header.
 *
 * id   empty   code1   code2   code3
 * 0    <[2,4, true); [4,6,false)>  <[2,4,0];[4,6,1]> ..and so on
 *
 * See <a href="https://gephi.org/users/supported-graph-formats/spreadsheet/">Gephi spreadsheet</a> for more informations.
 */
public class ASObserver implements Control
{

    //CONSTANTS
    private static final String PAR_ITERINTERVAL = "logInterval";
    private static final String PAR_PROT = "protocol";
    private static final String F_VALUE = "f_value";

    private static final String NODEFILENAME = "nodes";
    private static final String EDGEFILENAME = "edges";
    private static final String[] EDGE_HEADERS = {"source", "target"};

    //FIELDS
    private int iterations;
    private int interval;
    private int pid;
    private int culturalCodeSize;
    private Map<Long, List<Object>> dump;

    private CSVPrinter edgePrinter;
    private CSVPrinter nodePrinter;

    public ASObserver(String prefix)
    {
        interval = Configuration.getInt(prefix + "." + PAR_ITERINTERVAL);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        iterations = 0;
        culturalCodeSize = Configuration.getInt(prefix + "." + F_VALUE);
        dump = new HashMap<Long, List<Object>>();

        FileWriter outEdges = null;
        FileWriter outNodes = null;

        try
        {
            outEdges = new FileWriter("./snapshots/" + EDGEFILENAME  + ".csv");
            outNodes = new FileWriter("./snapshots/" + NODEFILENAME + ".csv");

            edgePrinter = new CSVPrinter(outEdges, CSVFormat.DEFAULT.withHeader(EDGE_HEADERS));
            String[] NODE_HEADERS;

            //node headers depends on cultural code size, cannot be defined as a static final field
            NODE_HEADERS = new String[2 + culturalCodeSize];
            NODE_HEADERS[0] = "Id";
            NODE_HEADERS[1] = "Empty";
            //NODE_HEADERS[2] = "Interval";

            for(int j = 2; j < NODE_HEADERS.length; j++)
            {
                NODE_HEADERS[j] = "code" + (j-2);
            }
            nodePrinter = new CSVPrinter(outNodes, CSVFormat.DEFAULT.withHeader(NODE_HEADERS).withQuote(null).withDelimiter('\t'));
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check if no moves or imitations occured. If so, stops the simulation.
     * Moreover a network state snapshot is saved at regular intervals, with the interval
     * defined in the configuration file.
     * @return      boolean, true if the simulation must be stopped
     */
    @Override
    public boolean execute()
    {
        //serialize the edges only one time (they do not change in this model), then serialize network snapshot
        if(iterations == 0)
        {
            serializeEdges();
        }

        //saves state at the beginning and at regular intervals
        iterations++;
        if(iterations % interval == 0 || iterations == 1)
        {
            saveNetworkSnapshot(iterations);
        }

        //if no imitation or movements occur, stop the simulation
        if((Interaction.getCulturalChanges() == false && Interaction.getMoveActivity() == false && iterations != 1) == true)
        {
            //always saves the state at the end of the simulation
            if(iterations % interval != 0)
                saveNetworkSnapshot(iterations);

            serializeNetworkSnapshot();
            closeCSVPrinters();
            return true;
        }
        else
            return false;
    }

    /**
     * Store the network's info in the hashmap. It iterates through the nodes and calls {@link #storeSiteInfo(Site, int)}
     *
     * @param iterAppend    used to set the appropriate time intervals for the dynamic graph, making it readable by gephi
     */
    private void saveNetworkSnapshot(int iterAppend)
    {

        for(int i=0; i < Network.size(); i++)
        {
            Site site = (Site) Network.get(i);

            storeSiteInfo(site, iterAppend);
        }

    }

    /**
     * Store the site's info in the hashmap. If the site has already been added, the values get updatet, otherwise
     * a new list of values gets created and initialized appropriately.
     * @param site
     * @param iterAppend
     */
    public void storeSiteInfo(Site site, int iterAppend)
    {
        //collect site data
        long siteID = site.getID();
        boolean isEmpty = site.isEmpty();
        int[] culturalCode = site.getWholeSigma();

        //if this Site has never been added, a new list must be created, else we simply update the values
        List<Object> retList = dump.get(siteID);
        if(retList == null)
        {
             List<Object> infolist = new ArrayList<Object>();
             infolist.add("<[" + iterAppend + ", " + (iterAppend + interval) + "," + Boolean.toString(isEmpty) + ")>");
             //infolist.add("<[" + iterAppend + "]>");

             for(int i = 0; i < culturalCodeSize; i++)
             {
                 infolist.add("<[" + iterAppend + ", " + (iterAppend + interval) + "," + culturalCode[i] + ")>");
             }

             dump.put(siteID, infolist);
        }
        else
        {
            retList.set(0, retList.get(0).toString().replaceAll(">$", "") + ";" + "[" + iterAppend + ", " + (iterAppend + interval) + "," + Boolean.toString(isEmpty) + ")>");
            //retList.set(1, retList.get(1).toString().replaceAll("\\]>$", "") + "," + iterAppend + "]>");

            for(int i = 0; i < culturalCodeSize; i++)
            {
                retList.set(1 + i, retList.get(1 + i).toString().replaceAll(">$", "") + ";" + "[" + iterAppend + ", " + (iterAppend + interval) + "," + culturalCode[i] + ")>");
            }
        }
    }

    /**
     * Simply close the {@link org.apache.commons.csv.CSVPrinter} used to store the network state in CSV format
     */
    public void closeCSVPrinters()
    {
        try
        {
            nodePrinter.close();
            edgePrinter.close();
        } catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Saves the edges of the network in edges.csv.
     * Syntax of the csv:
     * sourceID,targetID
     */
    public void serializeEdges()
    {
        for(int i=0; i < Network.size(); i++)
        {
            Site site = (Site) Network.get(i);
            Linkable linkable = (Linkable) site.getProtocol(FastConfig.getLinkable(pid));

            //collect site data
            long siteID = site.getID();

            //collecte neighbors IDs
            List<Long> neighborsID = new ArrayList<Long>();

            for(int j = 0; j < linkable.degree(); j++)
            {
                neighborsID.add(linkable.getNeighbor(j).getID());
            }

            for(long id : neighborsID)
            {
                try
                {
                    edgePrinter.printRecord(siteID, id);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Saves the network's sites attributes in nodes.csv
     */
    public void serializeNetworkSnapshot()
    {
        for(Map.Entry<Long, List<Object>> entry : dump.entrySet())
        {
            try
            {
                String infostring = entry.getValue().toString().replaceAll("^\\[|\\]$", "");
                infostring = infostring.replaceAll(">, ", ">\t");
                nodePrinter.printRecord(entry.getKey(), infostring);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
