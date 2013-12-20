package br.cin.ufpe.wsn2rbd;

import java.util.Random;

/**
 *
 * @author avld
 */
public class Estatistica
{
    /**
     * Esse metodo foi conseguido atraves do projeto SimJava.
     * 
     * @param min   valor minimo
     * @param max   valor maximo
     * @return      valor aleatorio seguindo a distribuição Uniforme
     */
    public static double uniform( double min , double max )
    {
        Random gen = new Random();
        double mag = max - min;
        
        return mag * gen.nextDouble() + min;
    }
}
