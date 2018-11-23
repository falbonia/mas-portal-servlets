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

import sg.mas.servlet.activemq.IncomingQueue;
import sg.mas.servlet.activemq.OutgoingQueue;
import sg.mas.servlet.activemq.ReceivedMessage;
import sg.mas.servlet.activemq.TemporaryFileManager;
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
          String uploadFileMessage = "Empty Message";
          String UPLOADED_PATH = "/home/virtuser/logs/outgoing/";
          //String UPLOADED_PATH = "D:/logs/";
          logger.debug("Check MULTIPART CONTENT");
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
                	logger.debug("Check FileItem Size: " + items.size());

                    System.out.println("Read through FileItems");
                    	if(item!=null && !items.isEmpty()){
                    	    // To write the file in disk
                    	    logger.debug("Before saving to disk");
                            fileInputStream = item.getInputStream();
                            File writeFile = new File(UPLOADED_PATH, item.getName());
                            fileOutputStream = new FileOutputStream(writeFile);
                            IOUtils.copy(fileInputStream,fileOutputStream);
                            logger.debug("After saving to disk");
                            System.out.println("After saving to disk");
                            
                            System.out.println("Before Sending to Queue");
                            logger.debug("Before sending to Queue");                            
                            // To send file to queue
                            OutgoingQueue outQueue = new OutgoingQueue( new URI("tcp://activemq.tnisp-demo.sg.cfl.io:61616"), "INCOMING.SUBMISSION");
                    		BytesMessage bytesMessage = outQueue.createNewBytesMessage();
                    		bytesMessage.setStringProperty("submissionID", "20181120185200111111");
                    		bytesMessage.setStringProperty("serviceName", "tier1Validation");
                    		bytesMessage.setStringProperty("returnQueueName", "ACCENTURE.OUTGOING.RESPONSE");
                    		bytesMessage.setStringProperty("formName", "Form_A__V1.0");
                    		bytesMessage.setStringProperty("REIdentifier", "");
                    		bytesMessage.setStringProperty("JMSXGroupID", "");
                    		bytesMessage.setStringProperty("PeriodStartDate", "");
                    		bytesMessage.setStringProperty("PeriodEndDate", "");
                    		bytesMessage.setStringProperty("periodCode", "");
                    		bytesMessage.writeBytes(item.get());
                    		outQueue.sendBytesMessageMessage(bytesMessage);
                    		logger.debug("After sending to the queue");
                    		
                    		uploadFileMessage = "Successfully Uploaded";
                      }
                }
                } catch (Exception e){
                	logger.error(e);
                	uploadFileMessage = e.getMessage();
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
		bytesMessage.setStringProperty("submissionID", "1");
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

	@RequestMapping(value="/listenQueue",method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> getCoreFilingStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("Inside listenQueue get call");
		try{		
			logger.debug("Inside listenQueue get call");
			System.out.println("Retrieving parameters....");
			String getParameter = request.getParameter("id");
			System.out.println(request.getParameter("id"));
			Integer id = Integer.parseInt(request.getParameter("id")); 
			System.out.println("Integer has been parsed");
			TemporaryFileManager fileManager = new TemporaryFileManager();
			logger.debug("Establishing MessageConsumer to retrieve information");
			System.out.println("Establishing MessageConsumer to retrieve information");
			IncomingQueue inQueue = new IncomingQueue(id, new URI("tcp://activemq.tnisp-demo.sg.cfl.io:61616"), "ACCENTURE.OUTGOING.RESPONSE", fileManager);
			System.out.println("MessageConsumer is created and listening");
			logger.debug("MessageConsumer is created and listening");
					
			if (inQueue.hasReceivedMessage(id)){
				System.out.println("This Message ID: " + id + " has been received");
				logger.debug("This Message ID: " + id + " has been received");
				ReceivedMessage receive = inQueue.getReceivedMessage(id);
				// Do something with the ReceivedMessage to check or update
			}
		}catch(Exception e){
			logger.error("Error while getting message: " + e.getMessage());
			System.out.println("Error while getting message: " + e.getMessage());
			e.printStackTrace();
		}
		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		logger.debug("Exit listenQueue");
		System.out.println("Exit listenQueue");
		return new ResponseEntity<String>("success", httpHeaders, HttpStatus.OK);
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}