package br.cin.ufpe.wsn2rbd;

import br.cin.ufpe.wsn2cpn.Topology;
import br.cin.ufpe.wsn2rbd.protocols.Protocol;
import br.cin.ufpe.wsn2rbd.protocols.ProtocolFactory;
import java.util.ArrayList;
import java.util.List;
import org.modcs.tools.rbd.blocks.RBDModel;

/**
 *
 * @author avld
 */
public class Wsn2Rbd
{
    private Topology topology;
    
    public Wsn2Rbd( Topology topology )
    {
        this.topology = topology;
    }
    
    public RBDModel convert( int from , int to )
    {
        List<Integer> fromList = new ArrayList<>();
        fromList.add( from );
        
        return convert( fromList , to );
    }
    
    public RBDModel convert( List<Integer> from , int to )
    {
        String name = topology.getConfigurationMap().get( "network_layer" );
        Protocol protocol = ProtocolFactory.getInstance().getProtocol( name );
        
        protocol.setTopology( topology );
        List<Route> route = protocol.getRoute( from , to );
        
        if( route == null 
                ? true 
                : route.isEmpty() )
        {
            RBDModel RBD = new RBDModel( "WSN" );
            RBD.setModel( null );
            
            return RBD;
        }
        else
        {
            ConvertRouteToRBD convert = new ConvertRouteToRBD();
            return convert.convert( route );
        }
    }
    
    public double evaluate( RBDModel model )
    {
        EvaluateRBD evaluator = new EvaluateRBD( model );
        return evaluator.evaluate();
    }
    
}
