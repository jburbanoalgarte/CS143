package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile{

	/*
	*/
	private File f;
	private TupleDesc td;
	private ArrayList<PageId> pages;
	
    private ArrayList<Byte> pageLocks;
	
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
		this.f = f;
		this.td = td;
		
		this.pageLocks = new ArrayList<Byte>();
		for(int i=0; i < (this.numPages() + 1); i++)
		{
			this.pageLocks.add((byte) 0);
		}//end for
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        //return null;
		return this.f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particulFile f;
	private TupleDesc td;
ar HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
		return this.f.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        //throw new UnsupportedOperationException("implement this");
		return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        //return null;
		int pageNo = pid.pageNumber();
		int pageSize = BufferPool.PAGE_SIZE;
		
		try
		{
			RandomAccessFile raf = new RandomAccessFile(this.f, "r");
		
			byte data [] = new byte [pageSize];
			
			long pos = (long)pageNo * pageSize;
			
			raf.seek(pos);			
			raf.read(data);
			raf.close();
			
			HeapPageId hpid = (HeapPageId) pid;
			HeapPage thePage = new HeapPage(hpid, data);
			return thePage;
		} catch(Exception e)
		{
			throw new IllegalArgumentException();
		}
		
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
		int pageNo = page.getId().pageNumber();
		int pageSize = BufferPool.getPageSize();
		
		try
		{
			RandomAccessFile raf = new RandomAccessFile(this.f, "rw");
		
			byte data [] = new byte [pageSize];
			data = page.getPageData();
			long pos = (long)pageNo * pageSize;
			
			raf.seek(pos);
			raf.write(data);
			raf.close();
			
			//HeapPageId hpid = (HeapPageId) pid;
			//HeapPage thePage = new HeapPage(hpid, data);
			//return thePage;
		} catch(Exception e)
		{
			throw new IOException("Write failed in HeapFile.writePage()");
		}
		
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        //return 0;		
		return (int)(Math.ceil((double)f.length() / BufferPool.PAGE_SIZE));
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        //return null;
        // not necessary for lab1
		
		BufferPool bp = Database.getBufferPool();
		
		//search for free page
		HeapPageId pid = null;
		int pageNo=0;
		for( ; pageNo < this.numPages(); pageNo++ )
		{
		
			HeapPageId apid = new HeapPageId(this.getId(), pageNo);
			HeapPage aPage = (HeapPage) bp.getPage(tid, apid, Permissions.READ_ONLY);
			if( aPage.getNumEmptySlots() > 0 ) //if a page has free space, then select it for tuple insertion
			{
				pid = aPage.getId();
				break;
			}//end if
			
			if( pageNo == (this.numPages() - 1) ) //no free page was found, so must add new page to heapfile
			{
				this.pageLocks.add((byte) 0);
				byte[] blankData = new byte[BufferPool.getPageSize()];
				apid = new HeapPageId(this.getId(), ++pageNo);
				pid = apid;
				HeapPage newPage = new HeapPage(apid, blankData);
				this.writePage(newPage);
				break;
			}//end if
			
		}//end for
		
		HeapPage thePage;
		synchronized( this.pageLocks.get(pageNo) )
		{
			thePage = (HeapPage) bp.getPage(tid, pid, Permissions.READ_WRITE);
		
			//insert tuple into free page
			thePage.insertTuple(t);
			
			//mark as dirty
			thePage.markDirty(true, tid);
		}//end synchronized
		
		//add free page to return value
		ArrayList<Page> modifiedPages = new ArrayList<Page>();
		modifiedPages.add(thePage);
		return modifiedPages;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        //return null;
        // not necessary for lab1
		HeapPageId pid = (HeapPageId) t.getRecordId().getPageId();
		
		if( pid.getTableId() != this.getId() )
		{
			throw new DbException("The tuple is not a member of the file.");
		}//end if
		
		HeapPage thePage;
		synchronized( this.pageLocks.get( pid.pageNumber() ) )
		{
			thePage = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
			thePage.deleteTuple(t);
			thePage.markDirty(true, tid);
		}//end synchronized
		
		ArrayList<Page> modifiedPage = new ArrayList<Page>();
		modifiedPage.add(thePage);
		
		return modifiedPage;
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        //return null;
    	pages = new ArrayList<PageId>();
    	for(int i=0; i<numPages();i++){
			try{
				pages.add(Database.getBufferPool().getPage(null, new HeapPageId(getId(),i), Permissions.READ_ONLY).getId());
			}catch(DbException e){
				System.err.println("HeapFile.iterator: DbException");
			}catch(TransactionAbortedException e){
				System.err.println("HeapFile.iterator: TransactionAbortedException");
			}
		}
    	return new TransactionDbFileIterator(tid, pages);	
    }

}

