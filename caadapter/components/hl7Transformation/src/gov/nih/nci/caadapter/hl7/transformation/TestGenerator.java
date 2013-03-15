/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.transformation;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;

public class TestGenerator {

	public MIFClass mifClass = new MIFClass();
    private Hashtable datatypes = new Hashtable();

	public void saveMIFs(String fileName) throws Exception {
		OutputStream os = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(mifClass);
		oos.close();
		os.close();
	}

	public MIFClass loadMIF(String mifFileName) {
		try {
			InputStream is = this.getClass().getResourceAsStream("/mif/" + mifFileName);
			ObjectInputStream ois = new ObjectInputStream(is);
			mifClass = (MIFClass)ois.readObject();
			ois.close();
			is.close();
			return mifClass;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setupDatatype(Datatype datatype) {
		System.out.println("--------datatype----:"+datatype.getName()+" is simple " + datatype.isSimple());
		if (datatype.isSimple()) {
			datatype.setEnabled(true);
		}
		else {
			Hashtable attrs = datatype.getAttributes();
			Iterator it = attrs.keySet().iterator();

			while (it.hasNext()) {
				String name = (String)it.next();
				Attribute attribute = (Attribute)attrs.get(name);
				System.out.println("    " + attribute.getName()+ "-" + attribute.getType() + " "+attribute.isSimple());
				if (attribute.getName().equalsIgnoreCase("translation")) continue;
				if (attribute.getName().equalsIgnoreCase("reference")) continue;
				if (attribute.getName().equalsIgnoreCase("originalText")) continue;
				if (attribute.getName().equalsIgnoreCase("qualifier")) continue;
				if (attribute.getName().equalsIgnoreCase(attribute.getType())) continue;
				Datatype dt = (Datatype)((Datatype)datatypes.get(attribute.getType())).clone();
				setupDatatype(dt);
				attribute.setReferenceDatatype(dt);
			}
			if (datatype.getName().startsWith("adxp.")||datatype.getName().startsWith("en.")||datatype.getName().equals("ON")) {
				Attribute attr = new Attribute();
				attr.setName("inlineText");
				attr.setType("cs");
				attr.setSimple(true);
				datatype.addAttribute("inlineText", attr);
			}
			datatype.setEnabled(true);
		}
	}
	public void setup(MIFClass mifClass, String parentXmlPath) {
		for(MIFAttribute mifAttribute:mifClass.getAttributes()) {
			Datatype dt = (Datatype)((Datatype)datatypes.get(mifAttribute.getType())).clone();
			System.out.println("===================Attribute:"+mifAttribute.getName());
			setupDatatype(dt);
			dt.setEnabled(true);
			mifAttribute.setDatatype(dt);
			mifAttribute.setParentXmlPath(parentXmlPath);
		}

		for(MIFAssociation mifAsso:mifClass.getAssociations()) {
			if (mifAsso.getMifClass()!= null) setup(mifAsso.getMifClass(),parentXmlPath+"."+mifAsso.getName());
		}
	}

	public void loadDatatypes() {
		try {
			InputStream is = this.getClass().getResourceAsStream("/datatypes");
			ObjectInputStream ois = new ObjectInputStream(is);
			datatypes = (Hashtable)ois.readObject();
			ois.close();
			is.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		TestGenerator tg = new TestGenerator();
		tg.loadMIF("COCT_MT150003UV03.mif");
		tg.loadDatatypes();
		tg.setup(tg.mifClass,tg.mifClass.getName());
		tg.saveMIFs("C:/projects/caadapter-gforge-2007-May/tests/150003-1.h3s");
	}

}
