/*
 * Brandeis COSI 12b
 * PA7 - HTML Validator
 * HtmlValidator class
 *
 * An HtmlValidator object contains a queue of HtmlTags.
 *
 *
 * @version 08/08/2022
 * @author Linfeng Zhu
 */
import java.util.*;  //for queues

public class HtmlValidator {

	private Queue<HtmlTag> tags;
	private int size;  //since the size of the queue will change when looping, it's better to use a variable to store the size
	
	/**
	 * Default constructor that will initialize the validator to store an empty queue of HTML tags
	 */
	public HtmlValidator() {
		this.tags = new LinkedList<HtmlTag>();
	}
	
	/**
	 * Constructor that initialize the validator to store a given queue of HTML tags
	 * Throws IllegalArgumentException
	 * @param tags
	 */
	public HtmlValidator(Queue<HtmlTag> tags) {
		if (tags!=null) {
			this.size = tags.size();
			this.tags = passTags(tags);
		}else {
			throw new IllegalArgumentException("Given que of tags is null");
		}
	}
	
	/**
	 * Since we need to do operations, such as remove, to the queue, the queue will be destroyed afterwards
	 * and we will be unable to print the original queue. Therefore, we need to create a new method
	 * that stores the queue into a new queue so that we can get access to the original queue when 
	 * printing it.
	 */
	public Queue<HtmlTag> passTags(Queue<HtmlTag> tags){
		Queue<HtmlTag> originalTags = new LinkedList<HtmlTag>();
		for(int i=0;i<size;i++) {
			HtmlTag element = tags.remove();
			originalTags.add(element);
			tags.add(element);    //make sure that the queue passed as parameter is not destroyed
		}
		return originalTags;
	}
	
	/**
	 * This method will add new tags to the queue
	 * Throws IllegalArgumentException
	 */
	public void addTag(HtmlTag tag) {
		if(tag!=null) {
			this.tags.add(tag);
			this.size = this.tags.size();
		}else {
			throw new IllegalArgumentException("The gieven tag is null");
		}
	}
	
	/**
	 * A getter that will return the queue of tags stored in this object
	 */
	public Queue<HtmlTag> getTags(){
		return passTags(this.tags);
	}
	
	/**
	 * This method remove any tags that match the given element
	 * Throws IllegalArgumentException
	 */
	public void removeAll(String element) {
		if (element!=null) {
			for(int i=0;i<size;i++) {
				HtmlTag tag = tags.remove();
				if(!tag.getElement().equals(element)) {
					tags.add(tag);
				}
			}
		}
		this.size=tags.size();
	}
	
	/**
	 *  This method will print indented text representation of the HTML tags in the queue
	 */
	public void validate() {
	    int indent = 0;
		//Create a stack that stores the tags that are open 
	    Stack<HtmlTag> openTags = new Stack<HtmlTag>();
	    for(int i = 0 ; i < size; i++) {          
	       HtmlTag tag = tags.remove();
	       //When an unexpected tag is detected
	       if((!tag.isOpenTag() && openTags.isEmpty())||(!openTags.isEmpty() && !tag.matches(openTags.peek()) && !tag.isOpenTag()) ) { 
	             System.out.println("ERROR unexpected tag: " + tag.toString());
	             indent++;
	             
	       }else {
	          //print the indent
	          for(int n = 0; n < indent; n++) {
	                System.out.print("    ");
	          }
	          //print the current tag
	          System.out.println(tag.toString());
	          //Determines whether this tag is open (excluding self-closing tags)
	          if(tag.isOpenTag() && !tag.isSelfClosing()) { 
	        	 //Store the open tag into the stack of open tags
	             openTags.push(tag);
	             if(!tags.peek().matches(tag)) {
	                indent++;
	             }
	          }else if( (!tags.peek().isOpenTag()&&!tag.isOpenTag()) ||(!openTags.isEmpty()&&!tag.isOpenTag())) {
	             openTags.pop();
	          }
	       }
	       
	       if(!tags.isEmpty() && !tags.peek().isOpenTag() && !tags.peek().matches(tag)) {
	          indent--;
	       } 
	       tags.add(tag);   
	    }
	    
	    
	    //Print out the error that a tag is not closed
	    while(!openTags.isEmpty()) {
	       System.out.println("ERROR unclosed tag: " + openTags.pop().toString());
	    }
	    
  }
	
	  
		   
}
