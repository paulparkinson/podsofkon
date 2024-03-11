package podsofkon.oci;

import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vault {
    private static final Logger log = LoggerFactory.getLogger(Vault.class);

    public static String getSecreteFromVault(boolean isInstancePrincipal, String regionIdString, String secretOcid)  {
        log.info("getSecretFromVault");
        SecretsClient secretsClient = new SecretsClient(InstancePrincipalsAuthenticationDetailsProvider.builder().build());
        secretsClient.setRegion(regionIdString);
        GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest
                .builder()
                .secretId(secretOcid)
                .stage(GetSecretBundleRequest.Stage.Current)
                .build();
        GetSecretBundleResponse getSecretBundleResponse = secretsClient.getSecretBundle(getSecretBundleRequest);
        Base64SecretBundleContentDetails base64SecretBundleContentDetails =
                (Base64SecretBundleContentDetails) getSecretBundleResponse.getSecretBundle().getSecretBundleContent();
        byte[] secretValueDecoded = Base64.decodeBase64(base64SecretBundleContentDetails.getContent());
        return new String(secretValueDecoded);
    }

}

