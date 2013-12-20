package br.cin.ufpe.wsn2rbd.xml;

import java.io.IOException;
import org.modcs.tools.rbd.blocks.RBDModel;

/**
 * Esta classe deve ser usada para salvar e abrir um modelo RBD.
 * Ela segue o padrão FACHADA, o qual serve apenas como ponte ele aplicação
 * e as classes que faram, de fato, a ação de salvar e abrir o arquivo.
 * 
 * @author avld
 */
public class RbdFile
{
    
    /**
     * Criar um modelo RBD de acordo com um arquivo
     * 
     * @param filename          arquivo que possue o modelo inserido
     * @return                  modelo RBD
     * @throws IOException      erro ao abrir o arquivo
     * @throws Exception        formatação do XML esta errada
     */
    public static RBDModel open( String filename ) throws IOException, Exception
    {
        RbdFileOpen opener = new RbdFileOpen();
        return opener.open( filename );
    }
    
    /**
     * Salva um modelo em um arquivo XML
     * 
     * @param filename          nome do arquivo a ser criado
     * @param model             modelo RBD a ser salvo
     * @throws IOException      erro gerado quando tenta criar/escrever um arquivo
     */
    public static void save( String filename , RBDModel model ) throws IOException
    {
        RbdFileSave saver = new RbdFileSave( model );
        saver.save( filename );
    }
    
}
