package podsofkon.oci;

import com.oracle.bmc.auth.*;
import com.oracle.bmc.artifacts.ArtifactsClient;
import com.oracle.bmc.artifacts.model.*;
import com.oracle.bmc.artifacts.requests.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateContainerRepos {
    private static final Logger log = LoggerFactory.getLogger(CreateContainerRepos.class);

    public String createReposForImage(String compartmentid, String reposName)  {
        log.info("getSecretFromVault compartmentid:" + compartmentid + " reposName:" + reposName);
        ArtifactsClient client = new ArtifactsClient(InstancePrincipalsAuthenticationDetailsProvider.builder().build());
            CreateContainerRepositoryDetails createContainerRepositoryDetails = CreateContainerRepositoryDetails.builder()
                    .compartmentId(compartmentid)
                    .displayName(reposName )
                    .isImmutable(false)
                    .isPublic(false)
                    .readme(ContainerRepositoryReadme.builder()
                            .content("springapp readme")
                            .format(ContainerRepositoryReadme.Format.TextPlain).build()).build();
            CreateContainerRepositoryRequest createContainerRepositoryRequest = CreateContainerRepositoryRequest.builder()
                    .createContainerRepositoryDetails(createContainerRepositoryDetails)
//                    .opcRequestId("RAFQUYLW8KBGSAOM6MPJ<unique_ID>")
//                    .opcRetryToken("EXAMPLE-opcRetryToken-Value")
                    .build();
            return client.createContainerRepository(createContainerRepositoryRequest).toString();
        }
}
