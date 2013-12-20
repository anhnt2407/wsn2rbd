package br.cin.ufpe.wsn2rbd.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author avld
 */
public class ProtocolFactory
{
    private static ProtocolFactory instance;
    private Map<String,Protocol> protocolMap;
    
    private ProtocolFactory()
    {
        init();
    }
    
    private void init()
    {
        protocolMap = new HashMap<>();
        add( new FloodingProtocol()  );
        add( new GossipingProtocol() );
        add( new DirectProtocol()    );
        add( new LeachProtocol()     );
        add( new DijkstraProtocol()  );
    }
    
    public static ProtocolFactory getInstance()
    {
        if( instance == null )
        {
            instance = new ProtocolFactory();
        }
        
        return instance;
    }
    
    // ----------------------------
    
    public Protocol getProtocol( String name )
    {
        return protocolMap.get( name.toLowerCase() );
    }
    
    public List<Protocol> getProtocolList()
    {
        return new ArrayList<>( protocolMap.values() );
    }
    
    public void add( Protocol protocol )
    {
        protocolMap.put( protocol.getName().toLowerCase() , protocol );
    }
}
