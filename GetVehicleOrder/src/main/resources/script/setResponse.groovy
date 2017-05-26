import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

import com.sap.gateway.ip.core.customdev.util.Message;

def Message processData(Message message) {

	Map mapMessageProperty = message.getProperties();
	String strMessageGuid = mapMessageProperty.get("SAP_MessageProcessingLogID");

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

	Element elmGetVehicleOrderResponse = docMappedResponse.createElementNS(
			"http://sales.vehicle.order.naza.com/GetVehicleOrder/",
			"GetVehicleOrderResponse");
	elmGetVehicleOrderResponse.setPrefix("vo");
	docMappedResponse.appendChild(elmGetVehicleOrderResponse);

	Element elmOriginalApplicationArea = (Element) docFerrariResponse.getElementsByTagNameNS("http://www.starstandard.org/STAR/5", "OriginalApplicationArea").item(0);
	String strBODID = ((Element) elmOriginalApplicationArea.getElementsByTagNameNS("http://www.starstandard.org/STAR/5", "BODID").item(0)).getTextContent();

	Element elmMessageGuid = docMappedResponse.createElement("MessageGuid");
	elmMessageGuid.setTextContent(strMessageGuid);
	elmGetVehicleOrderResponse.appendChild(elmMessageGuid);

	Element elmBODID = docMappedResponse.createElement("BODID");
	elmBODID.setTextContent(strBODID);
	elmGetVehicleOrderResponse.appendChild(elmBODID);

	// get FerrariVehicleOrder, 0..*
	NodeList nodesFerrariVehicleOrder = docFerrariResponse.getElementsByTagNameNS(
			"http://www.starstandard.org/STAR/5",
			"FerrariVehicleOrder");
	// Zero or more
	if (nodesFerrariVehicleOrder != null && nodesFerrariVehicleOrder.getLength() > 0) {

		for (int i = 0; i < nodesFerrariVehicleOrder.getLength(); i++) {

			Element elmFerrariVehicleOrder = (Element)nodesFerrariVehicleOrder.item(i);
			Element elmImportedFerrariVehicleOrder = (Element) docMappedResponse.importNode(elmFerrariVehicleOrder, true);
			elmGetVehicleOrderResponse.appendChild(elmImportedFerrariVehicleOrder);

			// remove namespaces

			NodeList nodesElmFerrariVehicleOrderMapped = elmGetVehicleOrderResponse.getElementsByTagNameNS("http://www.starstandard.org/STAR/5",
					"FerrariVehicleOrder");
			for (int j = 0; j < nodesElmFerrariVehicleOrderMapped.getLength(); j++) {
				// FerrariVehicleOrder element
				Element tmpElement = (Element) nodesElmFerrariVehicleOrderMapped.item(j);
				if (tmpElement.getNodeType() == Node.ELEMENT_NODE) {
					docMappedResponse.renameNode(tmpElement, "", tmpElement.getLocalName());
					renameNode(docMappedResponse, tmpElement);
				}
			}
		}
	}

	message.setBody(elmGetVehicleOrderResponse);

	// get the body as String
	String strXML = "";
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = null;
	try {
		transformer = transformerFactory.newTransformer();
	} catch (TransformerConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	DOMSource source = new DOMSource(docMappedResponse);

	StringWriter sw = new StringWriter();
	StreamResult result = new StreamResult(sw);
	try {
		transformer.transform(source, result);
	} catch (TransformerException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	strXML = sw.toString();

	def messageLog = messageLogFactory.getMessageLog(message);
	if(messageLog != null){
		messageLog.addAttachmentAsString("3#Response from Ferrari", respBody, "text/xml");
		messageLog.addAttachmentAsString("4#Response To ECC", strXML, "text/xml");
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