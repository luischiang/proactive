package org.sdnhub.proactive;
 
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListProgramFlow {
	
	List<ProgramFlow> rules = new ArrayList<ProgramFlow>();

}
