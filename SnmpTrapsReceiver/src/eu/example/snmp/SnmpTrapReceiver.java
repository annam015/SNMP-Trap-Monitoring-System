package eu.example.snmp;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.PDU;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

public class SnmpTrapReceiver {

	public static void main(String[] args) {
		try {
			UdpAddress listenAddress = new UdpAddress("0.0.0.0/162");
			TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping(listenAddress);
			Snmp snmp = new Snmp(transport);
			transport.listen();

			MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
			MongoDatabase database = mongoClient.getDatabase("snmp");
			MongoCollection<Document> trapsCollection = database.getCollection("traps");

			CommandResponder trapHandler = new CommandResponder() {
				@Override
				public void processPdu(CommandResponderEvent event) {
					System.out.println("Trap Received!");
					PDU pdu = event.getPDU();
					if (pdu != null) {
						try {
							Document trapDocument = new Document();
							trapDocument.append("sourceIP", event.getPeerAddress().toString()).append("timestamp",
									new Date());
							List<? extends VariableBinding> varBinds = pdu.getVariableBindings();
							for (VariableBinding vb : varBinds) {
								String oid = vb.getOid().toString();
								String value = vb.getVariable().toString();
								switch (oid) {
								case "1.3.6.1.2.1.1.5":
									trapDocument.append("sysName", value);
									break;
								case "1.3.6.1.4.1.2021.11.10":
									trapDocument.append("cpuUsage", value);
									break;
								case "1.3.6.1.4.1.2021.4.6":
									trapDocument.append("freeMemory", value);
									break;
								}
							}

							trapsCollection.insertOne(trapDocument);
							System.out.println("Trap inserted into MongoDB: " + trapDocument.toJson());
						} catch (Exception e) {
							System.err.println("Error inserting trap into MongoDB: " + e.getMessage());
							e.printStackTrace();
						}
					}
				}
			};

			snmp.addCommandResponder(trapHandler);
			System.out.println("SNMP Trap Receiver is listening on " + transport.getListenAddress());
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				System.out.println("\n\nType 'Q' for closing the SNMP Trap Receiver!\n\n");
				try {
					String input = reader.readLine();
					if ("Q".equalsIgnoreCase(input.trim())) {
						break;
					}
				} catch (IOException ioe) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}