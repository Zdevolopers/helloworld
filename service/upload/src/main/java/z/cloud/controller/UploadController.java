package z.cloud.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import z.cloud.entity.Message;
import z.cloud.utils.FDFSUtils;

import java.io.File;
import java.util.UUID;

/**
 * @author zming
 * @version 1.0
 * @date 2019/9/19
 */
@Api(value = "上传接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    private static FDFSUtils fdfsUtils = new FDFSUtils("config/fdfs_client.conf");

    @Value("${fdfs.addr}")
    private String fileAddr;

    @ApiOperation(value = "上传",response = String.class)
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public Message upload(MultipartFile file){
        String addr;
        try {
            String fileName = file.getOriginalFilename();
            String extName = fileName.substring(fileName.lastIndexOf(".")+1);
            addr = fdfsUtils.uploadFile( file,extName,null);
            System.out.println("图片的访问路径为： " + fileAddr + addr);
        } catch (Exception e) {
            return Message.error("上传失败，原因：" + e.getMessage());
        }
        return Message.success(fileAddr + addr);
    }

    @ApiOperation(value = "大文件上传接口",response = String.class)
    @RequestMapping(value = "/uploadBig",method = RequestMethod.POST)
    public Message uploadBig(MultipartFile file){
        String addr;
        try {
            String fileName = file.getOriginalFilename();
            String extName = fileName.substring(fileName.lastIndexOf("."));
            addr = fdfsUtils.uploadBigFile(file,extName,null);
        } catch (Exception e) {
            return Message.error("上传大文件失败，原因：" + e.getMessage());
        }
        return Message.success(fileAddr + addr);
    }

    @ApiOperation(value = "下载")
    @RequestMapping(value = "/download",method = RequestMethod.POST)
    public Message download(String url,String local){
        File file = new File(local);
        if(!file.exists()){
            file.mkdirs();
        }
        String fileName = UUID.randomUUID().toString();
        String result = fdfsUtils.download(url, fileName, local);
        return new Message(10000,result,null);
    }


}
