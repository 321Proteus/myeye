function registerScroll(id, level) {
    const item = document.getElementById(id);
    item.onclick = () => {
        console.log("initialized scrolling of " + id)
        window.scrollTo({
            top: document.body.scrollHeight / level / 2,
            behavior: "smooth"
        });
    }
}

registerScroll("arrow-container", 2);
registerScroll("btn-demo", 3);
registerScroll("btn-info", 4);
registerScroll("btn-call", 5);