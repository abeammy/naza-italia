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
import org.xml.sax.InputSource

import com.sap.gateway.ip.core.customdev.util.Message;

def Message processData(Message message) {
	def messageLog = messageLogFactory.getMessageLog(message);

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
	messageLog.setStringProperty("Logging#respBody", "respBody = " + respBody);
	InputSource is = new InputSource();
	is.setCharacterStream(new StringReader(respBody));
	Document docFerrariResponse = db.parse(is);

	// for response mapping
	Document docMappedResponse = db.newDocument();

	Element elmGetVehicleOrderFault = docMappedResponse.createElementNS(
			"http://sales.vehicle.order.naza.com/GetVehicleOrder/",
			"GetVehicleOrderFault");
	elmGetVehicleOrderFault.setPrefix("voex");
	docMappedResponse.appendChild(elmGetVehicleOrderFault);

	String strFaultCode = ((Element) docFerrariResponse.getElementsByTagName("faultcode").item(0)).getTextContent();
	Element elmFaultCode = docMappedResponse.createElementNS(
			"http://sales.vehicle.order.naza.com/GetVehicleOrder/",
			"faultcode");
	elmFaultCode.setTextContent(strFaultCode);
	elmGetVehicleOrderFault.appendChild(elmFaultCode);

	String strFaultString = ((Element) docFerrariResponse.getElementsByTagName("faultstring").item(0)).getTextContent();
	Element elmFaultString = docMappedResponse.createElementNS(
			"http://sales.vehicle.order.naza.com/GetVehicleOrder/",
			"faultstring");
	elmFaultString.setTextContent(strFaultString);
	elmGetVehicleOrderFault.appendChild(elmFaultString);

	String strFaultActor = ((Element) docFerrariResponse.getElementsByTagName("faultactor").item(0)).getTextContent();
	Element elmFaultActor = docMappedResponse.createElementNS(
			"http://sales.vehicle.order.naza.com/GetVehicleOrder/",
			"faultstring");
	elmFaultActor.setTextContent(strFaultActor);
	elmGetVehicleOrderFault.appendChild(elmFaultActor);

	message.setBody(elmGetVehicleOrderFault);

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


	if(messageLog != null){
		messageLog.addAttachmentAsString("3#Response from Ferrari", respBody, "text/xml");
		messageLog.addAttachmentAsString("4#SOAP Fault", strXML, "text/xml");
	}
	return message;
}

