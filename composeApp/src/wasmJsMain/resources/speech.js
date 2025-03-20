let recognizer;

async function initSpeech(name, language, grammar) {

    try {

    console.log("test")

    let ctx = new AudioContext({sinkId: {type: "none"}});

    let micNode = ctx.createMediaStreamSource(await navigator.mediaDevices.getUserMedia({
        video: false,
        audio: {
            sampleRate: ctx.sampleRate,
            echoCancellation: true,
            noiseSuppression: true,
            channelCount: 1
        },
    }));

    console.log(ctx);

    if (!recognizer || !recognizer.ea.isDeleted()) {
        let module = await loadVosklet();
        console.log("module ", module)
        let model = await module.createModel("http://localhost:8000/" + name + ".tar.gz", language, name);
        console.log("model ", model);
        recognizer = await module.createRecognizer(model, ctx.sampleRate);

        recognizer.setWords(true);
        recognizer.setPartialWords(true);

        if (grammar) {
            recognizer.setGrm(grammar);
        }

        console.log("recognizer ", recognizer);

        console.log("starting micNode", micNode);

        recognizer.addEventListener("result", ev => {
            var a = JSON.parse(ev.detail);
            console.log(a);
            if (a.text != "") document.getElementById("speech-result").innerText = JSON.stringify(a);
        });

        let transferer = await module.createTransferer(ctx, 128 * 150);

        transferer.port.onmessage = ev => {
            if (!recognizer.ea.isDeleted())
            recognizer.acceptWaveform(ev.data);
        }

        micNode.connect(transferer);
    } else {
        console.warn("why is it here");
    }

} catch(err) {
    console.error(err);
}
}

async function closeSpeech() {
    console.log("attempting to close recognizer");
    await recognizer?.delete(true);
    console.log("Recognizer", recognizer);
}

function initResultContainer() {
    try {
        if (!document.getElementById("speech-result")) {
            const resultDiv = document.createElement("div");
            console.log(resultDiv);
            resultDiv.id = "speech-result";
            document.body.appendChild(resultDiv);
        }
    } catch(err) {
        console.error("Result container error: ", err);
    }
}