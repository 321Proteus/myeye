class ASRProcessor extends AudioWorkletProcessor {
    constructor() {
        console.log("constructor called")
        super();
        this.audioBuffer = new Float32Array(4096);
    }

    process(inputs, outputs, parameters) {
        const input = inputs[0];

        if (input.length > 0) {
            this.audioBuffer.set(input[0]);
            
            this.port.postMessage({
                action: "process",
                audioBuffer: this.audioBuffer
            });
        }

        return true;
    }
}

registerProcessor('vosk', ASRProcessor);