package sender;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The main server of traffic source
 * @author Liming Liu
 *
 */
public class SourceServer {

	private static final Logger logger = Logger.getLogger("TrafficSource");
	//private final byte[] responseHttpHeaderContent;
	private final int port;
	private String destinationAddress;
	private int frequency=0;
	private int size=0;
	
	public SourceServer(int port,
			String destinationAddress, int frequency, int size) {
		this.port=port;
		this.destinationAddress=destinationAddress;
		this.frequency=frequency;
		this.size=size;
		//String header = "HTTP/1.0 200 OK\r\n"
		//+ "Server: OneFile 2.0\r\n"+ "\r\n"
		//+ "Content-type: " + "text" + "; charset=utf-8" + "\r\n\r\n";
		//this.responseHttpHeaderContent = header.getBytes(Charset.forName("utf-8"));
	}
	

	public void start() {

		ExecutorService pool = Executors.newFixedThreadPool(100);
		try (ServerSocket server = new ServerSocket(this.port)) {
			logger.info("Accepting connections on port " + server.getLocalPort());
			

		//start running the server
		while (true) {
			try {
				Socket connection = server.accept();
				System.out.println("Received trigger, now start sending packets");
				pool.submit(new HTTPRequestHandler(connection));
			} catch (IOException ex) {
				logger.log(Level.WARNING, "Exception accepting connection", ex);
			} catch (RuntimeException ex) {
				logger.log(Level.SEVERE, "Unexpected error", ex);
			}
			}
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "Could not start server", ex);
		}
	}
	
	private class HTTPRequestHandler implements Callable<Void> {
	
		private final Socket connection;
		
		
		HTTPRequestHandler(Socket connection) {
			this.connection = connection;
		}
		
		
		@Override
		public Void call() throws IOException {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			OutputStream out = new BufferedOutputStream(connection.getOutputStream());
			String line;
			StringBuilder request=new StringBuilder();
			//read POST request from dashboard
			int flag=0;
	        while ((line = in.readLine()) != null) {
	          if (line.length() == 0) {
	        	  if(flag==0) {
	        		  flag=1;
	        	  }else {
	        		  break;
	        	  }
	          }
	          request.append(line+"\n");
	        }
	       String requestString=request.toString();
	      
	       //send response
	        String responseString=new String("HTTP/1.0 200 OK\r\n");
	        byte[] responseBytes=responseString.getBytes();
	        out.write(responseBytes);
	        out.flush();
	       
			try {
				TrafficGenerator newSender=new TrafficGenerator(destinationAddress, size, frequency);
				newSender.start();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error writing to client", e);
				e.printStackTrace();
			} finally {
				connection.close();
			}
			return null;
		}
}
	
}
