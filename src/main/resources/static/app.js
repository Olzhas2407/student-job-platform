let accessToken = null;
let expiresIn = null;

let currentApplicationId = null;
let currentApplicationStatus = null;


const loginForm =
    document.getElementById("login-form");

const loginCard =
    document.getElementById("login-card");

const identityCard =
    document.getElementById("identity-card");

const loginResult =
    document.getElementById("login-result");

const createResult =
    document.getElementById("create-result");

const submitResult =
    document.getElementById("submit-result");

const currentUsername =
    document.getElementById("current-username");

const currentRole =
    document.getElementById("current-role");

const tokenExpiry =
    document.getElementById("token-expiry");

const applicationIdElement =
    document.getElementById("application-id");

const applicationStatusElement =
    document.getElementById("application-status");

const createButton =
    document.getElementById("create-button");

const submitButton =
    document.getElementById("submit-button");

const logoutButton =
    document.getElementById("logout-button");


function correlationId() {

    if (window.crypto && crypto.randomUUID) {
        return crypto.randomUUID();
    }

    return "web-" + Date.now();
}


async function apiRequest(
    url,
    {
        method = "GET",
        body = null,
        authenticated = true
    } = {}
) {

    const headers = {
        "X-Correlation-Id": correlationId()
    };


    if (body !== null) {
        headers["Content-Type"] =
            "application/json";
    }


    if (authenticated && accessToken) {

        headers["Authorization"] =
            "Bearer " + accessToken;
    }


    const response =
        await fetch(
            url,
            {
                method: method,
                headers: headers,
                body: body === null
                    ? null
                    : JSON.stringify(body)
            }
        );


    let responseBody = {};

    try {
        responseBody = await response.json();
    } catch (ignored) {
        responseBody = {};
    }


    return {
        status: response.status,
        body: responseBody
    };
}


function showResult(
    element,
    status,
    body,
    extraText = ""
) {

    element.classList.remove(
        "hidden",
        "success",
        "error"
    );


    if (status >= 200 && status < 300) {

        element.classList.add("success");

    } else {

        element.classList.add("error");
    }


    let text =
        "HTTP " + status;


    if (body.code) {

        text +=
            "\ncode: " +
            body.code;
    }


    if (body.message) {

        text +=
            "\nmessage: " +
            body.message;
    }


    if (body.correlationId) {

        text +=
            "\ncorrelationId: " +
            body.correlationId;
    }


    if (body.status) {

        text +=
            "\nstatus: " +
            body.status;
    }


    if (extraText) {

        text +=
            "\n" +
            extraText;
    }


    element.textContent = text;
}


async function loadMe() {

    const result =
        await apiRequest(
            "/api/me"
        );


    if (result.status !== 200) {

        accessToken = null;
        expiresIn = null;

        showResult(
            loginResult,
            result.status,
            result.body
        );

        return false;
    }


    currentUsername.textContent =
        result.body.username;


    const authorities =
        result.body.authorities || [];


    currentRole.textContent =
        authorities.join(", ");


    tokenExpiry.textContent =
        expiresIn +
        " seconds (15 minutes)";


    loginCard.classList.add(
        "hidden"
    );


    identityCard.classList.remove(
        "hidden"
    );


    return true;
}


loginForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        // A failed login must not keep an old token.
        accessToken = null;
        expiresIn = null;


        const username =
            document.getElementById(
                "username"
            ).value;


        const password =
            document.getElementById(
                "password"
            ).value;


        const result =
            await apiRequest(
                "/api/v1/auth/login",
                {
                    method: "POST",

                    authenticated: false,

                    body: {
                        username: username,
                        password: password
                    }
                }
            );


        if (result.status !== 200) {

            showResult(
                loginResult,
                result.status,
                result.body
            );

            return;
        }


        // Raw token exists only in this JavaScript variable.
        // It is NOT printed and NOT stored in localStorage.
        accessToken =
            result.body.accessToken;

        expiresIn =
            result.body.expiresIn;


        loginResult.classList.add(
            "hidden"
        );


        await loadMe();
    }
);


createButton.addEventListener(
    "click",
    async function () {

        const uniqueKey =
            "APP-WEB-" + Date.now();


        const result =
            await apiRequest(
                "/api/v1/applications",
                {
                    method: "POST",

                    body: {
                        businessKey: uniqueKey,
                        title: "Lab 5 browser demo"
                    }
                }
            );


        if (
            result.status >= 200 &&
            result.status < 300
        ) {

            currentApplicationId =
                result.body.id;

            currentApplicationStatus =
                result.body.status;


            applicationIdElement.textContent =
                currentApplicationId;

            applicationStatusElement.textContent =
                currentApplicationStatus;
        }


        showResult(
            createResult,
            result.status,
            result.body
        );
    }
);


submitButton.addEventListener(
    "click",
    async function () {

        if (!currentApplicationId) {

            showResult(
                submitResult,
                400,
                {
                    code: "DEMO",
                    message:
                        "Create an application first",
                    correlationId:
                        correlationId()
                }
            );

            return;
        }


        const result =
            await apiRequest(
                "/api/v1/applications/"
                + currentApplicationId
                + "/submit",
                {
                    method: "POST"
                }
            );


        if (
            result.status >= 200 &&
            result.status < 300
        ) {

            currentApplicationStatus =
                result.body.status;


            applicationStatusElement.textContent =
                currentApplicationStatus;


            showResult(
                submitResult,
                result.status,
                result.body,
                "Locked action succeeded."
            );

            return;
        }


        if (
            result.status === 403 &&
            accessToken
        ) {

            const readResult =
                await apiRequest(
                    "/api/v1/applications/"
                    + currentApplicationId
                );


            if (readResult.status === 200) {

                currentApplicationStatus =
                    readResult.body.status;


                applicationStatusElement.textContent =
                    currentApplicationStatus;


                showResult(
                    submitResult,
                    result.status,
                    result.body,
                    "Row unchanged. Current status: "
                    + currentApplicationStatus
                );

                return;
            }
        }


        showResult(
            submitResult,
            result.status,
            result.body
        );
    }
);


logoutButton.addEventListener(
    "click",
    function () {

        // Logout means dropping the in-memory token.
        accessToken = null;
        expiresIn = null;


        currentUsername.textContent = "-";
        currentRole.textContent = "-";
        tokenExpiry.textContent = "-";


        identityCard.classList.add(
            "hidden"
        );


        loginCard.classList.remove(
            "hidden"
        );


        loginForm.reset();


        loginResult.classList.remove(
            "hidden",
            "error"
        );

        loginResult.classList.add(
            "success"
        );

        loginResult.textContent =
            "Logged out. Token removed from memory."
            + "\nTry Create application now to see HTTP 401.";
    }
);