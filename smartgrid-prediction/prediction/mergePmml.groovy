import groovy.xml.StreamingMarkupBuilder

// Get the raw skeleton for the full model
metaModel = new XmlParser().parse("metaModel.pmml.skeleton")

// This snippet contains the timestamp -> derived_timestamp and value->derived_timestamp normalization
transform = new XmlParser().parse("transform.pmml")

// We'll use the element dedicated to "value" to build the derived_value -> value reverse xform
inverseTransform = transform.DerivedField.NormContinuous.find { it.@field == 'value'}
// This is the source field we'll go from
inverseTransform.@field = 'Predicted_derived_value'
// Then we swap the original and normalized attributes
inverseTransform.LinearNorm.each {
	orig = it.@orig
	it.@orig = it.@norm
	it.@norm = orig
}

// THEN, for each house...
(0..39).each {h ->
	subModel = new XmlParser().parse("model-${h}.pmml")


	// Append a new 'Segment'...
	segment = metaModel.MiningModel.Segmentation[0].
		appendNode ('Segment', [id: h])

	// ... whose predicate is simply that house == h
	segment.appendNode('SimplePredicate', [field: 'house', operator: 'equal', value: h])

	// and inside that, stick our NN element with a synthetic outfield named "FinalResult"
	subModel.NeuralNetwork.Output[0]
		.appendNode('OutputField', [name: 'FinalResult', feature: 'transformedValue'])
			.append(inverseTransform)

	subModel.NeuralNetwork.MiningSchema[0].appendNode('MiningField', [name: 'FinalResult', usageType: 'supplementary'])

	segment.append(subModel.NeuralNetwork)

}

def writer = new FileWriter("metaModel.pmml") 
new XmlNodePrinter(new PrintWriter(writer)).print(metaModel)
