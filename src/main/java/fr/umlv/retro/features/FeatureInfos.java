package fr.umlv.retro.features;

import java.util.Objects;

import org.objectweb.asm.tree.AbstractInsnNode;

public class FeatureInfos {
	private final String feature;
	private final String classLocation;
	private final String sourceLocation;
	private final String details;
	private final AbstractInsnNode instrMarker;
	
	public FeatureInfos(String feature, String classLocation, String sourceLocation, String details, AbstractInsnNode instrMarker) {
		this.feature = Objects.requireNonNull(feature);
		this.classLocation = Objects.requireNonNull(classLocation);
		this.sourceLocation = Objects.requireNonNull(sourceLocation);
		this.details = Objects.requireNonNull(details);
		this.instrMarker = instrMarker;
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
	
	public AbstractInsnNode getInstrMarker() {
		return instrMarker;
	}
	
	@Override
	public String toString() {
		return getName() + " at " + getClasslocation() + 
				" (" + getSourcelocation() + "): " +
				getDetails();
	}
}
