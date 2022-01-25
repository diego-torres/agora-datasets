package com.demo.agora_datasets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.process.workitem.core.util.RequiredParameterValidator;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.WidResult;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidAuth;
import org.jbpm.process.workitem.core.util.service.WidService;
import org.jbpm.process.workitem.core.util.WidMavenDepends;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Wid(widfile="SlackPostMessageDefinitions.wid", name="SlackPostMessage",
        displayName="SlackPostMessage",
        defaultHandler="mvel: com.demo.agora_datasets.SlackWorkItemHandler()",
        documentation = "Slack/index.html",
        category = "Slack",
        icon = "SlackPostMessage.png",
        parameters={
                @WidParameter(name = "channelName", required = true),
                @WidParameter(name = "message", required = true)
        },
        mavenDepends={
        		@WidMavenDepends(group = "${groupId}", artifact = "${artifactId}", version = "${version}")
        },
        serviceInfo = @WidService(category = "${name}", description = "${description}",
                keywords = "slack,message,send,channel",
                action = @WidAction(title = "Send message to a slack channel"),
                authinfo = @WidAuth(required = true, params = {"accessToken"},
                        paramsdescription = {"Slack access token"},
                        referencesite = "https://api.slack.com/tokens")
        )
)


public class SlackWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
        private static final Logger logger = LoggerFactory.getLogger(SlackWorkItemHandler.class);
        private String accessToken;
        private Slack slack = Slack.getInstance();

        public SlackWorkItemHandler(String accessToken){
            this.accessToken = accessToken;        
        }       
        
        private String SendMessage(String channel, String message) {
           try {	
	        	ChatPostMessageResponse response = slack.methods(accessToken).chatPostMessage(req -> req
	        			  .channel(channel)
	        			  .text(message));
	        	return (response.isOk() ? response.getMessage().getText() : response.getError());
		   } catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
		   }  
       }
        



        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        try {
            RequiredParameterValidator.validate(this.getClass(), workItem);

            // sample parameters
            String channelName = (String) workItem.getParameter("channelName");
            String message = (String) workItem.getParameter("message");

            // complete workitem impl...
                        
            // return results
            String sampleResult = this.SendMessage(channelName, message);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", sampleResult);


            manager.completeWorkItem(workItem.getId(), null);
        } catch(Throwable cause) {
            handleException(cause);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // stub
    }
}


