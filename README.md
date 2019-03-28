# send-mail-cordova-plugin

- This Cordova plugin allows to send an email using Android platform without email composer.
- This is a https://github.com/raguilera82/send-mail-cordova-plugin.git fork.
- The original uses a G-mail server, but this has been changed to use an arbitrary server.
- It only supports Android. (Anyone please support iOS version. :) )
- Multiple attachments can be used. (The source of the file is base64 character string)

# Add in Cordova/PhoneGap project

```bash
cordova plugin add https://github.com/Yanchi4425/send-mail-cordova-plugin.git
```

# Usage

```javascript

    // After firing the deviceReady event
    function sendEmail(){
        var attachments = [];
        attachments.push({fileName:"hoge.csv", base64Source: "aG9nZSxmdWdhLHBpeW8="});
        attachments.push({fileName:"fuga.png", base64Source: "iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAIAAAD91JpzAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAUSURBVBhXYwCBRS8Y3sqo/P//HwAZpAW19iezEgAAAABJRU5ErkJggg=="});
        
        let mailData = {
                host: "smtp.example.com", // SMTP serverhost 
                from : "your-address@example.com",//from (login id.)
                password:"password", // login password.
                port:"587", // SMTP Server Port No.
                to: "to-address@example.com",
                subject: "Subject",
                body: "mail body",
                // body: "<span style='color:orange'>mail body</span>", // can use html.
                attachment: attachments
        };

        // Go.
        sendmail.send(sendMailSuccess, sendMailError,mailData);
    }
    
    function sendMailSuccess(successMessage) {
        console.log(successMessage);
    }
    
    function sendMailError(error) {
        console.log('Error: ' + error);
    }
```