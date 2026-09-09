package com.taufiqhashmi.foundryai.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taufiqhashmi.foundryai.entities.WorkflowTask;

public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, UUID> {

}
