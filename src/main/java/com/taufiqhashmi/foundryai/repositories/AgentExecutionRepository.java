package com.taufiqhashmi.foundryai.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taufiqhashmi.foundryai.entities.AgentExecution;

public interface AgentExecutionRepository extends JpaRepository<AgentExecution, UUID> {

}
