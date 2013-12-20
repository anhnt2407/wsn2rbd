package br.cin.ufpe.wsn2rbd;

import org.modcs.tools.rbd.blocks.Block;
import org.modcs.tools.rbd.blocks.BlockExponential;
import org.modcs.tools.rbd.blocks.BlockParallel;
import org.modcs.tools.rbd.blocks.BlockSeries;
import org.modcs.tools.rbd.blocks.RBDModel;

/**
 *
 * @author avld
 */
public class EvaluateRBD
{
    private RBDModel model;
    
    public EvaluateRBD( RBDModel model )
    {
        this.model = model;
    }
    
    public double evaluate()
    {
        return evaluate( model.getModel() );
    }
    
    private double evaluate( Block block )
    {
        if( block instanceof BlockSeries )
        {
            return evaluateSeries( (BlockSeries) block );
        }
        else if( block instanceof BlockParallel )
        {
            return evaluateParallel( (BlockParallel) block );
        }
        else if( block instanceof BlockExponential )
        {
            return ((BlockExponential) block).getFailureRate();
        }
        else
        {
            return 0.0;
        }
    }
    
    private double evaluateSeries( BlockSeries blockSeries )
    {
        double total = 1;
        
        for( Block block : blockSeries.getBlocks() )
        {
            total *= evaluate( block );
        }
        
        return total;
    }
    
    private double evaluateParallel( BlockParallel blockParallel )
    {
        double total = 1;
        
        for( Block block : blockParallel.getBlocks() )
        {
            total *= (1 - evaluate( block ));
        }
        
        return (1 - total);
    }
}
