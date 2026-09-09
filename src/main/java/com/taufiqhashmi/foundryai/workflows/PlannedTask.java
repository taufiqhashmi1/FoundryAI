package com.taufiqhashmi.foundryai.workflows;

import com.taufiqhashmi.foundryai.agents.AgentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlannedTask {

    private String taskKey;

    private AgentType agentType;

    private String objective;

    private List<String> dependencies;
}