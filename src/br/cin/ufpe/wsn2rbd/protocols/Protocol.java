package br.cin.ufpe.wsn2rbd.protocols;

import br.cin.ufpe.wsn2cpn.Node;
import br.cin.ufpe.wsn2cpn.Topology;
import br.cin.ufpe.wsn2rbd.Route;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author avld
 */
public abstract class Protocol
{
    private Topology topology;
    private Map<Integer, List<Integer>> neighborMap;
    
    public Protocol()
    {
        neighborMap = new HashMap<>();
    }

    public Topology getTopology()
    {
        return topology;
    }

    public void setTopology( Topology topology )
    {
        this.topology = topology;
        createNeighborList();
    }
    
    public boolean isValidTopology()
    {
        return topology == null 
                ? false 
                : topology.getNodeMap() != null;
    }
    
    private void createNeighborList()
    {
        if( !isValidTopology() )
        {
            return ;
        }
        
        neighborMap.clear();
        
        for( Node node : topology.getNodeMap().values() )
        {
            List<Integer> neighborList = new ArrayList<>();
            
            for( Node other : topology.getNodeMap().values() )
            {
                if( isNeighbor( node , other ) )
                {
                    neighborList.add( other.getId() );
                }
            }
            
            neighborMap.put( node.getId() , neighborList );
        }
    }
    
    private boolean isNeighbor( Node n1 , Node n2 )
    {
        if( n1.getId() == n2.getId() )
        {
            return false;
        }
        
        int x1 = Integer.parseInt( n1.getProperties().get( "X" ) ); //pos.x
        int y1 = Integer.parseInt( n1.getProperties().get( "Y" ) ); //pos.y
    
        int x2 = Integer.parseInt( n2.getProperties().get( "X" ) );
        int y2 = Integer.parseInt( n2.getProperties().get( "Y" ) );
        
        double distance = Math.sqrt( Math.pow( x1 - x2 , 2 ) + Math.pow( y1 - y2 , 2 ) );
        double range = Double.parseDouble( n1.getProperties().get( "range" ) );
        
        return distance <= range;
    }

    public Map<Integer, List<Integer>> getNeighborMap()
    {
        return neighborMap;
    }
    
    // ---------------------------------------- //
    // ---------------------------------------- //
    // ---------------------------------------- //
    
    protected double getAvailability( Node n )
    {
        double link         = 1.0;
        double application  = 1.0;
        double hardware     = 1.0;
        double tinyos       = 1.0;
        double batteryLevel = 1.0;
        double batteryMax   = 100;
        double batteryMin   =   0;
        
        if( topology.getConfigurationMap().containsKey( "dependability.link" ) )
        {
            String str = topology.getConfigurationMap().get( "dependability.link" );
            link = Double.parseDouble( str );
        }
        
        if( topology.getConfigurationMap().containsKey( "dependability.application" ) )
        {
            String str = topology.getConfigurationMap().get( "dependability.application" );
            application = Double.parseDouble( str );
        }
        
        if( topology.getConfigurationMap().containsKey( "dependability.hardware" ) )
        {
            String str = topology.getConfigurationMap().get( "dependability.hardware" );
            hardware = Double.parseDouble( str );
        }
        
        if( topology.getConfigurationMap().containsKey( "dependability.tinyos" ) )
        {
            String str = topology.getConfigurationMap().get( "dependability.tinyos" );
            hardware = Double.parseDouble( str );
        }
        
        if( topology.getConfigurationMap().containsKey( "dependability.batteryMax" ) )
        {
            String str = topology.getConfigurationMap().get( "dependability.batteryMax" );
            batteryMax = Double.parseDouble( str );
        }
        
        if( topology.getConfigurationMap().containsKey( "dependability.batteryMin" ) )
        {
            String str = topology.getConfigurationMap().get( "dependability.batteryMin" );
            batteryMin = Double.parseDouble( str );
        }
        
        if( n.getProperties().containsKey( "battery_level" ) )
        {
            String str = n.getProperties().get( "battery_level" );
            double battery = 100.00;
                
            try
            {
                battery = Double.parseDouble( str );
            }
            catch( Exception err )
            {
                battery = 100.00;
                System.err.println( "[ERROR] " + err.getMessage() );
            }
                
            //caso o valor da bateria seja maior do que o maximo, a confiança é 1.0
            if( battery >= batteryMax )
            {
                batteryLevel = 1.0;
            }
            //caso o valor da bateria seja menor do que o minimo, a confiança é 0.0
            else if( battery <= batteryMin )
            {
                batteryLevel = 0.0;
            }
            //caso o valor da bateria esteja entre o maximo e o minimo,
            //a confiança deve ser proporcional
            else
            {
                try
                {
                    double value    = battery    - batteryMin;
                    double interval = batteryMax - batteryMin;

                    batteryLevel = value / interval;
                }
                catch( Exception err )
                {
                    batteryLevel = 1.0;
                    System.err.println( "[ERROR] Interval is zero." );
                }
            }
        }
        else
        {
            System.err.println( "[ERROR] Node without battery level." );
        }
        
        return link * application * hardware * tinyos * batteryLevel;
    }
    
    protected Route convertNodeToRoute( Node n )
    {
        Route route = new Route();
        route.setName( n.getId() + "" );
        route.setAvailability( getAvailability( n ) );
        
        return route;
    }
    
    public abstract Route getRoute( int senderId , int receiverId );
    public abstract String getName();
    
    public List<Route> getRoute( List<Integer> nodeList , int receiver )
    {
        List<Route> routeList = new ArrayList<>();
        
        for( Integer nodeId : nodeList )
        {
            Route route = getRoute( nodeId , receiver );
            
            if( route != null )
            {
                routeList.add( route );
            }
        }
        
        return routeList;
    }
    
}
