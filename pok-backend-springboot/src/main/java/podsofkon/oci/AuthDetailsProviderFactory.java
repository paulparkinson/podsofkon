package podsofkon.oci;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AuthDetailsProviderFactory {


    @NotNull
    public  static AuthenticationDetailsProvider getAuthenticationDetailsProvider() {
        return new AuthenticationDetailsProvider() {
            @Override
            public String getKeyId() {
                return "ocid1.tenancy.oc1..aaaaasdfy33na6d3jja/ocid1.user.oc1..aaaasdfwffaa/7f:asdf:2:3f";
            }

            @Override
            public InputStream getPrivateKey() {
                String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
                        "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDGcWlNH082wT8u\n" +
                        "C5uT4VWcVa5w27bT1FgORUeuzqXHH682tM1QDBipbxvb0nThAZY9kkwaPNIfCng3\n" +
                        "DHsyRP9g1U1qB+MxtuQNqYO6negdlcQR1+toQzkCBcsaJZgr90TEBXNH3whqQAvC\n" +
                        "bfB5/TTQuA8r0V6+Igsxtg==\n" +
                        "-----END PRIVATE KEY-----\n";
                return new ByteArrayInputStream(privateKey.getBytes());
                //todo close inputstream

            }

            @Override
            public String getPassPhrase() {
                return null;
            }

            @Override
            public char[] getPassphraseCharacters() {
                return new char[0];
            }

            @Override
            public String getFingerprint() {
                return "7f:65:asdf2:3f";
            }

            @Override
            public String getTenantId() {
                return "ocid1.tenancy.oc1..aaaasdfjja";
            }

            @Override
            public String getUserId() {
                return "ocid1.user.oc1..aaaaaasdfztjwffaa";
            }
        };
    }
}
