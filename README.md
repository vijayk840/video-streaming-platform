Adaptive Bitrate Video Streaming Platform with Transcoding

This project has three microservices -  Upload Service, Transcode Service, and Watch Service and one client

-client - client gives the option to upload the video ,it divides the video into multiple chunks and make the post request to upload service.

-Upload Service: Utilized Amazon S3’s multipart upload to efficiently upload video files in chunks, Stored
video metadata in MySQL and published events to Kafka for processing.

– Transcode Service: Implemented Kafka to receive events about uploaded videos from the Upload Service.
Transcoded videos using HLS and FFmpeg, converting them into multiple formats for adaptive bitrate
streaming, and uploaded the transcoded videos back to S3.

– Watch Service: Provided video listings on the frontend with support for adaptive bitrate streaming,
generating presigned URLs for secure video playback from S3.

Apart from these microservices authentication and authorization has been handled in upload service itself - Implemented Spring Security with JWT for secure user authentication
and authorization

