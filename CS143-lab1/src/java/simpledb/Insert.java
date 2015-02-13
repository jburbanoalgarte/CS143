package simpledb;

import java.io.IOException;
import java.util.*;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;
    
    private TransactionId t;
    private DbIterator child;
    private int tableid;
    
    //private ArrayList<Tuple> insertedTuples= new ArrayList<Tuple>();
    private boolean inserted=false;
    private Tuple result;
    
    /**
     * Constructor.
     * 
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableid
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    public Insert(TransactionId t,DbIterator child, int tableid)
            throws DbException {
        // some code goes here
    	this.t=t;
    	this.child=child;
    	this.tableid=tableid;

    	this.result=new Tuple(new TupleDesc(new Type[]{Type.INT_TYPE}, new String[]{"Number of inserted tuples"}));
    	this.result.setField(0, new IntField(0));
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        //return null;
    	//return child.getTupleDesc();
    	return result.getTupleDesc();
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	child.open();
        super.open();
    }

    public void close() {
        // some code goes here
    	super.close();
        child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	child.rewind();
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     * 
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        //return null;
    	
    	/*
    	if(!child.hasNext())
    		return null;
    	
    	Tuple tuple=child.next();
    	for(Iterator<Tuple> i=this.insertedTuples.iterator();i.hasNext();){
    		if(i.next().equals(tuple))
    			return null;
    	}
    	try{
    		Database.getBufferPool().insertTuple(this.t, tableid, tuple);
    	}catch(IOException e){
    		StackTraceElement[] ele=e.getStackTrace();
    		for(int i=0;i<ele.length;i++)
    			System.err.println(ele[i]);
    	}
    	insertedTuples.add(tuple);
		int currentNum=((IntField)(this.result.getField(0))).getValue();
		this.result.setField(0, new IntField(++currentNum));
		return result;
		*/

    	if(!inserted){
    	
        	while(child.hasNext()){
        		Tuple tuple=child.next();
        	
        		try{
        			Database.getBufferPool().insertTuple(this.t, tableid, tuple);
        		}catch(IOException e){
        			StackTraceElement[] ele=e.getStackTrace();
        			for(int i=0;i<ele.length;i++)
        				System.err.println(ele[i]);
        		}
        		int currentNum=((IntField)(this.result.getField(0))).getValue();
        		this.result.setField(0, new IntField(++currentNum));
        	}
    		inserted=true;
    		return result;
    	}
    	return null;
    	
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        //return null;
    	return new DbIterator[] { this.child }; // DbIterator child
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
    	if (this.child!=children[0]){
    		this.child = children[0];
    	}
    }
}
