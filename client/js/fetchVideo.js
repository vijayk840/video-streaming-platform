document.addEventListener("DOMContentLoaded", async function () {
  try {
    // Fetch and display all videos
    const response = await fetch(
      "http://localhost:8082/api/v1/watch/getAllVideos"
    );
    const data = await response.json();
    const videoList = document.getElementById("videoList");

    data.data.forEach((video) => {
      const videoCard = document.createElement("div");
      videoCard.className = "col-md-4 mb-4";
      videoCard.innerHTML = `
        <div class="card video-card">
          <div class="card-body">
            <h5 class="card-title">${video.title || "No Title"}</h5>
            <p class="card-text">Author: ${video.author || "Unknown"}</p>
            <p class="card-text">${video.description || "No Description"}</p>
            <video id="video-${
              video.title
            }" class="video-player" controls style="width: 100%; height: auto;" data-key="${
        video.title
      }">
              <source src="${video.url}" type="video/mp4">
            </video>
          </div>
        </div>
      `;
      videoList.appendChild(videoCard);
    });

    // Add event listener to each video element
    document.querySelectorAll(".video-player").forEach((videoElement) => {
      videoElement.addEventListener("play", async function () {
        const key = this.getAttribute("data-key");

        // Fetch the presigned HLS URL only if it hasn't been set yet
        if (!this.getAttribute("data-hls-url")) {
          try {
            const playResponse = await fetch(
              `http://localhost:8082/api/v1/watch/getVideo?key=${encodeURIComponent(
                key
              )}`
            );
            const playData = await playResponse.json();

            if (playData.presignedUrl) {
              const hlsUrl = playData.presignedUrl;
              this.setAttribute("data-hls-url", hlsUrl); // Store the HLS URL

              // Initialize HLS.js if HLS is supported
              if (Hls.isSupported()) {
                const hls = new Hls();
                hls.loadSource(hlsUrl);
                hls.attachMedia(this);
                hls.on(Hls.Events.MANIFEST_PARSED, () => {
                  this.play();
                });
                hls.on(Hls.Events.ERROR, (event, data) => {
                  console.error("HLS.js error:", data.fatal);
                });
              } else if (this.canPlayType("application/vnd.apple.mpegurl")) {
                this.src = hlsUrl;
                this.play();
              } else {
                console.error("HLS not supported in this browser");
              }
            } else {
              console.error("Error fetching HLS stream:", playData.message);
            }
          } catch (error) {
            console.error("Error fetching HLS stream:", error);
          }
        } else {
          // Use the stored HLS URL to play the video
          const storedHlsUrl = this.getAttribute("data-hls-url");

          if (Hls.isSupported()) {
            const hls = new Hls();
            hls.loadSource(storedHlsUrl);
            hls.attachMedia(this);
            hls.on(Hls.Events.MANIFEST_PARSED, () => {
              this.play();
            });
            hls.on(Hls.Events.ERROR, (event, data) => {
              console.error("HLS.js error:", data.fatal);
            });
          } else if (this.canPlayType("application/vnd.apple.mpegurl")) {
            this.src = storedHlsUrl;
            this.play();
          }
        }
      });
    });
  } catch (error) {
    console.error("Error fetching videos:", error);
  }
});
