function initMap(top, bottom) {

    var mapDiv = document.getElementById("map");

    console.log(top, bottom, mapDiv);

    mapDiv.style.top = top + "px";
    mapDiv.style.height = window.innerHeight - top - bottom + "px";

    mapDiv.style.width = "100%";
    mapDiv.style.position = "absolute";
    mapDiv.style.zIndex = "2";

    window.addEventListener("resize", () => {
//        console.log(top, bottom, mapDiv);
//        mapDiv.style.height = window.innerHeight - top - bottom + "px";
    })

    var miejsce = { lat: 37.7749, lng: -122.4194 };
    var map = new google.maps.Map(mapDiv, {
        zoom: 12,
        center: miejsce,
        mapId: "MyEye_map"
    });

    map.addListener("click", async e => {

        const req = await fetch("myeye_logo.svg")
            .then(res => res.text())
            .then(svg => {
                const parser = new DOMParser();

                const pin = new google.maps.marker.PinElement({
                    background: "red",
                    glyph: parser.parseFromString(svg, "image/svg+xml").documentElement,
                    scale: 1.5
                });

                const marker = new google.maps.marker.AdvancedMarkerElement({
                    position: e.latLng,
                    map: map,
                    content: pin.element
                });
                marker.addListener("click", e => console.log(e));
                map.panTo(e.latLng);
            })
    })

}

function test() {
    console.log("test")
}
