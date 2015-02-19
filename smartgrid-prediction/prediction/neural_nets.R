# cd into the springxd-smartgrid-demo/prediction dir:
#setwd('xxxxx')

require(pmml)
require(pmmlTransformations)
require(nnet)

# Read the dataset in csv format
grid = read.csv(file="agg_load_min_by_h.csv", sep=',', col.names=c('id','timestamp','value','pty','plug','household','house'))

# The gridBox object will remember the normalization we're doing
gridBox <- WrapData(subset(grid, pty==1))

gridBox <- ZScoreXform(gridBox, xformInfo="value")
gridBox <- ZScoreXform(gridBox, xformInfo="timestamp")

valueXForm <- pmml(NULL, transforms=gridBox)
saveXML(valueXForm, file="transform.pmml")

for (h in 0:39) {
	thisHouse <- subset(gridBox$data, house == h)
	# Train the NN
	nn <- nnet(derived_value ~derived_timestamp, thisHouse, linout=TRUE, size=10, maxit=1000)

	# Plot the results
	plot(thisHouse[, "derived_timestamp"], thisHouse[, "derived_value"])
	lines(thisHouse[, "derived_timestamp"], predict(nn, data.frame("derived_timestamp"=thisHouse[, "derived_timestamp"])), col="green")

	pmml <- pmml(nn, transforms=gridBox)
	pmmlModelName = paste('model-', h, '.pmml', sep='')
	saveXML(pmml, file=pmmlModelName)

}



