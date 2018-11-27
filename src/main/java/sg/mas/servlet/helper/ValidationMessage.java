package sg.mas.servlet.helper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vm:validationMessage")
public class ValidationMessage {
	String severity;
	String errorCode; 
	
	String messageDetail;
	String[] messageCauses;
	
	public ValidationMessage(String severity, String errorCode, String messageDetail, String[] messageCauses){
		this.severity = severity;
		this.errorCode = errorCode;
		this.messageDetail = messageDetail;
		this.messageCauses = messageCauses;
	}
	
	public String getSeverity(){
		return this.severity;
	}
	public void setSeverity(String severity){
		 severity = this.severity;
	}
	
	
	public String getErrorCode(){
		return this.errorCode;
	}
	public void setErrorCode(String errorCode){
		errorCode = this.errorCode;
	}
	
	@XmlElement(name = "vm:messageDetail")
	public String getMessageDetail(){
		return this.messageDetail;
	}
	public void setMessageDetail(String messageDetail){
		messageDetail = this.messageDetail;
	}
	
	@XmlElement(name = "vm:messageCauses")
	public String[] getMessageCauses(){
		return this.messageCauses;
	}

		public void setMessageCause(String[] messageCauses){
		messageCauses = this.messageCauses;
	}
}
