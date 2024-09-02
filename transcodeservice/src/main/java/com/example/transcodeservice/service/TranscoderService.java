package com.example.transcodeservice.service;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

@Service
public class TranscoderService {
    private final S3Service s3Service;
    public TranscoderService( S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public void transcodeVideoToHLS(String Key) throws IOException,InterruptedException{
        String localFilePath = "local.mp4";
        String hlsFolder = "hls";

        //download the video from s3 with given key
        s3Service.downloadFileFromS3(Key, localFilePath);

        //generate HLS Files of the downloaded video
        generateHLSFiles(localFilePath, hlsFolder,Key);

        //delete the locally downloaded video file
        Files.deleteIfExists(Paths.get(localFilePath));

        //upload hls files one by one and delete it;
        uploadHLSFilesToS3AndDeleteLocally(hlsFolder);


    }

    private void uploadHLSFilesToS3AndDeleteLocally(String hlsFolder) throws IOException {
        System.out.println("Uploading media m3u8 playlists and ts segments to S3");
        File folder = new File(hlsFolder);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try{
                        s3Service.uploadFileToS3AndDeleteLocally(file);
                    }catch(Exception e){
                        System.out.println("error uploading and deleting file");
                    }

                }
            }
        }
    }


    private void generateHLSFiles(String inputFilePath, String hlsFolder,String Key) throws IOException, InterruptedException{
        if (!Files.exists(Paths.get(hlsFolder))) {
            Files.createDirectory(Paths.get(hlsFolder));
        }


        List<String> resolutions= Arrays.asList("320x180", "854x480", "1280x720");
        List<String> videoBitrates = Arrays.asList("500k", "1000k", "2500k");
        List<String> audioBitrates = Arrays.asList("64k", "128k", "192k");
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists()) {
            System.out.println("No file location here");
            throw new FileNotFoundException("Input file not found: " + inputFilePath);
        }


        for(int i=0;i<resolutions.size();i++)
        {
            String resolution= resolutions.get(i);
            String videoBitrate = videoBitrates.get(i);
            String audioBitrate = audioBitrates.get(i);

            //specify the name of the master playlist file for a given resolution, which lists the .ts segment files for that resolution
            String outputM3U8 = hlsFolder + "/" + Key +  "_output_" + resolution + ".m3u8";

            //specify the naming pattern for the .ts segment files generated during the HLS conversion. The %03d format specifier will be replaced by numbers to uniquely name each segment file
            String segmentFileName = hlsFolder + "/" + Key +  "_output_"+ resolution + "_%03d.ts";

            //processBuilder helps to configure and control the execution of external commands, handle input and output streams, and manage environment variables and working directories.
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",  // invokes the ffmpeg tool
                    "-i", inputFilePath, //video file to transcode
                    "-c:v", "h264",      //h256 codec for video compression
                    "-b:v", videoBitrate, //videobitrate to use
                    "-c:a", "aac",        //aac is audio codec to use
                    "-b:a", audioBitrate, //audio bitrate
                    "-vf", "scale=" + resolution, //-vf specifies a video filter. scale= followed by resolution (e.g., 320x180, 854x480) scales the video to the specified resolution.
                    "-f", "hls", //-f specifies the format. hls stands for HTTP Live Streaming, which will be used to output the video in HLS format.
                    "-hls_time", "10", // 10-second segments
                    "-hls_list_size", "0",
                    "-hls_segment_filename", segmentFileName,
                    outputM3U8
            );


            //redirects the standard error stream of the process to the standard output stream
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Successfully transcoded video to HLS format with resolution " + resolution);
            } else {
                System.out.println("Failed to transcode video to HLS format with resolution " + resolution);
            }
        }
        try{
            createMasterPlaylist(resolutions, hlsFolder,Key);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    //creating the master file that will be used for adaptive bitstreaming
    private void createMasterPlaylist(List<String> resolutions, String hlsFolder,String Key) throws IOException{
        File masterPlaylistFile = new File(hlsFolder + "/" + Key + "_master.m3u8");
        try(FileWriter writer = new FileWriter(masterPlaylistFile)){
            writer.write("#EXTM3U\n");
            for (String resolution : resolutions) {
                String outputM3U8 = "output_" + resolution + ".m3u8";
                int bandwidth = getBandwidth(resolution);
                writer.write("#EXT-X-STREAM-INF:BANDWIDTH=" + bandwidth + ",RESOLUTION=" + resolution + "\n");
                writer.write(outputM3U8 + "\n");
            }
        }
        System.out.println("Master playlist created at " + masterPlaylistFile.getPath());
    }

    private int getBandwidth(String resolution) {
        switch (resolution) {
            case "320x180":
                return 676800;
            case "854x480":
                return 1353600;
            case "1280x720":
                return 3230400;
            default:
                return 0;
        }
    }

}
