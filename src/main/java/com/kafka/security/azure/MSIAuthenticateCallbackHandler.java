package com.kafka.security.azure;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.AppServiceMSICredentials;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

public class MSIAuthenticateCallbackHandler implements AuthenticateCallbackHandler {

    
    private static final AppServiceMSICredentials MSI_CREDENTIALS = new AppServiceMSICredentials(AzureEnvironment.AZURE);

    private String sbUri;

    @Override
    public void configure(final Map<String, ?> configs, final String mechanism, final List<AppConfigurationEntry> jaasConfigEntries) {
        String bootstrapServer = Arrays.asList(configs.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)).get(0).toString();
        bootstrapServer = bootstrapServer.replaceAll("\\[|\\]", "");
        final URI uri = URI.create("https://" + bootstrapServer);
        sbUri = uri.getScheme() + "://" + uri.getHost();
       
    }

    @Override
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (callback instanceof OAuthBearerTokenCallback) {
                try {
                    final OAuthBearerToken token = getOAuthBearerToken();
                    final OAuthBearerTokenCallback oauthCallback = (OAuthBearerTokenCallback) callback;
                    oauthCallback.token(token);
                } catch (InterruptedException | ExecutionException | TimeoutException | ParseException e) {
                    e.getMessage();
                }
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }

    public OAuthBearerToken getOAuthBearerToken()
            throws InterruptedException, ExecutionException, TimeoutException, IOException, java.text.ParseException {
        final String accesToken = MSI_CREDENTIALS.getToken(sbUri);
        final JWT jwt = JWTParser.parse(accesToken);
        final JWTClaimsSet claims = jwt.getJWTClaimsSet();

        return new OAuthBearerTokenImp(accesToken, claims.getExpirationTime());
    }

    @Override
    public void close() throws KafkaException {
        // NOOP
    }
}