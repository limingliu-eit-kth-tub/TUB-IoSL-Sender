package sender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
/**
 * The dummy client used to test the connection
 * @author Liming Liu
 *
 */
public class DummyClient {
	
		private URL url;
		private String message;
		
		public DummyClient (URL url,String message) {
			if (!url.getProtocol().toLowerCase().startsWith("http")) {
				throw new IllegalArgumentException("Posting only works for http URLs");
			}
			this.url = url;
			this.message=message;
		}
		
		
		public InputStream post() throws IOException {
			URLConnection uc = url.openConnection();
			uc.setDoOutput(true);
			try (OutputStreamWriter out
					= new OutputStreamWriter(uc.getOutputStream(), "UTF-8")) {
		
				try {
					out.write(message);
					out.write("\n");
					out.write("\n");
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return uc.getInputStream();
		}
		
		public static void main(String args[]) {
			try {
				URL url=new URL("http://172.17.0.3:5000");
				String message="Message_Size=50;Frequency=5;";
				DummyClient newDummy=new DummyClient(url, message);
				try {
					newDummy.post();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			
		}
}
