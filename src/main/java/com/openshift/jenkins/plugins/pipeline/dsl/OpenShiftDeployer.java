package com.openshift.jenkins.plugins.pipeline.dsl;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepMonitor;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.openshift.jenkins.plugins.pipeline.ParamVerify;
import com.openshift.jenkins.plugins.pipeline.model.IOpenShiftDeployer;

public class OpenShiftDeployer extends OpenShiftBaseStep implements IOpenShiftDeployer {
	
	protected final String depCfg;
	protected String waitTime;
    
    @DataBoundConstructor public OpenShiftDeployer(String depCfg) {
    	this.depCfg = depCfg;
	}   
    
	public String getDepCfg() {
		return depCfg;
	}

	public String getWaitTime() {
		return waitTime;
	}
	
	public String getWaitTime(Map<String, String> overrides) {
		String val = getOverride(getWaitTime(), overrides);
		if (val.length() > 0)
			return val;
		return "60000";
	}
	
	@DataBoundSetter public void setWaitTime(String waitTime) {
		this.waitTime = waitTime;
	}
	
	@Override
	public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
		return true;
	}

	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return null;
	}

	@Override
	public Collection<? extends Action> getProjectActions(
			AbstractProject<?, ?> project) {
		return null;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return null;
	}

    private static final Logger LOGGER = Logger.getLogger(OpenShiftDeployer.class.getName());


	@Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(OpenShiftDeployerExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "openShiftDeploy";
        }

        @Override
        public String getDisplayName() {
            return DISPLAY_NAME;
        }

        @Override
        public Step newInstance(Map<String, Object> arguments) throws Exception {
            if (!arguments.containsKey("deploymentConfig"))
            	throw new IllegalArgumentException("need to specify deploymentConfig");
            OpenShiftDeployer step = new OpenShiftDeployer(arguments.get("deploymentConfig").toString());
            
            if (arguments.containsKey("waitTime")) {
            	Object waitTime = arguments.get("waitTime");
            	if (waitTime != null)
            		step.setWaitTime(waitTime.toString());
            }
            
            ParamVerify.updateDSLBaseStep(arguments, step);
            return step;
        }
    }


}