package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;

    private TransactionId t;
    private DbIterator child;
    
    private boolean deleted=false;
    private Tuple result;
    
    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
    	this.t=t;
    	this.child=child;
    	
    	this.result=new Tuple(new TupleDesc(new Type[]{Type.INT_TYPE}, new String[]{"Number of deleted tuples"}));
    	this.result.setField(0, new IntField(0));
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        //return null;
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
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        //return null;
    	
    	if(!deleted){
    		while(child.hasNext()){
    			Tuple tuple=child.next();
    			try{
    				Database.getBufferPool().deleteTuple(this.t, tuple);
    			}catch(IOException e){
    				StackTraceElement[] ele=e.getStackTrace();
    				for(int i=0;i<ele.length;i++)
    					System.err.println(ele[i]);
    			}
    			int currentNum=((IntField)(this.result.getField(0))).getValue();
    			this.result.setField(0, new IntField(++currentNum));
    		}
    		deleted=true;
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
