package com.ljs.ljsbackend.common.file.controller;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.ljs.ljsbackend.common.file.dto.FileDto;
import com.ljs.ljsbackend.common.file.service.FileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @Value("${ssh.password}")
    private String password;


    @RequestMapping("/api/v1/fileUpload")
    public String fileUpload(@RequestParam MultipartFile uploadFile, HttpSession session2) throws IOException {

        String rfileName = "";

        try {

            //파일을 이동시키고자 하는 위치
            String des = "/home/lee/jongPjr/html/movieFile";

            //파일이 존재 하는 위치
            String realPath = session2.getServletContext().getRealPath("/");
            File file = new File(realPath+uploadFile.getOriginalFilename());
            uploadFile.transferTo(file);

            //db 저장
            String fileName = uploadFile.getOriginalFilename();
            String path = des;
            Map<String,Object> map = new HashMap<>();
            map.put("FILE_NM", fileName);
            map.put("FILE_SEQ", 0);
            map.put("FILE_PATH", "movieFile");
            fileService.save(map);


            JSch jsch = new JSch();

            //user, ip, port
            Session session = jsch.getSession("lee", "58.148.100.28", 22092);
            session.setConfig("StrictHostKeyChecking", "no");

            //이동시킬 서버의 password
            //session.setPassword("password");
            session.setPassword(password);

            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
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

    @RequestMapping("/api/v1/file/getList")
    public List<FileDto> getList() {
        List<FileDto> list =  fileService.getList();
        return list;

    }
}
