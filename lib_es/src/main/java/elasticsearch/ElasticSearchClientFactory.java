package elasticsearch;

import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import bottle.util.Log4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@PropertiesFilePath("/elasticsearch.properties")
public class ElasticSearchClientFactory {


	@PropertiesName("elasticsearch.hosts")
	public  static String hosts;
	@PropertiesName("elasticsearch.username")
	public static String userName ;
	@PropertiesName("elasticsearch.password")
	public static String psw ;

	@PropertiesName("elasticsearch.max.conn.total")
	public static int maxConnTotal=300 ;

	@PropertiesName("elasticsearch.max.conn.per.route")
	public static int maxConnPerRoute = 100;


	private static RestHighLevelClient client = null;


	static {
		ApplicationPropertiesBase.initStaticFields(ElasticSearchClientFactory.class,properties -> {
			destroy();
			create();
		});
	}

	private static synchronized void create() {
		try {
			String[] hostArr = hosts.split(",");
			HttpHost[] httpHosts = new HttpHost[hostArr.length];
			for (int i = 0; i < hostArr.length; i++) {
				String[] hpArr = hostArr[i].split(":");
				httpHosts[i] = new HttpHost(InetAddress.getByName(hpArr[0]), Integer.parseInt(hpArr[1]));
			}

			if (httpHosts.length==0) throw new RuntimeException("elasticsearch hosts is empty");

			if (client == null) {
				final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, psw));  //es账号密码（默认用户名为elastic）

				client = new RestHighLevelClient(
						RestClient.builder(httpHosts)
								.setHttpClientConfigCallback(httpClientBuilder -> {

							httpClientBuilder.setKeepAliveStrategy((httpResponse, httpContext) -> TimeUnit.MINUTES.toMillis(3));
							httpClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setSoKeepAlive(true).build());
									httpClientBuilder.setMaxConnTotal(maxConnTotal);
									httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}));
				Log4j.info("ES 服务集群地址信息: " + hosts
						+ " ,连接池信息: "+maxConnTotal+","+maxConnPerRoute
						+ " ,创建客户端: "+ client );
			}
		} catch (Exception e) {
			Log4j.error("创建ES错误",e);
			client = null;
		}

	}

	//销毁
	public static void destroy(){
		try {
			if (client!=null){
				client.close();
				client = null;
			}
		} catch (Exception e) {
			Log4j.error("销毁ES错误",e);
		}
	}

	private static void ping() {
		try {
			client.ping(RequestOptions.DEFAULT);
		} catch (Exception e) {
			destroy();
			create();
		}
	}



	public static RestHighLevelClient getClientInstance() {
		ping();
		return client;
	}





}
