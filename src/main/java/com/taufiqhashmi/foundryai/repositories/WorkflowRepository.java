package com.taufiqhashmi.foundryai.repositories;


import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.taufiqhashmi.foundryai.entities.Workflow;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {

}
