package simpledb;

import java.util.NoSuchElementException;
import java.util.*;

/**
 * One TransactionDbFileIterator is made for one particular transaction and HeapFile
 */

public class TransactionDbFileIterator implements DbFileIterator{
	
	private TransactionId tid = null;
	private ArrayList<Page> pages = null;
	private Iterator<Page> iterPage = null;
	private Iterator<Tuple> iterTuple = null;
	
	public TransactionDbFileIterator(TransactionId tid, ArrayList<Page> pages){
		this.tid = tid;
		this.pages = pages;
	}

	@Override
	public void open() throws DbException, TransactionAbortedException {
		iterPage = pages.iterator();
		while(iterPage.hasNext()){
			iterTuple = ((HeapPage)(Database.getBufferPool().getPage(tid, iterPage.next().getId(), Permissions.READ_ONLY))).iterator();
			if(iterTuple.hasNext())
				break;
			else
				iterTuple=null;
		}
	}

	@Override
	public boolean hasNext() throws DbException, TransactionAbortedException {
		if(iterPage == null || iterTuple == null){
			return false;
		}
		
		if(iterTuple.hasNext()){
			return true;
		}else{
			while(iterPage.hasNext()){
				iterTuple = ((HeapPage)(Database.getBufferPool().getPage(tid, iterPage.next().getId(), Permissions.READ_ONLY))).iterator();
				if(iterTuple.hasNext())
					return true;
				else
					iterTuple=null;
			}
			return false;
		}
	}

	@Override
	public Tuple next() throws DbException, TransactionAbortedException,
			NoSuchElementException {
		//return null;
		if(iterTuple != null){
			return iterTuple.next();
		}else{
			throw new NoSuchElementException();
		}
	}

	@Override
	public void rewind() throws DbException, TransactionAbortedException {
		close();
		open();
	}

	@Override
	public void close() {
		iterPage = null;
		iterTuple = null;
	}

}
