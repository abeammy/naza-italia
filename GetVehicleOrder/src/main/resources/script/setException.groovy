import com.sap.gateway.ip.core.customdev.util.Message;
def Message processData(Message message) {

	def map = message.getProperties();
	def ex = map.get("CamelExceptionCaught");

	def msgBody = null;
	String strStatusCode = null;
	//String strStatusText = null;
	String strFaultCode = null;
	if (ex!=null) {
		message.setBody(ex.getMessage());
		msgBody = ex.getMessage();
		strStatusCode = ex.getStatusCode();
		strFaultCode = ex.getFaultCode();

		//strStatusText = ex.getStatusText();
	}
	def messageLog = messageLogFactory.getMessageLog(message);

	messageLog.setStringProperty("Logging#STATUS_CODE", strStatusCode);
	messageLog.setStringProperty("Logging#FaultCode", strFaultCode);
	//messageLog.setStringProperty("Logging#STATUS_TEXT", strStatusText);

	messageLog.addAttachmentAsString("3#Response from Ferrari", msgBody, "text/xml");

	return message;
}

