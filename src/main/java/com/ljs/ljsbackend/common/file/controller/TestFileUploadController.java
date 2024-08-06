package com.ljs.ljsbackend.common.file.controller;

import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Vector;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class TestFileUploadController {

    @Value("${ssh.password}")
    private String password;

    @Value("${ssh.id}")
    private String id;

    @RequestMapping("/convert-and-upload")
    public String fileUpload(
            @RequestParam(value="uploadFiles") List<MultipartFile> uploadFiles
            ,@RequestParam(value="createDates") List<String> createDates
    ) {







        try {
            MultipartFile uploadFile;
            String createDate;

            for(int i=0; i<uploadFiles.size(); i++) {
                uploadFile = uploadFiles.get(i);
                createDate = createDates.get(i);


                // Create temporary files for input and output
                File inputFile = File.createTempFile("input", ".heic");
                File outputFile = File.createTempFile("output", ".jpg");

                // Transfer the input file content to the temporary input file
                uploadFile.transferTo(inputFile);

                // Prepare the command to run ImageMagick for conversion
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("magick", "convert", inputFile.getAbsolutePath(), outputFile.getAbsolutePath());

                Process process = processBuilder.start();

                StringBuilder output = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    StringBuilder errorOutput = new StringBuilder();
                    while ((line = errorReader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                    return "Error during image conversion: " + errorOutput.toString();
                }

                // Read the converted image file into a byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (InputStream is = new FileInputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, length);
                    }
                }

                // Upload the converted image to the server using JSch
                String result = uploadToServer(new ByteArrayInputStream(baos.toByteArray()), outputFile.getName());

                // Clean up temporary files
                inputFile.delete();
                outputFile.delete();

                return result;

            }

        } catch (IOException | InterruptedException e) {
            return "Exception: " + e.getMessage();
        }

        return null;
    }

    private String uploadToServer(InputStream inputStream, String remoteFileName) {



    String des = "/home/lee/jongPjr/html/file/images/20240806";






        try {
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

            sftpChannel.put(inputStream, "newFile");
            sftpChannel.disconnect();
            channel.disconnect();
            session.disconnect();






            return "File uploaded successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during file upload: " + e.getMessage();
        } finally {

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



}
