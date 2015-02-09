package simpledb;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    
    private int gbfield;
	private Type gbfieldtype;
	private int afield;
	private Op what;
	
	// key: Object (either int or String) representing the group by field
	// value: aggregate data for the specified group by field eg a sum
	// if there is no grouping, then use null as key
	private HashMap<Object, Integer> aggregateData;
		
    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what){
        // some code goes here
    	if(what.toString()!="count"){
    		throw new IllegalArgumentException();
    	}else{
    		this.what = what;
    	}
    	
    	this.gbfield = gbfield;
		this.gbfieldtype = gbfieldtype;
		this.afield = afield;
		
		this.aggregateData = new HashMap<Object, Integer>();
    	
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
    	
    	//obtain gbfieldValue, key to hashmap
    	Field theField=null;
    	Object gbfieldValue = null;
    			
    	if( this.gbfield == Aggregator.NO_GROUPING )
    	{
    		gbfieldValue = null;
    	}
    	else
    	{
    		theField = tup.getField(this.gbfield);
    		if( theField.getType() == Type.INT_TYPE )
    		{
    			gbfieldValue = ( (IntField) theField ).getValue();
    		}
    		else if( theField.getType() == Type.STRING_TYPE )
    		{
    			gbfieldValue = ( (StringField) theField ).getValue();
    		}
    	}
    	//if key is not in hashmap, then initialize a value
    	if( !aggregateData.containsKey(gbfieldValue) )
    	{	
    		aggregateData.put(gbfieldValue, 0);					
    	}
    			
    	//calculate new aggregateValue
    	int aggregateValue = aggregateData.get(gbfieldValue) +1;

    	//update aggregate data and gbfield counts
    	aggregateData.put(gbfieldValue, aggregateValue);
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        //throw new UnsupportedOperationException("please implement me for lab2");
    	
    	//first, create a TupleDesc, with 2 possibilities
    	// no grouping: (aggregateValue)
    	Type[] iteratorTypeAr;
    	String[] iteratorFieldAr;
    	if( this.gbfield == Aggregator.NO_GROUPING )
    	{
    		iteratorTypeAr = new Type[1];
    		iteratorTypeAr[0] = Type.INT_TYPE;
    				
    		iteratorFieldAr = new String[1];
    		iteratorFieldAr[0] = "aggregateValue";
   		}
 		// grouping: (groupValue, aggregateValue)
    	else
    	{
    		iteratorTypeAr = new Type[2];
    		iteratorTypeAr[0] = this.gbfieldtype;	//gbfieldtype
    		iteratorTypeAr[1] = Type.INT_TYPE;		//afield type
    				
    		iteratorFieldAr = new String[2];
    		iteratorFieldAr[0] = "groupValue";
    		iteratorFieldAr[1] = "aggregateValue";
    	}
    			
    	TupleDesc iteratorTd = new TupleDesc(iteratorTypeAr, iteratorFieldAr);
    			
    	//populate list of tuples
    	ArrayList<Tuple> tuples = new ArrayList<Tuple>();
    			
    	for( Object gbfield : this.aggregateData.keySet() )
    	{
    			
    		Tuple tup = new Tuple(iteratorTd);
    				
    		//set values of tup fields
    		Field groupValueField = new IntField(0);
    		if( this.gbfield != Aggregator.NO_GROUPING )
    		{
    				
    			if( this.gbfieldtype == Type.INT_TYPE )
    			{
    				groupValueField = new IntField( (Integer) gbfield );
    			}//end if
    			else if( this.gbfieldtype == Type.STRING_TYPE )
    			{
    				groupValueField = new StringField( (String) gbfield, this.gbfieldtype.getLen() );
    			}
    				
    		}//end if
    			
    		int aggregateValue = this.aggregateData.get(gbfield);
    				
    		// case
    		Field aggregateField = new IntField(aggregateValue);
    				
    		if( this.gbfield != Aggregator.NO_GROUPING )
    		{
    			tup.setField(0, groupValueField);
    			tup.setField(1, aggregateField);
    		}//end if
    		else
    		{
    			tup.setField(0, aggregateField);
    		}//end else
    				
    		tuples.add(tup);
    			
    	}//end for
    			
    	//return iterator over list of tuples
    	return new TupleIterator(iteratorTd, tuples);	
    }

}
