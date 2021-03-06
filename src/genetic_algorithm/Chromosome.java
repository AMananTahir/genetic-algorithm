package genetic_algorithm;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;


public class Chromosome implements IChromosome, Comparable<Chromosome>
{

    public static final int CO_TYPE_SINGLE_POINT = 1;
    public static final int CO_TYPE_DOUBLE_POINT = 2;
    
    int size;
    IGene [] genes = null;
    private static Polynomial polynomial = null;
    private static int crossOverType = CO_TYPE_SINGLE_POINT;
    

    public static int getCrossOverType()
    {
        return crossOverType;
    }

    public static void setCrossOverType(int crossOverType)
    {
        Chromosome.crossOverType = crossOverType;
    }
    
    

    public static Polynomial getPolynomial()
    {
        return polynomial;
    }

    public static void setPolynomial(Polynomial polynomial) 
    {
        Chromosome.polynomial = polynomial;
    }
    
    
    public Chromosome(Chromosome ch)
    {
        setSize(ch.getSize());
        for (int i = 0; i < size; i++)
        {
            genes[i]=new Gene((Double)ch.getGenes()[i].getValue());
        }
    }
    

    public Chromosome(int s)
    {
        setSize(s);
        for (int i = 0; i < size; i++)
        {
            genes[i]=new Gene(true);//true : random
        }
    }

    
    @Override
    public int getSize()
    {
        return size;
    }
    
    @Override
    public void setSize(int size)
    {
        this.size = size;
        genes = new IGene[size];
    }
    
    @Override
    public IGene[] getGenes()
    {
        return genes;
    }

    @Override
    public void setGenes(IGene[] genes)
    {
        this.genes = genes;
    }
    
    public double[] getGenesValue() throws NullPointerException , ArrayIndexOutOfBoundsException
    {
        if(genes == null)
            throw new NullPointerException("Genes are empty");
        int gsize = genes.length;
        double[] res = new double[gsize];
        for (int i = 0; i < gsize; i++)
        {
            res[i] = (double)genes[i].getValue();
        }
        return res;
    }
    
    @Override
    public IGene getGene(int i) throws NullPointerException , ArrayIndexOutOfBoundsException
    {
        if(genes == null)
            throw new NullPointerException("Genes are empty");
        if(i>=size || i<0)
            throw new ArrayIndexOutOfBoundsException("I out of range");
        return genes[i];
    }
    public void setGene(int i , IGene val) throws NullPointerException , ArrayIndexOutOfBoundsException
    {
        if(genes == null)
            throw new NullPointerException("Genes are empty");
        if(i>=size || i<0)
            throw new ArrayIndexOutOfBoundsException("I out of range");
        genes[i] = val;
    }
    


    @Override
    public double getFitness() throws IllegalArgumentException
    {
        if(getSize() != polynomial.getSize())
            throw new IllegalArgumentException("Sizes are not same");
        double [] vals = new double[size];
        for (int i = 0; i < size; i++)
        {
            vals[i] = (double)getGene(i).getValue();
        }
        double res = polynomial.getValue(vals);
        return Math.abs(1/res);
    }

    @Override
    public IChromosome[] crossOver(IChromosome c2)
    {
        if(Chromosome.getCrossOverType()==CO_TYPE_SINGLE_POINT)
            return singlePointCrossOver(c2);
        else if(Chromosome.getCrossOverType()==CO_TYPE_DOUBLE_POINT)
            return doublePointCrossOver(c2);
        else
            throw new NoSuchMethodError("no such cross over type");
    }
    
    
    private IChromosome[] singlePointCrossOver(IChromosome c2)
    {
        int cop = new Random().nextInt(size-1);//cross over point
        cop++;
        Chromosome [] res = new Chromosome[2];
        res[0] = new Chromosome(size);
        res[1] = new Chromosome(size);
        for (int i = 0; i < cop; i++)
        {
            res[0].setGene(i, new Gene((double)c2.getGene(i).getValue()));
            res[1].setGene(i, new Gene((double)getGene(i).getValue()));
        }
        for (int i = cop; i < size; i++)
        {
            res[0].setGene(i, new Gene((double)getGene(i).getValue()));
            res[1].setGene(i, new Gene((double)c2.getGene(i).getValue()));
        }
        return res;
    }
    
    
    private IChromosome[] doublePointCrossOver(IChromosome c2)
    {
        int cop1,cop2;
        do
        {
            cop1 = new Random().nextInt(size-1);//cross over point
            cop2 = new Random().nextInt(size-1);//cross over point
        }
        while (cop1==cop2);
        
        if(cop1>cop2)
        {
            int temp = cop1;
            cop1 = cop2;
            cop2 = temp;
        }
        
        Chromosome [] res = new Chromosome[2];
        res[0] = new Chromosome(size);
        res[1] = new Chromosome(size);
        for (int i = 0; i < cop1; i++)
        {
            res[0].setGene(i, new Gene((double)getGene(i).getValue()));
            res[1].setGene(i, new Gene((double)c2.getGene(i).getValue()));
        }
        for (int i = cop1; i < cop2; i++)
        {
            res[0].setGene(i, new Gene((double)c2.getGene(i).getValue()));
            res[1].setGene(i, new Gene((double)getGene(i).getValue()));
        }
        for (int i = cop2; i < size; i++)
        {
            res[0].setGene(i, new Gene((double)getGene(i).getValue()));
            res[1].setGene(i, new Gene((double)c2.getGene(i).getValue()));
        }
        //System.out.println("Co Result: "+res[0]+" , "+res[1]+"  from: "+toString()+" , "+c2);
        return res;
    }

    @Override
    public IChromosome mutate()
    {
        Chromosome ch = new Chromosome(size);
        for (int i = 0; i < size; i++)
        {
            ch.setGene(i, new Gene((double)getGene(i).getValue()));
        }
        int m = new Random().nextInt(size);
        ch.getGene(m).random();
        return ch;
    }

    @Override
    public String toString()
    {
        String s = "";
        for (int i = 0; i < size; i++)
        {
            s += "["+String.format("%.0"+Gene.points+"f", genes[i].getValue() )+"]";
            //s += "["+genes[i].getValue()+"]";
            
        }
        //return "Chromosome{" + "size=" + size + ", genes= " + s + '}';
        return "{" + s + " "+String.format("%.03f", getFitness() ) +'}';
    }

 

    @Override
    public int compareTo(Chromosome o)
    {
        if(getFitness() < o.getFitness())
            return -1;
        if(getFitness() > o.getFitness())
            return 1;
        return 0;    
    }


    @Override
    public boolean equals(Object obj)
    {
//        if (obj == null)
//        {
//            return false;
//        }
//        if (getClass() != obj.getClass())
//        {
//            return false;
//        }
        final Chromosome other = (Chromosome) obj;
        if (this.size != other.size)
        {
            return false;
        }
        for (int i = 0; i < size; i++)
        {
            if(Math.abs((double)genes[i].getValue() - (double)other.genes[i].getValue())>0.0001)
                return false;
        }
        return true;
    }
    
    
    
}
