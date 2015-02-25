function createGridStats(){


    var GridStats = function GridStatsClass() {

        GridStats.prototype.onDataArrived = function (data) {

            var totalLoad = 0;
            var gridData = data[appData.gridKey];
            var dataPoints = gridData.timeSeries[0].data;

            var lastDataPoint = dataPoints[dataPoints.length-1];

            totalLoad += lastDataPoint.value;

            $(".kpi-total-load").text(Math.round(totalLoad));
        };
    };

    return new GridStats();
}