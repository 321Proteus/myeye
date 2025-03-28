function goBack(route, inclusive) {
    function getCurrentRoute() {
        return window.location.hash.replace("#/", "").split('/')[0];
    }

    let page = getCurrentRoute();

    function stepBack() {
        if (page === route) {
            if (inclusive) {
                window.history.go(-1);
            }
        } else {
            window.history.go(-1);
        }
    }

    function listener() {
        page = getCurrentRoute();
        if (page === route || window.history.length <= 1) {
            window.removeEventListener("hashchange", listener);
        } else {
            stepBack();
        }
    }

    window.addEventListener("hashchange", listener);
    stepBack();
}
