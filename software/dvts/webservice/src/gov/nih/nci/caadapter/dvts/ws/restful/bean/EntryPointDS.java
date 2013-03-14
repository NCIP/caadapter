/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws.restful.bean;

import java.util.*;

public class EntryPointDS {

    private static Map<Long, EntryPoint> allEntryPoints;
    static {
        allEntryPoints = new HashMap<Long, EntryPoint>();
        EntryPoint e1 = new EntryPoint(1L, "Entry Point Sample 1", "huangy@ibm.com");
        EntryPoint e2 = new EntryPoint(2L, "Entry Point Sample 2", "wudong@ibm.com");
        EntryPoint e3 = new EntryPoint(3L, "Ki Sung Um", "abcdefg@hanmail.net");
        allEntryPoints.put(e1.getId(), e1);
        allEntryPoints.put(e2.getId(), e2);
        allEntryPoints.put(e3.getId(), e3);
    }

    //private String tag = "";

    public void add(EntryPoint e) {
        allEntryPoints.put(e.getId(), e);
    }

    public EntryPoint get(long id, String tg) {

        if ((tg != null)&&(!tg.trim().equals("")))
        {
            //System.out.println("CCC tag=" + tg + ", id=" + id);
            return allEntryPoints.get(3L);
        }
        return allEntryPoints.get(id);
    }


    public List<EntryPoint> getAll() {
        List<EntryPoint> EntryPoints = new ArrayList<EntryPoint>();
        for( Iterator<EntryPoint> it = allEntryPoints.values().iterator(); it.hasNext(); ) {
            EntryPoint e = it.next();
            EntryPoints.add(e);
        }
        return EntryPoints;
    }

    public void remove(long id) {
        allEntryPoints.remove(id);
    }

    public void update(EntryPoint e) {
        allEntryPoints.put(e.getId(), e);
    }


}
