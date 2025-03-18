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

    let module = await loadVosklet();
    console.log("module ", module)
    // let model = await module.createModel("http://localhost:8000/vosk-model-small-en-us-0.15.tar.gz","English","vosk-model-small-en-us-0.15");
    let model = await module.createModel("http://localhost:8000/" + name + ".tar.gz", language, name);
    console.log("model ", model);
    let recognizer = await module.createRecognizer(model, ctx.sampleRate);

    recognizer.setWords(true);
    recognizer.setPartialWords(true);

    if (grammar) {
        recognizer.setGrm(grammar);
    }

    console.log("recognizer ", recognizer);

    console.log("starting micNode", micNode);

    recognizer.addEventListener("result", ev => console.log("Result: ", ev.detail));
    recognizer.addEventListener("partialResult", ev => {
        console.log("Partial result:", JSON.parse(ev.detail));
        postMessage(ev.detail, "*");
    });

    let transferer = await module.createTransferer(ctx, 128 * 150);

    // console.log("word: " + model.findWord("germany"))

    transferer.port.onmessage = ev => {
        // console.log("ev ", ev);
        recognizer.acceptWaveform(ev.data);
    }

    micNode.connect(transferer);
} catch(err) {
    console.error(err);
}
}

async function test(a, b) {
    console.log(a, b);
}

function getKey(obj, key) {
    console.log("object", obj);
    console.log("string", JSON.parse(obj));
    return Object.toString(obj);
}