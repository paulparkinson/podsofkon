package podsofkon.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PushImageResultCallback;
import podsofkon.XRController;
import podsofkon.cli.Microservice;
import podsofkon.cli.MicroserviceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContainerOperations {

    private static final Logger log = LoggerFactory.getLogger(ContainerOperations.class);
    String registryPassword = System.getenv("registry.password");
    String registryUrl = System.getenv("registry.url");
    String registryUsername = System.getenv("registry.username");

    public void buildAndPush(File baseDir, String appName, String serviceName, String imageversion, String javaVersion)
            throws Exception {
//        String imageName = appName + "/" + serviceName;
        String imageName = appName + "-" + serviceName;
        log.debug("buildAndPush " +
                "registryUrl = " + registryUrl + " registryUsername = " + registryUsername + " baseDir = " + baseDir +
                ", imageName = " + imageName + ", imageversion = " + imageversion + ", javaVersion = " + javaVersion);
        createDockerFileIfNoneExists(appName, serviceName, baseDir, javaVersion);
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withRegistryUrl(registryUrl)
                .withRegistryPassword(registryPassword)
                .withRegistryUsername(registryUsername)
                .withDockerHost("tcp://localhost:2375")
                .build();
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
        log.debug("buildAndPush dockerClient:" + dockerClient);
        buildAndPushImage(dockerClient, baseDir, appName, serviceName,
                XRController.registryUrl + "/" + imageName, imageversion);
    }

    void createDockerFileIfNoneExists(String appName, String serviceName, File baseDir, String javaVersion)
            throws Exception {
        File dockerFile = new File(baseDir + "/" + "Dockerfile");
        if (dockerFile.createNewFile()) {
            log.debug("dockerFile created: " + dockerFile.getName());
            PrintStream printStream = new PrintStream(dockerFile);
            printStream.println("FROM " + javaVersion );
            printStream.println("ENTRYPOINT [\"java\", \"-jar\", \"/usr/share/springservice/springservice.jar\"]");
            MicroserviceDetails microserviceDetails =
                    XRController.microserviceDetailsMap.get(new Microservice(appName, serviceName));
            if (microserviceDetails == null) throw new Exception("no microserviceDetailsMap entry exists for " +
                    "appName:" + appName + ", serviceName:" + serviceName);
            log.debug("microserviceDetails:" + microserviceDetails.getMicroserviceFilePath());
            printStream.println("ADD " +
                    microserviceDetails.getMicroserviceJarFile().getOriginalFilename() +
                    " /usr/share/springservice/springservice.jar");
            printStream.close();
            log.debug("Dockerfile written");
        } else {
            log.debug("Dockerfile already exists");
        }
    }

    public String buildAndPushImage(DockerClient dockerClient, File baseDir,
                                    String appName, String serviceName,
                                    String imageName, String imageversion) throws InterruptedException {
        String imageNamePlusVersionTag = imageName + ":" + imageversion;
        log.debug("buildAndPush imageNamePlusVersionTag:" + imageNamePlusVersionTag + " baseDir:" + baseDir.getPath());
        String imageId = dockerClient.buildImageCmd(baseDir)
                .withPull(true)
                .withNoCache(true)
                .withTag(imageNamePlusVersionTag)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
//        InspectImageResponse image  = dockerClient.inspectImageCmd(imageId).exec();
        log.debug("imageId:" + imageId);
        InspectImageResponse image
                = dockerClient.inspectImageCmd(imageId).exec();
        log.debug("built image:" + image);
        log.debug("built image.getRepoTags():" + image.getRepoTags());
        dockerClient.tagImageCmd(imageId, imageName, imageversion).exec();
        log.debug("tag command complete, pushing image...");
        AuthConfig authConfig = new AuthConfig().withUsername(registryUsername).withPassword(registryPassword)
                .withRegistryAddress(registryUrl);
//        log.debug("auth status:" + dockerClient.authCmd().withAuthConfig(authConfig).exec().getStatus() ); //auth status:Login Succeeded
        dockerClient.pushImageCmd(imageName)
                .withTag(imageversion)
                .withAuthConfig(authConfig)
                .exec(new OracleSpringPushImageResultCallback())
                .awaitCompletion(900, TimeUnit.SECONDS);
        log.debug("push command complete for imageNamePlusVersionTag:" + imageNamePlusVersionTag);
//        listImages(dockerClient, false);
        return imageId;
    }

    class OracleSpringPushImageResultCallback extends PushImageResultCallback {

        @Override
        public void onNext(PushResponseItem item) {
            super.onNext(item);
        }

        @Override
        public void onStart(Closeable stream) {
            super.onStart(stream);
        }

        @Override
        public void onError(Throwable throwable) {
            log.warn("onError during push:" + throwable);
            super.onError(throwable);
        }

        @Override
        public void onComplete() {
            log.warn("onComplete during push");
            super.onComplete();
        }
    }

    private static void listImages(DockerClient dockerClient, boolean isCleanUp) {
        List<Image> images = dockerClient.listImagesCmd().exec();
        log.debug("listImages images size:" + images.size());
        int currentImage = 0;
        for (Image image : images) {
            currentImage++;
            log.debug("listImages image[" + currentImage + "]:" + image.toString());
            String[] repoTags = image.getRepoTags();
            log.debug("listImages image[" + currentImage + "]: getRepoTags.length:" + repoTags.length);
            for (int i = 0; i < repoTags.length; i++) {
                log.debug("listImages image[" + currentImage + "]:  repoTags[" + i + "] =" + repoTags[i]);
            }
            log.debug("listImages image getId:" + image.getId());
        }
    }

    private static void deleteImages(DockerClient dockerClient, boolean isCleanUp) {
        List<Image> images = dockerClient.listImagesCmd().exec();
        log.debug("deleteImages images size:" + images.size());
        for (Image image : images) {
            if (isCleanUp) {
                dockerClient.removeImageCmd(image.getId()).withForce(true).exec();
            }
            log.debug("deleteImages image getId:" + image.getId());
        }
    }

}
