//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.27 at 02:54:53 PM CDT 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * These are attached to the stdout or stderr streams in order to capture the
 * output of the command and add values into the resource manager environment.
 * If displayStdout or displayStderr of the command is true, the stream will be
 * passed on to an output stream will also be sent to the terminal.
 * 
 * 
 * <p>
 * Java class for tokenizer complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="tokenizer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="target" type="{http://org.eclipse.ptp/rm}target" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="all" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="applyToAll" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="delim" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="includeDelim" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="maxMatchLen" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *       &lt;attribute name="save" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tokenizer", propOrder = { "type", "target" })
public class Tokenizer {

	protected String type;
	protected List<Target> target;
	@XmlAttribute
	protected Boolean all;
	@XmlAttribute
	protected Boolean applyToAll;
	@XmlAttribute
	protected String delim;
	@XmlAttribute
	protected Boolean includeDelim;
	@XmlAttribute
	protected Integer maxMatchLen;
	@XmlAttribute
	protected Integer save;

	/**
	 * Gets the value of the delim property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDelim() {
		return delim;
	}

	/**
	 * Gets the value of the maxMatchLen property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public int getMaxMatchLen() {
		if (maxMatchLen == null) {
			return 0;
		} else {
			return maxMatchLen;
		}
	}

	/**
	 * Gets the value of the save property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public int getSave() {
		if (save == null) {
			return 0;
		} else {
			return save;
		}
	}

	/**
	 * Gets the value of the target property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the target property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getTarget().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Target }
	 * 
	 * 
	 */
	public List<Target> getTarget() {
		if (target == null) {
			target = new ArrayList<Target>();
		}
		return this.target;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the value of the all property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isAll() {
		if (all == null) {
			return false;
		} else {
			return all;
		}
	}

	/**
	 * Gets the value of the applyToAll property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isApplyToAll() {
		if (applyToAll == null) {
			return false;
		} else {
			return applyToAll;
		}
	}

	/**
	 * Gets the value of the includeDelim property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isIncludeDelim() {
		if (includeDelim == null) {
			return false;
		} else {
			return includeDelim;
		}
	}

	/**
	 * Sets the value of the all property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setAll(Boolean value) {
		this.all = value;
	}

	/**
	 * Sets the value of the applyToAll property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setApplyToAll(Boolean value) {
		this.applyToAll = value;
	}

	/**
	 * Sets the value of the delim property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDelim(String value) {
		this.delim = value;
	}

	/**
	 * Sets the value of the includeDelim property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setIncludeDelim(Boolean value) {
		this.includeDelim = value;
	}

	/**
	 * Sets the value of the maxMatchLen property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMaxMatchLen(Integer value) {
		this.maxMatchLen = value;
	}

	/**
	 * Sets the value of the save property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setSave(Integer value) {
		this.save = value;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value) {
		this.type = value;
	}

}