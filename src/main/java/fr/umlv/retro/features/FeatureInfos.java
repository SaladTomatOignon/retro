package fr.umlv.retro.features;

import java.util.Objects;

public class FeatureInfos {
	private final String feature;
	private final String source;
	private final String method;
	private final String details;
	private final int line;
	
	public FeatureInfos(String feature, String source, String method, String details, int line) {
		if ( line < 0 ) {
			throw new IllegalArgumentException("Line number can not be negative");
		}
		
		this.feature = Objects.requireNonNull(feature);
		this.source = Objects.requireNonNull(source);
		this.method = Objects.requireNonNull(method);
		this.details = Objects.requireNonNull(details);
		this.line = line;
	}
	
	public String getName() {
		return feature;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getMethod() {
		return method;
	}
	
	public String getDetails() {
		return details;
	}
	
	public int getLine() {
		return line;
	}
}
