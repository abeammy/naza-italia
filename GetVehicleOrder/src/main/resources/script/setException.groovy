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

import com.sap.gateway.ip.core.customdev.util.Message;
def Message processData(Message message) {

	def map = message.getProperties();
	def ex = map.get("CamelExceptionCaught");

	String strFault = null;
	String strStatusCode = null;
	String strFaultCode = null;
	String strFaultActor = null;
	String strFaultString = null;
	if (ex!=null) {
		strStatusCode = ex.getStatusCode();
		strFaultString = ex.getMessage();
		strFaultCode = ex.getFaultCode();
		strFaultActor = ex.getRole();
	}

	if(strStatusCode == "500"){
		strStatusCode = strStatusCode + " : Internal Server Error.";
	}

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
	// for response mapping
	Document docMappedResponse = db.newDocument();

	Element elmGetVehicleOrderResponse = docMappedResponse.createElementNS(
			"http://sales.vehicle.order.naza.com/GetVehicleOrder/",
			"GetVehicleOrderResponse");
	elmGetVehicleOrderResponse.setPrefix("vo");

	docMappedResponse.appendChild(elmGetVehicleOrderResponse);

	Element elmGetVehicleOrderFault = docMappedResponse.createElement("GetVehicleOrderFault");

	elmGetVehicleOrderResponse.appendChild(elmGetVehicleOrderFault);

	Element elmFaultCode = docMappedResponse.createElement("faultcode");
	elmFaultCode.setTextContent(strFaultCode);
	elmGetVehicleOrderFault.appendChild(elmFaultCode);

	Element elmFaultString = docMappedResponse.createElement("faultstring");
	elmFaultString.setTextContent(strFaultString);
	elmGetVehicleOrderFault.appendChild(elmFaultString);

	Element elmFaultActor = docMappedResponse.createElement("faultactor");
	elmFaultActor.setTextContent(strFaultActor);
	elmGetVehicleOrderFault.appendChild(elmFaultActor);

	message.setBody(elmGetVehicleOrderResponse);

	// get the body as String
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
	strFault = sw.toString();


	def messageLog = messageLogFactory.getMessageLog(message);

	messageLog.setStringProperty("Logging#ex.getStatusCode()", strStatusCode);
	messageLog.setStringProperty("Logging#ex.getMessage()", strFaultString);
	messageLog.setStringProperty("Logging#ex.getFaultCode()", strFaultCode);
	messageLog.setStringProperty("Logging#ex.getRole()", strFaultActor);

	//messageLog.setStringProperty("Logging#STATUS_TEXT", strStatusText);

	messageLog.addAttachmentAsString("3#Exception/Fault", strFault, "text/xml");

	return message;
}

