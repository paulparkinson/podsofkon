package podsofkon.objectstorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import podsofkon.XRApplication;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
public class XRObjectStoreController {

    private static Logger log = LoggerFactory.getLogger(XRObjectStoreController.class);
//
//    @GetMapping("/getObject")
//    public String getObject(@RequestParam("bucket") String bucket, @RequestParam("object") String object)
//    throws Exception {
//        log.debug("getObject bucket = " + bucket + ", object = " + object);
//        return new StorageStreamService().getBytesFromStorage(bucket, object);
//    }



    @Autowired
    StorageStreamService storageService;

    private final String BUCKET_NAME = "xrbucket";

    @ResponseBody
    @RequestMapping(value = "/objectstoredownload/{file}", method = RequestMethod.GET)
    public byte[] objectstoredownload(@PathVariable String file,
                              @RequestParam(value = "region", defaultValue =  XRApplication.REGION, required = false) String _region)
            throws Exception {
        return storageService.getBytesFromStorage(BUCKET_NAME, file, _region, XRApplication.OBJECTSTORE_NAMESPACENAME);
    }
    @ResponseBody
    @RequestMapping(value = "/image/{file}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] streamImage(@PathVariable String file,
                              @RequestParam(value = "region", defaultValue =  XRApplication.REGION, required = false) String _region)
            throws Exception {

        return storageService.getBytesFromStorage(BUCKET_NAME, file, _region, XRApplication.OBJECTSTORE_NAMESPACENAME);
    }

    @RequestMapping(value = "/video/{file}", method = RequestMethod.GET)
    public Mono<ResponseEntity<byte[]>> streamVideo(
            @PathVariable String file,
            @RequestParam(value = "region", defaultValue = XRApplication.REGION, required = false) String _region)
            throws Exception {
        byte[] bytes = storageService.getBytesFromStorage(BUCKET_NAME, file, _region, XRApplication.OBJECTSTORE_NAMESPACENAME);
        return Mono.just(ResponseEntity.status(HttpStatus.OK).header("Content-Type", "video/mp4")
                .header("Content-Length", String.valueOf(bytes.length)).body(bytes));
    }


    @PostMapping("/objectstoreupload")
    public String objectstoreupload(HttpServletRequest request, @RequestParam("id") String id, @RequestParam("file") MultipartFile file)
            throws Exception {
        log.info("got objectstore file, now analyze " + "id = " + id + ", file = " + file);
        Principal principal = request.getUserPrincipal();
        log.info("principal " + principal);
        if(principal!=null) log.info("principal.getName() " + principal.getName());
//        log.info("client ip: " + getClientIpAddress(request)); //just gives 10.244.0.1
        if (!id.equalsIgnoreCase("deRez") && !id.equalsIgnoreCase("deRezConfig"))
            throw new Exception("invalid id");
//        new ObjectStorage().upload(file, file.getBytes(), id.equalsIgnoreCase("deRezConfig"));
        return new ObjectStorageService().upload(file);
    }

}
