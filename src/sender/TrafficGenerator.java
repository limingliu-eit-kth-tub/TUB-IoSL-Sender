package sender;

import java.util.Timer;
import java.util.TimerTask;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrafficGenerator {
	
	private int Message_Size=0;
	private int Frequency=0;
	private String destinationAddress;
	private final OkHttpClient client;
	private static int count=0;

	
	public TrafficGenerator(String destinationAddress, int Message_Size, int Frequency) {
		this.Message_Size=Message_Size;
		this.Frequency=Frequency;
		this.destinationAddress=destinationAddress;
		this.client = new OkHttpClient();
	}
	
	
	public void start() {
		
		Timer timer=new Timer();
		//define the periodic task
		timer.scheduleAtFixedRate(new TimerTask() {
		       @Override
		       public void run() {
		    	   try {
		    		count+=1;
		    		Tracer tracer=Tracing.init("internet_of_services_5G_http_trace_"+count);
		   			String newMessage=new String(RandomByteGenerator.getRandomBytes(Message_Size));

		   			Scope scope = tracer.buildSpan("sender_span").startActive(true);
		   			
		   			
		   			RequestBody requestBody = new MultipartBody.Builder()
		   			        .setType(MultipartBody.FORM)
		   			        .addFormDataPart("message", newMessage)
		   			        .build();
		   			Request.Builder requestBuilder = new Request.Builder().url(destinationAddress).header("Connection", "close");
		   			requestBuilder.post(requestBody);
		   			
		   			
		   			
		   			Tags.SPAN_KIND.set(tracer.activeSpan(), Tags.SPAN_KIND_CLIENT);
		   			Tags.HTTP_METHOD.set(tracer.activeSpan(), "POST");
		   			Tags.HTTP_URL.set(tracer.activeSpan(), destinationAddress);
		   			
		   			tracer.inject(tracer.activeSpan().context(), Format.Builtin.HTTP_HEADERS, Tracing.requestBuilderCarrier(requestBuilder));
		   			 
		   			

		   			Request request = requestBuilder
		   					.build();
		   			
		   			Response response = client.newCall(request).execute();
		   			
		               if (response.code() != 200) {
		                   throw new RuntimeException("Bad HTTP result: " + response);
		               }
		               System.out.println(response.body().toString());
		             
		               
		   		}catch(Exception e) {
		   			e.printStackTrace();
		   		}
		    	  
		       }
		}, 0, 1000/Frequency);
		
	}
	
}
