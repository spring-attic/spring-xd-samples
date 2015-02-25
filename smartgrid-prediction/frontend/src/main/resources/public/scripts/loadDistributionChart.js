function createLoadDistributionChart() {

    var LoadDistributionChart = function LoadDistributionChartClass(selector) {

        $loadDistributionChart = $(selector);
        $parent = $loadDistributionChart.parent();

        var initialLoad = true;
        var x, chart, bar, barHeight = 20, width;

        function update(data, initialLoad) {

            var max = d3.max(data, function (d) {
                return d.value
            });

            for (var i = 0; i < data.length; i++) {
                data[i].value = data[i].value * 100 / max;
            }

            var colorScheme = d3.scale.category10();

            width = $parent.width();

            if(!initialLoad){

                d3.selectAll(selector).selectAll('rect')
                    .data(data)
                    .transition()
                    .attr("width", function (d) {
                        return x(d.value);
                    })
                    .attr("height", barHeight - 1)
                    .attr('fill', function (d, i) {
                        return colorScheme(i);
                    });

                return;
            }


            x = d3.scale.linear()
                .domain([0, 100])
                .range([0, width]);

            chart = d3.select(selector)
                .attr("width", width)
                .attr("height", barHeight * data.length);

            bar = chart.selectAll("g")
                .data(data)
                .enter().append("g")
                .attr("transform", function (d, i) {
                    return "translate(0," + i * barHeight + ")";
                });


            bar.append("rect")
                .attr("width", function (d) {
                    return x(d.value);
                })
                .attr("height", barHeight - 1)
                .attr('fill', function (d, i) {
                    return colorScheme(i);
                });


            bar.append("text")
                .attr("x", 2)
                .attr("class","load-distribution-bar-label")
                .attr("y", barHeight / 2)
                .attr("dy", ".35em")
                .text(function (d) {
                    return d.label;
                });
        }

        LoadDistributionChart.prototype.onDataArrived = function (data) {

            var dataSet = [];
            for (var houseKey in data) {

                if(houseKey === appData.gridKey){
                    continue;
                }

                var houseId = houseKey.split('_')[1];

                if (parseInt(houseId) >= 0) {

                    var actualLoadTimeSeries = data[houseKey].timeSeries[0];

                    var item = {
                        label: 'House ' + houseId,
                        value: actualLoadTimeSeries.data[actualLoadTimeSeries.data.length -1].value
                    };

                    dataSet.push(item);
                }
            }

            update(dataSet, initialLoad);

            if (initialLoad) {
                initialLoad = false;
            }
        }
    };

    return new LoadDistributionChart('.smart-grid-load-distribution-chart');
}
