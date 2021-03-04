var admin = require("firebase-admin");

var serviceAccount = require("./firebase-sdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

var token = 'dKnkRUOpRNiyTUn69CbI0l:APA91bFsRxNvoalHRD_zImVuIvj3Vay-7dp_BGUGxx3gNi6Fn_Nsb6MqaofjbvO7dqrjCO5bfkDjBG5evtAaDRouEFPcQnfGk6kmwAWnnFsu_vdo3adkUf9do4go8iXd-mDRl98_bUm6';

var message = {
    notification: {
        title: "Hello",
        body: "Test Pushnotification",
    }
};

admin.messaging().sendToDevice(token, message)
    .then(function(response) {
        console.log("Message sent successfully: ", response);
    })
    .catch(function(error) {
        console.log("Error sending message: ", error);
    }); 