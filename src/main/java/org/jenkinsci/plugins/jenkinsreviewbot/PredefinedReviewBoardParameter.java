/*
The MIT License

Copyright (c) 2013, Sylvain Benner.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package org.jenkinsci.plugins.jenkinsreviewbot;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.TaskListener;
import hudson.plugins.parameterizedtrigger.AbstractBuildParameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.filters.StringInputStream;
import org.kohsuke.stapler.DataBoundConstructor;

public class PredefinedReviewBoardParameter extends AbstractBuildParameters {

	private final String properties;

	@DataBoundConstructor
	public PredefinedReviewBoardParameter(String properties) {
		this.properties = properties;
	}

	public Action getAction(AbstractBuild<?, ?> build, TaskListener listener)
	        throws IOException, InterruptedException {

		EnvVars env = getEnvironment(build, listener);

		Properties p = new Properties();
		p.load(new StringInputStream(properties));

		List<ParameterValue> values = new ArrayList<ParameterValue>();
		for (Map.Entry<Object, Object> entry : p.entrySet()) {
			values.add(new ReviewboardParameterValue(entry.getKey()
			        .toString(), env.expand(entry.getValue().toString())));
		}

		return new ParametersAction(values);
	}

	public String getProperties() {
		return properties;
	}

	@Extension
	public static class DescriptorImpl extends
	        Descriptor<AbstractBuildParameters> {
		@Override
		public String getDisplayName() {
			return "Predefined review url parameter";
		}
	}

}
