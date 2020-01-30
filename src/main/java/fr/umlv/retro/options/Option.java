package fr.umlv.retro.options;

import java.util.Objects;

public class Option {
	private final String opt;
	private final String longOpt;
	private final String description;
	private final boolean hasArg;
	private boolean hasArgs;
	private boolean required;
	private char sep;

	public Option(String opt, String longOpt, boolean hasArg, String description) {
		Objects.requireNonNull(opt);
		Objects.requireNonNull(longOpt);
		Objects.requireNonNull(description);

		this.opt = opt;
		this.longOpt = longOpt;
		this.description = description;
		this.hasArg = hasArg;
	}
	
	public String getOptName() {
		return opt;
	}
	
	public String getLongOptName() {
		return longOpt;
	}
	
	public boolean hasArg() {
		return hasArg;
	}
	
	public boolean hasArgs() {
		return hasArgs;
	}
	
	public char separator() {
		return sep;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isRequired() {
		return required;
	}

	public void setRequired() {
		required = true;
	}

	private void setSeparator(char sep) {
		this.sep = sep;
	}

	private void setHasArgs() {
		this.hasArgs = true;
	}
	
	public static Option.Builder builder(String opt) {
		var builder = new Builder(opt); 
		return builder;
	}
	
	public static class Builder {
		private String opt;
		private String longOpt;
		private boolean hasArg;
		private boolean hasArgs;
		private String desc;
		private char sep;

		public Builder(String opt) {
			Objects.requireNonNull(opt);
			this.opt = opt;

		}

		public Builder longOpt(String longOpt) {
			Objects.requireNonNull(longOpt);
			this.longOpt = longOpt;
			return this;
		}

		public Builder hasArg() {
			this.hasArg = true;
			return this;
		}

		public Builder desc(String desc) {
			Objects.requireNonNull(desc);
			this.desc = desc;
			return this;
		}

		public Builder valueSeparator(char sep) {
			this.sep = sep;
			return this;
		}

		public Builder hasArgs() {
			this.hasArgs = true;
			return this;
		}

		public Option build() {
			var option = new Option(opt, longOpt, hasArg, desc);
			if (hasArgs) {
				// l'option peut avoir plusieurs arguments
				option.setHasArgs();
				option.setSeparator(sep);
			}
			return option;
		}
	}

}
