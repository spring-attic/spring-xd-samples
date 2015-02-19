function createLoadChart() {

    var LoadChart = function LoadChartClass(selector) {

        var x;
        var y;
        var xAxis;
        var yAxis;
        var area;
        var line;
        var margin;
        var width;
        var height;
        var initialLoad = true;

        function init() {

            $grid = $(selector);
            $parent = $grid.parent();
            margin = {top: 20, right: 20, bottom: 80, left: 80};
            width = $parent.width() - margin.left - margin.right;
            height = 500 - margin.top - margin.bottom;

            // Scales and axes. Note the inverted domain for the y-scale: bigger is up!
            x = d3.time.scale().range([0, width]);
            y = d3.scale.linear().range([height, 0]);
            xAxis = d3.svg.axis().scale(x).ticks(6).tickFormat(d3.time.format("%H:%M"));
            yAxis = d3.svg.axis().scale(y).ticks(10).orient("left");

            line = d3.svg.line()
                .interpolate("basis")
                .defined(function (d) {
                    return d.value != -1;
                })
                .x(function (d) {
                    return x(d.ts);
                })
                .y(function (d) {
                    return y(d.value);
                });
        }

        function extractGridDataFrom(data) {
            return data["h_" + appData.currentHouseSelection];
        }

        function getMaxFromGivenLists(listA, listB, extractor) {

            return Math.max(d3.max(listA, function (d) {
                    return extractor(d);
                }),
                d3.max(listB, function (d) {
                    return extractor(d);
                })
            );
        }

        function update(data, initialLoad) {

            var houseData = extractGridDataFrom(data);

            var actual = houseData.timeSeries[0].data;
            actual.class = 'actual';

            var prediction = houseData.timeSeries[1].data;
            prediction.class = 'prediction';

            var tsMin = actual[0].ts;
            var tsMax = getMaxFromGivenLists(actual, prediction, function (d) {
                return d.ts;
            });

            var valueMax = getMaxFromGivenLists(actual, prediction, function (d) {
                return d.value;
            });

            x.domain([tsMin, tsMax]);
            y.domain([0, valueMax]);

            if (!initialLoad) {

                var svg = d3.select(selector).transition();

                svg.select(".x.axis") // change the x axis
                    .duration(0)
                    .call(xAxis);
                svg.select(".y.axis") // change the y axis
                    .duration(0)
                    .call(yAxis);

                svg.select(".line.actual")
                    .duration(0)
                    .attr("d", [line(actual)]);
                svg.select(".line.prediction")
                    .duration(0)
                    .attr("d", [line(prediction)]);

                return;
            }

            var svg = d3.select(selector)
                .attr("width", "100%")
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")")

            // Add the clip path.
            svg.append("clipPath")
                .attr("id", "clip")
                .append("rect")
                .attr("width", width)
                .attr("height", height);

            // Add the x-axis.
            svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);

            // Add the y-axis.
            svg.append("g")
                .attr("class", "y axis")
                .attr("transform", "translate(" + 0 + ",0)")
                .call(yAxis);

            var colorScheme = d3.scale.category10();
            svg.selectAll('.line')
                .data([actual, prediction])
                .enter()
                .append('path')
                .attr('class', function (d) {
                    return 'line ' + d.class;
                })
                .style('stroke', function (d) {
                    return colorScheme(Math.random() * 50);
                })
                .attr('clip-path', 'url(#clip)')
                .attr('d', function (d) {
                    return line(d);
                });
        }

        LoadChart.prototype.onDataArrived = function (data) {

            update(data, initialLoad);

            if (initialLoad) {
                initialLoad = false;
            }
        };

        init();
    };

    return new LoadChart('.smart-grid-load-chart');
}
