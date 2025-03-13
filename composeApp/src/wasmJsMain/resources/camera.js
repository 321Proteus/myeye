function startCamera(videoId) {
    const video = document.getElementById(videoId);
    if (!video) {
        console.error("Nie znaleziono elementu video o ID:", videoId);
        return;
    }

    navigator.mediaDevices.getUserMedia({ video: true })
        .then(function (stream) {
            video.srcObject = stream;
            video.play();
        })
        .catch(function (error) {
            console.error("Błąd dostępu do kamery: ", error);
        });
}

console.log(FaceDetection.FaceDete)

const faceDetector = new FaceDetection.FaceDetector({
    locateFile: (file) =>
        "https://cdn.jsdelivr.net/npm/@mediapipe/face_detection/" + file,
});

faceDetector.setOptions({
    model: "short", // TODO: check if "full" suits better
    minDetectionConfidence: 0.5,
});

async function detectFaces(videoId) {
    const video = document.getElementById(videoId);

    async function processFrame() {
        if (video.readyState >= 2) {
            const results = await faceDetector.send({ image: video });

            if (results.detections) {
                results.detections.forEach((detection) => {
                    console.log(detection.boundingBox)
                });
            }
        }
        requestAnimationFrame(processFrame);
    }

    processFrame();
}

