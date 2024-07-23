package com.ljs.ljsbackend.common.file.controller;

import com.jcraft.jsch.*;
import com.ljs.ljsbackend.common.file.dto.FileDto;
import com.ljs.ljsbackend.common.file.service.FileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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


    @RequestMapping("/api/v1/fileUpload")
    public String fileUpload(@RequestParam MultipartFile uploadFile,@RequestParam String createDate, HttpSession session2) throws IOException {

        String rfileName = "";

        try {
            //파일을 이동시키고자 하는 위치
            String des = "/home/lee/jongPjr/html";

            //데이터추출 날짜,파일이름,파일사이즈,파일타입
            String fileCreateDt = createDate;
            String fileSize = String.valueOf(uploadFile.getSize());
            String fileNm = uploadFile.getOriginalFilename();
            String fileType = uploadFile.getContentType();
            //String fileType = uploadFile.getOriginalFilename().split(".")[1];

            String extension = fileNm.substring(fileNm.lastIndexOf("."), fileNm.length()).substring(1).toLowerCase();
            UUID uuid = UUID.randomUUID();
            String newFileName = uuid.toString() + "." + extension;

            //파일이 존재 하는 위치
            String realPath = session2.getServletContext().getRealPath("/");
            File file = new File(realPath+newFileName);
            uploadFile.transferTo(file);

            //db 저장
            String path = "";

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
                return null;
            }

            // 현재 날짜 구하기
            //LocalDate now = LocalDate.now();
            // 포맷 정의
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            // 포맷 적용
            String formatedNow = LocalDate.now().format(formatter);
            if(extension.equals("mp4") || extension.equals("mov")) {
                path = "/file/movies/";
            } else {
                path = "/file/images/";
            }
            path += formatedNow;
            Map<String,Object> map = new HashMap<>();
            map.put("FILE_NM", newFileName);
            map.put("FILE_ORG_NM", fileNm);
            map.put("FILE_SEQ", 0);
            map.put("FILE_PATH", path);
            map.put("FILE_TYPE", fileType);
            map.put("FILE_SIZE", fileSize);
            map.put("FILE_CREATE_DT", fileCreateDt);
            fileService.save(map);


            JSch jsch = new JSch();

            //user, ip, port
            Session session = jsch.getSession("lee", "58.148.100.28", 22092);
            session.setConfig("StrictHostKeyChecking", "no");

            //이동시킬 서버의 password
            session.setPassword(password);

            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            des+=path;

            boolean exists = exists(sftpChannel, des);
            if(!exists) {
                sftpChannel.mkdir(des);
            }




            sftpChannel.cd(des);



            FileInputStream fis = new FileInputStream(file);
            sftpChannel.put(fis, file.getName());
            fis.close();

            sftpChannel.disconnect();
            channel.disconnect();
            session.disconnect();

            rfileName = file.getName();
            System.out.println("업로드 성공 하였습니다.");
        } catch(Exception e) {
            log.error("Exception:",e);
            //logger.error("Exception:",e);
        } finally {
            return rfileName;
        }
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
