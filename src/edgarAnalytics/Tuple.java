package edgarAnalytics;



/**
 * Data type that consists of a pair of keys
 * @param <Key1> first key
 * @param <Key2> second key
 */
public class Tuple<Key1 extends Comparable<Key1>, 
                   Key2 extends Comparable<Key2>> 
             implements Comparable<Tuple<Key1, Key2>>{

    private final Key1 key1;
    private final Key2 key2;
    
    
    /**
     * Initializes a key pair
     * @param key1 - first key
     * @param key2 - second key
     */
    public Tuple(Key1 key1, Key2 key2)
    {
        this.key1 = key1;
        this.key2 = key2;
    }
    
    
    /**
     * Implementation of Comparable Interface for a pair of keys.
     * First, the key {@code key1} is compared, then {@code key2} is compared.
     */
    public int compareTo(Tuple<Key1, Key2> that)
    {
        int cmp = (this.key1).compareTo(that.getKey1());
        if (cmp != 0) return cmp;
        
        return (this.key2).compareTo(that.getKey2());
    }
    
    
    /**
     * Returns first key
     * @return key {@code key1}
     */
    public Key1 getKey1()
    { return key1; }
    
    
    /**
     * Returns second key
     * @return key {@code key2}
     */
    public Key2 getKey2()
    { return key2; }
    
    
    
    /**
     * Overrides {@code equals} method
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        
        if (!(o instanceof Tuple<?, ?>)) return false;
        Tuple<?, ?> that = (Tuple<?, ?>) o;
        
        return ((this.key1).equals(that.getKey1()) && (this.key2).equals(that.getKey2()));
    }
}
