async function initMap(top, bottom) {

    const oko = await initPin();

    var miejsce = { lat: 50, lng: 50 };

    var mapDiv = document.getElementById("map");

    mapDiv.style.top = top + "px";
    mapDiv.style.height = window.innerHeight - top - bottom + "px";
    mapDiv.style.width = "100%";
    mapDiv.style.position = "absolute";
    mapDiv.style.zIndex = "2";

    const map = new google.maps.Map(mapDiv, {
        zoom: 12,
        center: miejsce,
        mapId: "MyEye_map"
    });

    const marker = new google.maps.marker.AdvancedMarkerElement({
        position: miejsce,
        map: map
    });

    marker.addListener("gmp-click", e => console.log(marker));

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(pos => {
            miejsce.lat = pos.coords.latitude;
            miejsce.lng = pos.coords.longitude;
            map.panTo(miejsce);
        }, e => geoError(e));
    }

    map.addListener("click", async e => {
        const json = e.latLng.toJSON()
        miejsce = { lat: json.lat, lng: json.lng }

        const places = await searchPlaces(miejsce);
        places.forEach(place => {
            const placeMarker = new google.maps.marker.AdvancedMarkerElement({
                position: place.location,
                map: map,
                content: new google.maps.marker.PinElement({
                    background: "red",
                    glyph: oko,
                    scale: 1.5
                }).element
            })
            placeMarker.addListener("gmp-click", () => console.log(place));
        });

        marker.position = new google.maps.LatLng(json.lat, json.lng);

        map.panTo(e.latLng);

    })

}

async function searchPlaces(pos) {
    const request = {
        textQuery: "okulistyczny",
        fields: ["displayName", "location", "id"],
        locationBias: pos,
        language: navigator.language,
        maxResultCount: 10
        // isOpenNow: true,
        // minRating: 3.2
    };

    const { places } = await google.maps.places.Place.searchByText(request);
    return places
}

function geoError(err) {
    switch(err.code) {
        case err.PERMISSION_DENIED:
            console.error("Geolocation data permission denied"); break;
        case err.POSITION_UNAVAILABLE:
            console.error("Geolocation data is unavailable"); break;
        case err.TIMEOUT:
            console.error("Geolocation request timed out"); break;
        case err.UNKNOWN_ERROR:
            console.error("Geolocation data unknown error"); break;
    }
}

async function initPin() {
    const res = await fetch("myeye_logo.svg");
    const svg = await res.text();

    const parser = new DOMParser();

    return parser.parseFromString(svg, "image/svg+xml").documentElement;
}