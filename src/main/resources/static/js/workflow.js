const workflowLoading =
    document.getElementById("workflowLoading");

const workflowError =
    document.getElementById("workflowError");

const workflowErrorMessage =
    document.getElementById("workflowErrorMessage");

const workflowContent =
    document.getElementById("workflowContent");

const workflowIdElement =
    document.getElementById("workflowId");

const workflowStatusElement =
    document.getElementById("workflowStatus");

const workflowObjective =
    document.getElementById("workflowObjective");

const taskGrid =
    document.getElementById("taskGrid");

const recommendationSection =
    document.getElementById("recommendationSection");

const recommendationText =
    document.getElementById("recommendationText");

const keyFindings =
    document.getElementById("keyFindings");

const assumptions =
    document.getElementById("assumptions");

const risks =
    document.getElementById("risks");

const nextSteps =
    document.getElementById("nextSteps");


/*
 * Get workflow ID from:
 *
 * /workflows/{id}
 */
function getWorkflowId() {

    const path =
        window.location.pathname;

    const parts =
        path.split("/").filter(Boolean);

    return parts[parts.length - 1];

}


/*
 * Escape user/backend-generated text before
 * inserting it into HTML.
 */
function escapeHtml(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");

}


/*
 * Format status class.
 */
function getStatusClass(status) {

    if (!status) {
        return "";
    }

    return status
        .toLowerCase()
        .replaceAll("_", "-");

}


/*
 * Render workflow status.
 */
function renderStatus(status) {

    const normalized =
        status || "UNKNOWN";

    workflowStatusElement.textContent =
        normalized;

    workflowStatusElement.className =
        "status-badge " +
        getStatusClass(normalized);

}


/*
 * Render tasks.
 */
