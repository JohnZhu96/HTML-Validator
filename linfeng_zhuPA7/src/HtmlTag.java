/*
 * Brandeis COSI 12b
 * PA7 - HTML Validator
 * HtmlTag class
 *
 * An HtmlTag object represents an HTML tag, such as <b> or </table>.
 *
 * @version 07/30/2021
 * @author BrandeisCOSI
 *
 * @version 08/08/2022
 * @author Linfeng Zhu
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.text.html.HTML.Tag;

import java.util.*;

public class HtmlTag {
    // fields
	private String element;
	private boolean isOpenTag;


    /** Constructs an HTML "opening" tag with the given element (e.g. "table").
      * Throws a NullPointerException if element is null. */
    public HtmlTag(String element) throws NullPointerException{
       this(element,true);
    }

    /** Constructs an HTML tag with the given element (e.g. "table") and type.
      * Self-closing tags like <br /> are considered to be "opening" tags,
      * but return false from the requiresClosingTag method.
      * Throws a NullPointerException if element is null. */
    public HtmlTag(String element, boolean isOpenTag) throws NullPointerException{
        this.element = element.toLowerCase();
        this.isOpenTag = isOpenTag;
    }

    /** Returns true if this tag has the same element and type as the given other tag. */
    public boolean equals(Object o) {
        if(o instanceof HtmlTag) {
        	HtmlTag newTag = (HtmlTag) o;
        	return this.element==newTag.element && this.isOpenTag==newTag.isOpenTag;
        }
        return false;
    }

    /** Returns this HTML tag's element, such as "table" or "p". */
    public String getElement() {
        return this.element;
    }

    /** Returns true if this HTML tag is an "opening" (starting) tag and false
      * if it is a closing tag.
      * Self-closing tags like <br /> are considered to be "opening" tags. */
    public boolean isOpenTag() {
        return this.isOpenTag;
    }

    /** Returns true if the given other tag is non-null and matches this tag;
      * that is, if they have the same element but opposite types,
      * such as <body> and </body>. */
    public boolean matches(HtmlTag other) {
        if (other!=null) {
        	return this.element==other.element && this.isOpenTag != other.isOpenTag;
        }
        return false;
    }

    /** Returns true if this tag does not requires a matching closing tag,
      * which is the case for certain elements such as br and img. */
    public boolean isSelfClosing() {
        return SELF_CLOSING_TAGS.contains(this.element);
    }

    /** Returns a string representation of this HTML tag, such as "</table>". */
    public String toString() {
        if (this.isOpenTag()||this.isSelfClosing()) {
        	if (this.element.equals("!--")) {
        		return "<" + this.element + " --" + ">";  //if the element is comment, return "!-- --"
        	}
        	return "<" + this.element + ">";  // return open tags
        }else {
        	return "<" + "/" + this.element + ">";  //return close tags
        }
    }



    // a set of tags that don't need to be matched (self-closing)
    private static final Set<String> SELF_CLOSING_TAGS = new HashSet<String>(
            Arrays.asList("!doctype", "!--", "area", "base", "basefont",
                          "br", "col", "frame", "hr", "img", "input",
                          "link", "meta", "param"));

    // all whitespace characters; used in text parsing
    private static final String WHITESPACE = " \f\n\r\t";

    /** Reads a string such as "<table>" or "</p>" and converts it into an HtmlTag,
      * which is returned.
      * Throws a NullPointerException if tagText is null. */
    public static HtmlTag parse(String tagText) throws NullPointerException{
    	boolean isOpenTag = !tagText.contains("</"); //determine if the tag is an open tag or not
    	String tagElement = tagText.replaceAll("[^a-zA-Z']+", "");
    	if (tagElement.equals("!-- --")) {
    		tagElement = "!--";
    	}
    	return new HtmlTag(tagElement,isOpenTag);
    }

    /** Reads the file or URL given, and tokenizes the text in that file,
      * placing the tokens into the given Queue.
      * You don't need to call this method in your homework code.
      * Precondition: text != null */
    public static LinkedList<HtmlTag> tokenize(String text) {
        LinkedList<HtmlTag> tagQueue = new LinkedList<HtmlTag>();
        StringBuffer buf =  new StringBuffer(text);
        HtmlTag nextTag = nextTag(buf);
        while(nextTag!=null) {
        	tagQueue.add(nextTag);
        	nextTag = nextTag(buf);
        }
        return tagQueue;
    }

    /**
     * advances to next tag in input;
     * not a perfect HTML tag tokenizer, but it will do for this PA
     */
    private static HtmlTag nextTag(StringBuffer buf) {
        int index1 = buf.indexOf("<");
        int index2 = buf.indexOf(">");

        if (index1 >= 0 && index2 > index1) {
            // check for HTML comments: <!-- -->
            if (index1 + 4 <= buf.length() && buf.substring(index1 + 1, index1 + 4).equals("!--")) {
                // a comment; look for closing comment tag -->
                index2 = buf.indexOf("-->", index1 + 4);
                if (index2 < 0) {
                    return null;
                } else {
                    buf.insert(index1 + 4, " ");    // fixes things like <!--hi-->
                    index2 += 3;    // advance to the closing >
                }
            }

            String element = buf.substring(index1 + 1, index2).trim();

            // remove attributes
            for (int i = 0; i < WHITESPACE.length(); i++) {
                int index3 = element.indexOf(WHITESPACE.charAt(i));
                if (index3 >= 0) {
                    element = element.substring(0, index3);
                }
            }

            // determine whether opening or closing tag
            boolean isOpenTag = true;
            if (element.indexOf("/") == 0) {
                isOpenTag = false;
                element = element.substring(1);
            }
            element = element.replaceAll("[^a-zA-Z0-9!-]+", "");

            buf.delete(0, index2 + 1);
            return new HtmlTag(element, isOpenTag);
        } else {
            return null;
        }
    }
}
