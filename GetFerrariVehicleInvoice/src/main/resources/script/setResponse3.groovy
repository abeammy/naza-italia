import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
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

	Element elmGetVehicleInvoiceResponse = docMappedResponse.createElementNS(
			"http://sales.invoice.naza.com/GetFerrariVehicleInvoice/",
			"GetVehicleInvoiceResponse");
	elmGetVehicleInvoiceResponse.setPrefix("n0");
	docMappedResponse.appendChild(elmGetVehicleInvoiceResponse);

	// get FerrariVehicleInvoice, 0..*
	NodeList nodesFerrariVehicleInvoice = docFerrariResponse.getElementsByTagNameNS(
			"http://www.starstandard.org/STAR/5",
			"FerrariVehicleInvoice");
	// Zero or more
	if (nodesFerrariVehicleInvoice != null && nodesFerrariVehicleInvoice.getLength() > 0) {

		for (int i = 0; i < nodesFerrariVehicleInvoice.getLength(); i++) {

			Element elmFerrariVehicleInvoice = (Element)nodesFerrariVehicleInvoice.item(i);
			Element elmImportedFerrariVehicleInvoice = (Element) docMappedResponse.importNode(elmFerrariVehicleInvoice, true);
			elmGetVehicleInvoiceResponse.appendChild(elmImportedFerrariVehicleInvoice);

			// remove namespaces

			NodeList nodesElmFerrariVehicleInvoiceMapped = elmGetVehicleInvoiceResponse.getChildNodes();
			for (int j = 0; j < nodesElmFerrariVehicleInvoiceMapped.getLength(); j++) {
				// FerrariVehicleInvoice element
				Element tmpElement = (Element) nodesElmFerrariVehicleInvoiceMapped.item(j);
				if (tmpElement.getNodeType() == Node.ELEMENT_NODE) {
					docMappedResponse.renameNode(tmpElement, "", tmpElement.getLocalName());
					renameNode(docMappedResponse, tmpElement);
				}
			}
		}
	}

	message.setBody(elmGetVehicleInvoiceResponse);

	def messageLog = messageLogFactory.getMessageLog(message);
	if(messageLog != null){
		messageLog.addAttachmentAsString("Ferrari Response", respBody, "text/xml");
	}
	return message;
}

def renameNode(Document doc, Element elm)
{
	if (elm.hasChildNodes()) {
		NodeList tmpNodeList = elm.getChildNodes();
		for (int k = 0; k < tmpNodeList.getLength(); k++) {
			Node tmpElementK = tmpNodeList.item(k);
			if (tmpElementK.getNodeType() == Node.ELEMENT_NODE) {
				doc.renameNode(tmpElementK, "", tmpElementK.getLocalName());
				// check next level if exist
				if (tmpElementK.hasChildNodes()){
					renameNode(doc, tmpElementK);
				}
			}
		}
	}
}

