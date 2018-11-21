package sg.mas.servlet.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.jms.BytesMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;

import sg.mas.servlet.activemq.OutgoingQueue;
import sg.mas.servlet.helper.MasHelper;

@Controller
public class MasController implements ServletContextAware {

	final static Logger logger = Logger.getLogger(MasController.class);

	@Autowired
	private ServletContext context; 
	
	@Autowired
	private MasHelper masHelper;
	
	@RequestMapping(value="/uploadfiles",method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> uploadMasFiles(HttpServletRequest request, HttpServletResponse response) throws Exception {
                      
          HttpStatus status = null;
          String uploadFileMessage = "";
          String UPLOADED_PATH = "/home/virtuser/logs/";
          
          if (ServletFileUpload.isMultipartContent(request)){
                logger.debug("IS MULTIPART CONTENT");
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);

                // Parse the request
                List<FileItem> items = upload.parseRequest(request);
                logger.debug("Less than 11 items, going to start!");
                
                InputStream fileInputStream = null;
                FileOutputStream fileOutputStream = null;
                
                logger.debug("Reading through FileItems");
                try{
                for(FileItem item : items){
                      if(item!=null && !items.isEmpty()){
                    	    // To write the file in disk
                    	    logger.debug("Before saving to disk");
                            fileInputStream = item.getInputStream();
                            File writeFile = new File(UPLOADED_PATH, item.getName());
                            fileOutputStream = new FileOutputStream(writeFile);
                            IOUtils.copy(fileInputStream,fileOutputStream);
                            logger.debug("After saving to disk");
                            
                            logger.debug("Before sending to Queue");                            
                            // To send file to queue
                            OutgoingQueue outQueue = new OutgoingQueue( new URI("tcp://activemq.tnisp-demo.sg.cfl.io:61616"), "INCOMING.SUBMISSION");
                    		BytesMessage bytesMessage = outQueue.createNewBytesMessage();
                    		bytesMessage.setStringProperty("submissionID", "20181120185200111111");
                    		bytesMessage.setStringProperty("serviceName", "tier1Validation");
                    		bytesMessage.setStringProperty("returnQueueName", "jms/masSystemMessageOutgoingQueue");
                    		bytesMessage.setStringProperty("formName", "Form_A__V1.0");
                    		bytesMessage.setStringProperty("REIdentifier", "");
                    		bytesMessage.setStringProperty("JMSXGroupID", "");
                    		bytesMessage.setStringProperty("PeriodStartDate", "");
                    		bytesMessage.setStringProperty("PeriodEndDate", "");
                    		bytesMessage.setStringProperty("periodCode", "");
                    		bytesMessage.writeBytes(item.get());
                    		logger.debug("After sending to the queue");
                      }
                }
                } catch (Exception e){
                	logger.error(e);
                	e.printStackTrace();
                }
                
                if(items!=null && items.isEmpty()){
                      status = HttpStatus.BAD_REQUEST;
                }else{
                      status = HttpStatus.OK;
                }                 
          }          
          
          final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
          
          return new ResponseEntity<String>(uploadFileMessage, httpHeaders, status);
    }

	
	@RequestMapping(value="/getFiles",method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> getMasFiles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Inside getFiles");
		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		System.out.println("Inside getFiles");
		
		OutgoingQueue outQueue = new OutgoingQueue( new URI("tcp://activemq.tnisp-demo.sg.cfl.io:61616"), "INCOMING.SUBMISSION");
		BytesMessage bytesMessage = outQueue.createNewBytesMessage();
		bytesMessage.setStringProperty("submissionID", "20181120185200111111");
		bytesMessage.setStringProperty("serviceName", "tier1Validation");
		bytesMessage.setStringProperty("returnQueueName", "jms/masSystemMessageOutgoingQueue");
		bytesMessage.setStringProperty("formName", "Form_A__V1.0");
		bytesMessage.setStringProperty("REIdentifier", "");
		bytesMessage.setStringProperty("JMSXGroupID", "");
		bytesMessage.setStringProperty("PeriodStartDate", "");
		bytesMessage.setStringProperty("PeriodEndDate", "");
		bytesMessage.setStringProperty("periodCode", "");
		bytesMessage.writeBytes(null);
		
		outQueue.sendBytesMessageMessage(bytesMessage);
		
		logger.debug("Exit getFiles");
		return new ResponseEntity<String>("success", httpHeaders, HttpStatus.OK);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}

/*@RequestMapping(value="/uploadfiles",method = RequestMethod.POST, produces = "application/json")
public ResponseEntity<String> uploadMasFiles(HttpServletRequest request, HttpServletResponse response) throws Exception {

HttpStatus status = null;
String uploadFileMessage = "";
String UPLOADED_PATH = "/home/virtuser/logs/";
boolean errorFound = true;
String uploadedFileName = "";
byte[] bytes;
// Parse the request
if (ServletFileUpload.isMultipartContent(request)){
      logger.debug("IS MULTIPART CONTENT");
      
      MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
      String fileName = (String) multiRequest.getFileNames().next();
      MultipartFile checkFile = multiRequest.getFile(fileName);
      if(checkFile != null && !checkFile.isEmpty()){
  		uploadedFileName = checkFile.getOriginalFilename();
  		errorFound = false;
  	}
 		try {
      	// To write the file in disk
  	    logger.debug("Before saving to disk");
 			bytes = checkFile.getBytes();
          Path path = Paths.get(UPLOADED_PATH + checkFile.getOriginalFilename());
          Files.write(path, bytes);
          logger.debug("After saving to disk");
          
          logger.debug("Before sending to Queue");                            
          // To send file to queue
          OutgoingQueue outQueue = new OutgoingQueue( new URI("tcp://activemq.tnisp-demo.sg.cfl.io:61616"), "INCOMING.SUBMISSION");
  		BytesMessage bytesMessage = outQueue.createNewBytesMessage();
  		bytesMessage.setStringProperty("submissionID", "20181120185200111111");
  		bytesMessage.setStringProperty("serviceName", "tier1Validation");
  		bytesMessage.setStringProperty("returnQueueName", "jms/masSystemMessageOutgoingQueue");
  		bytesMessage.setStringProperty("formName", "Form_A__V1.0");
  		bytesMessage.setStringProperty("REIdentifier", "");
  		bytesMessage.setStringProperty("JMSXGroupID", "");
  		bytesMessage.setStringProperty("PeriodStartDate", "");
  		bytesMessage.setStringProperty("PeriodEndDate", "");
  		bytesMessage.setStringProperty("periodCode", "");
  		bytesMessage.writeBytes(bytes);
  		outQueue.sendBytesMessageMessage(bytesMessage);
  		logger.debug("After sending to the queue");
          
      } catch (Exception e) {
          errorFound = true;
      }
      
      if(errorFound){
            status = HttpStatus.BAD_REQUEST;
      }else{
            status = HttpStatus.OK;
      }                 
}          

final HttpHeaders httpHeaders= new HttpHeaders();
httpHeaders.setContentType(MediaType.APPLICATION_JSON);

return new ResponseEntity<String>(uploadFileMessage, httpHeaders, status);
}
*/