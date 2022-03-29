package edu.caltech.cs2.lab04;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;


public class DecisionTree {
    private final DecisionTreeNode root;

    public DecisionTree(DecisionTreeNode root) {
        this.root = root;
    }



    public String predict(Dataset.Datapoint point) {
        AttributeNode j = (AttributeNode) root;
        while (!j.children.get(point.attributes.get(j.attribute)).isLeaf()) {
            j = (AttributeNode) j.children.get(point.attributes.get(j.attribute));
        }
        if (j.children.get(point.attributes.get(j.attribute)).isLeaf()) {
            OutcomeNode t = (OutcomeNode) j.children.get(point.attributes.get(j.attribute));
            return t.outcome;
        }
        return "";
    }

    public static DecisionTreeNode id3helper(Dataset dataset, List<String> attributes){
        if (dataset.pointsHaveSameOutcome().length() > 0) {
            return new OutcomeNode(dataset.pointsHaveSameOutcome());
        }
        if (attributes.size() == 0) {
            return new OutcomeNode(dataset.getMostCommonOutcome());
        }
        String a = dataset.getAttributeWithMinEntropy(attributes);
        List<String> features = dataset.getFeaturesForAttribute(a);
        List<String> newAttributes = new ArrayList<>();
        Map<String, DecisionTreeNode> map = new HashMap<>();
        for (String feature : features) {
            if (dataset.getPointsWithFeature(feature).size() == 0) {
                map.put(feature, new OutcomeNode(dataset.getMostCommonOutcome()));
            }
            else {
                for (String attribute : attributes) {
                    if (!attribute.equals(a)) {
                        newAttributes.add(attribute);
                    }
                }
                map.put(feature, id3helper(dataset.getPointsWithFeature(feature), newAttributes));
            }
        }
        return new AttributeNode(dataset.getAttributeWithMinEntropy(attributes), map);

    }

    public static DecisionTree id3(Dataset dataset, List<String> attributes) {
        return new DecisionTree(id3helper(dataset, attributes));
    }
}
