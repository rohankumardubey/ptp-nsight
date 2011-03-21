//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.15 at 06:57:07 PM CDT 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for script complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="script">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="directive" type="{http://org.eclipse.ptp/rm}directive-definition" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="variable" type="{http://org.eclipse.ptp/rm}environment-variable" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pre-execute-command" type="{http://org.eclipse.ptp/rm}arglist" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="execute-command" type="{http://org.eclipse.ptp/rm}arglist"/>
 *         &lt;element name="post-execute-command" type="{http://org.eclipse.ptp/rm}arglist" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="shellDirective" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "script", propOrder = { "directive", "variable", "preExecuteCommand", "executeCommand", "postExecuteCommand" })
public class Script {

	protected List<DirectiveDefinition> directive;
	protected List<EnvironmentVariable> variable;
	@XmlElement(name = "pre-execute-command")
	protected List<Arglist> preExecuteCommand;
	@XmlElement(name = "execute-command", required = true)
	protected Arglist executeCommand;
	@XmlElement(name = "post-execute-command")
	protected List<Arglist> postExecuteCommand;
	@XmlAttribute(required = true)
	protected String shellDirective;

	/**
	 * Gets the value of the directive property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the directive property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDirective().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link DirectiveDefinition }
	 * 
	 * 
	 */
	public List<DirectiveDefinition> getDirective() {
		if (directive == null) {
			directive = new ArrayList<DirectiveDefinition>();
		}
		return this.directive;
	}

	/**
	 * Gets the value of the executeCommand property.
	 * 
	 * @return possible object is {@link Arglist }
	 * 
	 */
	public Arglist getExecuteCommand() {
		return executeCommand;
	}

	/**
	 * Gets the value of the postExecuteCommand property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the postExecuteCommand property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getPostExecuteCommand().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Arglist }
	 * 
	 * 
	 */
	public List<Arglist> getPostExecuteCommand() {
		if (postExecuteCommand == null) {
			postExecuteCommand = new ArrayList<Arglist>();
		}
		return this.postExecuteCommand;
	}

	/**
	 * Gets the value of the preExecuteCommand property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the preExecuteCommand property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getPreExecuteCommand().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Arglist }
	 * 
	 * 
	 */
	public List<Arglist> getPreExecuteCommand() {
		if (preExecuteCommand == null) {
			preExecuteCommand = new ArrayList<Arglist>();
		}
		return this.preExecuteCommand;
	}

	/**
	 * Gets the value of the shellDirective property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getShellDirective() {
		return shellDirective;
	}

	/**
	 * Gets the value of the variable property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the variable property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getVariable().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link EnvironmentVariable }
	 * 
	 * 
	 */
	public List<EnvironmentVariable> getVariable() {
		if (variable == null) {
			variable = new ArrayList<EnvironmentVariable>();
		}
		return this.variable;
	}

	/**
	 * Sets the value of the executeCommand property.
	 * 
	 * @param value
	 *            allowed object is {@link Arglist }
	 * 
	 */
	public void setExecuteCommand(Arglist value) {
		this.executeCommand = value;
	}

	/**
	 * Sets the value of the shellDirective property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setShellDirective(String value) {
		this.shellDirective = value;
	}

}