function renderTasks(tasks) {

    taskGrid.innerHTML = "";

    if (!tasks || tasks.length === 0) {

        taskGrid.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">—</div>
                <h3>No specialist tasks</h3>
                <p>
                    This workflow did not produce any specialist tasks.
                </p>
            </div>
        `;

        return;
    }


    tasks.forEach((task) => {

        const status =
            task.status || "UNKNOWN";

        const statusClass =
            getStatusClass(status);

        const result =
            task.result ||
            "No result available.";

        let content = "";

        if (status === "FAILED") {

            content = `
                <div class="task-error">
                    ${escapeHtml(
                        task.errorMessage ||
                        "Task execution failed."
                    )}
                </div>
            `;

        } else {

            content = `
                <p class="task-result">
                    ${escapeHtml(result)}
                </p>
            `;

        }

        const card =
            document.createElement("article");

        card.className =
            "task-card";

        card.innerHTML = `

            <div class="task-card-header">

                <div>

                    <div class="task-agent">
                        ${escapeHtml(
                            task.agentType ||
                            "UNKNOWN"
                        )}
                    </div>

                    <div class="task-order">
                        Execution ${escapeHtml(
                            task.executionOrder ?? "-"
                        )}
                    </div>

                </div>

                <span
                    class="task-status ${statusClass}"
                >
                    ${escapeHtml(status)}
                </span>

            </div>

            <div class="task-card-body">

                <div class="task-objective">
                    ${escapeHtml(
                        task.objective ||
                        "No objective provided."
                    )}
                </div>

                ${content}

                <div class="task-meta">

                    ${
                        task.startedAt
                            ? `<span>Started: ${escapeHtml(
                                formatDate(task.startedAt)
                            )}</span>`
                            : ""
                    }

                    ${
                        task.completedAt
                            ? `<span>Completed: ${escapeHtml(
                                formatDate(task.completedAt)
                            )}</span>`
                            : ""
                    }

                </div>

            </div>
        `;

        taskGrid.appendChild(card);

    });

}


/*
 * Render CEO recommendation.
 */
function renderRecommendation(recommendation) {

    if (!recommendation) {

        recommendationSection
            .classList
            .add("hidden");

        return;
    }

    recommendationSection
        .classList
        .remove("hidden");


    recommendationText.textContent =
        recommendation.recommendation ||
        "No recommendation available.";


    /*
     * Key findings
     */
    keyFindings.innerHTML = "";

    const findings =
        recommendation.keyFindings || {};

    const findingEntries =
        Object.entries(findings);

    if (findingEntries.length === 0) {

        keyFindings.innerHTML = `
            <div class="finding-item">
                <div class="finding-value">
                    No key findings available.
                </div>
            </div>
        `;

    } else {

        findingEntries.forEach(
            ([key, value]) => {

                const item =
                    document.createElement("div");

                item.className =
                    "finding-item";

                item.innerHTML = `

                    <div class="finding-key">
                        ${escapeHtml(key)}
                    </div>

                    <div class="finding-value">
                        ${escapeHtml(
                            formatValue(value)
                        )}
                    </div>

                `;

                keyFindings.appendChild(item);

            }
        );

    }


    /*
     * Lists
     */
    renderList(
        assumptions,
        recommendation.assumptions
    );

    renderList(
        risks,
        recommendation.risks
    );

    renderList(
        nextSteps,
        recommendation.nextSteps
    );

}


/*
 * Render array.
 */
function renderList(element, values) {

    element.innerHTML = "";

    if (!Array.isArray(values) ||
        values.length === 0) {

        const li =
            document.createElement("li");

        li.textContent =
            "None provided.";

        element.appendChild(li);

        return;
    }

    values.forEach((value) => {

        const li =
            document.createElement("li");

        li.textContent =
            formatValue(value);

        element.appendChild(li);

    });

}


/*
 * Convert structured values into readable text.
 */
function formatValue(value) {

    if (value === null ||
        value === undefined) {

        return "";
    }

    if (typeof value === "object") {

        try {

            return JSON.stringify(
                value,
                null,
                2
            );

        } catch (_) {

            return String(value);

        }

    }

    return String(value);

}


/*
 * Format dates.
 */
function formatDate(value) {

    if (!value) {
        return "";
    }

    const date =
        new Date(value);

    if (Number.isNaN(
        date.getTime()
    )) {
        return value;
    }

    return date.toLocaleString(
        undefined,
        {
            dateStyle: "medium",
            timeStyle: "short"
        }
    );

}


/*
 * Load workflow.
 */
async function loadWorkflow(id) {

    try {

        /*
         * Existing workflow GET endpoint.
         */
        const response =
            await fetch(
                `/api/workflows/${encodeURIComponent(id)}`
            );

        if (!response.ok) {

            if (response.status === 404) {

                throw new Error(
                    "The requested workflow was not found."
                );

            }

            throw new Error(
                "Failed to load workflow."
            );

        }

        const workflow =
            await response.json();

        renderWorkflow(workflow);

    } catch (error) {

        console.error(
            "Workflow loading error:",
            error
        );

        showWorkflowError(
            error.message ||
            "Unable to load workflow."
        );

    }

}


/*
 * Render complete workflow.
 */
function renderWorkflow(workflow) {

    workflowLoading
        .classList
        .add("hidden");

    workflowError
        .classList
        .add("hidden");

    workflowContent
        .classList
        .remove("hidden");


    workflowIdElement.textContent =
        workflow.id || "Unknown workflow";


    renderStatus(
        workflow.status
    );


    /*
     * The workflow response currently
     * contains requestId rather than the
     * full request object.
     *
     * If your WorkflowResponseDTO later
     * exposes objective, it can be rendered
     * directly here.
     *
     * For now, don't invent it.
     */
    workflowObjective.textContent =
        workflow.objective ||
        "Business objective associated with request " +
        (workflow.requestId || "unknown") +
        ".";


    renderTasks(
        workflow.tasks
    );


    renderRecommendation(
        workflow.recommendation
    );

}


/*
 * Show workflow error.
 */
function showWorkflowError(message) {

    workflowLoading
        .classList
        .add("hidden");

    workflowContent
        .classList
        .add("hidden");

    workflowError
        .classList
        .remove("hidden");

    workflowErrorMessage.textContent =
        message;

}


/*
 * Initialize.
 */
const workflowId =
    getWorkflowId();

if (!workflowId) {

    showWorkflowError(
        "No workflow ID was provided."
    );

} else {

    loadWorkflow(workflowId);

}