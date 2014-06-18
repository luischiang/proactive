package org.sdnhub.proactive;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opendaylight.controller.sal.core.ConstructionException;
import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.controller.sal.match.Match;
import org.opendaylight.controller.sal.match.MatchField;
import org.opendaylight.controller.sal.match.MatchType;
import org.opendaylight.controller.sal.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ListMatch {
	
	protected static final Logger log = LoggerFactory.getLogger(ListMatch.class);
	
	private String DL_SRC = "", DL_DST = "";
	
	private String NW_DST = "", NW_SRC = "";
	
	private short TP_DST, DL_TYPE, TP_SRC;
	
	private byte NW_PROTO;
	
	private short IN_PORT = 0;

	public String getNW_DST() {
		return NW_DST;
	}

	public void setNW_DST(String nW_DST) {
		NW_DST = nW_DST;
	}

	public short getTP_DST() {
		return TP_DST;
	}

	public void setTP_DST(short tP_DST) {
		TP_DST = tP_DST;
	}

	public short getTP_SRC() {
		return TP_SRC;
	}

	public void setTP_SRC(short tP_SRC) {
		TP_SRC = tP_SRC;
	}

	public byte getNW_PROTO() {
		return NW_PROTO;
	}

	public void setNW_PROTO(byte nW_PROTO) {
		NW_PROTO = nW_PROTO;
	}

	public short getIN_PORT() {
		return IN_PORT;
	}

	public void setIN_PORT(short iN_PORT) {
		IN_PORT = iN_PORT;
	}

	public String getNW_SRC() {
		return NW_SRC;
	}

	public void setNW_SRC(String nW_SRC) {
		NW_SRC = nW_SRC;
	}

	public short getDL_TYPE() {
		return DL_TYPE;
	}

	public void setDL_TYPE(short dL_TYPE) {
		DL_TYPE = dL_TYPE;
	}
	 
	public Match joinMatchFields(Node node){
		
		Match match = new Match();
		
		if (DL_SRC.length() > 0){
			match.setField( new MatchField(MatchType.DL_SRC, parseMacAddress(DL_SRC) ));
		}
		
		if (DL_DST.length() > 0){
			match.setField( new MatchField(MatchType.DL_DST, parseMacAddress(DL_DST) ));
		}
		
		try {
			
			if ( NW_DST.length() > 0){
				
					match.setField( new MatchField(MatchType.NW_DST, InetAddress.getByName(NW_DST)));
			}
			
			if ( NW_SRC.length() > 0){
				
				match.setField( new MatchField(MatchType.NW_SRC, InetAddress.getByName(NW_SRC)));
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if(TP_SRC > 0){
			match.setField( new MatchField(MatchType.TP_SRC, TP_SRC) );
		}
		
		if(TP_DST > 0){
			match.setField( new MatchField(MatchType.TP_DST, TP_DST ));
		}
		
		if(DL_TYPE > 0){
			match.setField( new MatchField(MatchType.DL_TYPE, DL_TYPE));
		}
		
		if(NW_PROTO > 0){
			match.setField( new MatchField(MatchType.NW_PROTO, NW_PROTO) ); 
		}
		
		if(IN_PORT > 0 ){
			try {
				
				match.setField( new MatchField(MatchType.IN_PORT, new NodeConnector("OF", IN_PORT, node) ));
				
				
			} catch (ConstructionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return match;
	}

	public String getDL_SRC() {
		return DL_SRC;
	}

	public void setDL_SRC(String dL_SRC) {
		DL_SRC = dL_SRC;
	}

	public String getDL_DST() {
		return DL_DST;
	}

	public void setDL_DST(String dL_DST) {
		DL_DST = dL_DST;
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
