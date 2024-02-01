package eu.example.snmp;

import java.io.IOException;
import java.net.InetAddress;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.mp.SnmpConstants;

public class SnmpTrapSender {
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Snmp snmp = null;
		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			Address targetAddress = GenericAddress.parse("172.17.0.2/162");
			CommunityTarget target = new CommunityTarget();
			target.setCommunity(new OctetString("public"));
			target.setAddress(targetAddress);
			target.setVersion(SnmpConstants.version2c);

			while (true) {
				String hostname = InetAddress.getLocalHost().getHostName();
				OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
				double cpuLoad = osBean.getSystemCpuLoad() * 100;
				long freeMemory = osBean.getFreePhysicalMemorySize();

				PDU pdu = new PDU();
				pdu.setType(PDU.TRAP);
				pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5"), new OctetString(hostname)));
				pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.2021.11.10"),
						new OctetString(String.valueOf(cpuLoad))));
				pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.2021.4.6"),
						new OctetString(String.valueOf(freeMemory))));

				snmp.send(pdu, target);
				System.out.println("SNMP Trap Sent!");
				Thread.sleep(180000);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("SnmpTrapSender interrupted: " + e.getMessage());
			Thread.currentThread().interrupt();
		} finally {
			try {
				snmp.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}