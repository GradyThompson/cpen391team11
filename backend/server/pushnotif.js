var admin = require("firebase-admin");

var serviceAccount = require("./firebase-sdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

var token = 'cmRGIQ48Qd6EfZoxeh_6AI:APA91bE6NIDYJ831HAPnAzhoBNom2CXOTCYJfrKczGQCwcDwXRuAzbFG_bV-Az0_loUibG-QZoSTLcKA0rojPCtLZfVQLFjvv27uPUBzfrwT5n69-NIqL0xMMrVjLNm_hruYadr8TAPl';

var message = {
    notification: {
        title: "Smarti Alert",
        body: "A new video is available",
    }
};

admin.messaging().sendToDevice(token, message)
    .then(function(response) {
        console.log("Message sent successfully: ", response);
    })
    .catch(function(error) {
        console.log("Error sending message: ", error);
    }); 