const requestForm =
    document.getElementById("requestForm");

const objectiveInput =
    document.getElementById("objective");

const characterCount =
    document.getElementById("characterCount");

const executeButton =
    document.getElementById("executeButton");

const buttonText =
    document.getElementById("buttonText");

const buttonArrow =
    document.getElementById("buttonArrow");

const buttonSpinner =
    document.getElementById("buttonSpinner");

const formError =
    document.getElementById("formError");


/*
 * Character counter
 */
objectiveInput.addEventListener("input", () => {

    const length = objectiveInput.value.length;

    characterCount.textContent =
        `${length} / 5000`;

});


/*
 * Display error
 */
function showError(message) {

    formError.textContent = message;

    formError.classList.remove("hidden");

}


/*
 * Clear error
 */
function clearError() {

    formError.textContent = "";

    formError.classList.add("hidden");

}


/*
 * Loading state
 */
function setLoading(isLoading) {

    executeButton.disabled = isLoading;

    if (isLoading) {

        buttonText.textContent =
            "Executing Workflow";

        buttonArrow.classList.add("hidden");

        buttonSpinner.classList.remove("hidden");

    } else {

        buttonText.textContent =
            "Execute Workflow";

        buttonArrow.classList.remove("hidden");

        buttonSpinner.classList.add("hidden");

    }

}


/*
 * Submit request
 */
requestForm.addEventListener(
    "submit",
    async (event) => {

        event.preventDefault();

        clearError();

        const objective =
            objectiveInput.value.trim();

        if (!objective) {

            showError(
                "Please provide a business objective."
            );

            return;
        }

        if (objective.length < 3) {

            showError(
                "The business objective must contain at least 3 characters."
            );

            return;
        }

        if (objective.length > 5000) {

            showError(
                "The business objective cannot exceed 5000 characters."
            );

            return;
        }

        setLoading(true);

        try {

            /*
             * Create the request.
             *
             * This uses the existing REST API.
             */
            const createResponse =
                await fetch(
                    "/api/requests",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify({
                            objective: objective
                        })
                    }
                );

            if (!createResponse.ok) {

                let message =
                    "Failed to create request.";

                try {

                    const error =
                        await createResponse.json();

                    if (error.message) {
                        message = error.message;
                    }

                } catch (_) {
                    // Keep default message.
                }

                throw new Error(message);
            }

            const request =
                await createResponse.json();

            if (!request.id) {

                throw new Error(
                    "Request was created but no request ID was returned."
                );

            }


            /*
             * Execute workflow.
             *
             * This is your existing endpoint.
             */
            const workflowResponse =
                await fetch(
                    `/api/workflows/requests/${request.id}/execute`,
                    {
                        method: "POST"
                    }
                );

            if (!workflowResponse.ok) {

                let message =
                    "Workflow execution failed.";

                try {

                    const error =
                        await workflowResponse.json();

                    if (error.message) {
                        message = error.message;
                    }

                } catch (_) {
                    // Keep default message.
                }

                throw new Error(message);
            }

            const workflow =
                await workflowResponse.json();

            if (!workflow.id) {

                throw new Error(
                    "Workflow executed but no workflow ID was returned."
                );

            }


            /*
             * Navigate to workflow result.
             */
            window.location.href =
                `/workflows/${workflow.id}`;

        } catch (error) {

            console.error(
                "FoundryAI request execution error:",
                error
            );

            showError(
                error.message ||
                "Something went wrong while executing the workflow."
            );

            setLoading(false);

        }

    }
);