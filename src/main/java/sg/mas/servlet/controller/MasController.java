package sg.mas.servlet.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
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
                            String submissionID = getCurrentTimeStamp();
                            String startDate = getStartDate();
                            String endDate = getEndDate();
                            OutgoingQueue outQueue = new OutgoingQueue( new URI("tcp://activemq.tnisp-demo.sg.cfl.io:61616"), "INCOMING.SUBMISSION");
                    		BytesMessage bytesMessage = outQueue.createNewBytesMessage();
                    		bytesMessage.setStringProperty("submissionID", submissionID);
                    		bytesMessage.setStringProperty("serviceName", "tier1Validation");
                    		bytesMessage.setStringProperty("returnQueueName", "ACCENTURE.OUTGOING.RESPONSE");
                    		bytesMessage.setStringProperty("formName", item.getName());
                    		bytesMessage.setStringProperty("REIdentifier", "AccentureDemo");
                    		bytesMessage.setStringProperty("JMSXGroupID", "AccentureDemo");
                    		bytesMessage.setStringProperty("PeriodStartDate", startDate);
                    		bytesMessage.setStringProperty("PeriodEndDate", endDate);
                    		bytesMessage.setStringProperty("periodCode", "2018");
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
	
	
	@RequestMapping(value="/readFilesFromFolder",method = RequestMethod.GET, produces = "application/xhtml+xml")
	public ResponseEntity<String> readFilesFromFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Enter readFilesFromFolder");
		System.out.println("Enter readFilesFromFolder");
		StringBuilder fileListStr = new StringBuilder();
		if(request.getParameter("filePath")!=null && !request.getParameter("filePath").isEmpty()) {
			String filePath = request.getParameter("filePath");
			
			File folder = new File(filePath);
			File[] listOfFiles = folder.listFiles();
			int fileCnt = 0;
			for (File file : listOfFiles) {
			    if (file.isFile()) {
			    	fileCnt++;
			    	StringBuilder strBldr = new StringBuilder();
			    	strBldr.append("<tr>");
			    	strBldr.append("<td>");
			    	strBldr.append(fileCnt);
			    	strBldr.append("</td>");
			    	strBldr.append("<td colspan='2'>");
			    	strBldr.append("<a href='"+ "/mas/mas-servlets/files/"+file.getAbsolutePath() + "' target='_blank' download='"+ file.getName() +"'>" + file.getName() + "</a>");
			    	strBldr.append("</td>");
			    	strBldr.append("<td>");			    	
			    	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");			    	
		    		Date lastModifiedDate=new Date(file.lastModified());			    		
					strBldr.append(format.format(lastModifiedDate));
			    	strBldr.append("</td>");
			    	if(filePath.indexOf("incoming")<0) {
			    		strBldr.append("<td class='submitted'>");
				    	strBldr.append("Submitted");
				    	strBldr.append("</td>");
			    	}else{
			    		strBldr.append("<td class='confirmed'>");
				    	strBldr.append("Confirmed");
				    	strBldr.append("</td>");
			    	}
			    	
			    	strBldr.append("</tr>");
			        fileListStr.append(strBldr.toString());
			    }
			}
		}else {
			fileListStr.append("Folder is empty");
		}
		System.out.println("fileListStr:: "+fileListStr.toString());	
		logger.debug("fileListStr:: "+fileListStr.toString());
		final HttpHeaders httpHeaders= new HttpHeaders();
		logger.debug("Exit readFilesFromFolder");
		System.out.println("Exit readFilesFromFolder");
		response.setContentType("text/html; charset=utf-8");
		return new ResponseEntity<String>(fileListStr.toString(), httpHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/files/{file_name:.+}", method = RequestMethod.GET)
	public void getFileDownload(@PathVariable("file_name") String fileName, HttpServletResponse response) {
		// reads input file from an absolute path
		
		logger.debug("MasController.getFileDownload start");
		logger.debug("fileName: " + fileName);
		
		String filePath = fileName;
		File downloadFile = new File(filePath);
		OutputStream outStream = null;
		FileInputStream inStream = null;
		
		try {
			inStream = new FileInputStream(downloadFile);
			
			// if you want to use a relative path to context root:
			String relativePath = context.getRealPath("");
			logger.debug("relativePath = " + relativePath);
			
			// gets MIME type of the file
			String mimeType = context.getMimeType(filePath);
			if (mimeType == null) {			
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}
			logger.debug("MIME type: " + mimeType);
			
			// modifies response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());
			
			// forces download
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// obtains response's output stream
			outStream = response.getOutputStream();
			
			IOUtils.copy(inStream,outStream);	
			logger.debug("MasController.getFileDownload end");
		} catch (IOException e) {
			logger.error("exception in getFile:: " +e.getMessage());
		}
		finally {
			try {
				if (inStream != null) {
					inStream.close();	
				}
				if (outStream != null) {
					outStream.close();	
				}		
			} catch (IOException e) {
			}
		}
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		
	}
	
	public static String getCurrentTimeStamp() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	public static String getStartDate() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	public static String getEndDate() {
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	
	
	
}