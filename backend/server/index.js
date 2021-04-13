const express = require('express');
const app = express();

const { MongoClient } = require('mongodb');
const CONNECTION_URL = 'mongodb+srv://testboy:dudwotkrhks2@cluster0.i6ov7.mongodb.net/myFirstDatabase?retryWrites=true&w=majority'
const client = new MongoClient(CONNECTION_URL, { useUnifiedTopology: true });
client.connect();

var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

const ffmpegPath = require('@ffmpeg-installer/ffmpeg').path;
var ffmpeg = require("fluent-ffmpeg");
ffmpeg.setFfmpegPath(ffmpegPath);

const multer = require('multer');
const path = require('path');
const mstorage = multer.diskStorage({
	destination: (req, file, cb) => {
		cb(null, 'uploads/')
	},
	filename: (req, file, cb) => {
		cb(null, file.originalname)
	},
});
const upload = multer({storage: mstorage});

var admin = require('firebase-admin');
var serviceAccount = require('./firebasecpen391.json');
const { SSL_OP_DONT_INSERT_EMPTY_FRAGMENTS } = require('constants');
admin.initializeApp({
	credential: admin.credential.cert(serviceAccount),
	storageBucket: 'macro-dogfish-306517.appspot.com'
});
var bucket =  admin.storage().bucket();

var registrationToken = 'cmRGIQ48Qd6EfZoxeh_6AI:APA91bE6NIDYJ831HAPnAzhoBNom2CXOTCYJfrKczGQCwcDwXRuAzbFG_bV-Az0_loUibG-QZoSTLcKA0rojPCtLZfVQLFjvv27uPUBzfrwT5n69-NIqL0xMMrVjLNm_hruYadr8TAPl';

app.get('/', (req, res) => {
	res.send("we are connected");
});

app.post('/createuser', (req, response) => {
	// check is user_id is already in db, return error\
	if (req.body.uid == undefined) {
		response.status(400).send({ "message": "Create new user failed due to request fields not being valid" });
	}
	else {
		var userDB = client.db("smarti").collection("user");
		userDB.findOne({ Uid: req.body.uid }, function (err, item) {
			if (err) {
				throw err;
			}
			/**
			 * If user does not exist, we have to insert the user into the DB
			 */
			if (item == undefined) {
				userDB.insertOne({
					Uid: req.body.uid,
					Token: req.body.token,
					Threshold: 0,
					Notification: true,
				}, function (err) {
					if (err) {
						response.status(400).send({ "message": "Error occured when creating user" }, 400);
						throw err;
					}
					else {
						response.status(200).send({ success: true });
					}
				});
			}
			else {
				// User was already created
				response.status(200).send({ success: true });
			}
		});
	}
});

app.get('/getvideo', (req, response) => {
	var fileDB = client.db("smarti").collection("file");
	fileDB.find({}, {projection: {_id: 0}}).toArray(function(error, documents) {
		if (error) throw error;
	
		response.send(documents);
	});
	
});

app.post('/setting', (req, response) => {
	if (req.body.Threshold == undefined || req.body.Toggle == undefined) {
		response.status(400).send({ "message": "Setting failed due to request fields not being valid" });
	} else {
		var userDB = client.db("smarti").collection("user");
		userDB.insertOne({
			Threshold: req.body.Threshold,
			Notification: req.body.Toggle
		}, function (err) {
			if (err) {
				response.status(400).send({ "message": "Error occured when changing settings" }, 400);
				throw(err);
			} else {
				response.status(200).send({ "message": "Succesfully changed settings"});
			}
		});
	}
});

app.post('/uploadvideo', upload.single('video'), (req, res, next) => {
	console.log(req.file);

	var inFile = path.parse(req.file.filename);
	var date = req.body.date;
	var length = req.body.length;

	if (date == undefined || length == undefined) {
		res.status(400).send({ "message": "date/length fields are not valid" });
	}

	console.log(date);
	console.log(length);
	
	var outFile = 'uploads/' + inFile.name + '.mp4';

	console.log("start converting");

	// video processing, convert mpeg to mp4
	ffmpeg(req.file.path)
	.save(outFile)
	.on('end', function() {
		console.log('processing done');
		// upload converted video to cloud storage
		bucket.upload(outFile, function(err, file, apiResponse) {
			if (err) {
				console.log('upload error');
				res.status(400).send({ "message": "upload was unsuccessful" });
			}
		});

		// run motion detection / severity calculation
		const spawn = require("child_process").spawn;
		const mt = spawn('python3', ['./motion_detector.py', outFile, date]);
		console.log('start motion detection');

		mt.stdout.on('data', (data) => {
			console.log(data.toString());
			var score = data.toString().replace(/\n/g, '');

			// insert file into to the database
			var fileDB = client.db("smarti").collection("file");
			fileDB.insertOne({
				Url: "gs://macro-dogfish-306517.appspot.com/" + inFile.name + '.mp4',
				Date: date,
				Length: length,
				Severity: score
			}, function (err) {
				if (err) {
					throw err;
				}
				else {
					console.log("successful!")

					// check if push notification needs to be sent to the user
					var userDB = client.db("smarti").collection("user");
					userDB.findOne({}, function (err, item) {
						if (err) {
							throw err;
						}

						console.log(item.Threshold);
						console.log(item.Notification);
						vidResult = parseInt(score);
						console.log(vidResult);

						if (item.Threshold <= vidResult && item.Notification) {
							console.log("need to send notification!");
							const message = {
								notification: {
									title: 'Smarti Alert',
									body: 'New video is available'
								},
								token: registrationToken
							};
						
							// Send a message to the device corresponding to the provided
							// registration token.
							admin.messaging().send(message)
								.then((response) => {
									console.log('Successfully sent message:', response);
									res.send("successful");
								})
								.catch((error) => {
									console.log('Error sending message:', error);
							});
						} else {
							console.log("no need to send notification");
							res.send("no notif end");
						}
					});
				}
			});
		});

		mt.stderr.on('data', (data) => {
			console.log(data.toString());
		});
	})
});


app.listen(3000, () => console.log('listening on port 3000'));