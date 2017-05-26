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
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import org.apache.cxf.binding.soap.SoapHeader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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

	def reqBody = message.getBody(java.lang.String) as String;
	InputSource is = new InputSource();
	is.setCharacterStream(new StringReader(reqBody));

	Document docRequestBody = db.parse(is);

	String strReleaseID = "";
	String strVersionID = "";
	String strSystemEnvironmentCode = "";
	String strLanguageCode = "";
	String strCreatorNameCode = "";
	String strSenderNameCode = "";
	String strDealerNumberID = "";
	String strStoreNumber = "";
	String strDealerCountryCode = "";
	String strCreationDateTime = "";
	String strBODID = "";
	String strDestinationNameCode = "";
	String strExpression = "";
	String strExpressionLanguage = "";
	String strDocumentID = "";

	NodeList nodeListVehicleInvoice = docRequestBody.getElementsByTagNameNS("http://sales.invoice.naza.com/GetFerrariVehicleInvoice/", "GetVehicleInvoice");

	if(nodeListVehicleInvoice.length > 0){
		Element elmGetVehicleInvoice = (Element) nodeListVehicleInvoice.item(0);

		if(elmGetVehicleInvoice.hasAttribute("releaseID")){
			strReleaseID = elmGetVehicleInvoice.getAttribute("releaseID");
		}
		strReleaseID = (strReleaseID == "") ? "5.5.4" : strReleaseID;

		if(elmGetVehicleInvoice.hasAttribute("versionID")){
			strVersionID = elmGetVehicleInvoice.getAttribute("versionID");
		}
		strVersionID = (strVersionID == "") ? "1" : strVersionID;

		if(elmGetVehicleInvoice.hasAttribute("systemEnvironmentCode")){
			strSystemEnvironmentCode = elmGetVehicleInvoice.getAttribute("systemEnvironmentCode");
		}

		if(elmGetVehicleInvoice.hasAttribute("languageCode")){
			strLanguageCode = elmGetVehicleInvoice.getAttribute("languageCode");
		}

		NodeList nodeListTemp = docRequestBody.getElementsByTagName("CreatorNameCode");
		if(nodeListTemp.length > 0){
			strCreatorNameCode = ((Element)nodeListTemp.item(0)).getTextContent()
		}

		nodeListTemp = docRequestBody.getElementsByTagName("SenderNameCode");
		if(nodeListTemp.length > 0){
			strSenderNameCode = ((Element)nodeListTemp.item(0)).getTextContent()
		}

		nodeListTemp = docRequestBody.getElementsByTagName("DealerNumberID");
		if(nodeListTemp.length > 0){
			strDealerNumberID = ((Element)nodeListTemp.item(0)).getTextContent()
		}

		nodeListTemp = docRequestBody.getElementsByTagName("StoreNumber");
		if(nodeListTemp.length > 0){
			strStoreNumber = ((Element)nodeListTemp.item(0)).getTextContent()
		}

		nodeListTemp = docRequestBody.getElementsByTagName("DealerCountryCode");
		if(nodeListTemp.length > 0){
			strDealerCountryCode = ((Element)nodeListTemp.item(0)).getTextContent()
		}

		nodeListTemp = docRequestBody.getElementsByTagName("CreationDateTime");
		if(nodeListTemp.length > 0){
			strCreationDateTime = ((Element)nodeListTemp.item(0)).getTextContent()
		}

		nodeListTemp = docRequestBody.getElementsByTagName("BODID");
		if(nodeListTemp.length > 0){
			strBODID = ((Element)nodeListTemp.item(0)).getTextContent()
		}

		nodeListTemp = docRequestBody.getElementsByTagName("DestinationNameCode");
		if(nodeListTemp.length > 0){
			strDestinationNameCode = ((Element)nodeListTemp.item(0)).getTextContent()
		}

		nodeListTemp = docRequestBody.getElementsByTagName("Expression");
		if(nodeListTemp.length > 0){
			Element elmTemp = (Element)nodeListTemp.item(0);
			strExpression = elmTemp.getTextContent();
			if(elmTemp.hasAttribute("expressionLanguage")){
				strExpressionLanguage = elmTemp.getAttribute("expressionLanguage");
			}
		}

		nodeListTemp = docRequestBody.getElementsByTagName("DocumentID");
		if(nodeListTemp.length > 0){
			strDocumentID = ((Element)nodeListTemp.item(0)).getTextContent()
		}
	}

	Document docMappedRequestHeader = db.newDocument();

	Element payloadManifest = docMappedRequestHeader.createElementNS("http://www.starstandards.org/webservices/2009/transport", "payloadManifest");
	docMappedRequestHeader.appendChild(payloadManifest);
	Element manifest = docMappedRequestHeader.createElementNS("http://www.starstandards.org/webservices/2009/transport", "manifest");

	manifest.setAttribute("contentID", "Content0");
	manifest.setAttribute("namespaceURI", "http://www.starstandards.org/STAR/5");
	manifest.setAttribute("element", "GetVehicleInvoice");
	manifest.setAttribute("version", "5.5.4");

	payloadManifest.appendChild(manifest);

	Document docMappedRequestBody = db.newDocument();

	Element processMessage = docMappedRequestBody.createElementNS("http://www.starstandards.org/webservices/2009/transport",
			"ProcessMessage");
	docMappedRequestBody.appendChild(processMessage);

	Element payload = docMappedRequestBody.createElementNS("http://www.starstandards.org/webservices/2009/transport", "payload");
	processMessage.appendChild(payload);

	Element content = docMappedRequestBody.createElementNS("http://www.starstandards.org/webservices/2009/transport", "content");
	content.setAttribute("id", "Content0");
	payload.appendChild(content);

	Element GetVehicleInvoice = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5",
			"GetVehicleInvoice");
	GetVehicleInvoice.setAttribute("releaseID", strReleaseID);
	GetVehicleInvoice.setAttribute("versionID", strVersionID);
	GetVehicleInvoice.setAttribute("systemEnvironmentCode", strSystemEnvironmentCode);
	GetVehicleInvoice.setAttribute("languageCode", strLanguageCode);

	content.appendChild(GetVehicleInvoice);

	Element applicationArea = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "ApplicationArea");
	GetVehicleInvoice.appendChild(applicationArea);

	Element sender = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "Sender");
	applicationArea.appendChild(sender);

	Element creatorNameCode = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "CreatorNameCode");
	creatorNameCode.setTextContent(strCreatorNameCode);

	Element senderNameCode = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "SenderNameCode");
	senderNameCode.setTextContent(strSenderNameCode);

	Element dealerNumberID = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "DealerNumberID");
	dealerNumberID.setTextContent(strDealerNumberID);

	Element storeNumber = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "StoreNumber");
	storeNumber.setTextContent(strStoreNumber);

	Element dealerCountryCode = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "DealerCountryCode");
	dealerCountryCode.setTextContent(strDealerCountryCode);

	sender.appendChild(creatorNameCode);
	sender.appendChild(senderNameCode);
	sender.appendChild(dealerNumberID);
	sender.appendChild(storeNumber);
	sender.appendChild(dealerCountryCode);

	Element creationDateTime = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "CreationDateTime");
	creationDateTime.setTextContent(strCreationDateTime);

	applicationArea.appendChild(creationDateTime);

	Element bodID = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "BODID");
	bodID.setTextContent(strBODID);

	applicationArea.appendChild(bodID);

	Element destination = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "Destination");
	Element destinationNameCode = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "DestinationNameCode");
	destinationNameCode.setTextContent(strDestinationNameCode);

	destination.appendChild(destinationNameCode);

	applicationArea.appendChild(destination);

	Element GetVehicleInvoiceDataArea = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5",
			"GetVehicleInvoiceDataArea");
	GetVehicleInvoice.appendChild(GetVehicleInvoiceDataArea);
	Element get = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "Get");
	Element expression = docMappedRequestBody.createElementNS("http://www.openapplications.org/oagis/9", "Expression");
	expression.setAttribute("expressionLanguage", strExpressionLanguage);
	expression.setTextContent(strExpression);

	get.appendChild(expression);
	GetVehicleInvoiceDataArea.appendChild(get);

	Element vehicleInvoice = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "VehicleInvoice");
	GetVehicleInvoiceDataArea.appendChild(vehicleInvoice);
	Element vehicleInvoiceHeader = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5",
			"VehicleInvoiceHeader");
	vehicleInvoice.appendChild(vehicleInvoiceHeader);
	Element documentIdentificationGroup = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5",
			"DocumentIdentificationGroup");
	vehicleInvoiceHeader.appendChild(documentIdentificationGroup);
	Element documentIdentification = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5",
			"DocumentIdentification");
	documentIdentificationGroup.appendChild(documentIdentification);
	Element documentID = docMappedRequestBody.createElementNS("http://www.starstandard.org/STAR/5", "DocumentID");
	documentID.setTextContent(strDocumentID);
	documentIdentification.appendChild(documentID);

	// Create SOAP header instance.
	SoapHeader header = new SoapHeader(new QName(payloadManifest.getNamespaceURI(), payloadManifest.getLocalName()), payloadManifest);

	// Add the SOAP header to the header list and set the list to the message header "org.apache.cxf.headers.Header.list".
	List  headersList  = new ArrayList<SoapHeader>();
	headersList.add(header);
	message.setHeader("org.apache.cxf.headers.Header.list", headersList);
	message.setBody(processMessage);

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
	DOMSource source = new DOMSource(docMappedRequestBody);

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
		messageLog.addAttachmentAsString("1#Request From ECC", reqBody, "text/xml");
		messageLog.addAttachmentAsString("2#Request To Ferrari", strXML, "text/xml");
	}

	return message;
}

