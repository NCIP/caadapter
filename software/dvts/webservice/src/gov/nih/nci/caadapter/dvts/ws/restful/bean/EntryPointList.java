/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws.restful.bean;



import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 26, 2011
 * Time: 11:23:05 AM
 * To change this template use File | Settings | File Templates.
 */

@XmlRootElement(name="entryPoints")
public class EntryPointList {

	private int count;
	private List<EntryPoint> employees;

	public EntryPointList() {}

	public EntryPointList(List<EntryPoint> employees) {
		this.employees = employees;
		this.count = employees.size();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@XmlElement(name="employee")
	public List<EntryPoint> getEmployees() {
		return employees;
	}
	public void setEmployees(List<EntryPoint> employees) {
		this.employees = employees;
	}

}
