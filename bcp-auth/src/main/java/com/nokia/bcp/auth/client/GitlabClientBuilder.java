package com.nokia.bcp.auth.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.nokia.bcp.auth.entity.DevopsGitlab;
import com.nokia.bcp.auth.repository.GitlabRepository;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

@Component
public class GitlabClientBuilder {

	private static final Logger logger = LoggerFactory.getLogger(GitlabClientBuilder.class);

	private static GitlabRepository gitlabRepository;

	private static HttpClient httpClient;

	public static GitlabClient getOrCreateByGroup(long groupId) {
		DevopsGitlab gitlabServer = gitlabRepository.findByGroupId(groupId);
		long gitlabId = null == gitlabServer ? -1 : gitlabServer.getId();
		return getOrCreate(gitlabId);
	}

	public static GitlabClient getOrCreate(long gitlabId) {
		if (!gitlabClientMap.containsKey(gitlabId)) {
			synchronized (gitlabClientMap) {
				if (!gitlabClientMap.containsKey(gitlabId)) {
					DevopsGitlab gitlabServer = gitlabRepository.findById(gitlabId).get();
					ApacheHttpClient apacheHttpClient = new ApacheHttpClient(httpClient);
					GitlabClient gitlabClient = Feign.builder().decoder(new JacksonDecoder())
							.encoder(new JacksonEncoder()).options(new Request.Options(10000, 30000))
							.retryer(new Retryer.Default(5000, 5000, 3)).client(apacheHttpClient)
							// .target(GitLabClient.class, localGitLabUrl);
							.target(GitlabClient.class, gitlabServer.getApiBaseUrl());
					gitlabClientMap.put(gitlabId, gitlabClient);
					gitlabTokenMap.put(gitlabId, gitlabServer.getPrivateToken());
					logger.info("Create GitLabClient for " + gitlabServer.getApiBaseUrl());
				}
			}
		}

		return gitlabClientMap.get(gitlabId);
	}

	public static String getTokenByGroup(long groupId) {
		DevopsGitlab gitlabServer = gitlabRepository.findByGroupId(groupId);
		return gitlabTokenMap.get(null == gitlabServer ? -1 : gitlabServer.getId());
	}

	public static String getToken(long gitLabId) {
		return gitlabTokenMap.get(gitLabId);
	}

	public GitlabRepository getGitlabRepository() {
		return gitlabRepository;
	}

	@Autowired
	public void setGitlabRepository(GitlabRepository gitlabRepository) {
		GitlabClientBuilder.gitlabRepository = gitlabRepository;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	@Autowired
	public void setHttpClient(HttpClient httpClient) {
		GitlabClientBuilder.httpClient = httpClient;
	}

	@Bean
	public HttpClient httpClient() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext ctx = SSLContext.getInstance("SSL");
		X509TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		// HttpClient client = HttpClientBuilder().build();
		// Client client = new Client.Default(ctx.getSocketFactory(), new
		// HostnameVerifier() {
		// public boolean verify(String hostname, SSLSession sslSession) {
		// return true;
		// }
		// });
		CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(ctx)
				.setSSLHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession sslSession) {
						return true;
					}
				}).build();
		return httpClient;
	}

	private static Map<Long, GitlabClient> gitlabClientMap = new HashMap<Long, GitlabClient>();
	private static Map<Long, String> gitlabTokenMap = new HashMap<Long, String>();
}
