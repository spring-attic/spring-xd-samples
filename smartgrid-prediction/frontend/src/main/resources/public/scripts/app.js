//

// TZ workaround for now
var offset = -(new Date().getTimezoneOffset()/60);
var sOffset = offset < 0 ? "-" : "+";
sOffset += offset.length == 2 ? Math.abs(offset) : "0" + Math.abs(offset);
sOffset += ":00";

var appData = {

    currentHouseSelection: -1

    , minDate : new Date("2013-08-31T22:00:00.000Z")
    , maxDate: new Date("2013-09-01T04:30:00.000Z")
//    , minDate : new Date("2013-09-01T00:00:00.000" + sOffset)
//    , maxDate: new Date("2013-09-01T14:30:00.000" + sOffset)
    , requestTimeIncrementInMinutes: 1
    , gridKey : "h_-1"

    , houses: [
        {"house_id": -1, "households": 343, "plugs": 2125},
        {"house_id": 0, "households": 14, "plugs": 14},
        {"house_id": 1, "households": 3, "plugs": 7},
        {"house_id": 2, "households": 6, "plugs": 13},
        {"house_id": 3, "households": 4, "plugs": 14},
        {"house_id": 4, "households": 14, "plugs": 14},
        {"house_id": 5, "households": 2, "plugs": 7},
        {"house_id": 6, "households": 4, "plugs": 9},
        {"house_id": 7, "households": 11, "plugs": 12},
        {"house_id": 8, "households": 5, "plugs": 9},
        {"house_id": 9, "households": 4, "plugs": 14},
        {"house_id": 10, "households": 5, "plugs": 9},
        {"house_id": 11, "households": 6, "plugs": 13},
        {"house_id": 12, "households": 4, "plugs": 13},
        {"house_id": 13, "households": 2, "plugs": 13},
        {"house_id": 14, "households": 18, "plugs": 14},
        {"house_id": 15, "households": 4, "plugs": 14},
        {"house_id": 16, "households": 11, "plugs": 14},
        {"house_id": 17, "households": 4, "plugs": 8},
        {"house_id": 18, "households": 5, "plugs": 10},
        {"house_id": 19, "households": 1, "plugs": 7},
        {"house_id": 20, "households": 8, "plugs": 11},
        {"house_id": 21, "households": 14, "plugs": 14},
        {"house_id": 22, "households": 4, "plugs": 14},
        {"house_id": 23, "households": 4, "plugs": 7},
        {"house_id": 24, "households": 8, "plugs": 14},
        {"house_id": 25, "households": 6, "plugs": 13},
        {"house_id": 26, "households": 6, "plugs": 14},
        {"house_id": 27, "households": 10, "plugs": 10},
        {"house_id": 28, "households": 18, "plugs": 14},
        {"house_id": 29, "households": 3, "plugs": 14},
        {"house_id": 30, "households": 2, "plugs": 4},
        {"house_id": 31, "households": 1, "plugs": 7},
        {"house_id": 32, "households": 13, "plugs": 14},
        {"house_id": 33, "households": 12, "plugs": 14},
        {"house_id": 34, "households": 6, "plugs": 6},
        {"house_id": 35, "households": 4, "plugs": 14},
        {"house_id": 36, "households": 7, "plugs": 13},
        {"house_id": 37, "households": 9, "plugs": 14},
        {"house_id": 38, "households": 11, "plugs": 6},
        {"house_id": 39, "households": 15, "plugs": 14}
    ]
};

function initUi() {

    $('.today').text(new Date().toDateString());

    $selectedHouseDropDown = $('#selected-house');

    function onHouseSelectionChanged() {

        $this = $(this);

        var houseId = $this.attr('data-house-id');
        $selectedHouseDropDown.text($this.text());
        appData.currentHouseSelection = houseId;

        // $(".kpi-houses").text(1);
        // $(".kpi-households").text(appData.houses[houseId].households);
        // $(".kpi-plugs").text(appData.houses[houseId].plugs)
    }

    function getHouseCaption(houseId) {

        if(houseId == -1){
            return "Grid";
        }

        return "House " + houseId;
    }

    function populateHouseSelection() {

        var houses = appData.houses;

        var houseSelection = $('#house-filter');
        houseSelection.empty();

        for (var i = 0, len = houses.length; i < len; i++) {

            var currentHouse = houses[i];
            var houseItem = $("<li/>").appendTo(houseSelection);
            var houseLink = $("<a/>")
                .attr("role", "menuitem")
                .attr("tabindex", "-1")
                .attr("href", "#")
                .attr("data-house-id", currentHouse.house_id)
                .text(getHouseCaption(currentHouse.house_id))
                .click(onHouseSelectionChanged);

            houseLink.appendTo(houseItem);
        }
    }

    populateHouseSelection();
}

function initCharts() {

    var dataService = createDataService();

    var gridStats = createGridStats();
    var loadChart = createLoadChart();
    var loadDistributionChart = createLoadDistributionChart();

    dataService.addObserver(loadChart);
    dataService.addObserver(loadDistributionChart);
    dataService.addObserver(gridStats);

    dataService.start();
}

function initApp() {

    initUi();
    initCharts();
}

$(initApp);
