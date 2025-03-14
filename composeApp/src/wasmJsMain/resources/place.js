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
    console.log(data);
    el.getElementById("place-name").textContent = data.Eg.displayName;
    el.getElementById("place-address").textContent = "Adres: " + data.Eg.formattedAddress;
    el.getElementById("place-id").textContent = data.id;

    var url = data.Eg.websiteURI;
    if (url) {
        url = url.substr(0, url.includes('/') ? url.lastIndexOf('/') : url.length);
        el.getElementById("place-web-page").innerHTML = `WWW: <a href=${url}>${url}</>`;
    }

    el.getElementById("place-phone").textContent = "Telefon: " + data.Eg.nationalPhoneNumber;

    const hoursDiv = el.getElementById("place-hours");
    const hoursData = data.Eg.regularOpeningHours;

    const now = new Date();
    const period = hoursData.periods[now.getDay()-1];
    const isOpen = period && // w periods nie ma dni kiedy jest niczynne
        now.getHours() >= period.open.hour &&
        now.getHours() < period.close.hour;

    hoursDiv.innerHTML += `Godziny otwarcia (obecnie ${isOpen ? "otwarte" : "zamknięte"}):`
    console.log(hoursData)
    hoursData.weekdayDescriptions.forEach(day => {
        hoursDiv.innerHTML += "<br>" + day;
    })

}

async function showPlace(id) {

    var placeDocument = await loadPlacePage()

    const mapDiv = document.getElementById("map");
    mapDiv.style.width = "60%";

    const placeData = await fetchPlace(id);
    displayPlaceData(placeDocument, placeData);

    var placeDiv = document.getElementById("place");

    if (placeDiv) {
        document.body.removeChild(placeDiv);
    }

    placeDiv = placeDocument.body.firstElementChild;
    placeDiv.style.height = mapDiv.style.height;
    placeDiv.style.top = mapDiv.style.top;

    placeDiv.id = "place";
    document.body.appendChild(placeDiv);

}