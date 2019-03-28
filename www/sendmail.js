var sendmail = {

    send: function(successCallback, errorCallback,data){
        cordova.exec(successCallback,
            errorCallback,
            "SendMail",
            "send",
            [{
                "host": data.host,
                "sender": data.from,
                "password": data.password,
                "port": data.port,
                "recipients": data.to,
                "subject":data.subject,
                "body": data.body,
                "attachment": data.attachment
            }]
        );
    }
}

module.exports = sendmail;
