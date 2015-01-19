package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;

        /**
         * The name of the field
         * */
        public final String fieldName;
	
        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    private List<TDItem> TDItems = new ArrayList<TDItem>();

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        //return null;
    	return TDItems.iterator();
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
		//
		if (typeAr.length < 1 || typeAr.length == fieldAr.length)
			throw new RuntimeException("lengths don't match");
		
    	for(int i=0;i<typeAr.length;i++){
    		TDItems.add(new TDItem(typeAr[i], fieldAr[i]));
    	}
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
    	this(typeAr, new String[typeAr.length]);
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        //return 0;
    	return TDItems.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
        //return null;
    	if(i<numFields()){
    		return TDItems.get(i).fieldName;
    	}else{
    		throw new NoSuchElementException();
    	}
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here
        //return null;
    	if(i<numFields()){
    		return TDItems.get(i).fieldType;
    	}else{
    		throw new NoSuchElementException();
    	}
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        //return 0;
    	if(name==null)
    		throw new NoSuchElementException();
    	int i=0;
    	for(Iterator<TDItem> iter=iterator();iter.hasNext();){
    		String str = iter.next().fieldName;
    		if(name.equals(str)){
    			return i;
    		}
    		i++;	
    	}
    	throw new NoSuchElementException();
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
        //return 0;
    	int size=0;
    	for(Iterator<TDItem> iter=iterator();iter.hasNext();){
    		int l=iter.next().fieldType.getLen();
    		size+=l;
    	}
    	return size;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here
        //return null;
    	Type[] t = new Type[td1.numFields()+td2.numFields()];
    	String[] s = new String[td1.numFields()+td2.numFields()];
    	int i = 0;
    	for(Iterator<TDItem> iter=td1.iterator();iter.hasNext();){
    		TDItem item = iter.next();
    		t[i] = item.fieldType;
    		s[i] = item.fieldName;
    		i++;
    	}
    	for(Iterator<TDItem> iter=td2.iterator();iter.hasNext();){
    		TDItem item = iter.next();
    		t[i] = item.fieldType;
    		s[i] = item.fieldName;
    		i++;
    	}
    	return new TupleDesc(t,s);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
        //return false;
    	
    	if (o == null)
			return false;
    	if (getClass() != o.getClass())
			return false;
    	return hashCode() == ((TupleDesc)o).hashCode();
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        //throw new UnsupportedOperationException("unimplemented");
    	
    	//referred generate hashCode() and equals() on Eclipse
    	final int prime = 23;
    	int hash = prime + getSize();
    	for(Iterator<TDItem> iter=iterator();iter.hasNext();){
    		hash = prime * hash + iter.next().fieldType.getLen();
    	}
    	return hash;    	
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        //return "";
    	StringBuilder sb = new StringBuilder();
    	for(Iterator<TDItem> iter=iterator();;){
    		TDItem it = iter.next();
    		sb.append(it.fieldType);
    		sb.append("(");
    		sb.append(it.fieldName);
    		sb.append(")");
    		if(!iter.hasNext())
    			break;	
    		sb.append(", ");
    	}
    	return sb.toString();
    }
    
    public static void main(String[] args){
    	TupleDesc td = new TupleDesc(
    			new Type[]{Type.INT_TYPE,Type.INT_TYPE,Type.STRING_TYPE},
    			new String[]{"ID1","ID2","STR"});
    	System.out.println(td);
    }
}
