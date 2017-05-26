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

	Element getVehicleInvoiceResponse = docMappedResponse.createElementNS(
			"http://crm.sales.naza.com/GetVehicleInvoice",
			"GetVehicleInvoiceResponse");
	docMappedResponse.appendChild(getVehicleInvoiceResponse);

	// get FerrariVehicleInvoice, 0..*
	NodeList nodesFerrariVehicleInvoice = docFerrariResponse.getElementsByTagNameNS(
			"http://www.starstandard.org/STAR/5",
			"FerrariVehicleInvoice");
	// Zero or more
	if (nodesFerrariVehicleInvoice != null && nodesFerrariVehicleInvoice.getLength() > 0) {

		for (int i = 0; i < nodesFerrariVehicleInvoice.getLength(); i++) {
			// Create element FerrariVehicleInvoice
			Element elmFerrariVehicleInvoiceMapped = docMappedResponse.createElement("FerrariVehicleInvoice");
			getVehicleInvoiceResponse.appendChild(elmFerrariVehicleInvoiceMapped);

			// Get Value of FerrariVehicleInvoice, 0..* FerrariInvoice
			Element elmFerrariVehicleInvoice = (Element) nodesFerrariVehicleInvoice.item(i);
			NodeList nodesFerrariInvoice =  elmFerrariVehicleInvoice.getElementsByTagNameNS("http://www.starstandard.org/STAR/5",
					"FerrariInvoice");
			// Loop through all FerrariInvoice and add to elmFerrariVehicleInvoiceMapped
			for (j = 0; j < nodesFerrariInvoice.length; j++){

				Element elmFerrariInvoice = (Element)nodesFerrariInvoice.item(j);
				Element elmImportedFerrariInvoice = (Element) docMappedResponse.importNode(elmFerrariInvoice, true);

				elmFerrariVehicleInvoiceMapped.appendChild(elmImportedFerrariInvoice);
			}
		}
	}

	message.setBody(getVehicleInvoiceResponse);

	def messageLog = messageLogFactory.getMessageLog(message);
	if(messageLog != null){
		messageLog.addAttachmentAsString("Ferrari Response", respBody, "text/xml");
	}
	return message;
}

