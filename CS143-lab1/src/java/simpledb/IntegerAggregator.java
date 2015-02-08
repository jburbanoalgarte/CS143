package simpledb;

//added packages
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

	private int gbfield;
	private Type gbfieldtype;
	private int afield;
	private Op what;
	
	// key: Object (either IntField or StringField) representing the group by field
	// value: aggregate data for the specified group by field eg a sum
	// if there is no grouping, then use null as key
	private HashMap<Object, Integer> aggregateData;
	
	// key: Object (either IntField or StringField) representing the group by field
	// value: number of tuples in the specified group by field
	// if there is no grouping, then use null as key
	private HashMap<Object, Integer> groupByFieldCounts;
	
    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
		this.gbfield = gbfield;
		this.gbfieldtype = gbfieldtype;
		this.afield = afield;
		this.what = what;
		this.aggregateData = new HashMap<Object, Integer>();
		this.groupByFieldCounts = new HashMap<Object, Integer>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
		//obtain gbfieldValue, key to hashmap
		Field theField = tup.getField(this.gbfield);
		
		Object gbfieldValue = null;
		if( this.gbfield == Aggregator.NO_GROUPING )
		{
			gbfieldValue = null;
		}
		else if( theField.getType() == Type.INT_TYPE )
		{
			gbfieldValue = ( (IntField) theField ).getValue();
		}
		else if( theField.getType() == Type.STRING_TYPE )
		{
			gbfieldValue = ( (StringField) theField ).getValue();
		}
		
		//if key is not in hashmap, then initialize a value
		if( !aggregateData.containsKey(gbfieldValue) )
		{
			switch( what ) {
		
				case COUNT:
				{
					aggregateData.put(gbfieldValue, 0);
					break;
				}//end case COUNT
				case SUM:
				{
					aggregateData.put(gbfieldValue, 0);
					break;
				}//end case SUM
				case AVG:
				{
					aggregateData.put(gbfieldValue, 0);
					break;
				}//end case AVG
				case MIN:
				{
					aggregateData.put(gbfieldValue, Integer.MAX_VALUE);
					break;
				}//end case MIN
				case MAX:
				{				
					aggregateData.put(gbfieldValue, Integer.MIN_VALUE);
					break;
				}//end case MAX
		
			}//end switch
			
			// initialize counter to zero
			groupByFieldCounts.put(gbfieldValue, 0);
		}
		
		//determine new aggregateValue
		int aggregateValue = aggregateData.get(gbfieldValue);
		int gbFieldCount = groupByFieldCounts.get(gbfieldValue);
		gbFieldCount++; //add tuple
		int tupleValue = ( (IntField) tup.getField(this.afield) ).getValue();
		switch( what ) {
		
			case COUNT:
			{
				aggregateValue++;
				break;
			}//end case COUNT
			case SUM:
			{
				aggregateValue += tupleValue;
				break;
			}//end case SUM
			case AVG:
			{
				aggregateValue = ( (gbFieldCount-1)*aggregateValue + tupleValue ) / gbFieldCount;
				break;
			}//end case AVG
			case MIN:
			{
				if( tupleValue < aggregateValue )
				{
					aggregateValue = tupleValue;
				}
				break;
			}//end case MIN
			case MAX:
			{
			
				if( tupleValue > aggregateValue )
				{
					aggregateValue = tupleValue;
				}
				break;			
			}//end case MAX
		
		}//end switch
		
		//update aggregate data and gbfield counts
		aggregateData.put(gbfieldValue, aggregateValue);
		groupByFieldCounts.put(gbfieldValue, gbFieldCount);
    }

    /**
     * Create a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        //throw new
        //UnsupportedOperationException("please implement me for lab2");
		
		//first, create a TupleDesc, with 2 possibilites
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
			else
			{
				groupValueField = new IntField(0);
			}//end else
		
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
