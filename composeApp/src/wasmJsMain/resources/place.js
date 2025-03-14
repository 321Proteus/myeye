async function loadPlacePage() {
    const res = await fetch("place.html");
    const html = await res.text();

    const parser = new DOMParser();

    return parser.parseFromString(html, "text/html");
}

async function fetchPlace(id) {
    const place = new google.maps.places.Place({
        id: id,
        requestedLanguage: navigator.language,
    })

    await place.fetchFields({
        fields: [
            "displayName",
            "formattedAddress",
            "location",
            "regularOpeningHours",
            "nationalPhoneNumber",
            "websiteURI"
        ]
    });

    return place;
}

function displayPlaceData(el, data) {
    el.getElementById("place-name").textContent = data.Eg.displayName;
    el.getElementById("place-address").textContent = data.Eg.formattedAddress;
    el.getElementById("place-hours").textContent = "todo";
    el.getElementById("place-id").textContent = data.id;
    el.getElementById("place-web-page").textContent = data.Eg.websiteURI;
    el.getElementById("place-phone").textContent = data.Eg.nationalPhoneNumber;
}

async function showPlace(id) {

    var placeDocument = await loadPlacePage()

    const mapDiv = document.getElementById("map");
    mapDiv.style.width = "50%";

    const placeData = await fetchPlace(id);
    displayPlaceData(placeDocument, placeData);

    var placeDiv = document.getElementById("place");

    if (!placeDiv) {
        placeDiv = placeDocument.body.firstElementChild;
        placeDiv.style.height = mapDiv.style.height;
        placeDiv.style.top = mapDiv.style.top;

        placeDiv.id = "place";
        document.body.appendChild(placeDiv);
    }


    // placeDiv.innerHTML = `<div>${id}</div>`

}