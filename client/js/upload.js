document
  .getElementById("uploadForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();
    const fileInput = document.getElementById("file");
    const file = fileInput.files[0];
    if (!file) {
      alert("Please select a file");
      return;
    }

    const chunkSize = 10 * 1024 * 1024;
    const numChunks = Math.ceil(file.size / chunkSize);

    //initiating multipart upload
    const uploadId = await initiateMultipartUpload(file.name);

    const uploadPromises = [];
    for (let i = 0; i < numChunks; i++) {
      const start = i * chunkSize;
      const end = Math.min(start + chunkSize, file.size);
      const chunk = file.slice(start, end);
      uploadPromises.push(uploadChunk(uploadId, i, chunk));
    }

    try {
      const eTags = await Promise.all(uploadPromises);
      await completeMultipartUpload(file.name, uploadId, eTags);
    } catch (error) {
      console.error("upload failed", error);
    }

    async function initiateMultipartUpload(fileName) {
      const response = await fetch(
        "http://localhost:8080/api/v1/upload/initiate",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ fileName }),
        }
      );
      const data = await response.json();
      return data.data.uploadId;
    }

    async function uploadChunk(uploadId, partNumber, chunk) {
      const formData = new FormData();
      formData.append("uploadId", uploadId);
      formData.append("chunkIndex", partNumber);
      formData.append("chunk", chunk);
      formData.append("fileName", file.name);

      const response = await fetch(
        "http://localhost:8080/api/v1/upload/uploadChunks",
        {
          method: "POST",
          body: formData,
        }
      );

      const data = await response.json();
      return data.data.etag;
    }

    async function completeMultipartUpload(fileName, uploadId, eTags) {
      const response = await fetch(
        "http://localhost:8080/api/v1/upload/completeUpload",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ fileName, uploadId, eTags }),
        }
      );

      return response.json();
    }
  });
