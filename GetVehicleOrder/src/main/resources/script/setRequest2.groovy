/*
 * The integration developer needs to create the method processData
 * This method takes Message object of package com.sap.gateway.ip.core.customdev.util
 * which includes helper methods useful for the content developer:
 *
 * The methods available are:
 public java.lang.Object getBody()
 //This method helps User to retrieve message reqBody as specific type ( InputStream , String , byte[] ) - e.g. message.getBody(java.io.InputStream)
 public java.lang.Object getBody(java.lang.String fullyQualifiedClassName)
 public void setBody(java.lang.Object exchangeBody)
 public java.util.Map<java.lang.String,java.lang.Object> getHeaders()
 public void setHeaders(java.util.Map<java.lang.String,java.lang.Object> exchangeHeaders)
 public void setHeader(java.lang.String name, java.lang.Object value)
 public java.util.Map<java.lang.String,java.lang.Object> getProperties()
 public void setProperties(java.util.Map<java.lang.String,java.lang.Object> exchangeProperties)
 public void setProperty(java.lang.String name, java.lang.Object value)
 *
 */
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.dom.DOMSource

import org.apache.cxf.binding.soap.SoapHeader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sap.gateway.ip.core.customdev.util.Message;

def Message processData(Message message) {

	//Body
	//String reqBody = message.getBody();

	//message.setBody(reqBody + "Body is modified");

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

	// get value of DealerNumberID
	def reqBody = message.getBody(java.lang.String) as String;
	InputSource is = new InputSource();
	is.setCharacterStream(new StringReader(reqBody));

	Document docBody = db.parse(is);
	NodeList nodesDealerNumberID = docBody.getElementsByTagName("DealerNumberID");

	if(nodesDealerNumberID.length < 1){
		throw new Exception("DealerNumberID is mandatory");
	}
	Element elementDealerNumberID = (Element) nodesDealerNumberID.item(0);
	String strDealerNumberID = elementDealerNumberID.getTextContent();

	// get VehicleOrderVehicleLineItem
	// will be implemented later after confirming the elements of VehicleOrderVehicleLineItem with Naza


	Document doc1 = db.newDocument();

	Element payloadManifest = doc1.createElementNS("http://www.starstandards.org/webservices/2009/transport", "payloadManifest");
	doc1.appendChild(payloadManifest);
	Element manifest = doc1.createElementNS("http://www.starstandards.org/webservices/2009/transport", "manifest");

	manifest.setAttribute("contentID", "Content0");
	manifest.setAttribute("namespaceURI", "http://www.starstandards.org/STAR/5");
	manifest.setAttribute("element", "GetVehicleOrder");
	manifest.setAttribute("version", "5.5.4");

	payloadManifest.appendChild(manifest);

	Document doc = db.newDocument();

	Element processMessage = doc.createElementNS("http://www.starstandards.org/webservices/2009/transport",
			"ProcessMessage");
	doc.appendChild(processMessage);

	Element payload = doc.createElementNS("http://www.starstandards.org/webservices/2009/transport", "payload");
	processMessage.appendChild(payload);

	Element content = doc.createElementNS("http://www.starstandards.org/webservices/2009/transport", "content");
	content.setAttribute("id", "Content0");
	payload.appendChild(content);

	Element GetVehicleOrder = doc.createElementNS("http://www.starstandard.org/STAR/5",
			"GetVehicleOrder");
	GetVehicleOrder.setAttribute("releaseID", "5.5.4");
	GetVehicleOrder.setAttribute("versionID", "1");
	GetVehicleOrder.setAttribute("systemEnvironmentCode", "Test");
	GetVehicleOrder.setAttribute("languageCode", "en-GB");

	content.appendChild(GetVehicleOrder);

	Element applicationArea = doc.createElementNS("http://www.starstandard.org/STAR/5", "ApplicationArea");
	GetVehicleOrder.appendChild(applicationArea);

	Element sender = doc.createElementNS("http://www.starstandard.org/STAR/5", "Sender");
	applicationArea.appendChild(sender);

	Element creatorNameCode = doc.createElementNS("http://www.starstandard.org/STAR/5", "CreatorNameCode");
	creatorNameCode.setTextContent("DMS");

	Element senderNameCode = doc.createElementNS("http://www.starstandard.org/STAR/5", "SenderNameCode");
	senderNameCode.setTextContent("DMSNAMEANDVERSION");

	Element dealerNumberID = doc.createElementNS("http://www.starstandard.org/STAR/5", "DealerNumberID");
	dealerNumberID.setTextContent(strDealerNumberID);

	Element storeNumber = doc.createElementNS("http://www.starstandard.org/STAR/5", "StoreNumber");
	storeNumber.setTextContent("001");

	Element dealerCountryCode = doc.createElementNS("http://www.starstandard.org/STAR/5", "DealerCountryCode");
	dealerCountryCode.setTextContent("IT");

	sender.appendChild(creatorNameCode);
	sender.appendChild(senderNameCode);
	sender.appendChild(dealerNumberID);
	sender.appendChild(storeNumber);
	sender.appendChild(dealerCountryCode);

	Element creationDateTime = doc.createElementNS("http://www.starstandard.org/STAR/5", "CreationDateTime");
	creationDateTime.setTextContent("2013-03-27T14:57:41Z");

	applicationArea.appendChild(creationDateTime);

	Element bodID = doc.createElementNS("http://www.starstandard.org/STAR/5", "BODID");
	bodID.setTextContent("de84cdfd-149e-495c-b08a-16002f8331b4");

	applicationArea.appendChild(bodID);

	Element destination = doc.createElementNS("http://www.starstandard.org/STAR/5", "Destination");
	Element destinationNameCode = doc.createElementNS("http://www.starstandard.org/STAR/5", "DestinationNameCode");
	destinationNameCode.setTextContent("FE");

	destination.appendChild(destinationNameCode);

	applicationArea.appendChild(destination);

	Element GetVehicleOrderDataArea = doc.createElementNS("http://www.starstandard.org/STAR/5",
			"GetVehicleOrderDataArea");
	GetVehicleOrder.appendChild(GetVehicleOrderDataArea);
	Element get = doc.createElementNS("http://www.starstandard.org/STAR/5", "Get");
	Element expression = doc.createElementNS("http://www.openapplications.org/oagis/9", "Expression");
	expression.setAttribute("expressionLanguage", "Ferrari Lucene");
	expression.setTextContent("DATE:[2012-01-01 TO 2012-01-15]");

	get.appendChild(expression);
	GetVehicleOrderDataArea.appendChild(get);

	Element vehicleOrder = doc.createElementNS("http://www.starstandard.org/STAR/5", "VehicleOrder");
	GetVehicleOrderDataArea.appendChild(vehicleOrder);
	Element vehicleOrderHeader = doc.createElementNS("http://www.starstandard.org/STAR/5",
			"VehicleOrderHeader");
	vehicleOrder.appendChild(vehicleOrderHeader);
	Element documentIdentificationGroup = doc.createElementNS("http://www.starstandard.org/STAR/5",
			"DocumentIdentificationGroup");
	vehicleOrderHeader.appendChild(documentIdentificationGroup);
	Element documentIdentification = doc.createElementNS("http://www.starstandard.org/STAR/5",
			"DocumentIdentification");
	documentIdentificationGroup.appendChild(documentIdentification);
	Element documentID = doc.createElementNS("http://www.starstandard.org/STAR/5", "DocumentID");
	documentID.setTextContent("2013234343434");
	documentIdentification.appendChild(documentID);

	Element vehicleOrderVehicleLineItem = doc.createElementNS("http://www.starstandard.org/STAR/5",
			"VehicleOrderVehicleLineItem");
	vehicleOrder.appendChild(vehicleOrderVehicleLineItem);

	// Create SOAP header instance.
	SoapHeader header = new SoapHeader(new QName(payloadManifest.getNamespaceURI(), payloadManifest.getLocalName()), payloadManifest);

	// Add the SOAP header to the header list and set the list to the message header "org.apache.cxf.headers.Header.list".
	List  headersList  = new ArrayList<SoapHeader>();
	headersList.add(header);
	message.setHeader("org.apache.cxf.headers.Header.list", headersList);
	message.setBody(processMessage);

	DOMSource source = new DOMSource(doc);

	def messageLog = messageLogFactory.getMessageLog(message);
	if(messageLog != null){
		messageLog.setStringProperty("Logging#1", "strDealerNumberID = " + strDealerNumberID);
		messageLog.addAttachmentAsString("Request", reqBody, "text/xml");

	}



	return message;
}

