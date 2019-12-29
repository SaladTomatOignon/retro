package fr.umlv.retro.features;

import java.util.Objects;

public class FeatureInfos {
	private final String feature;
	private final String classLocation;
	private final String sourceLocation;
	private final String details;
	
	public FeatureInfos(String feature, String classLocation, String sourceLocation, String details) {
		this.feature = Objects.requireNonNull(feature);
		this.classLocation = Objects.requireNonNull(classLocation);
		this.sourceLocation = Objects.requireNonNull(sourceLocation);
		this.details = Objects.requireNonNull(details);
	}
	
	public String getName() {
		return feature;
	}
	
	public String getClasslocation() {
		return classLocation;
	}
	
	public String getSourcelocation() {
		return sourceLocation;
	}
	
	public String getDetails() {
		return details;
	}
	
}
