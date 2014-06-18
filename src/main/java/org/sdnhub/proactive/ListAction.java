package org.sdnhub.proactive;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opendaylight.controller.sal.action.Action;
import org.opendaylight.controller.sal.action.Controller;
import org.opendaylight.controller.sal.action.Output;
import org.opendaylight.controller.sal.action.SetDlDst;
import org.opendaylight.controller.sal.action.SetDlSrc;
import org.opendaylight.controller.sal.action.SetNwDst;
import org.opendaylight.controller.sal.action.SetNwSrc;
import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ListAction {

	protected static final Logger log = LoggerFactory.getLogger(ListAction.class);
	
	// format "00:d0:04:d6:14:00"
	private String dlsrc = null;
	private String dldst = null;
	
	private String nwsrc = null;
	private String nwdst = null;
	
	private Controller controller = null;
	private short outputPort = 0;
	
	public short getOutputPort() {
		return outputPort;
	}
	public void setOutputPort(short outputPort) {
		this.outputPort = outputPort;
	}
	public String getDlsrc() {
		return dlsrc;
	}
	public void setDlsrc(String dlsrc) {
		this.dlsrc = dlsrc;
	}
	public String getDldst() {
		return dldst;
	}
	public void setDldst(String dldst) {
		this.dldst = dldst;
	}
	 
	public String getNwsrc() {
		return nwsrc;
	}
	public void setNwsrc(String nwsrc) {
		this.nwsrc = nwsrc;
	}
	 
	public String getNwdst() {
		return nwdst;
	}
	public void setNwdst(String nwdst) {
		this.nwdst = nwdst;
	}
	
	public List<Action> joinActionList(Node node) {
		
		
		List<Action> tmp = new ArrayList<Action>();
		
		if(dlsrc != null){ 
			tmp.add( new SetDlSrc(parseMacAddress(dlsrc) ) );
		}
		
		if(dldst != null){ 
			tmp.add( new SetDlDst(parseMacAddress(dldst) ) );
		}
		
		try {
			if(nwsrc != null){
				
					tmp.add(new SetNwSrc(  InetAddress.getByName(nwsrc)  ));
				
			}
			
			if(nwdst != null){ 
				tmp.add(new SetNwDst(  InetAddress.getByName(nwdst)  )); 
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(controller != null){ tmp.add(controller); }
		
		if(outputPort > 0 ){
			try {
				tmp.add( new Output(new NodeConnector("OF", (short)outputPort, node)) );
			} catch (ConstructionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		return tmp;
	}
	 
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	//MacAddress format intput, 00:d0:04:d6:14:00
	private byte[] parseMacAddress(String MacAddress){
		
		byte[] output = new byte[NetUtils.MACAddrLengthInBytes];
		
		try{
			int cnt = 0;
			for (String part : MacAddress.split(":")){
				
				BigInteger temp = new BigInteger(part, 16);
	            byte[] raw = temp.toByteArray();
	            
	            output[cnt] = raw[raw.length - 1];
				cnt = cnt + 1;
			}
		}catch(Exception e){
			
			log.error("failed parsing mac " + MacAddress);
			return null;
		}
		
		return output;
	}
	
}
