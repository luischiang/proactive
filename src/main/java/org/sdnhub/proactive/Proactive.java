package org.sdnhub.proactive;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.opendaylight.controller.sal.action.Controller;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.core.Property;
import org.opendaylight.controller.sal.core.UpdateType;
import org.opendaylight.controller.sal.flowprogrammer.Flow;
import org.opendaylight.controller.sal.flowprogrammer.IFlowProgrammerService;
import org.opendaylight.controller.sal.utils.EtherTypes;
import org.opendaylight.controller.sal.utils.GlobalConstants;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.switchmanager.IInventoryListener;
import org.opendaylight.controller.switchmanager.ISwitchManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Proactive implements IInventoryListener, IProactive{
 
	protected static final Logger log = LoggerFactory.getLogger(Proactive.class);

	private String ROOT = GlobalConstants.STARTUPHOME.toString();
	public String rulesetFile = ROOT + "proactive-ruleset.xml"; 
	
	private ListProgramFlow ruleset;
	
	// ------------------------------------
	private IFlowProgrammerService programmer = null;
	private ISwitchManager switchManager = null;
	
	public void setFlowProgrammerService(IFlowProgrammerService s)
    {
        this.programmer = s;
    }

    public void unsetFlowProgrammerService(IFlowProgrammerService s) {
        if (this.programmer == s) {
            this.programmer = null;
        }
    }
    
    void setSwitchManager(ISwitchManager s) {
    	log.debug("SwitchManager set");
        this.switchManager = s;
    }

    void unsetSwitchManager(ISwitchManager s) {
        if (this.switchManager == s) {
        	log.debug("SwitchManager removed!");
            this.switchManager = null;
        }
    }
    // ------------------------------------
    
    
	void init() {
		export_sample();
		readconfig();
	}


	private void readconfig(){
		
		File fconfig = new File(rulesetFile);
		
		if( fconfig.exists() == false || fconfig.canRead() == false){ 
			log.error("Could not open: " + rulesetFile);
			return;
		}
		 
		JAXBContext jaxbContext;
		try {
			
			jaxbContext = JAXBContext.newInstance(ListProgramFlow.class);
			
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			ruleset = (ListProgramFlow) jaxbUnmarshaller.unmarshal(fconfig);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void notifyNode(Node node, UpdateType type,
			Map<String, Property> propMap) {
		// TODO Auto-generated method stub
		
    	if (type == UpdateType.ADDED){
    		log.info("Sw added");
    		config_node(node);
    	}else if (type == UpdateType.REMOVED){
    		log.info("Sw removed");
    	}
    	
	}
	
	public void config_node(Node node){
		
		// find the rules related to the node, and install the rule
		
		for( ProgramFlow pflow : ruleset.rules){
			
			Node lnode = pflow.getNode();
			
			if (lnode.getNodeIDString().equals( node.getNodeIDString() )){
				
				Status status = programmer.addFlow(node, pflow.program());
		        log.info( "Node " + node.getNodeIDString() + " flow installed: " + status.isSuccess() );
			}
		}
	}
	
	@Override
	public void notifyNodeConnector(NodeConnector nodeConnector,
			UpdateType type, Map<String, Property> propMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String echo(String in) {
		// TODO Auto-generated method stub
		return "Echo " + in + " from Class";
	}
	
	public void export_sample(){
		
		// read config file
		
		Node node = null;
//		NodeConnector incoming_conn = null;
//		NodeConnector outgoing_conn = null;
		
		try {
			node = new Node("OF", 1L);
//			incoming_conn = new NodeConnector("OF", (short)1, node);
//			outgoing_conn = new NodeConnector("OF", (short)2, node);
			
		} catch (ConstructionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		File foutput = new File( ROOT + "example-ruleset.xml");

		ListMatch lmatch = new ListMatch();
		  
			//lmatch. ( "aa:aa:aa:aa:aa:aa");
			lmatch.setNW_SRC( "1.1.1.1" );
			lmatch.setNW_DST( "2.2.2.2" );
			lmatch.setTP_DST( (short)10);
			
			lmatch.setDL_TYPE( EtherTypes.IPv4.shortValue() );
			lmatch.setNW_PROTO( (byte)17 );
			
			lmatch.setTP_SRC( (short)53 );
			
			lmatch.setIN_PORT((short)1);
			 
	        ListAction laction = new ListAction();
	        
	        laction.setDlsrc(  "00:d0:04:d6:14:00" );
	        
	        laction.setNwsrc( "3.3.3.3" ); 
	        laction.setNwdst( "4.4.4.4" );
	        
	        laction.setOutputPort( (short)2 );
	        laction.setController( new Controller() );
	        
	        Flow f = new Flow();
	        f.setIdleTimeout((short)  50);
	        f.setHardTimeout((short)  50);
	        f.setPriority((short) 65535);

	        // Modify the flow on the network node
	        //Node incoming_node = incoming_connector.getNode();
	        
	        ProgramFlow pf = new ProgramFlow();
	        pf.setNode(node);
	        pf.setMatchs(lmatch);
	        pf.setActions(laction);
	        pf.setFlow(f);
	        
	        ListProgramFlow lpf = new ListProgramFlow();
	        
	        lpf.rules.add(pf);
	        lpf.rules.add(pf);
	        
		try {     
		    JAXBContext jaxbContext = JAXBContext.newInstance(ListProgramFlow.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	 
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			  
			jaxbMarshaller.marshal(lpf,foutput);
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			log.error( e.getMessage().toString());
		} 
		
	}
 
}
