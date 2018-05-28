Components:
    -Site: node extending peersim.core.GeneralNode and holding the cultural code vector sigma and the inhabited boolean
    -Linkable protocol for the topology
    -Initializer for the empty probability e
    -Initializer for the legnth F of cultural trait vector and the q possible value for each cultural trait
    -Initializer for the threshold T
    -Initializer for the topology
    -Protocol for Axelrod-Schelling model
        *Axelrod-Schelling must have access to the linkable protocol for the network topology
        *Axelrod-Schelling must have access to a list of empty sites
    -Observers:
        *to log data
        *to stop the simulation