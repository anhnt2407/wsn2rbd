package br.cin.ufpe.wsn2rbd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modcs.tools.rbd.blocks.Block;
import org.modcs.tools.rbd.blocks.BlockExponential;
import org.modcs.tools.rbd.blocks.BlockParallel;
import org.modcs.tools.rbd.blocks.BlockSeries;
import org.modcs.tools.rbd.blocks.RBDModel;

/**
 *
 * @author avld
 */
public class ConvertRouteToRBD
{
    private Map<String,Integer> nodeMap;
    private int parallelCounter;
    private int seriesCounter;
    
    public ConvertRouteToRBD()
    {
        // do nothing
    }
    
    public RBDModel convert( List<Route> routeList )
    {
        if( routeList.isEmpty() )
        {
            return null;
        }
        else if( routeList.size() == 1 )
        {
            return convert( routeList.get( 0 ) );
        }
        
        // --------------------
        
        nodeMap = new HashMap<>();
        parallelCounter = 0;
        seriesCounter   = 0;
        
        // --------------------
        
        BlockParallel parallel = new BlockParallel( getParallelName() );
        
        for( Route route : routeList )
        {
            Block block = convertRouteToBlock( route );
            parallel.addBlock( block );
        }
        
        RBDModel model = new RBDModel( "WSN" );
        model.setModel( parallel );
        
        return model;
    }
    
    public RBDModel convert( Route route )
    {
        nodeMap = new HashMap<>();
        parallelCounter = 0;
        seriesCounter   = 0;
        
        Block block = convertRouteToBlock( route );
        
        if ( block == null )
        {
            return null;
        }
        else
        {
            RBDModel model = new RBDModel( "WSN" );
            model.setModel( block );

            return model;
        }
    }
    
    private Block convertRouteToBlock( Route route )
    {
        String name  = getBlockName( route );
        
        BlockExponential block = new BlockExponential( name , null , null );
        block.setFailureRate( route.getAvailability() );
        
        // ----------------------
        
        if( route.getRouteList().isEmpty() )                     // um block normal
        {
            return block;
        }
        else if( route.getRouteList().size() == 1 )             // um block seriado
        {
            Block block01 = convertRouteToBlock( route.getRouteList().get( 0 ) );
            
            BlockSeries series = new BlockSeries( getSeriesName() );
            series.addBlock( block );
            series.addBlock( block01 );
            
            return series;
        }
        else                                                    // um block paralelo
        {
            BlockParallel parallel = new BlockParallel( getParallelName() );
            
            for( Route r : route.getRouteList() )
            {
                Block b = convertRouteToBlock( r );
                parallel.addBlock( b );
            }
            
            BlockSeries series = new BlockSeries( getSeriesName() );
            series.addBlock( block );
            series.addBlock( parallel );
            
            return series;
        }
    }
    
    private String getBlockName( Route route )
    {
        String name = route.getName();
        int counter = 1;
        
        if( nodeMap.containsKey( name ) )
        {
            counter = nodeMap.get( name ) + 1;
        }
        
        nodeMap.put( name , counter );
        
        return name + " (" + counter + ")";
    }
    
    private String getSeriesName()
    {
        return "Series (" + (++seriesCounter) + ")";
    }
    
    private String getParallelName()
    {
        return "Parallel (" + (++parallelCounter) + ")";
    }
}
