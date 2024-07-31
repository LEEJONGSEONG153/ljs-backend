package com.ljs.ljsbackend.common.file.controller;

import com.jcraft.jsch.*;
import com.ljs.ljsbackend.common.file.dto.FileDto;
import com.ljs.ljsbackend.common.file.service.FileService;
import com.ljs.ljsbackend.response.DefaultRes;
import com.ljs.ljsbackend.response.ResponseMessage;
import com.ljs.ljsbackend.response.StatusCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @Value("${ssh.password}")
    private String password;

    @Value("${ssh.id}")
    private String id;


    @RequestMapping("/api/v1/fileUpload")
    public ResponseEntity<Object> fileUpload(
                             @RequestParam(value="uploadFiles") List<MultipartFile> uploadFiles
                            ,@RequestParam(value="createDates") List<String> createDates
                            ) {

        try {
            MultipartFile uploadFile;
            String createDate;

            for(int i=0; i<uploadFiles.size(); i++) {
                uploadFile = uploadFiles.get(i);
                createDate = createDates.get(i);

                //저장 base 위치
                String des = "/home/lee/jongPjr/html";
                String des2 = "/home/lee/jongPjr/html";

                //1.데이터추출
                String fileCreateDt = createDate;
                String fileSize = String.valueOf(uploadFile.getSize());
                String fileType = uploadFile.getContentType();
                String fileNm = uploadFile.getOriginalFilename();
                String extension = fileNm.substring(fileNm.lastIndexOf(".") + 1, fileNm.length()).toLowerCase();
                UUID uuid = UUID.randomUUID();
                String newFileName = uuid + "." + extension;
                UUID uuid2 = UUID.randomUUID();
                String newFileName2 = uuid2 + "." + extension;

                String path = "";
                // 포맷 정의
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                // 포맷 적용
                String formatedNow = LocalDate.now().format(formatter);
                if (extension.equals("mp4") || extension.equals("mov")) {
                    path = "/file/movies/";
                } else {
                    path = "/file/images/";
                }
                path += formatedNow;
                des += path;
                Map<String, Object> map = new HashMap<>();
                map.put("FILE_NM", newFileName);
                map.put("FILE_ORG_NM", fileNm);
                map.put("FILE_SEQ", 0);
                map.put("FILE_PATH", path);
                map.put("FILE_TYPE", fileType);
                map.put("FILE_SIZE", fileSize);
                map.put("FILE_CREATE_DT", fileCreateDt);

                //2.확장자 체크
                if (!checkExtension(extension)) {
                    return null;
                }
                ;

                //3.db 저장
                fileService.save(map);

                //5.실제 서버에 저장
                saveFileToServer(uploadFile,des,newFileName);

                //6.이미지 리사이즈
                InputStream inputStream = checkImageSize(uploadFile, extension);

                if (extension.equals("mp4") || extension.equals("mov")) {
                    return null;
                } else {
                    path = "/file/imagesThumbnail/";

                    path += formatedNow;
                    des2 += path;

                    //3.db 저장
                    map.put("FILE_NM", newFileName2);
                    map.put("FILE_ORG_NM", fileNm);
                    map.put("FILE_SEQ", 1);
                    map.put("FILE_PATH", path);
                    map.put("FILE_TYPE", fileType);
                    map.put("FILE_SIZE", fileSize);
                    map.put("FILE_CREATE_DT", fileCreateDt);
                    //db 저장
                    fileService.save(map);
                    //실제 서버에 저장
                    saveFileToServer(inputStream,des2,newFileName2);
                }

                System.out.println("업로드 성공 하였습니다.");
            }
            return new ResponseEntity(DefaultRes.res(StatusCode.OK, ResponseMessage.CONNECT_SUCCESS, null), HttpStatus.OK);
        } catch(Exception e) {
            log.error("Exception:",e);
        } finally {
            return null;
        }

    }

    private <T> void saveFileToServer(T file, String des, String newFileName) throws Exception {
        JSch jsch = new JSch();

        //user, ip, port
        Session session = jsch.getSession(id, "58.148.100.28", 22092);
        session.setConfig("StrictHostKeyChecking", "no");

        //이동시킬 서버의 password
        session.setPassword(password);

        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp sftpChannel = (ChannelSftp) channel;


        boolean exists = exists(sftpChannel, des);
        if(!exists) {
            sftpChannel.mkdir(des);
        }

        sftpChannel.cd(des);

        InputStream inputStream = null;

        if(file instanceof MultipartFile) {
            inputStream = ((MultipartFile) file).getInputStream();
        } else if(file instanceof InputStream) {
            inputStream = (InputStream) file;
        }

        sftpChannel.put(inputStream, newFileName);
        sftpChannel.disconnect();
        channel.disconnect();
        session.disconnect();
    }


    private Boolean checkExtension(String extension) throws Exception {
        String[] PERMISSION_FILE_EXT_ARR = {"GIF", "JPEG", "JPG", "PNG", "BMP", "MP4", "MOV"};
        Boolean permission = false;
        for(int i=0; i<PERMISSION_FILE_EXT_ARR.length; i++) {
            if(extension.equals(PERMISSION_FILE_EXT_ARR[i].toLowerCase())){
                permission = true;
                break;
            }
        }
        if(!permission){
            System.out.println("확장자 체크를 통과하지 못했습니다. 가능한 확장자 :GIF, JPEG, JPG, PNG, BMP, MP4");
            return false;
        }
        return true;
    }

    private InputStream checkImageSize(MultipartFile file, String extension) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
//            int width = bufferedImage.getWidth();
//            int height = bufferedImage.getHeight();

            int targetWidth = 1200;  // 원하는 너비
            BufferedImage resizedImage = Scalr.resize(bufferedImage, targetWidth);

            // 리사이즈된 BufferedImage를 InputStream으로 변환
            InputStream inputStream = bufferedImageToInputStream(resizedImage, extension);
            return inputStream;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream bufferedImageToInputStream(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        baos.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }


    //디렉토리 존재
    public static boolean exists(ChannelSftp channelSftp, String uploadPath) {
        Vector res = null;
        try {
            res = channelSftp.ls(uploadPath);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
        }
        return res != null && !res.isEmpty();
    }

    @RequestMapping("/api/v1/file/getList")
    public List<FileDto> getList(@RequestBody Map<String,Object> paramMap) {
        List<FileDto> list =  fileService.getList(paramMap);
        return list;

    }
}
