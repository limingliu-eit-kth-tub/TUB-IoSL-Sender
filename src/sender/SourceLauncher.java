package sender;

import io.jaegertracing.internal.JaegerTracer;
import java.io.IOException;

import com.google.common.collect.ImmutableMap;

import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * The launcher of source server
 * @author Liming Liu
 *
 */
public class SourceLauncher {

	
	public static void main(String[] args) {
		
		int port=5000;
		
		if(args.length!=6) {
			System.out.println("Parameter string number error! Please check the command!");
			System.out.println("Correct format:");
			System.out.println("java -jar (fileName) (destination_address) (frequency) (pkt_size)");
			return;
		}
		
		System.out.print("Read command line: ");
		for(String arg:args) {
			System.out.print(arg+" ");
		}
		System.out.println();
		
		
		String destinationAddress=args[3];
		int frequency=0;
		int pkt_size=0;
		try {
			frequency = Integer.parseInt(args[4]);
			pkt_size=Integer.parseInt(args[5]);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println("Parameter error: frequency or pkt_size are not integers");
			System.out.println("Correct format:");
			System.out.println("jar (fileName) (destination_address) (frequency) (pkt_size)");
			e.printStackTrace();
		}
		System.out.println("Send to next hop: "+destinationAddress);
		System.out.println("Frequency: "+frequency);
		System.out.println("Packet size: "+pkt_size);
		SourceServer newServer=new SourceServer(port,destinationAddress,frequency,pkt_size);
		newServer.start();

	}
}
