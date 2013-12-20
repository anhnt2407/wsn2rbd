package br.cin.ufpe.wsn2rbd.xml;

import java.io.FileWriter;
import java.io.IOException;
import org.modcs.tools.rbd.blocks.Block;
import org.modcs.tools.rbd.blocks.BlockBridge;
import org.modcs.tools.rbd.blocks.BlockExponential;
import org.modcs.tools.rbd.blocks.BlockParallel;
import org.modcs.tools.rbd.blocks.BlockSeries;
import org.modcs.tools.rbd.blocks.RBDModel;

/**
 * Esta classe é responsável por salvar o modelo RBD em um arquivo XML.
 * Esta classe só pode ser usada por outra classe dentro desse pacote. Isso foi
 * necessário para força o usuário a usar o método RbdFile.save().
 * 
 * @author avld
 */
public class RbdFileSave
{
    private String SPACE_VALUE = "  ";
    private RBDModel model;
    private FileWriter writer;
    
    protected RbdFileSave( RBDModel model )
    {
        this.model = model;
    }
    
    protected void save( String filename ) throws IOException
    {
        writer = new FileWriter( filename );
        writer.write( "<rbd>\n\n" );
        writer.write( SPACE_VALUE + "<name>"+ model.getName() +"</name>\n\n" );
        writer.write( SPACE_VALUE + "<label_list></label_list>\n\n" );
        
        save( SPACE_VALUE , model.getModel() );
        
        writer.write( "</rbd>" );
        writer.close();
    }
    
    /**
     * Salva um block qualquer (ex. exponencial ou seriado) no XML
     * 
     * @param space        alinhamento no texto
     * @param block        block que deseja salvar
     * @throws IOException problema na escrita no arquivo
     */
    private void save( String space , Block block ) throws IOException
    {
        if( block instanceof BlockExponential )
        {
            save( space , (BlockExponential) block );
        }
        else if( block instanceof BlockSeries )
        {
            save( space , (BlockSeries) block );
        }
        else if( block instanceof BlockParallel )
        {
            save( space , (BlockParallel) block );
        }
        else if( block instanceof BlockBridge )
        {
            save( space , (BlockBridge) block );
        }
    }
    
    /**
     * Salva um bloco exponencial no arquivo XML
     * 
     * @param space        alinhamento no texto
     * @param block        block que deseja salvar
     * @throws IOException problema na escrita no arquivo
     */
    private void save( String space , BlockExponential block ) throws IOException
    {
        String name = block.getName();
        String type = "rate";                       //TODO: qual é o campo? RATE ou TIME
        String status = block.getState();
        String avaliability = "0";                  //TODO: esse campo é para o futuro
        String mttf = block.getFailureRate() + "";
        String mttr = block.getRepairRate() + "";
        String cost = "0";                          //TODO: qual é o campo relacionado com custo?
        
        // ---------------------------
        
        writer.write( space + "<block>\n" );
        writer.write( space + SPACE_VALUE + "<name>"+ name +"</name>\n" );
        writer.write( space + SPACE_VALUE + "<type>"+ type +"</type>\n" );
        writer.write( space + SPACE_VALUE + "<status>"+ status +"</status>\n" );
        writer.write( space + SPACE_VALUE + "<availability>"+ avaliability +"</availability>\n" );
        writer.write( space + SPACE_VALUE + "<mttf>"+ mttf +"</mttf>\n" );
        writer.write( space + SPACE_VALUE + "<mttr>"+ mttr +"</mttr>\n" );
        writer.write( space + SPACE_VALUE + "<cost>"+ cost +"</cost>\n" );
        writer.write( space + "</block>\n\n" );
    }
    
    /**
     * Salva um bloco seriado no arquivo XML
     * 
     * @param space        alinhamento no texto
     * @param block        block que deseja salvar
     * @throws IOException problema na escrita no arquivo
     */
    private void save( String space , BlockSeries block ) throws IOException
    {
        writer.write( space + "<series>\n\n" );
        
        for( Block b : block.getBlocks() )
        {
            save( space + SPACE_VALUE , b );
        }
        
        writer.write( space + "</series>\n\n" );
    }
    
    /**
     * Salva um bloco paralelo no arquivo XML
     * 
     * @param space        alinhamento no texto
     * @param block        block que deseja salvar
     * @throws IOException problema na escrita no arquivo
     */
    private void save( String space , BlockParallel block ) throws IOException
    {
        writer.write( space + "<parallel>\n\n" );
        
        for( Block b : block.getBlocks() )
        {
            save( space + SPACE_VALUE , b );
        }
        
        writer.write( space + "</parallel>\n\n" );
    }
    
    /**
     * Salva um bloco ponte no arquivo XML
     * 
     * @param space        alinhamento no texto
     * @param block        block que deseja salvar
     * @throws IOException problema na escrita no arquivo
     */
    private void save( String space , BlockBridge block ) throws IOException
    {
        writer.write( space + "<bridge>\n\n" );
        
        for( Block b : block.getBlocks() )
        {
            save( space + SPACE_VALUE , b );
        }
        
        writer.write( space + "</bridge>\n\n" );
    }
    
}
