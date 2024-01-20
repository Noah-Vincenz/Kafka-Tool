const uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

function notUUID(input) {
    if (uuidPattern.test(input) === false) {
        alert('\"' + input + '\" is not a valid UUID');
        return true;
    } else {
        return false;
    }
}

function isEmpty(inputName, intputValue) {
    if (intputValue === "") {
        alert('Please enter a value for ' + inputName);
        return true;
    } else {
        return false
    }
}

function eventNameIsValid(eventName, topicName) {
    switch(topicName) {
        case "SharedKafka2_600000190_accountingentry":
            switch (eventName) {
                case "AccountingEntry":
                    return true;
                default: return alertAndReturnFalse(topicName);
            }
        case "SharedKafka2_600000190_payment":
            switch (eventName) {
                case "PaymentAccepted": case  "PaymentCancellationRequested": case "PaymentRequestCancelled": case "PendingSettlement": case "PaymentSubmitted": case "PaymentDelivered": case "PaymentRequestRejected":
                    return true;
                default: return alertAndReturnFalse(topicName);
            }
        case "SharedKafka2_600000190_settlement-instruction":
            switch (eventName) {
                case "SettlementCancellationRejected": case "SettlementCancelled": case "SettlementDelivered": case "SettlementReturned": case "PaymentDeliveryDateAdjusted":
                    return true;
                default: return alertAndReturnFalse(topicName);
            }
        case "SharedKafka2_600000190_settlement-gw-out":
            switch (eventName) {
                case "PaymentsInitiation": case "SettlementNotification":
                    return true;
                default: return alertAndReturnFalse(topicName);
            }
        case "SharedKafka2_600000190_settlement-gw-in":
            switch (eventName) {
                case "SettlementStatus": case "SettlementFundsTransferStatusReport": case "SettlementMoneyMovement":
                    return true;
                default: return alertAndReturnFalse(topicName);
            }
        case "SharedKafka2_600000190_trading-gw-in":
            switch (eventName) {
                case "TradingPositionCovered":
                    return true;
                default: return alertAndReturnFalse(topicName);
            }
        case "SharedKafka2_600000190_trading-gw-out":
            switch (eventName) {
                case "TradingSpotForward":
                    return true;
                default: return alertAndReturnFalse(topicName);
            }
        case "SharedKafka2_600000190_trading-positions":
            switch (eventName) {
                case "PositionCovered": case "PositionExposed":
                    return true;
                default: return alertAndReturnFalse(topicName);
            }
        default: return false;
    }
}

function alertAndReturnFalse(topicName) {
    alert('Please enter a valid eventName for topic: ' + topicName);
    return false;
}

function today() {
    const dt = new Date();
    const dtUTC =  new Date(Date.UTC(dt.getUTCFullYear(), dt.getUTCMonth(), dt.getUTCDate(), dt.getUTCHours(), dt.getUTCMinutes(), dt.getUTCSeconds()));
    return dtUTC.getFullYear() + "-" + ("0" + (dtUTC.getMonth()+1)).slice(-2) + "-" + ("0" + dtUTC.getDate()).slice(-2)
}

function tenDaysAgo() {
    const dt = new Date();
    const dtUTC =  new Date(Date.UTC(dt.getUTCFullYear(), dt.getUTCMonth(), dt.getUTCDate() - 10, dt.getUTCHours(), dt.getUTCMinutes(), dt.getUTCSeconds()));
    return dtUTC.getFullYear() + "-" + ("0" + (dtUTC.getMonth()+1)).slice(-2) + "-" + ("0" + dtUTC.getDate()).slice(-2)
}

function tenDaysFromNow() {
    const dt = new Date();
    const dtUTC =  new Date(Date.UTC(dt.getUTCFullYear(), dt.getUTCMonth(), dt.getUTCDate() + 10, dt.getUTCHours(), dt.getUTCMinutes(), dt.getUTCSeconds()));
    return dtUTC.getFullYear() + "-" + ("0" + (dtUTC.getMonth() + 1)).slice(-2) + "-" + ("0" + dtUTC.getDate()).slice(-2);
}

function setup() {
    const date = today();
    const url = new URL(window.location.href);
    const fromDate = url.searchParams.get("fromDate");
    const toDate = url.searchParams.get("toDate");
    const filter = url.searchParams.get("filter");
    let fromTime = url.searchParams.get("fromTime");
    let toTime = url.searchParams.get("toTime");
    if (fromDate == null) {
        document.getElementById("from-date").value = date;
    } else {
        // contains from param
        document.getElementById("from-date").value = fromDate;
    }
    if (toDate == null) {
        document.getElementById("to-date").value = date;
    } else {
        // contains to param
        document.getElementById("to-date").value = toDate;
    }
    if (filter != null) {
        // contains filter param
        document.getElementById("filter-input").value = filter;
    }
    if (fromTime !== null) {
        fromTime = fromTime.substring(0, fromTime.length - 2); // strip off last two digits that were added
        fromTime = [fromTime.slice(0, 2), ":", fromTime.slice(2)].join(''); // add colon in the middle
        document.getElementById("from-time").value = fromTime;
    }
    if (toTime !== null) {
        toTime = toTime.substring(0, toTime.length - 2); // strip off last two digits that were added
        toTime = [toTime.slice(0, 2), ":", toTime.slice(2)].join(''); // add colon in the middle
        document.getElementById("to-time").value = toTime;
    }
    document.getElementById("to-date").setAttribute("min", tenDaysAgo());
    document.getElementById("to-date").setAttribute("max", tenDaysFromNow());
    document.getElementById("from-date").setAttribute("min", tenDaysAgo());
    document.getElementById("from-date").setAttribute("max", tenDaysFromNow());
}
