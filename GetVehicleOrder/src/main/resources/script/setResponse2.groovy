import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

import com.sap.gateway.ip.core.customdev.util.Message;

def Message processData(Message message) {

	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
	dbf.setIgnoringElementContentWhitespace(true);
	dbf.setValidating(false);
	DocumentBuilder db = null;
	try {
		db = dbf.newDocumentBuilder();
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	// response from Ferrari
	def respBody = message.getBody(java.lang.String) as String;
	InputSource is = new InputSource();
	is.setCharacterStream(new StringReader(respBody));
	Document docFerrariResponse = db.parse(is);

	// for response mapping
	Document docMappedResponse = db.newDocument();

	Element getVehicleOrderResponse = docMappedResponse.createElementNS(
			"http://sales.vehicle.order.naza.com/GetVehicleOrder",
			"GetVehicleOrderResponse");
	docMappedResponse.appendChild(getVehicleOrderResponse);

	// get FerrariVehicleOrder, 0..*
	NodeList nodesFerrariVehicleOrder = docFerrariResponse.getElementsByTagNameNS(
			"http://www.starstandard.org/STAR/5",
			"FerrariVehicleOrder");
	// Zero or more
	if (nodesFerrariVehicleOrder != null && nodesFerrariVehicleOrder.getLength() > 0) {

		for (int i = 0; i < nodesFerrariVehicleOrder.getLength(); i++) {

			// Get FerrariVehicleOrder
			Element elmFerrariVehicleOrder = (Element) nodesFerrariVehicleOrder.item(i);
			// Import the node FerrariVehicleOrder
			Element elmImportedFerrariVehicleOrder = (Element) docMappedResponse.importNode(elmFerrariVehicleOrder, true);
			// append FerrariVehicleOrder to the mapped Response
			getVehicleOrderResponse.appendChild(elmImportedFerrariVehicleOrder);

		}
	}

	message.setBody(getVehicleOrderResponse);

	def messageLog = messageLogFactory.getMessageLog(message);
	if(messageLog != null){
		messageLog.addAttachmentAsString("Ferrari Response", respBody, "text/xml");
	}
	return message;
}

